package com.example.appsplorationdev.ver20;

import android.app.Activity;

/**
 * Created by appsplorationdev on 8/12/14.
 */
public abstract class KeyConfiguration extends Activity {
    // JSON Node names
    public static final String TAG_CHANNEL = "channel";
    public static final String TAG_ITEM = "item";
    public static final String TAG_TITLE = "title";
    public static final String TAG_LINK = "link";
    public static final String TAG_COMMENTS = "comments";
    public static final String TAG_PUBDATE = "pubDate";
    public static final String TAG_CREATOR = "creator";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_GUID = "guid";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_COMMENTRSS = "commentRss";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_CONTENT = "content";
    public static final String TAG_TWITTER = "twitter";

    // URL to get contacts JSON
    public static String url = "http://i.appsploration.com/so/json.php";
}
