package com.mindpass.achacha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Start extends AppCompatActivity {

    Button sndbtn,  mapbtn, logoutbtn;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private Intent MyServiceIntent;
    private MyService MyService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent intent = new Intent(Start.this,MyService.class);
        startService(intent);

        sndbtn = (Button)findViewById(R.id.sndmebtn);
        //이미지를 띄울 위젯
        sndbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this,Input.class);
                startActivity(intent);

            }
        });
        mapbtn = (Button)findViewById(R.id.mapbtn);
        //이미지를 띄울 위젯
        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this,MainActivity.class);
                startActivity(intent);
            }
        });

        logoutbtn = (Button)findViewById(R.id.logout);
        //이미지를 띄울 위젯
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SaveSharedPreference.clearUserInfo(Start.this);
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

}
