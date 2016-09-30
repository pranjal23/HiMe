package com.prs.kw.notification;

/**
 * Created by pranjal on 6/6/15.
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NotificationFacade {

    private static final String TAG = "NotificationFacade";

    static Context mContext;

    public static void init(Context context){
        mContext = context;
    }

    static Uri insertNotificationIntoDB(NotificationItem item) {

        ContentValues values = new ContentValues();
        values.put(NotificationSqLiteHelper.COLUMN_ID, item.getNotificationId());
        values.put(NotificationSqLiteHelper.COLUMN_HEADER, item.getHeader());
        values.put(NotificationSqLiteHelper.COLUMN_BODY, item.getBody());
        int bool = item.isDismissed() ? 1 : 0;
        values.put(NotificationSqLiteHelper.COLUMN_IS_DISMISSED, bool);

        ContentResolver resolver = mContext
                .getApplicationContext().getContentResolver();
        return resolver.insert(NotificationProvider.CONTENT_URI_NOTIFICATION_ITEMS,
                values);
    }

    public static void updateOrInsertNotification(
            NotificationItem item) {

        int bool = item.isDismissed() ? 1 : 0;
        ContentValues values = new ContentValues();
        values.put(NotificationSqLiteHelper.COLUMN_IS_DISMISSED, bool);

        String where = NotificationSqLiteHelper.COLUMN_HEADER + "= \""
                + item.getHeader() + "\"";

        ContentResolver resolver = mContext
                .getApplicationContext().getContentResolver();

        int i = resolver.update(NotificationProvider.CONTENT_URI_NOTIFICATION_ITEMS,
                values, where, null);

        if (i <= 0)
        {
            Uri uri = insertNotificationIntoDB(item);
            Log.w("NotificationQueryHelper", "insert uri: " + uri);
        }
    }

    public static List<NotificationItem> getAllNotificationsFromDB() {

        List<NotificationItem> tempList = new ArrayList<NotificationItem>();

        ContentResolver resolver = mContext
                .getApplicationContext().getContentResolver();

        String[] projection = new String[] {
                NotificationSqLiteHelper.COLUMN_ID,
                NotificationSqLiteHelper.COLUMN_HEADER,
                NotificationSqLiteHelper.COLUMN_BODY,
                NotificationSqLiteHelper.COLUMN_IS_DISMISSED
        };

        String bool = "0";
        String where;
        String[] values;

        where = NotificationSqLiteHelper.COLUMN_IS_DISMISSED + "= ?";

        values = new String[] {
                bool
        };

        Cursor cursor = null;

        cursor = resolver.query(
                NotificationProvider.CONTENT_URI_NOTIFICATION_ITEMS,
                projection, where,
                values, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst()) {
                do {
                    long notification_id = Long.parseLong(cursor.getString(0));
                    String header = cursor.getString(1);
                    String body = cursor.getString(2);

                    NotificationItem item = NotificationItemBuilder.buildFromDB(notification_id,
                            header, body);
                    tempList.add(item);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return tempList;
    }
}
