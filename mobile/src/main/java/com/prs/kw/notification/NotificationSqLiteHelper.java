package com.prs.kw.notification;

/**
 * Created by pranjal on 6/6/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotificationSqLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTIFICATIONS = "table_notification_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HEADER = "header";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_IS_DISMISSED = "is_dismissed";

    private static final String DATABASE_NAME = "notifications.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTIFICATIONS
            + "("
            + COLUMN_ID + " integer primary key not null, "
            + COLUMN_HEADER + " text not null, "
            + COLUMN_BODY + " text not null, "
            + COLUMN_IS_DISMISSED + " integer not null"
            + ");";

    public NotificationSqLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NotificationSqLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

}
