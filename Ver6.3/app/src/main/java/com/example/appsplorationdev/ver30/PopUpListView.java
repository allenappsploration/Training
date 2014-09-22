package com.example.appsplorationdev.ver30;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by appsplorationdev on 8/18/14.
 */
public class PopUpListView extends Activity {
    //ArrayList<Contact> imageArray = new ArrayList<Contact>();
    //ContactImageAdapter adapter;

    ListView list;
    String[] web = {
            "Facebook",
            "Twitter",
            "Google +",
            "Email"
    };

    Integer[] imageId = {
            R.drawable.facebook_logo,
            R.drawable.twitter_logo,
            R.drawable.google_plus_icon,
            R.drawable.email_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
/*
        // add image and text in arraylist
        imageArray.add(new Contact(R.drawable.facebook_logo, "Facebook"));
        imageArray.add(new Contact(R.drawable.twitter_logo, "Twitter"));
        imageArray.add(new Contact(R.drawable.google_plus_icon, "Google+"));
        imageArray.add(new Contact(R.drawable.email_icon, "Email"));
        // add data in contact image adapter
        adapter = new ContactImageAdapter(this, R.layout.row, imageArray);
        ListView dataList = (ListView)findViewById(R.id.lvCustomChooser);
        dataList.setAdapter(adapter);*/

        /*
        CustomAppList adapter = new CustomAppList(PopUpListView.this, web, imageId);
        list = (ListView)findViewById(R.id.lvCustomChooser);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PopUpListView.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}
