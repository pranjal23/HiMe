package com.prs.kw.notification;

/**
 * Created by pranjal on 6/6/15.
 */

import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {

    public static final String NOTIFICATION_REFRESH_ACTION = "com.prs.kw.hime.notification_refresh";

    private static Context mContext;
    private static NotificationManager theInstance;
    private static NotificationPersistenceBroker mNotificationPersistenceBroker;

    private static CopyOnWriteArrayList<NotificationItem> localCopyFromDB = new CopyOnWriteArrayList<NotificationItem>();

    private NotificationManager()
    {
        mNotificationPersistenceBroker = new NotificationPersistenceBroker();
        reset();
    }

    public static NotificationManager getNotificationManagerInstance(Context context)
    {
        if (mContext == null)
            mContext = context.getApplicationContext();
        if (theInstance == null)
            theInstance = new NotificationManager();

        return theInstance;
    }

    public void addNotificationItem(NotificationItem ni)
    {
        if (!ni.isDismissed() && !localCopyFromDB.contains(ni))
            localCopyFromDB.add(ni);

        mNotificationPersistenceBroker.saveNotification(ni);
        broadCastRefreshAction();
    }

    public void updateNotification(NotificationItem ni)
    {
        if (ni.isDismissed())
            localCopyFromDB.remove(ni);

        mNotificationPersistenceBroker.saveNotification(ni);
        broadCastRefreshAction();
    }

    public List<NotificationItem> getNotifications()
    {
        return localCopyFromDB;
    }

    private void refresh()
    {
        new Thread() {
            public void run() {
                List<NotificationItem> items = mNotificationPersistenceBroker
                        .getAllNotifications();
                localCopyFromDB.addAll(items);
                broadCastRefreshAction();
            }
        }.start();
    }

    public void broadCastRefreshAction()
    {
        // Send broadcast to UI to update itself
        Intent intent = new Intent(NOTIFICATION_REFRESH_ACTION);
        mContext.sendBroadcast(intent);
    }

    public void reset()
    {
        // Call when user logs out, or logs in
        // Resets all the existing notifications
        localCopyFromDB.removeAll(localCopyFromDB);
        refresh();
    }
}
