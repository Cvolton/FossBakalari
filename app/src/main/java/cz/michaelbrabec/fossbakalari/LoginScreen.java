package cz.michaelbrabec.fossbakalari;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LoginScreen extends AppCompatActivity {

    String tokenBase;
    SharedPrefHandler sharedPrefHandler = new SharedPrefHandler();

    private class GetLoginContentTask extends AsyncTask<String, Integer, String> {
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
            // this is executed on the main thread after the process is over
            // update your UI here
            onLoginFinish(result);
        }
    }

    private class GetTokenVerificationTask extends AsyncTask<String, Integer, String> {
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
            // this is executed on the main thread after the process is over
            // update your UI here
            onTokenVerify(result);
        }
    }

    public String getWebContent(URL url){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = "", line;
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
            return content;
        }catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    final TextView textBakalari = findViewById(R.id.textBakalari);
                    final TextView textJmeno = findViewById(R.id.textJmeno);

                    new GetLoginContentTask().execute(textBakalari.getText() + "/login.aspx?gethx=" + textJmeno.getText());
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLoginFinish(String result){
        final TextView textUnderButton = findViewById(R.id.textUnderButton);
        final TextView textJmeno = findViewById(R.id.textJmeno);
        final TextView textBakalari = findViewById(R.id.textBakalari);
        final TextView textHeslo = findViewById(R.id.textHeslo);
        //textUnderButton.setText(result);

        try{
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            InputStream is = new ByteArrayInputStream(result.getBytes("UTF-8"));
            myParser.setInput(is, null);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            String response = getValue("res", element);
            Log.d("LoginResponse", response);

            if(response.equals("02")){
                textUnderButton.setText(R.string.error_user_not_found);
            }else if(response.equals("01")){
                //Generating SHA-512 Base64 hash of the password here
                String hashPasswd = getValue("salt", element) + getValue("ikod", element) + getValue("typ", element) + textHeslo.getText();
                hashPasswd = getSha512(hashPasswd);
                Log.d("HashPasswd", hashPasswd);

                //We still to generate the token though
                tokenBase = "*login*" + textJmeno.getText() + "*pwd*" + hashPasswd + "*sgn*ANDR";
                //continue
                String token = generateTokenFromBase(tokenBase);
                Log.d("hashPasswd", token);
                textUnderButton.setText(R.string.login_token_success);
                //verify if the token is correct and get real name + school
                new GetTokenVerificationTask().execute(textBakalari.getText() + "/login.aspx?hx="+token+"&pm=login");
            }else{
                textUnderButton.setText(R.string.error_unknown);
            }


        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void onTokenVerify(String result){
        final TextView textUnderButton = findViewById(R.id.textUnderButton);
        final TextView textBakalari = findViewById(R.id.textBakalari);
        try{
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            InputStream is = new ByteArrayInputStream(result.getBytes("UTF-8"));
            myParser.setInput(is, null);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            Log.d("TokenVerify", result);

            String response = getValue("result", element);
            if(response.equals("-1")) {
                textUnderButton.setText(R.string.error_password_incorrect);
                return;
            }

            SharedPrefHandler.setString(this, "tokenBase", tokenBase);
            SharedPrefHandler.setString(this, "loginJmeno", getValue("jmeno", element));
            SharedPrefHandler.setString(this,"loginSkola", getValue("skola", element));
            SharedPrefHandler.setString(this,"loginTrida", getValue("trida", element));
            SharedPrefHandler.setString(this,"loginRocnik", getValue("rocnik", element));
            SharedPrefHandler.setString(this,"loginModuly", getValue("moduly", element));
            SharedPrefHandler.setString(this,"loginTyp", getValue("typ", element));
            SharedPrefHandler.setString(this,"loginStrtyp", getValue("strtyp", element));
            SharedPrefHandler.setString(this,"bakalariUrl", textBakalari.getText().toString());

            startBakalari();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        textUnderButton.setText(result);
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public static String getSha512(String hashPasswd) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(hashPasswd.getBytes());
        byte[] bytes = md.digest();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static String generateTokenFromBase(String tokenBase) throws NoSuchAlgorithmException{
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("YYYYMMDD");
        String strDate = mdformat.format(calendar.getTime());
        String token = getSha512(tokenBase + strDate);
        token = token.replace("/", "_");
        token = token.replace("+", "-");
        return token;
    }

    private void startBakalari(){
        Intent intent = new Intent(this, UkolyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
