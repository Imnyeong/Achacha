package com.mindpass.achacha;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Choice extends AppCompatActivity implements View.OnClickListener {

    public String result, arrayString, big, MEMO;
    public Integer i, j, z, page, pg, tmppg, intid, code;
    private ListView m_oListView = null;
    Button choiceBtn,delete;
    ArrayList<ItemData> oData = new ArrayList<>();
    Long[] idarr = new Long[100];
    public Long sendid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Intent intent = getIntent();

        MEMO = intent.getExtras().getString("memo");

        page = 1;
        List lst = new List();
        lst.execute();

        choiceBtn = (Button)findViewById(R.id.btnChoice);

    }

    @Override
    public void onClick(View v) {
        int nViewTag = Integer.parseInt((String)v.getTag());
        String strViewName = "";
        View oParentView = (View) v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.
        String position = (String) oParentView.getTag();
        switch (nViewTag) {
            case 1: // 버튼

        /*AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);*/

                intid = Integer.parseInt(position);
                TextView oTextTitle = (TextView) oParentView.findViewById(R.id.textPlace);
                String Place = (String)oTextTitle.getText();
                Log.e("Place", Place);

                Intent intent = new Intent(Choice.this, ChoiceMap.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("place", Place);
                intent.putExtra("memo", MEMO);
                startActivity(intent);
                finish();
                break;
        }

    }

    public class List extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:3000/mlkonlpy");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("konlpy_memo").append("=").append(MEMO).append("&");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                outStream.flush();
                outStream.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                String lrslt;
                lrslt = buff.toString().trim();
                Log.e("Test", lrslt);
                Log.e("RECV DATA", data);
                pars(lrslt);
                parsr(arrayString);


            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
                m_oListView = (ListView) findViewById(R.id.listView);
                ListAdapter oAdapter = new ListAdapter(oData);
                m_oListView.setAdapter(oAdapter);
        }

        public void pars(String jsonString) throws ParseException, JSONException {

            JSONParser parser = new JSONParser();
            JSONObject univ = (JSONObject) parser.parse(jsonString);

            code = ((Long) univ.get("code")).intValue();
            if (code == 410) {
                Intent intent = new Intent();
                setResult(2, intent);
                finish();
            }
            else{
            JSONArray arr = (org.json.simple.JSONArray) univ.get("result");

            j = arr.size();
            String[] sendarr = new String[j];
            tmppg = ((Long) univ.get("total")).intValue();
            pg = (tmppg / 10) + 1;

            for (i = 0; i < j; i++) {

                String tmp = (String)arr.get(i);
                sendarr[i] = "\""+tmp+"\"";

                arrayString = Arrays.toString(sendarr);
            }
            Log.e("arrayString", arrayString);
            }

        }

        public void parsr(String jsonString) throws ParseException, JSONException {

            JSONParser parser = new JSONParser();

            if (jsonString == null) {

            } else {
                JSONArray arr = (JSONArray) parser.parse(jsonString);
                oData.clear();
                idarr = new Long[tmppg];
                for (z = 0; z < tmppg; ++z) {
                    ItemData oItem = new ItemData();
                    String tmp = (String)arr.get(z);
                    oItem.strPlace = tmp;
                    //idarr[z] = (Long) tmp.get("id");
                    oItem.onClickListener = Choice.this;
                    oData.add(oItem);
                }
            }
        }
    }

}

