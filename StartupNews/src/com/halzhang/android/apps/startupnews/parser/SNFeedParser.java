/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.parser;

import com.halzhang.android.apps.startupnews.entity.SNFeed;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.entity.SNUser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import java.util.ArrayList;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 18, 2013
 */
public class SNFeedParser extends BaseHTMLParser<SNFeed> {

    private static final String LOG_TAG = SNFeedParser.class.getSimpleName();

    @Override
    public SNFeed parseDocument(Document doc) throws Exception {
        SNFeed feed = new SNFeed();
        if (doc == null) {
            return feed;
        }
        long start = System.currentTimeMillis();
        Elements tableRows = doc.select("table tr table tr");
        tableRows.remove(0);// 顶部导航
        // 获取下一页链接
        Elements moreURLElements = tableRows.select("a:matches(More)");
        String moreURL = null;
        if (moreURLElements.size() > 0) {
            moreURL = resolveRelativeSNURL(moreURLElements.attr("href"));
        }
        feed.setMoreUrl(moreURL);
        ArrayList<SNNew> snNews = new ArrayList<SNNew>(32);
        String url = null;
        String title = null;
        String urlDomain = null;
        String voteURL = null;
        int points = 0;
        int commentsCount = 0;
        String discussURL = null;
        String subText = null;
        SNUser user = null;
        String postID = null;
        boolean endParse = false;
        for (int row = 0; row < tableRows.size(); row++) {
            int rowInPost = row % 3;
            Element rowElement = tableRows.get(row);
            switch (rowInPost) {
                case 0:
                    // 标题
                    Element titleAElement = rowElement.select("tr > td:eq(2) > a").first();
                    if (titleAElement == null) {
                        endParse = true;
                        break;
                    }
                    title = titleAElement.text();
                    url = resolveRelativeSNURL(titleAElement.attr("href"));
                    urlDomain = getDomainName(url);

                    Element voteAElement = rowElement.select("tr > td:eq(1) a").first();
                    if (voteAElement != null) {
                        voteURL = resolveRelativeSNURL(voteAElement.attr("href"));
                        // TODO 如果链接包含当前用户名，voteURL应该为空，不能vote
                    }
                    break;
                case 1:
                    // 副标题
                    subText = rowElement.select("tr > td:eq(1)").first().html();
                    points = getIntValueFollowedBySuffix(rowElement.select("tr > td:eq(1) > span")
                            .text(), " p");

                    String author = rowElement.select("tr > td:eq(1) > a[href*=user]").text();
                    user = new SNUser();
                    user.setName(author);
                    user.setId(author);
                    Element e2 = rowElement.select("tr > td:eq(1) > a[href*=item]").first();
                    if (e2 != null) {
                        commentsCount = getIntValueFollowedBySuffix(e2.text(), " c");
                        if (commentsCount == BaseHTMLParser.UNDEFINED
                                && e2.text().contains("discuss"))
                            commentsCount = 0;
                        postID = getStringValuePrefixedByPrefix(e2.attr("href"), "id=");
                        discussURL = e2.attr("href");
                    } else {
                        commentsCount = BaseHTMLParser.UNDEFINED;
                    }
                    snNews.add(new SNNew(url, title, urlDomain, voteURL, points, commentsCount,
                            subText, discussURL, user, postID));
                    break;
                default:
                    break;
            }
            if (endParse) {
                break;
            }
        }
        feed.setSnNews(snNews);
        Log.i(LOG_TAG, "Take Time:" + (System.currentTimeMillis() - start));
        return feed;
    }

}
