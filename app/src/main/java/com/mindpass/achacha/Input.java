package com.mindpass.achacha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class Input extends AppCompatActivity implements View.OnClickListener {

    EditText edit_MEMO;
    Double LATITUDE, LONGITUDE;
    String MEMO, PLACE;
    private ListView m_oListView = null;
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    private DbOpenHelper mDbOpenHelper;
    String sort = "memo";
    Button btn_Insert;
    long nowIndex;
    static ArrayList<String> arrayIndex =  new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        edit_MEMO = (EditText) findViewById(R.id.EditMemo);
        btn_Insert = (Button) findViewById(R.id.sndBtn);
        btn_Insert.setOnClickListener(this);

        btn_Insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MEMO= edit_MEMO.getText().toString();
                if(MEMO.getBytes().length <= 0 ){
                    Toast.makeText(getApplicationContext(), "입력된 메모가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                /*SndMemo sm = new SndMemo();
                sm.execute();*/
                    Intent intent = new Intent(Input.this, Choice.class);
                    intent.putExtra("memo", MEMO);/*송신*/
                    startActivityForResult(intent, 0);
                    Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //이미지를 띄울 위젯
        btn_Insert.setEnabled(true);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);
    }

    /*private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            nowIndex = Long.parseLong(arrayIndex.get(position));
            AlertDialog.Builder dialog = new AlertDialog.Builder(InputMemo.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(InputMemo.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            mDbOpenHelper.deleteColumn(nowIndex);
                            showDatabase(sort);
                            setInsertMode();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(InputMemo.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            setInsertMode();
                        }
                    })
                    .create()
                    .show();
        }
    };*/

    @Override
    public void onClick(View v)
    {
        View oParentView = (View)v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.
        TextView oTextTitle = (TextView) oParentView.findViewById(R.id.textTitle);
        String position = (String) oParentView.getTag();
        if (position == null) {
            position = "0";
        }
        Integer intpos = (Integer)parseInt(position);
        nowIndex = Long.parseLong(arrayIndex.get(intpos));
        AlertDialog.Builder dialog = new AlertDialog.Builder(Input.this);
        dialog.setTitle("데이터 삭제")
                .setMessage("해당 데이터를 삭제 하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Input.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                        mDbOpenHelper.deleteColumn(nowIndex);
                        showDatabase(sort);
                        setInsertMode();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Input.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                        setInsertMode();
                    }
                })
                .create()
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {

            case 1:
                Log.e("test", "여기까진 돼요");
                PLACE = data.getStringExtra("place");
                LATITUDE = data.getDoubleExtra("latitude",0);
                LONGITUDE = data.getDoubleExtra("longitude",0);
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(MEMO, PLACE, LATITUDE, LONGITUDE);
                showDatabase(sort);
                setInsertMode();
                edit_MEMO.requestFocus();
                edit_MEMO.setCursorVisible(true);

                break;

            case 2:

                Toast.makeText(getApplicationContext(), "잘못된 메모입니다.", Toast.LENGTH_SHORT).show();

            default:

                break;

        }

    }

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayIndex.clear();

        // 데이터 생성 ============================
        ArrayList<MemoData> oData = new ArrayList<>();
        while (iCursor.moveToNext()) {
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String smemo = iCursor.getString(iCursor.getColumnIndex("memo"));
            String splace = iCursor.getString(iCursor.getColumnIndex("place"));
            //smemo = setTextLength(smemo,20);
            //splace = setTextLength(splace,5);
            MemoData oItem = new MemoData();
            oItem.strTitle = splace;
            oItem.strMemo = smemo;
            Date time = new Date();
            String time1 = format1.format(time);
            oItem.strDate = time1;
            oItem.onClickListener = this;
            oData.add(oItem);
            arrayIndex.add(tempIndex);
        }
        // ListView 생성 ===============================

        m_oListView = (ListView) findViewById(R.id.db_list_view);
        MemoAdapter oAdapter = new MemoAdapter(oData);
        m_oListView.setAdapter(oAdapter);
    }

    public void setInsertMode(){
        edit_MEMO.setText("");
    }


}