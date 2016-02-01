/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.halzhang.android.startupnews.data.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A persistent cookie store which implements the Apache HttpClient
 * {@link CookieStore} interface. Cookies are stored and will persist on the
 * user's device between application sessions since they are serialized and
 * stored in {@link SharedPreferences}.
 * <p/>
 * Instances of this class are designed to be used with
 * {@link com.squareup.okhttp.OkHttpClient#setCookieHandler(CookieHandler)}, but can also be used with a
 * regular old apache HttpClient/HttpContext if you prefer.
 */
public class PersistentCookieStore implements CookieStore {
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    /**
     * -host:
     * --cookieToken:value
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, HttpCookie>> cookies;
    private final SharedPreferences cookiePrefs;

    /**
     * Construct a persistent cookie store.
     */
    public PersistentCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        cookies = new ConcurrentHashMap<>();
        // Load any previously stored cookies into the store
        Map<String, ?> cookiePrefsAll = cookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : cookiePrefsAll.entrySet()) {
            if (entry.getValue() != null && !entry.getKey().startsWith(COOKIE_NAME_PREFIX)) {
                String host = entry.getKey();
                if (!cookies.containsKey(host)) {
                    cookies.put(host, new ConcurrentHashMap<String, HttpCookie>());
                }
                String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                for (String cookieName : cookieNames) {
                    String encodedCookie = (String) cookiePrefsAll.get(COOKIE_NAME_PREFIX + cookieName);
                    if (!TextUtils.isEmpty(encodedCookie)) {
                        cookies.get(host).put(cookieName, decodeCookie(encodedCookie));
                    }
                }
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);
        if (!cookie.hasExpired()) {
            if (!cookies.containsKey(uri.getHost())) {
                cookies.put(uri.getHost(), new ConcurrentHashMap<String, HttpCookie>());
            }
            cookies.get(uri.getHost()).put(name, cookie);
        } else {
            if (cookies.containsKey(uri.getHost())) {
                cookies.get(uri.getHost()).remove(name);
            }
        }
        cookiePrefs.edit()
                .putString(uri.getHost(), TextUtils.join(",", cookies.keySet()))
                .putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableCookie(cookie)))
                .apply();

    }

    @Override
    public List<HttpCookie> get(URI uri) {
        List<HttpCookie> httpCookies = new ArrayList<>(0);
        if (cookies.containsKey(uri.getHost())) {
            httpCookies.addAll(cookies.get(uri.getHost()).values());
        }
        return httpCookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
        List<HttpCookie> httpCookies = new ArrayList<>(0);
        for (String host : cookies.keySet()) {
            httpCookies.addAll(cookies.get(host).values());
        }
        return httpCookies;
    }

    @Override
    public List<URI> getURIs() {
        List<URI> uris = new ArrayList<>(cookies.size());
        for (String key : cookies.keySet()) {
            try {
                uris.add(new URI(key));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return uris;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);
        if (cookies.containsKey(uri.getHost()) && cookies.get(uri.getHost()).containsKey(name)) {
            cookies.get(uri.getHost()).remove(name);
            SharedPreferences.Editor editor = cookiePrefs.edit();
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                editor.remove(COOKIE_NAME_PREFIX + name);
            }
            editor.putString(uri.getHost(), TextUtils.join(",", cookies.keySet()));
            editor.apply();
            return true;

        }
        return false;
    }

    @Override
    public boolean removeAll() {
        cookies.clear();
        return cookiePrefs.edit().clear().commit();
    }

    protected String getCookieToken(URI uri, HttpCookie cookie) {
        return cookie.getName() + cookie.getDomain();
    }


    //
    // Cookie serialization/deserialization
    //

    protected String encodeCookie(SerializableCookie cookie) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (Exception e) {
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    protected HttpCookie decodeCookie(String cookieStr) {
        byte[] bytes = hexStringToByteArray(cookieStr);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HttpCookie cookie = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            cookie = ((SerializableCookie) ois.readObject()).getCookie();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cookie;
    }

    // Using some super basic byte array <-> hex conversions so we don't have
    // to rely on any large Base64 libraries. Can be overridden if you like!
    protected String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (byte element : b) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    protected byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}