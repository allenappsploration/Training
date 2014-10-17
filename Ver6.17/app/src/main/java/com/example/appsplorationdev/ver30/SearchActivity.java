package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by appsplorationdev on 9/25/14.
 */
public class SearchActivity extends KeyConfigure {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONArray JSONArray = null;
    SearchAdapter searchAdapter;

    ArrayList<HashMap<String, String>> searchList;

    public String searchKey;
    private Boolean hasResult = true;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {
            setContentView(R.layout.activity_search);

            ActionBar actionBar = getActionBar();

            // Enabling back navigation on Action Bar icon
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            Intent myIntent = getIntent();

            searchKey = myIntent.getStringExtra("query");

            searchList = new ArrayList<HashMap<String, String>>();

            new searchFunction().execute();
        }else {
            showAlertDialog(SearchActivity.this, "Network Error", "No Internet Connection", false);
        }

    }

    private class searchFunction extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Searching...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            JSONParser jParser = new JSONParser();

            hasResult = true;

            String newURL = "http://hot.appsploration.com/hot/api.php?api=search&q="+ searchKey +"&site_id=8";

            JSONObject jsonObj = jParser.getJSONFromUrl(newURL);

            Log.i("Response: ", "> " + String.valueOf(jsonObj));

            if (jsonObj != null) {
                try {

                    JSONArray = jsonObj.getJSONArray(KEY_RESULTS);

                    if ((JSONArray != null) && (JSONArray.length() > 0)) {
                        // looping through All Products
                        for (int i = 0; i < JSONArray.length(); i++) {

                            // Storing each json item in variable
                            JSONObject c = JSONArray.getJSONObject(i);

                            String substring = c.getString(KEY_ARTICLE_TITLE);
                            String hasThumb = c.getString(KEY_ARTICLE_HAS_THUMB);

                            substring = replaceQuote(substring);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value

                            map.put(KEY_ARTICLE_URL, c.getString(KEY_ARTICLE_URL));
                            map.put(KEY_ARTICLE_TITLE, substring);
                            map.put(KEY_ARTICLE_PUBDATE, convertDate(c.getString(KEY_ARTICLE_PUBDATE)));
                            map.put(KEY_SMALL_ARTICLE_THUMB_URL, c.getString(KEY_SMALL_ARTICLE_THUMB_URL));
                            map.put(KEY_ARTICLE_HAS_THUMB, c.getString(KEY_ARTICLE_HAS_THUMB));

                            // adding HashList to ArrayList
                            searchList.add(map);
                        }
                    } else {
                        return hasResult = false;
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return true;
        }

        protected void onPostExecute(final Boolean result) {
            // dismiss the dialog after getting the related idioms
            pDialog.dismiss();

            final ListView lv = (ListView)findViewById(R.id.lv_search);

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if (result) {
                        searchAdapter = new SearchAdapter(SearchActivity.this, searchList);
                        searchAdapter.notifyDataSetChanged();

                        // updating listview
                        lv.setAdapter(searchAdapter);
                    } else {

                        lv.setBackgroundResource(R.drawable.search_not_found);
                        lv.getLayoutParams().height = 1200;
                    }
                }
            });

            // ListView on item click listener
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // getting values from selected ListItem
                    String hidItm, title, image;
                    title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                    image = ((TextView) view.findViewById(R.id.hidthumburl)).getText().toString();
                    hidItm = ((TextView) view.findViewById(R.id.hiditem)).getText().toString();
                    // Starting single contact activity
                    Intent in = new Intent(getApplicationContext(),
                            SingleActivity.class);
                    in.putExtra(KEY_ARTICLE_TITLE, title);
                    in.putExtra(KEY_SMALL_ARTICLE_THUMB_URL, image);
                    in.putExtra(KEY_ARTICLE_URL, hidItm);
                    in.putExtra("fromMainActivity", true);
                    startActivity(in);

                    overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
                }
            });
        }
    }

    private String replaceQuote(String params) {
        // HTML Character Entity Names

        // General
        params = params.replace("&copy;" ,"©");
        params = params.replace("&reg;" ,"®");
        params = params.replace("&trade;" ,"™");
        params = params.replace("&amp;", "&");
        params = params.replace("&para;" ,"¶");
        params = params.replace("&ndash;" ,"–");
        params = params.replace("&mdash;" ,"—");
        params = params.replace("&not;" ,"¬");
        params = params.replace("&lsaquo;" ,"‹");
        params = params.replace("&rsaquo;" ,"›");
        params = params.replace("&laquo;" ,"«");
        params = params.replace("&raquo;" ,"»");
        params = params.replace("&larr;" ,"←");
        params = params.replace("&uarr;" ,"↑");
        params = params.replace("&rarr;" ,"→");
        params = params.replace("&darr;" ,"↓");
        params = params.replace("&spades;" ,"♠");
        params = params.replace("&clubs;" ,"♣");
        params = params.replace("&hearts;" ,"♥");
        params = params.replace("&diams;" ,"♦");
        params = params.replace("&nbsp;", " ");

        // Accents
        params = params.replace("&acute;" ,"´");
        params = params.replace("&macr;" ,"¯");
        params = params.replace("&uml;" ,"¨");
        params = params.replace("&cedil;" ,"¸");
        params = params.replace("&middot;" ,"·");
        params = params.replace("&ordm;" ,"º");
        params = params.replace("&ordf;" ,"ª");

        // Letter A Accents
        params = params.replace("&Agrave;" ,"À");
        params = params.replace("&Aacute;" ,"Á");
        params = params.replace("&Acirc;" ,"Â");
        params = params.replace("&Atilde;" ,"Ã");
        params = params.replace("&Auml;" ,"Ä");
        params = params.replace("&Aring;" ,"Å");
        params = params.replace("&agrave;" ,"à");
        params = params.replace("&aacute;" ,"á");
        params = params.replace("&acirc;" ,"â");
        params = params.replace("&atilde;" ,"ã");
        params = params.replace("&auml;" ,"ä");
        params = params.replace("&aring;" ,"å");

        // Letter E Accents
        params = params.replace("&Egrave;" ,"È");
        params = params.replace("&Eacute;" ,"É");
        params = params.replace("&Ecirc;" ,"Ê");
        params = params.replace("&Euml;" ,"Ë");
        params = params.replace("&egrave;" ,"è");
        params = params.replace("&eacute;" ,"é");
        params = params.replace("&ecirc;" ,"ê");
        params = params.replace("&euml;" ,"ë");

        // Letter I Accents
        params = params.replace("&Igrave;" ,"Ì");
        params = params.replace("&Iacute;" ,"Í");
        params = params.replace("&Icirc;" ,"Î");
        params = params.replace("&Iuml;" ,"Ï");
        params = params.replace("&igrave;" ,"ì");
        params = params.replace("&iacute;" ,"í");
        params = params.replace("&icirc;" ,"î");
        params = params.replace("&iuml;" ,"ï");

        // Letter O Accents
        params = params.replace("&Ograve;" ,"Ò");
        params = params.replace("&Oacute;" ,"Ó");
        params = params.replace("&Ocirc;" ,"Ô");
        params = params.replace("&Otilde;" ,"Õ");
        params = params.replace("&Ouml;" ,"Ö");
        params = params.replace("&Oslash;" ,"Ø");
        params = params.replace("&ograve;" ,"ò");
        params = params.replace("&oacute;" ,"ó");
        params = params.replace("&ocirc;" ,"ô");
        params = params.replace("&otilde;" ,"õ");
        params = params.replace("&ouml;" ,"ö");
        params = params.replace("&oslash;" ,"ø");

        // Letter U Accents
        params = params.replace("&Ugrave;" ,"Ù");
        params = params.replace("&Uacute;" ,"Ú");
        params = params.replace("&Ucirc;" ,"Û");
        params = params.replace("&Uuml;" ,"Ü");
        params = params.replace("&ugrave;" ,"ù");
        params = params.replace("&uacute;" ,"ú");
        params = params.replace("&ucirc;" ,"û");
        params = params.replace("&uuml;" ,"ü");

        // Additional Accents
        params = params.replace("&AElig;" ,"Æ");
        params = params.replace("&aelig;" ,"æ");
        params = params.replace("&Ccedil;" ,"Ç");
        params = params.replace("&ccedil;" ,"ç");
        params = params.replace("&Ntilde;" ,"Ñ");
        params = params.replace("&ntilde;" ,"ñ");
        params = params.replace("&Yacute;" ,"Ý");
        params = params.replace("&yacute;" ,"ý");
        params = params.replace("&yuml;" ,"ÿ");
        params = params.replace("&ETH;" ,"Ð");
        params = params.replace("&eth;" ,"ð");
        params = params.replace("&THORN;" ,"Þ");
        params = params.replace("&thorn;" ,"þ");
        params = params.replace("&szlig;" ,"ß");

        // Quotes and Punctuation
        params = params.replace("&iexcl;", "¡");
        params = params.replace("&iquest;" ,"¿");
        params = params.replace("&hellip;" ,"…");
        params = params.replace("&dagger;" ,"†");
        params = params.replace("&Dagger;" ,"‡");
        params = params.replace("&permil;" ,"‰");
        params = params.replace("&sup1;" ,"¹");
        params = params.replace("&sup2;" ,"²");
        params = params.replace("&sup3;" ,"³");
        params = params.replace("&frac14;" ,"¼");
        params = params.replace("&frac12;" ,"½");
        params = params.replace("&frac34;" ,"¾");
        params = params.replace("&oline;" ,"‾");
        params = params.replace("&lsquo;" ,"‘");
        params = params.replace("&rsquo;" ,"’");
        params = params.replace("&ldquo;" ,"“");
        params = params.replace("&rdquo;" ,"”");
        params = params.replace("&sbquo;" ,"‚");
        params = params.replace("&bdquo;" ,"„");
        params = params.replace("&quot;", "\"");

        // Slashes and Brackets
        params = params.replace("&frasl;", "/");
        params = params.replace("&brvbar;" ,"¦");

        // Money and Math Symbols
        params = params.replace("&euro;" ,"€");
        params = params.replace("&cent;" ,"¢");
        params = params.replace("&pound;" ,"£");
        params = params.replace("&curren;" ,"¤");
        params = params.replace("&yen;" ,"¥");
        params = params.replace("&times;" ,"×");
        params = params.replace("&divide;" ,"÷");
        params = params.replace("&plusmn;" ,"±");
        params = params.replace("&micro;" ,"µ");
        params = params.replace("&lt;", "<");
        params = params.replace("&gt;", ">");
        params = params.replace("&deg;" ,"°");
        params = params.replace("&sect;" ,"§");
        params = params.replace("&le;" ,"≤");
        params = params.replace("&ge;" ,"≥");
        params = params.replace("&sum;" ,"∑");
        params = params.replace("&minus;" ,"−");
        params = params.replace("&lowast;" ,"∗");
        params = params.replace("&radic;" ,"√");
        params = params.replace("&prop;" ,"∝");
        params = params.replace("&infin;" ,"∞");
        params = params.replace("&fnof;" ,"ƒ");
        params = params.replace("&circ;" ,"ˆ");
        params = params.replace("&tilde;" ,"˜");
        params = params.replace("&int;" ,"∫");
        params = params.replace("&there4;" ,"∴");
        params = params.replace("&sim;" ,"∼");
        params = params.replace("&cong;" ,"≅");
        params = params.replace("&asymp;" ,"≈");
        params = params.replace("&ne;" ,"≠");
        params = params.replace("&equiv;" ,"≡");
        params = params.replace("&lang;" ,"⟨");
        params = params.replace("&rang;" ,"⟩");
        params = params.replace("&lceil;" ,"⌈");
        params = params.replace("&rceil;" ,"⌉");
        params = params.replace("&lfloor;" ,"⌊");
        params = params.replace("&rfloor;" ,"⌋");
        params = params.replace("&oplus;" ,"⊕");
        params = params.replace("&otimes;" ,"⊗");
        params = params.replace("&part;" ,"∂");
        params = params.replace("&exist;" ,"∃");
        params = params.replace("&empty;" ,"∅");
        params = params.replace("&nabla;" ,"∇");
        params = params.replace("&isin;" ,"∈");
        params = params.replace("&notin;" ,"∉");
        params = params.replace("&ni;" ,"∋");
        params = params.replace("&prod;" ,"∏");
        params = params.replace("&ang;" ,"∠");
        params = params.replace("&and;" ,"∧");
        params = params.replace("&or;" ,"∨");
        params = params.replace("&cap;" ,"∩");
        params = params.replace("&cup;" ,"∪");
        params = params.replace("&sub;" ,"⊂");
        params = params.replace("&sup;" ,"⊃");
        params = params.replace("&nsub;" ,"⊄");
        params = params.replace("&sube;" ,"⊆");
        params = params.replace("&supe;" ,"⊇");
        params = params.replace("&perp;" ,"⊥");
        params = params.replace("&sdot;" ,"⋅");
        params = params.replace("&OElig;" ,"Œ");
        params = params.replace("&oelig;" ,"œ");
        params = params.replace("&Scaron;" ,"Š");
        params = params.replace("&scaron;" ,"š");
        params = params.replace("&Yuml;" ,"Ÿ");
        params = params.replace("&shy;" ,"    ");
        params = params.replace("&prime;" ,"′");
        params = params.replace("&Prime;" ,"″");
        params = params.replace("&crarr;" ,"↵");
        params = params.replace("&forall;" ,"∀");
        params = params.replace("&apos;", "'");
        params = params.replace("&bull;" ,"•");
        params = params.replace("&harr;" ,"↔");
        params = params.replace("&loz;" ,"◊");
        params = params.replace("&lArr;" ,"⇐");
        params = params.replace("&uArr;" ,"⇑");
        params = params.replace("&rArr;" ,"⇒");
        params = params.replace("&dArr;" ,"⇓");
        params = params.replace("&hArr;" ,"⇔");
        params = params.replace("&weierp;" ,"℘");
        params = params.replace("&image;" ,"ℑ");
        params = params.replace("&real;" ,"ℜ");
        params = params.replace("&alefsym;" ,"ℵ");

        // Greek
        params = params.replace("&Alpha;" ,"Α");
        params = params.replace("&Beta;" ,"Β");
        params = params.replace("&Gamma;" ,"Γ");
        params = params.replace("&Delta;" ,"Δ");
        params = params.replace("&Epsilon;" ,"Ε");
        params = params.replace("&Zeta;" ,"Ζ");
        params = params.replace("&Eta;" ,"Η");
        params = params.replace("&Theta;" ,"Θ");
        params = params.replace("&Iota;" ,"Ι");
        params = params.replace("&Kappa;" ,"Κ");
        params = params.replace("&Lambda;" ,"Λ");
        params = params.replace("&Mu;" ," Μ");
        params = params.replace("&Nu;" ,"Ν");
        params = params.replace("&Xi;" ,"Ξ");
        params = params.replace("&Omicron;" ,"Ο");
        params = params.replace("&Pi;" ,"Π");
        params = params.replace("&Rho;" ,"Ρ");
        params = params.replace("&Sigma;" ,"Σ");
        params = params.replace("&Tau;" ,"Τ");
        params = params.replace("&Upsilon;" ,"Υ");
        params = params.replace("&Phi;" ,"Φ");
        params = params.replace("&Chi;" ,"Χ");
        params = params.replace("&Psi;" ,"Ψ");
        params = params.replace("&Omega;" ,"Ω");
        params = params.replace("&alpha;" ,"α");
        params = params.replace("&beta;" ,"β");
        params = params.replace("&gamma;" ,"γ");
        params = params.replace("&delta;" ,"δ");
        params = params.replace("&epsilon;" ,"ε");
        params = params.replace("&zeta;" ,"ζ");
        params = params.replace("&eta;" ,"η");
        params = params.replace("&theta;" ,"θ");
        params = params.replace("&iota;" ,"ι");
        params = params.replace("&kappa;" ,"κ");
        params = params.replace("&lambda;" ,"λ");
        params = params.replace("&mu;" ,"μ");
        params = params.replace("&nu;" ,"ν");
        params = params.replace("&xi;" ,"ξ");
        params = params.replace("&omicron;" ,"ο");
        params = params.replace("&pi;" ,"π");
        params = params.replace("&rho;" ,"ρ");
        params = params.replace("&sigmaf;" ,"ς");
        params = params.replace("&sigma;" ,"σ");
        params = params.replace("&tau;" ,"τ");
        params = params.replace("&upsilon;" ,"υ");
        params = params.replace("&phi;" ,"φ");
        params = params.replace("&chi;" ,"χ");
        params = params.replace("&psi;" ,"ψ");
        params = params.replace("&omega;" ,"ω");
        params = params.replace("&thetasym;" ,"ϑ");
        params = params.replace("&upsih;" ,"ϒ");
        params = params.replace("&piv;" ,"ϖ");

        return params;
    }

    private String convertDate(String mDate){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date d = dateFormat.parse(mDate);
            SimpleDateFormat serverFormat = new SimpleDateFormat("E d MMM yyyy h:mm a");
            return serverFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.failed);
        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                recreate();
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);  // Method to close the application
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
