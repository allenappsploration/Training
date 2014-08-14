package com.example.appsplorationdev.ver30;

import android.app.Activity;

/**
 * Created by appsplorationdev on 8/12/14.
 */
public abstract class KeyConfiguration extends Activity {
    // JSON Node names
    public static final String KEY_CHANNEL = "channel";
    public static final String KEY_ITEM = "item";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LINK = "link";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_PUBDATE = "pubDate";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_GUID = "guid";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_COMMENTRSS = "commentRss";
    public static final String KEY_THUMBNAIL = "thumbnail";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TWITTER = "twitter";

    // URL to get contacts JSON
    public static String URL = "http://i.appsploration.com/so/json.php";
}
