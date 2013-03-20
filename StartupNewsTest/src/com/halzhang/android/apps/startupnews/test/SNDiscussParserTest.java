/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.test;

import com.halzhang.android.apps.startupnews.entity.SNDiscuss;
import com.halzhang.android.apps.startupnews.parser.SNDiscussParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.test.AndroidTestCase;

/**
 * StartupNewsTest
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 19, 2013
 */
public class SNDiscussParserTest extends AndroidTestCase {

    private SNDiscussParser mParser;

    private Context mContext;

    public SNDiscussParserTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
        assertNotNull(mContext);
//        InputStream in = mContext.getResources().openRawResource(R.raw.discuss);
        Document doc = Jsoup.connect("http://news.dbanotes.net/item?id=1827").get();
        assertNotNull(doc);
        mParser = new SNDiscussParser();
        SNDiscuss discuss = mParser.parseDocument(doc);
        assertNotNull(discuss.getFnid());
        assertNotNull(discuss.getComments());
        assertTrue(discuss.getComments().size() > 0);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
