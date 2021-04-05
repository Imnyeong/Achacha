package com.mindpass.achacha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegChoice extends AppCompatActivity {

    Button normalbtn, storebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_choice);

        normalbtn = (Button)findViewById(R.id.normalreg);
        //이미지를 띄울 위젯
        normalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegChoice.this,Register.class);
                startActivity(intent);

            }
        });
        storebtn = (Button)findViewById(R.id.storereg);
        //이미지를 띄울 위젯
        storebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegChoice.this,RegisterSajang.class);
                startActivity(intent);
            }
        });
    }

}
