/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.parser;


import com.halzhang.android.startupnews.data.entity.SNComment;
import com.halzhang.android.startupnews.data.entity.SNComments;
import com.halzhang.android.startupnews.data.entity.SNUser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 19, 2013
 */
public class SNCommentsParserV1 extends BaseHTMLParser<SNComments> {

    @Override
    public SNComments parseDocument(Document doc) throws Exception {
        SNComments comments = new SNComments();
        if (doc == null) {
            return comments;
        }
        Element body = doc.body();
        Elements commentSpans = body.select("span.comment");
        Elements comHeadSpans = body.select("span.comhead");
        if (!commentSpans.isEmpty()) {
            Iterator<Element> spanCommentIt = commentSpans.iterator();
            Iterator<Element> spanComHeadIt = comHeadSpans.iterator();
            SNComment comment = null;
            SNUser user = null;
            while (spanComHeadIt.hasNext() && spanCommentIt.hasNext()) {
                String commentText = spanCommentIt.next().text();
                Element span = spanComHeadIt.next();
                Elements as = span.getElementsByTag("a");
                user = new SNUser();
                user.setId(as.get(0).text());
                String link = as.get(1).attr("href");
                String parent = as.get(2).attr("href");
                String discuss = as.get(3).attr("href");
                String title = as.get(3).text();
                comment = new SNComment();
                comment.setUser(user);
                comment.setLinkURL(resolveRelativeSNURL(link));
                comment.setParentURL(resolveRelativeSNURL(parent));
                comment.setDiscussURL(resolveRelativeSNURL(discuss));
                comment.setText(commentText);
                comment.setArtistTitle(title);
                comments.addComment(comment);
            }
        }
        Elements moreURLElements = body.select("a:matches(More)");
        String moreURL = null;
        if (moreURLElements.size() > 0) {
            moreURL = resolveRelativeSNURL(moreURLElements.attr("href"));
        }
        comments.setMoreURL(moreURL);
        return comments;
    }

}
