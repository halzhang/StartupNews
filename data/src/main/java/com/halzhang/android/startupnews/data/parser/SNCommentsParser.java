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

/**
 * StartupNews
 * <p>
 * 评论
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 18, 2013
 */
public class SNCommentsParser extends BaseHTMLParser<SNComments> {
    
    
    public SNCommentsParser(){}
    
    @Override
    public SNComments parseDocument(Document doc) throws Exception {
        SNComments comments = new SNComments();
        if (doc == null) {
            return comments;
        }
        Elements tableRows = doc.body().select("table tr table tr");
        if (tableRows != null && tableRows.size() > 0) {
            tableRows.remove(0);
            // 获取下一页链接
            Elements moreURLElements = tableRows.select("a:matches(More)");
            String moreURL = null;
            if (moreURLElements.size() > 0) {
                moreURL = resolveRelativeSNURL(moreURLElements.attr("href"));
            }
            comments.setMoreURL(moreURL);
            String linkURL = null;
            String parentURL = null;
            String discussURL = null;
            String text = null;
            String created = null;
            SNUser user = null;
            String artistTitle = null;// 文章标题
            String voteURL = null;
            for (int row = 0; row < tableRows.size(); row++) {
                int rowInPost = row % 2;
                Element rowElement = tableRows.get(row);
                if (rowInPost == 0) {
                    Element textElement = rowElement.select("tr > td:eq(1) > span").first();
                    if (textElement == null) {
                        break;
                    }
                    text = textElement.text();
                    user = new SNUser();
                    
                    Element spanElement = rowElement.select("tr > td:eq(1) > div > span").first();
                    created = getCreateAt(spanElement.text());
                    Elements aElements = spanElement.select("span > a");
                    if (aElements != null && aElements.size() >= 4) {
                        int size = aElements.size();
                        Element anthorURLElement = aElements.first();
                        user.setId(anthorURLElement.text());
                        Element linkURLElement = aElements.get(1);
                        linkURL = resolveRelativeSNURL(linkURLElement.attr("href"));
                        Element parentURLElement = aElements.get(2);
                        parentURL = resolveRelativeSNURL(parentURLElement.attr("href"));
                        Element artistAElement = aElements.last();
                        discussURL = resolveRelativeSNURL(artistAElement.attr("href"));
                        artistTitle = artistAElement.text();
                        if (size == 6) {
                            // TODO edit delete
                        }
                    }

                    Element voteAElement = rowElement.select("tr > td:eq(0) a").first();
                    if (voteAElement != null) {
                        // 登录用户的评论没有url
                        voteURL = resolveRelativeSNURL(voteAElement.attr("href"));
                    }
                    comments.addComment(new SNComment(linkURL, parentURL, discussURL, text, created,
                            user, artistTitle, voteURL,null));
                }
            }
        }
        return comments;
    }

}
