package com.mindpass.achacha;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_USER_ID = "userid";
    static final String PREF_USER_AGE = "userage";
    static final String PREF_USER_SEX = "usersex";
    static final String PREF_USER_EMAIL = "useremail";
    static final String PREF_USER_STORECHECK = "userstorecheck";
    static final String PREF_USER_STORENAME = "userstorename";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setUserInfo(Context ctx, String userName, Long userAge, String userSex, String userEmail, Long userstrCheck, String userstrName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, userName);
        editor.putLong(PREF_USER_AGE, userAge);
        editor.putString(PREF_USER_SEX, userSex);
        editor.putString(PREF_USER_EMAIL, userEmail);
        editor.putLong(PREF_USER_STORECHECK, userstrCheck);
        editor.putString(PREF_USER_STORENAME, userstrName);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }
    public static Long getUserAge(Context ctx) {
        return getSharedPreferences(ctx).getLong(PREF_USER_AGE, 0);
    }
    public static String getUserSex(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_SEX, "");
    }
    public static String getUserEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_EMAIL, "");
    }
    public static Long getUserStorecheck(Context ctx) {
        return getSharedPreferences(ctx).getLong(PREF_USER_STORECHECK, 0);
    }
    public static String getUserStorename(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_STORENAME, "");
    }

    // 로그아웃
    public static void clearUserInfo(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }

}
