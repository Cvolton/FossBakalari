package cz.michaelbrabec.fossbakalari;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ZnamkyActivity extends MainActivity implements SwipeRefreshLayout.OnRefreshListener{

    List<ZnamkyItem> znamkyItemList = new ArrayList<>();
    ZnamkyBasicAdapter adapter = new ZnamkyBasicAdapter(znamkyItemList);
    SwipeRefreshLayout swipeRefreshLayout;


    private class GetZnamkyTask extends AsyncTask<String, Integer, String> {
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
            updateZnamkyList(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_znamky, contentFrameLayout);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(2).setChecked(true);

        RecyclerView recyclerView = findViewById(R.id.znamky_recycler);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));


        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        if(!token.isEmpty()){
            new GetZnamkyTask().execute(bakalariUrl + "/login.aspx?hx="+token+"&pm=znamky");
        }

    }

    @Override
    public void onRefresh() {

        if(!token.isEmpty()){
            znamkyItemList.clear();
            new GetZnamkyTask().execute(bakalariUrl + "/login.aspx?hx="+token+"&pm=znamky");
        }
    }

    private void updateZnamkyList(String znamky) {

        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = new ByteArrayInputStream(znamky.getBytes("UTF-8"));
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            String tagName = "", tagContent = "";
            int event = parser.getEventType();

            ZnamkyItem znamka = new ZnamkyItem();

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
                            case "pred": znamka.predmet = tagContent;
                                break;
                            case "zn": znamka.znamka = tagContent;
                                break;
                            case "udeleno": znamka.datum =
                                    tagContent.substring(4, 6) + ". "
                                            + tagContent.substring(2,4) + ". 20"
                                            + tagContent.substring(0,2) + " "
                                            + tagContent.substring(6, 8) + ":"
                                            + tagContent.substring(8,10);
                                break;
                            case "vaha": znamka.vaha = tagContent;
                                break;
                            case "caption": znamka.popis = tagContent;
                                znamkyItemList.add(znamka);
                                znamka = new ZnamkyItem();
                                break;
                        }
                        break;
                }

                event = parser.next();
            }

            Collections.sort(znamkyItemList, new Comparator<ZnamkyItem>() {
                DateFormat f = new SimpleDateFormat("dd. MM. yyyy HH:mm", Locale.ENGLISH);
                @Override
                public int compare(ZnamkyItem o1, ZnamkyItem o2) {
                    try {
                        return f.parse(o2.datum).compareTo(f.parse(o1.datum));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });

            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);


        } catch (XmlPullParserException e) {

        } catch (IOException e) {
        }

    }


}
