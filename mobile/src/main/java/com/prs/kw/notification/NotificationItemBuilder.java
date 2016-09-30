package com.prs.kw.notification;

import java.util.Date;

/**
 * Created by pranjal on 6/6/15.
 */
public class NotificationItemBuilder {

    public static NotificationItem build(String header, String body)
    {
        long id = new Date().getTime();
        return new NotificationItem(id, header, body);
    }

    public static NotificationItem buildFromDB(long id, String header, String body)
    {
        return new NotificationItem(id, header, body);
    }
}
