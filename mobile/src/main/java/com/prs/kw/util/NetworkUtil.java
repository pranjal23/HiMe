package com.prs.kw.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtil {

    public enum NetWorkType {
        WIRELESS, WIRED, UNKNOWN
    }

    @SuppressWarnings("unused")
    private static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        } else {
            Log.v("NetworkUtil", "WIFI not connected");
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        NetWorkType activeNetworkType = getActiveNetWorkType(context);
        return activeNetworkType.equals(NetWorkType.WIRELESS)||activeNetworkType.equals(NetWorkType.WIRED);
    }

    public static NetWorkType getActiveNetWorkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NetWorkType.UNKNOWN;
        }
        if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetWorkType.WIRELESS;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            return NetWorkType.WIRED;
        }
        return NetWorkType.UNKNOWN;
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param //ipv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    @SuppressWarnings("rawtypes")
    public static String getIpAddress(String interfaceName) {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ipAddress = inetAddress.getHostAddress().toString();
                        Log.e("IP address", "" + ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.w("NetworkUtil", "Socket ex in GetIP Add of Util - " + ex.toString());
        }
        return null;
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static byte[] getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }
                byte[] mac = intf.getHardwareAddress();
                String macAddress = "";
                for (int i = 0; i < mac.length; i++) {
                    macAddress += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-"
                            : "");
                }
                Log.e("MAC address", macAddress);
                if (mac != null)
                    return mac;
            }
        } catch (SocketException ex) {
            Log.e("NetworkUtil", "Socket exception in GetIP Address of Utilities" + ex.toString());
        }
        throw new RuntimeException("Unable to fetch the mac Address!!!");

    }

    public static boolean validateIpAddressString(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        int[] ipParts = getIpParts(ip);
        if (ipParts != null) {
            for (int i : ipParts) {
                if (i < 0 || i > 255)
                    return false;
            }
            return true;
        }
        return false;
    }

    public static int[] getIpParts(String ip) {
        String[] strParts = ip.trim().split("\\.");
        if (strParts.length != 4) {
            return null;
        }

        try {
            int[] ipParts = new int[4];
            ipParts[0] = (int) Integer.parseInt(strParts[0]);
            ipParts[1] = (int) Integer.parseInt(strParts[1]);
            ipParts[2] = (int) Integer.parseInt(strParts[2]);
            ipParts[3] = (int) Integer.parseInt(strParts[3]);
            return ipParts;
        } catch (Exception e) {
            return null;
        }
    }
}
