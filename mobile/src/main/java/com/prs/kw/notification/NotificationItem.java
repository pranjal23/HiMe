package com.prs.kw.notification;

/**
 * Created by pranjal on 6/6/15.
 */

public class NotificationItem {
    /*
     * Notification Id is the time in milliseconds when the notification was
     * posted
     */
    private Long notificationId;
    private String header;
    private String body;
    private boolean dismissed;

    NotificationItem(long notificationId, String header, String body)
    {
        this.setNotificationId(notificationId);
        this.header = header;
        this.body = body;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    @Override
    public boolean equals(Object o) {
        NotificationItem ni = (NotificationItem) o;
        return this.header.equals(ni.getHeader());
    }

}
