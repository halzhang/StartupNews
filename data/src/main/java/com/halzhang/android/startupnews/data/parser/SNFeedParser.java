/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.parser;

import android.util.Log;

import com.halzhang.android.startupnews.data.entity.SNFeed;
import com.halzhang.android.startupnews.data.entity.SNNew;
import com.halzhang.android.startupnews.data.entity.SNUser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * StartupNews
 * <p>
 * news解析
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
        // Elements loginout = doc.select("a:matches(Login/Register|logout)");
        // if (loginout.size() > 0) {
        // String loginoutUrl = resolveRelativeSNURL(loginout.attr("href"));
        // Log.i(LOG_TAG, "Login or out url: " + loginoutUrl);
        // }

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
        String createat = null;
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
                    } else {
                        voteURL = null;
                    }
                    break;
                case 1:
                    // 副标题
                    Element tdElement = rowElement.select("tr > td:eq(1)").first();
                    subText = tdElement.text();
                    createat = getCreateAt(subText);
                    points = getIntValueFollowedBySuffix(tdElement.select("td > span").text(), " p");

                    String author = tdElement.select("td > a[href*=user]").text();
                    user = new SNUser();
                    user.setName(author);
                    user.setId(author);
                    Element e2 = tdElement.select("td > a[href*=item]").first();
                    if (e2 != null) {
                        commentsCount = getIntValueFollowedBySuffix(e2.text(), " c");
                        if (commentsCount == BaseHTMLParser.UNDEFINED
                                && e2.text().contains("discuss"))
                            commentsCount = 0;
                        postID = getStringValuePrefixedByPrefix(e2.attr("href"), "id=");
                        discussURL = resolveRelativeSNURL(e2.attr("href"));
                    } else {
                        commentsCount = BaseHTMLParser.UNDEFINED;
                    }
                    snNews.add(new SNNew(url, title, urlDomain, voteURL, points, commentsCount,
                            subText, discussURL, user, postID, createat));
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
