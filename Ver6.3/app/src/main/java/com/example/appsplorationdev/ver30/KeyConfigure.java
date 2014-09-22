package com.example.appsplorationdev.ver30;

import android.app.Activity;

/**
 * Created by appsplorationdev on 8/22/14.
 */
public abstract class KeyConfigure extends Activity {

    // JSON Node names
    public static final String KEY_RESULTS = "results";
    public static final String KEY_ARTICLE_ID = "article_id";
    public static final String KEY_ARTICLE_URL = "article_url";
    public static final String KEY_ARTICLE_TITLE = "article_title";
    public static final String KEY_ARTICLE_PUBDATE = "article_pubdate";
    public static final String KEY_ARTICLE_HAS_THUMB = "article_has_thumb";
    public static final String KEY_ARTICLE_THUMB_URL = "article_thumb_url";
    public static final String KEY_CATEGORY_NAME = "category_name";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";

    public static final String ARG_TITLE_NUMBER = "title_number";

    public static final Integer CONSTANT_TO_LOAD_MORE = 15;

    // URL to get contacts JSON
    public static final String URL = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=17&page_no=";

    public static String CAT = "http://hot.appsploration.com/hot/api.php?api=categories&site_id=8";
}
