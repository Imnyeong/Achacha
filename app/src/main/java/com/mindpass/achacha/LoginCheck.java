package com.mindpass.achacha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class LoginCheck extends AppCompatActivity {

    private Intent intent;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_check);

        mLayout = findViewById(R.id.layout_login_check);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            if (SaveSharedPreference.getUserName(LoginCheck.this).length() == 0) {
                // call Login Activity
                intent = new Intent(LoginCheck.this, Login.class);
                startActivity(intent);
                this.finish();
            } else if (SaveSharedPreference.getUserStorecheck(this) == 1) {
                // Call Next Activity
                intent = new Intent(LoginCheck.this, StartStore.class);
                intent.putExtra("STD_NUM", SaveSharedPreference.getUserName(this).toString());
                startActivity(intent);
                this.finish();
            } else if (SaveSharedPreference.getUserStorecheck(this) == 0) {
                // Call Next Activity
                intent = new Intent(LoginCheck.this, Start.class);
                intent.putExtra("STD_NUM", SaveSharedPreference.getUserName(this).toString());
                startActivity(intent);
                this.finish();
            }
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("백그라운드 위치 권한");
                builder.setMessage("이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 메모를 통한 알람을 위해 위치 데이터를 수집하여 실시간 위치 추적 기능, 푸시 알람 기능을 사용 설정합니다. \n이 데이터는 광고 제공/광고 기능 지원/광고 지원에도 사용됩니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(LoginCheck.this, REQUIRED_PERMISSIONS,
                                        PERMISSIONS_REQUEST_CODE);

                            }
                        });
                builder.show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("백그라운드 위치 권한");
                builder.setMessage("이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 메모를 통한 알람을 위해 위치 데이터를 수집하여 실시간 위치 추적 기능, 푸시 알람 기능을 사용 설정합니다. \n이 데이터는 광고 제공/광고 기능 지원/광고 지원에도 사용됩니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(LoginCheck.this, REQUIRED_PERMISSIONS,
                                        PERMISSIONS_REQUEST_CODE);
                            }
                        });
                builder.show();

            }

        }

    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.

                if (SaveSharedPreference.getUserName(LoginCheck.this).length() == 0) {
                    // call Login Activity
                    intent = new Intent(LoginCheck.this, Login.class);
                    startActivity(intent);
                    LoginCheck.this.finish();
                } else if (SaveSharedPreference.getUserStorecheck(LoginCheck.this) == 1) {
                    // Call Next Activity
                    intent = new Intent(LoginCheck.this, StartStore.class);
                    intent.putExtra("STD_NUM", SaveSharedPreference.getUserName(LoginCheck.this).toString());
                    startActivity(intent);
                    LoginCheck.this.finish();
                } else if (SaveSharedPreference.getUserStorecheck(LoginCheck.this) == 0) {
                    // Call Next Activity
                    intent = new Intent(LoginCheck.this, Start.class);
                    intent.putExtra("STD_NUM", SaveSharedPreference.getUserName(LoginCheck.this).toString());
                    startActivity(intent);
                    LoginCheck.this.finish();
                }
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }
}

