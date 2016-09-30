package com.prs.kw.notification;

/**
 * Created by pranjal on 6/6/15.
 */

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

public class NotificationPersistenceBroker {

    public static String TAG = "NotificationPersistenceBroker";
    private LinkedBlockingQueue<NotificationItem> persisterQueue = new LinkedBlockingQueue<>();
    PersistenceThread pThread;

    public NotificationPersistenceBroker()
    {
        pThread = new PersistenceThread();
        pThread.start();
    }

    public void saveNotification(NotificationItem ni)
    {
        persisterQueue.add(ni);
        LockSupport.unpark(pThread);
    }

    class PersistenceThread extends Thread
    {

        @Override
        public void run() {

            while (true)
            {
                while (persisterQueue.size() == 0)
                {
                    LockSupport.park();
                }

                // Save to DB using NotificationQueryHelper
                NotificationItem ni = persisterQueue.remove();
                NotificationFacade.updateOrInsertNotification(ni);

            }
        }

    }

    public List<NotificationItem> getAllNotifications()
    {
        List<NotificationItem> commomNotificationsList = NotificationFacade
                .getAllNotificationsFromDB();

        return commomNotificationsList;
    }
}
