package com.prs.kw.notification;

/**
 * Created by pranjal on 6/6/15.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class NotificationProvider extends ContentProvider {

    public NotificationSqLiteHelper mHelper;

    public static final String AUTHORITY = "com.prs.kw.hime.NOTIFICATION";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri CONTENT_URI_NOTIFICATION_ITEMS = Uri
            .withAppendedPath(BASE_CONTENT_URI, "notification_items");

    public static Uri getContentUriWithId(long id) {
        return ContentUris.withAppendedId(CONTENT_URI_NOTIFICATION_ITEMS, id);
    }

    public static final int NOTIFICATION_ITEMS = 1;
    public static final int NOTIFICATION_ITEM_ID = 2;

    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, "notification_items", NOTIFICATION_ITEMS);
        URI_MATCHER.addURI(AUTHORITY, "notification_items/#", NOTIFICATION_ITEM_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase sqlDB = mHelper.getWritableDatabase();
        int rowsAffected = 0;
        switch (URI_MATCHER.match(uri)) {
            case NOTIFICATION_ITEMS:
                rowsAffected = sqlDB.delete(
                        NotificationSqLiteHelper.TABLE_NOTIFICATIONS,
                        selection, selectionArgs);
                break;
            case NOTIFICATION_ITEM_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB
                            .delete(NotificationSqLiteHelper.TABLE_NOTIFICATIONS,
                                    NotificationSqLiteHelper.COLUMN_ID + "="
                                            + id, null);
                } else {
                    rowsAffected = sqlDB.delete(
                            NotificationSqLiteHelper.TABLE_NOTIFICATIONS,
                            selection + " and "
                                    + NotificationSqLiteHelper.COLUMN_ID + "="
                                    + id, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        return "text/plain";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != NOTIFICATION_ITEMS) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();

        long id = db.insert(
                NotificationSqLiteHelper.TABLE_NOTIFICATIONS, null,
                values);
        return getUriForId(id, uri);

    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            return itemUri;
        }

        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    @Override
    public boolean onCreate() {
        mHelper = new NotificationSqLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case NOTIFICATION_ITEMS:
                builder.setTables(NotificationSqLiteHelper.TABLE_NOTIFICATIONS);
                break;
            case NOTIFICATION_ITEM_ID:
                builder.setTables(NotificationSqLiteHelper.TABLE_NOTIFICATIONS);
                builder.appendWhere(NotificationSqLiteHelper.COLUMN_ID + " = "
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int updateCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case NOTIFICATION_ITEMS:
                updateCount = db.update(
                        NotificationSqLiteHelper.TABLE_NOTIFICATIONS,
                        values, selection, selectionArgs);
                break;
            case NOTIFICATION_ITEM_ID:
                String idStr = uri.getLastPathSegment();
                String where = NotificationSqLiteHelper.COLUMN_ID + " = "
                        + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        NotificationSqLiteHelper.TABLE_NOTIFICATIONS,
                        values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        return updateCount;
    }

}
