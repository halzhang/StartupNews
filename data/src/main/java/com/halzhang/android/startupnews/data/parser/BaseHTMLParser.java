/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.parser;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.w3c.dom.Node;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

/**
 * StartupNews
 * <p>
 * html解析
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 18, 2013
 */
public abstract class BaseHTMLParser<T> {

    public static final Pattern CREATEAT_PATTERN = Pattern.compile("\\d{1,2}\\s\\w+\\sago");

    public static final int UNDEFINED = -1;

    public T parse(String input) throws Exception {
        return parseDocument(Jsoup.parse(input));
    }

    public abstract T parseDocument(Document doc) throws Exception;

    public static String getDomainName(String url) {
        URI uri;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            return url;
        }
    }

    public static <T extends Object> T getSafe(List<T> list, int index) {
        if (list.size() - 1 >= index) {
            return list.get(index);
        } else {
            return null;
        }
    }

    public static String getFirstTextValueInElementChildren(Element element) {
        if (element == null) {
            return "";
        }
        for (org.jsoup.nodes.Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                return ((TextNode) node).text();
            }
        }
        return "";
    }

    public static String getStringValue(String query, Node source, XPath xpath) {
        try {
            return ((Node) xpath.evaluate(query, source, XPathConstants.NODE)).getNodeValue();
        } catch (Exception e) {
            // TODO insert Google Analytics tracking here?
        }
        return "";
    }

    public static Integer getIntValueFollowedBySuffix(String value, String suffix) {
        if (value == null || suffix == null)
            return 0;

        int suffixWordIdx = value.indexOf(suffix);
        if (suffixWordIdx >= 0) {
            String extractedValue = value.substring(0, suffixWordIdx);
            try {
                return Integer.parseInt(extractedValue);
            } catch (NumberFormatException e) {
                return UNDEFINED;
            }
        }
        return UNDEFINED;
    }

    public static String getStringValuePrefixedByPrefix(String value, String prefix) {
        int prefixWordIdx = value.indexOf(prefix);
        if (prefixWordIdx >= 0) {
            return value.substring(prefixWordIdx + prefix.length());
        }
        return null;
    }

    public static String resolveRelativeSNURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String snurl = "http://news.dbanotes.net/";

        if (url.startsWith("http") || url.startsWith("ftp")) {
            return url;
        } else if (url.startsWith("/")) {
            return snurl + url.substring(1);
        } else {
            return snurl + url;
        }
    }

    public String getCreateAt(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        Matcher matcher = CREATEAT_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
