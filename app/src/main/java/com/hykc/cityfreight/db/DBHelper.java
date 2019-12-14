package com.hykc.cityfreight.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hykc.cityfreight.entity.LocationEntity;
import com.hykc.cityfreight.entity.User;


/**
 * Created by zzu on 2016/4/6.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 3;
    private static final String NAME = "CITY_FREIGHT.db";

    private static final String SQL_LOGIN_HISTORY_CREAT = "create table "+ User.TABLE_NAME+"(_id integer primary key autoincrement,"
            +User.USERID+" text ,"+User.USERNAME+" text, "+User.PSD+" text, "+User.TOKEN+" text,"+User.RZ+" text)";
    private static final String SQL_LOGIN_HISTORY_DROP = "drop table if exists "+User.TABLE_NAME;

    //位置信息表
    private static final String SQL_LOCATIONENTITY_CREAT = "create table "+ LocationEntity.TABLE_NAME+"(_id integer primary key autoincrement,"
            +LocationEntity.ROWID+" text ,"+LocationEntity.LOCATION+" text)";
    private static final String SQL_LOCATIONENTITY_DROP = "drop table if exists "+LocationEntity.TABLE_NAME;

    public static DBHelper helper = null;
    public static Context mContext;

    public static DBHelper getInstance(Context context) {
        if (helper == null) {
            synchronized (DBHelper.class) {
                if (helper == null) {
                    helper = new DBHelper(context.getApplicationContext());
                }
            }
        }
        mContext = context;
        return helper;
    }

    private DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_LOGIN_HISTORY_CREAT);
        db.execSQL(SQL_LOCATIONENTITY_CREAT);
    }

    /**
     * 当数据库更新时，调用该方法
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearCache(db);
    }

    /**
     * 清空数据缓存
     *
     * @param db
     */
    public void clearCache(SQLiteDatabase db) {
        db.execSQL(SQL_LOGIN_HISTORY_DROP);
        db.execSQL(SQL_LOGIN_HISTORY_CREAT);

        db.execSQL(SQL_LOCATIONENTITY_DROP);
        db.execSQL(SQL_LOCATIONENTITY_CREAT);
    }
}
