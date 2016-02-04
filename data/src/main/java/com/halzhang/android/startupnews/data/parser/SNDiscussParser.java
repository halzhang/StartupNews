/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.parser;


import com.halzhang.android.startupnews.data.entity.SNComment;
import com.halzhang.android.startupnews.data.entity.SNDiscuss;
import com.halzhang.android.startupnews.data.entity.SNNew;
import com.halzhang.android.startupnews.data.entity.SNUser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 19, 2013
 */
public class SNDiscussParser extends BaseHTMLParser<SNDiscuss> {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = SNDiscussParser.class.getSimpleName();

    @Override
    public SNDiscuss parseDocument(Document doc) throws Exception {
        SNDiscuss discuss = new SNDiscuss();
        if (doc == null) {
            return discuss;
        }
        // news
        Elements tableElements = doc.select("table tr table");
        if (tableElements != null && tableElements.size() > 1) {
            String voteURL = null;
            String title = null;
            String url = null;
            String urlDomain = null;
            String subText = null;
            boolean isDiscuss = false;
            SNUser user = null;
            int points = 0;
            int commentsCount = 0;
            String postID = null;
            String discussURL = null;
            String text = null;
            String createat = null;
            String fnid = null;
            Element newsTableElement = tableElements.get(1);
            Elements trElements = newsTableElement.getElementsByTag("tr");
            isDiscuss = trElements.size() > 4;// 讨论帖的有6个tr
            Element titleTrElement = trElements.get(0);
            Element voteElement = titleTrElement.select("tr > td:eq(0) a").first();
            if (voteElement != null) {
                voteURL = resolveRelativeSNURL(voteElement.attr("href"));
            }
            Element titleAElement = titleTrElement.select("tr > td:eq(1) a").first();
            if (titleAElement != null) {
                /*
                 * fixed #5 issue “No Activity found to handle Intent { act=android.intent.action.VIEW dat=item?id=2901 }”
                 * 由于没有对url进行处理，对于讨论帖的标题的a标签的href（item?id=2901）是无法直接打开的
                 */
                url = resolveRelativeSNURL(titleAElement.attr("href"));
                urlDomain = getDomainName(url);
                title = titleAElement.text();
            }

            Element subTextTdeElement = trElements.get(1).select("td.subtext").first();
            subText = subTextTdeElement.html();
            createat = getCreateAt(subText);
            points = getIntValueFollowedBySuffix(subTextTdeElement.select("td > span").text(), " p");

            String author = subTextTdeElement.select("td > a[href*=user]").text();
            user = new SNUser();
            user.setName(author);
            user.setId(author);
            Element e2 = subTextTdeElement.select("td > a[href*=item]").first();
            if (e2 != null) {
                commentsCount = getIntValueFollowedBySuffix(e2.text(), " c");
                if (commentsCount == BaseHTMLParser.UNDEFINED && e2.text().contains("discuss"))
                    commentsCount = 0;
                postID = getStringValuePrefixedByPrefix(e2.attr("href"), "id=");
                discussURL = resolveRelativeSNURL(e2.attr("href"));
            } else {
                commentsCount = BaseHTMLParser.UNDEFINED;
            }
            if (isDiscuss) {
                text = trElements.get(3).text();
                fnid = trElements.get(5).getElementsByTag("input").first().attr("value");
            } else {
                fnid = trElements.get(3).getElementsByTag("input").first().attr("value");
            }
            discuss.setFnid(fnid);
            discuss.setSnNew(new SNNew(url, title, urlDomain, voteURL, points, commentsCount,
                    subText, discussURL, user, postID, isDiscuss, text, createat));

        }
        Elements tableRows = doc.select("table tr table tr table tr");
        if (tableRows != null && tableRows.size() > 0) {
            Element rowElement = null;
            SNComment comment = null;
            for (int row = 0; row < tableRows.size(); row++) {
                comment = new SNComment();
                rowElement = tableRows.get(row);
                Element voteAElement = rowElement.select("tr > td:eq(1) a").first();
                if (voteAElement != null) {
                    comment.setVoteURL(resolveRelativeSNURL(voteAElement.attr("href")));
                }
                Elements aElements = rowElement.select("tr > td:eq(2) a");
                if(aElements == null || aElements.size() < 1){
                    continue;
                }
                SNUser user = new SNUser();
                user.setId(aElements.first().text());
                comment.setUser(user);
                comment.setLinkURL(resolveRelativeSNURL(aElements.last().attr("href")));
                comment.setText(rowElement.select("tr > td:eq(2) > span").first().text());
                comment.setReplayURL(resolveRelativeSNURL(rowElement
                        .select("tr > td:eq(2) a[href^=reply]").first().attr("href")));
                discuss.getComments().add(comment);
            }
        }
        return discuss;
    }

}
