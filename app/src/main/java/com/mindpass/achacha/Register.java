package com.mindpass.achacha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Register extends AppCompatActivity {
    EditText ed_id, ed_pw, ed_pw2, ed_mail, ed_age;
    String sid, spw, spw2, smail, ssex = "", sage;
    Integer iage;
    public String rslt;
    private RadioGroup rdio;
    private RadioButton rdmale, rdfmale;

    //검색시 선택된 매세지를 띄우기 위한 선언하였습니다. 그냥 선언안하고 인자로 넘기셔도 됩니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button regbtn = (Button) findViewById(R.id.regbtn);
        Button backbtn = (Button) findViewById(R.id.backbtn);

        rdio = (RadioGroup)findViewById(R.id.radioreg);
        rdmale = (RadioButton)findViewById(R.id.radiomale);
        rdfmale = (RadioButton)findViewById(R.id.radiofemale);

        ed_id = (EditText) findViewById(R.id.idreg);
        ed_pw = (EditText) findViewById(R.id.pwreg);
        ed_pw2 = (EditText) findViewById(R.id.pwwreg);
        ed_mail = (EditText) findViewById(R.id.mailreg);
        ed_age = (EditText) findViewById(R.id.agereg);

        rdio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radiomale){
                    ssex ="M";
                } else if (checkedId == R.id.radiofemale){
                    ssex ="F";
                }
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            //버튼 클릭시 이벤트입니다.
            @Override
            public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                sid = ed_id.getText().toString();
                spw = ed_pw.getText().toString();
                spw2= ed_pw2.getText().toString();
                smail= ed_mail.getText().toString();
                sage = ed_age.getText().toString();

                if(sid.matches("") || spw.matches("") || spw2.matches("") || smail.matches("") || sage.matches("") || rdio.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getApplicationContext(), "빈 칸이 있습니다.", Toast.LENGTH_SHORT).show();
                } else{

                if(spw.getBytes().length < 4){
                    Toast.makeText(getApplicationContext(), "비밀번호는 4자리 이상 입력해주세요.", Toast.LENGTH_LONG).show();
                } else if(!smail.contains("@")){
                    Toast.makeText(getApplicationContext(), "이메일 형식을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else{
                    iage = Integer.parseInt(ed_age.getText().toString());
                Regist rg = new Regist();
                rg.execute();}}
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            //버튼 클릭시 이벤트입니다.
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public class Regist extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {

            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:3000/register");
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
                buffer.append("pwCheck").append("=").append(spw2).append("&");
                buffer.append("sex").append("=").append(ssex).append("&");
                buffer.append("age").append("=").append(iage).append("&");
                buffer.append("email").append("=").append(smail).append("&");
                buffer.append("store_Check").append("=").append(0);
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

    }
    public void ioregsetting() {
        if (rslt.contains("410")) {
            Toast.makeText(getApplicationContext(), "입력되지 않은 정보가 있습니다.", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("411")) {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인하세요", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("500")) {
            Toast.makeText(getApplicationContext(), "서버 오류", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("501")) {
            Toast.makeText(getApplicationContext(), "중복된 ID입니다.", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("210")) {
            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,Login.class);
            reset();
            startActivity(intent);
        }


    }
    public void reset(){
        ed_id.setText("");
        ed_pw.setText("");
        ed_pw2.setText("");
        ed_mail.setText("");
        ed_age.setText("");
        rdio.clearCheck();
    }
}
