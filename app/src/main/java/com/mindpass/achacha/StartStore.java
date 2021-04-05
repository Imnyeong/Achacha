package com.mindpass.achacha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartStore extends AppCompatActivity {

    Button sndbtn, mapbtn, hongbobtn, logoutbtn;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_store);

        Intent intent = new Intent(StartStore.this,MyService.class);
        startService(intent);

        sndbtn = (Button)findViewById(R.id.sndmebtn);
        //이미지를 띄울 위젯
        sndbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartStore.this,Input.class);
                startActivity(intent);

            }
        });

        mapbtn = (Button)findViewById(R.id.mapbtn);
        //이미지를 띄울 위젯
        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartStore.this,MainActivity.class);
                startActivity(intent);
            }
        });

        hongbobtn = (Button)findViewById(R.id.hongbo);
        //이미지를 띄울 위젯
        hongbobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartStore.this,Hongbo.class);
                startActivity(intent);
            }
        });

        logoutbtn = (Button)findViewById(R.id.logout);
        //이미지를 띄울 위젯
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartStore.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SaveSharedPreference.clearUserInfo(StartStore.this);
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
