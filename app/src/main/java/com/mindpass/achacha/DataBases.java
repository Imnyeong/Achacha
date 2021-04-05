package com.mindpass.achacha;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String MEMO = "memo";
        public static final String PLACE = "place";
        public static final String _TABLENAME0 = "usertable";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +MEMO+" text not null , "
                +PLACE+" text not null , "
                +LATITUDE+" text not null , "
                +LONGITUDE+" text not null );";

    }
}

