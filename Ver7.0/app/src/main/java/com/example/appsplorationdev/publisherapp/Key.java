package com.example.appsplorationdev.publisherapp;

import android.app.Activity;

/**
 * Created by appsplorationdev on 10/24/14.
 */
public class Key extends Activity {

    public static final String KEY_RESULTS = "results";
    public static final String KEY_ARTICLE_ID = "article_id";
    public static final String KEY_ARTICLE_URL = "article_url";
    public static final String KEY_ARTICLE_TITLE = "article_title";
    public static final String KEY_ARTICLE_PUBDATE = "article_pubdate";
    public static final String KEY_ARTICLE_HAS_THUMB = "article_has_thumb";
    public static final String KEY_ARTICLE_THUMB_URL = "article_thumb_url";
    public static final String KEY_SMALL_ARTICLE_THUMB_URL = "article_small_thumb_url";
    public static final String KEY_CATEGORY_NAME = "category_name";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";

    public static final Integer CONSTANT_TO_LOAD_MORE = 15;

    public static final String PACKAGE_NAME = "com.example.appsplorationdev.publisherapp";

    // URL to get JSON
    public static final String MAIN_URL = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=17&page_no=";

    public static final String CATEGORY_URL = "http://hot.appsploration.com/hot/api.php?api=categories&site_id=8";

    public static final String SEARCH_URL = "http://hot.appsploration.com/hot/api.php?api=search&q=" + "&site_id=8";
}
