package cz.michaelbrabec.fossbakalari;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;



public class UkolyActivity extends MainActivity {

    private class GetUkolyTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                return getWebContent(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            updateUkolyList(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_ukoly, contentFrameLayout);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(0).setChecked(true);

        setTitle(R.string.nav_item_ukoly); //workaround so we can have the name hardcoded to Fossaláři, so our app doesn't get renamed to Úkoly

        new GetUkolyTask().execute(bakalariUrl + "/login.aspx?hx="+token+"&pm=ukoly");
    }

    public void updateUkolyList(String ukoly)

    {
        //TextView temporary = findViewById(R.id.textView12);
        //temporary.setText(ukoly);
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = new ByteArrayInputStream(ukoly.getBytes("UTF-8"));
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            int eventType = parser.getEventType();


            String tagName = "", tagContent = "", predmet = "", popis = "";
            int event = parser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                tagName = parser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        tagContent = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        switch(tagName) {
                            case "predmet": predmet = tagContent;
                                break;
                            case "popis": popis = tagContent;
                                break;
                            case "status": renderUkol(predmet, popis, tagContent);
                                break;
                        }
                        break;
                }

                event = parser.next();
            }

            /*while (eventType != XmlPullParser.END_DOCUMENT) {
                String eltName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String predmet = parser.nextText();
                        String popis = parser.nextText();
                        String status = parser.nextText();

                        Log.d("updateUkolyList", predmet + ";" + popis + ";" + status);

                        renderUkol(predmet, popis, status);
                        break;
                }

                eventType = parser.next();
            }*/

        } catch (XmlPullParserException e) {

        } catch (IOException e) {
        }
    }

    public void renderUkol(String predmet, String popis, String status){
        Log.d("renderUkol", predmet + ";" + popis + ";" + status);

        popis = popis.replace("<br />", "\n");

        Resources r = this.getResources();

        //very top linearlayout (not including this causes it to fall apart for some reason)
        LinearLayout parent = findViewById(R.id.topParentUkoly);

        //linearlayout with checkbox
        /* <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="horizontal">   */

        LinearLayout layoutWithCheckbox = new LinearLayout(this);

        layoutWithCheckbox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        layoutWithCheckbox.setOrientation(LinearLayout.HORIZONTAL);

        parent.addView(layoutWithCheckbox);

        //the checkbox itself
        /*<CheckBox
                    android:id="@+id/checkBox3"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_vertical"
                    android:layout_marginHorizontal="10dp"
                    android:layout_weight="0"
                    android:orientation="vertical" />   */
        CheckBox checkBox = new CheckBox(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;

        if(status.equals("probehlo")){
            checkBox.setChecked(true);
        }

        int checkboxMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                r.getDisplayMetrics()
        );
        params.setMargins(checkboxMargin, 0, checkboxMargin, 0);

        layoutWithCheckbox.addView(checkBox, params);

        //another nested layout for textviews

        /*

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:orientation="vertical"> */

        LinearLayout layoutWithTextViews = new LinearLayout(this);

        layoutWithTextViews.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutWithTextViews.setOrientation(LinearLayout.VERTICAL);

        layoutWithCheckbox.addView(layoutWithTextViews);

        //textviews
        /*

                    <TextView
                        android:id="@+id/textView11"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Matematika"
                        android:textColor="#000"
                        android:textSize="16dp" />

                    <TextView
                        android:id="@+id/textView12"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Vypočítejte polohu kružnice na reálné ose v souřadnicovém systému za použití znalostí gaussovy..." /> */

        TextView predmetView = new TextView(this);
        predmetView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        predmetView.setTextColor(Color.BLACK);
        predmetView.setText(predmet);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutWithTextViews.addView(predmetView, params);

        TextView popisView = new TextView(this);
        popisView.setText(popis);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutWithTextViews.addView(popisView, params);

        /*      <View
                    android:id="@+id/divider3"
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:layout_margin="10dp"
                    android:background="?android:attr/listDivider" />
                </LinearLayout>         */

        View divider = new View(this);

        int dividerHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1,
                r.getDisplayMetrics()
        );

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dividerHeight);
        params.setMargins(checkboxMargin,checkboxMargin,checkboxMargin,checkboxMargin);
        divider.setBackgroundResource(android.R.drawable.divider_horizontal_textfield);

        parent.addView(divider, params);
    }
}
