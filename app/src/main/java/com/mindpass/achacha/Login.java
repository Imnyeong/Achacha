package com.mindpass.achacha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {
    EditText ed_id, ed_pw;
    String sid, spw, token;
    public String rslt, id, sex, email, store_Name;
    public Long age, store_Check, code;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    //검색시 선택된 매세지를 띄우기 위한 선언하였습니다. 그냥 선언안하고 인자로 넘기셔도 됩니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_id = (EditText) findViewById(R.id.idedit);
        ed_pw = (EditText) findViewById(R.id.pwedit);

        Button logbtn = (Button) findViewById(R.id.login);
        Button regbtn = (Button) findViewById(R.id.register);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        Log.d("TAG", token);
                    }
                });

        logbtn.setOnClickListener(new View.OnClickListener() {
            //버튼 클릭시 이벤트입니다.
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                sid = ed_id.getText().toString();
                spw = ed_pw.getText().toString();
                Sendlog etc = new Sendlog();
                etc.execute();
                Log.e("Test", sid + "" + spw );
            }
        });
        regbtn.setOnClickListener(new View.OnClickListener() {
            //버튼 클릭시 이벤트입니다.
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, RegChoice.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else {

            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }

    }

    public class Sendlog extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {

            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:3000/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(sid).append("&");
                buffer.append("pw").append("=").append(spw).append("&");
                buffer.append("token").append("=").append(token);
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
                rslt = buff.toString().trim();
                Log.e("RECV DATA", data);
                pars(rslt);
                Log.e("rslt", rslt);
            } catch (ParseException e1) {
                e1.printStackTrace();

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ioregsetting();
        }

        public void pars(String jsonString) throws ParseException, JSONException {
            JSONParser parser = new JSONParser();

            Log.e("jsonString", jsonString);

            JSONObject univ = (JSONObject) parser.parse(jsonString);
            id = (String)univ.get("id");
            age = (Long)univ.get("age");
            sex = (String)univ.get("sex");
            email = (String)univ.get("email");
            store_Check = (Long) univ.get("store_Check");
            store_Name = (String)univ.get("store_Name");
            code = (Long)univ.get("code");

        }
    }

    public void ioregsetting() {

        if (code==410) {
            Toast.makeText(getApplicationContext(), "ID 오류", Toast.LENGTH_LONG).show();
        } else if (code==411) {
            Toast.makeText(getApplicationContext(), "비밀번호 오류", Toast.LENGTH_LONG).show();
            ed_pw.setText("");
        } else if (code==500) {
            Toast.makeText(getApplicationContext(), "서버 오류", Toast.LENGTH_LONG).show();

        } else if(code ==210 && store_Check == 1){
            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, StartStore.class);
            intent.putExtra("id", id);
            intent.putExtra("age", age);
            intent.putExtra("sex", sex);
            intent.putExtra("email", email);
            intent.putExtra("store_Name", store_Name);
            SaveSharedPreference.setUserInfo(Login.this, id, age, sex, email, store_Check, store_Name);
            reset();

            startActivity(intent);
        } else if(code ==210 && store_Check == 0) {
            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Start.class);
            intent.putExtra("id", id);
            intent.putExtra("age", age);
            intent.putExtra("sex", sex);
            intent.putExtra("email", email);
            intent.putExtra("store_Name", store_Name);
            SaveSharedPreference.setUserInfo(Login.this, id, age, sex, email, store_Check, store_Name);
            reset();

            startActivity(intent);
        }

        /*Toast.makeText(getApplicationContext(), "환영합니다.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), Start.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }
    public void reset(){
        /*ed_id.setText("");*/
        ed_pw.setText("");
    }
}
