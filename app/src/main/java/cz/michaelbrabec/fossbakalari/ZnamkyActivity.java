package cz.michaelbrabec.fossbakalari;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import cz.michaelbrabec.fossbakalari.Utils.ItemClickSupport;
import cz.michaelbrabec.fossbakalari.Utils.Utils;

public class ZnamkyActivity extends MainActivity implements SwipeRefreshLayout.OnRefreshListener{

    List<ZnamkyItem> znamkyItemList = new ArrayList<>();
    ZnamkyBasicAdapter adapter = new ZnamkyBasicAdapter(znamkyItemList);
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_znamky, contentFrameLayout);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(2).setChecked(true);

        RecyclerView recyclerView = findViewById(R.id.znamky_recycler);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));


        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                //TODO Make something cool here
                Toast.makeText(getApplicationContext(), znamkyItemList.get(position).popis, Toast.LENGTH_LONG).show();

            }
        });

        if(!token.isEmpty()){
            swipeRefreshLayout.setRefreshing(true);
            makeRequest();
        }

    }

    @Override
    public void onRefresh() {

        if(!token.isEmpty()){
            adapter.notifyItemRangeRemoved(0, znamkyItemList.size());
            znamkyItemList.clear();
            makeRequest();
        }
    }


    private void makeRequest() {

        StringRequest stringRequest = new StringRequest(bakalariUrl + "/login.aspx?hx=" + token + "&pm=znamky",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        XmlParseTask xmlParseTask = new XmlParseTask(new Callback() {
                            @Override
                            public void onCallbackFinished(Object result) {

                                znamkyItemList.clear();
                                znamkyItemList.addAll((List<ZnamkyItem>)result);
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        xmlParseTask.execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        NetworkRequests.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private static class XmlParseTask extends AsyncTask<String, Void, List<ZnamkyItem>> {

        Callback callback;
        XmlParseTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected List<ZnamkyItem> doInBackground(String... xml) {
            List<ZnamkyItem> znamky = new ArrayList<>();
            XmlPullParserFactory parserFactory;

            try {
                parserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserFactory.newPullParser();
                InputStream is = new ByteArrayInputStream(xml[0].getBytes("UTF-8"));
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);

                String tagName, tagContent = "";
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
                                case "udeleno": znamka.datum = Utils.parseDate(tagContent);
                                    break;
                                case "vaha": znamka.vaha = tagContent;
                                    break;
                                case "caption": znamka.popis = tagContent.trim();
                                    znamky.add(znamka);
                                    znamka = new ZnamkyItem();
                                    break;
                            }
                            break;
                    }

                    event = parser.next();
                }

                Collections.sort(znamky, new Comparator<ZnamkyItem>() {
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

            } catch (XmlPullParserException e) {

            } catch (IOException e) {

            }

            return znamky;
        }

        @Override
        protected void onPostExecute(List<ZnamkyItem> list) {
            super.onPostExecute(list);
            callback.onCallbackFinished(list);
        }
    }


}
