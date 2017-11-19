/*
 * Copyright (c) 2014. kugou.com
 */

package jo.dis.player.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络操作的工具类
 * <p/>
 * <ul>
 * <li>{@link #isNetworkConected(android.content.Context)} 网络是否可用</li>
 * <li>{@link #isWifiConnected(android.content.Context)} 是否wifi网络中</li>
 * <li>{@link #getLocalIpAddress()} 获取手机IP地址</li>
 * </ul>
 */
public class NetworkUtils {

    public static final int NETWORK_TYPE_NOT_AVAILABLE = -1;
    public static final int NETWORK_TYPE_WIFI = 0;
    public static final int NETWORK_TYPE_2G = 1;
    public static final int NETWORK_TYPE_3G = 2;
    public static final int NETWORK_TYPE_4G = 3;
    public static final int NETWORK_TYPE_UNKNWON = 4;

    public static final String NETWORK_TYPE_DESC_NOT_AVAILABLE = "NOT_AVAILABLE";
    public static final String NETWORK_TYPE_DESC_WIFI = "WIFI";
    public static final String NETWORK_TYPE_DESC_2G = "2G";
    public static final String NETWORK_TYPE_DESC_3G = "3G";
    public static final String NETWORK_TYPE_DESC_4G = "4G";
    public static final String NETWORK_TYPE_DESC_UNKNWON = "UNKNOWN";

    private NetworkUtils() {
    }

    /**
     * 是否在wifi网络中
     *
     * @param context 上下文
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 网络是否可用
     *
     * @param context 上下文
     * @return
     */
    public static boolean isNetworkConected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isAvailable());
        }
        return false;
    }

    /**
     * 获取手机IP地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "";
    }

    /**
     * 根据IP类型获取手机IP地址
     *
     * @param isIpv4
     * @return
     */
    public static String getLocalIpAddress(boolean isIpv4) {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (isIpv4) {
                            if (inetAddress instanceof Inet4Address)
                                return inetAddress.getHostAddress().toString();
                        } else {
                            if (inetAddress instanceof Inet6Address)
                                return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取当前手机的网络类型
     *
     * @param ctx
     * @return
     */
    public static int getNetworkType(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return NETWORK_TYPE_NOT_AVAILABLE;
        }

        switch (networkinfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                return NETWORK_TYPE_WIFI;
            case ConnectivityManager.TYPE_MOBILE:
                switch (networkinfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                    case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                    case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETWORK_TYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETWORK_TYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_TYPE_4G;

                }
        }
        return NETWORK_TYPE_UNKNWON;
    }


    public static String getNetworkTypeDesc(Context ctx) {
        int type = getNetworkType(ctx);
        switch (type) {
            case NETWORK_TYPE_NOT_AVAILABLE:
                return NETWORK_TYPE_DESC_NOT_AVAILABLE;

            case NETWORK_TYPE_WIFI:
                return NETWORK_TYPE_DESC_WIFI;

            case NETWORK_TYPE_2G:
                return NETWORK_TYPE_DESC_2G;

            case NETWORK_TYPE_3G:
                return NETWORK_TYPE_DESC_3G;

            case NETWORK_TYPE_4G:
                return NETWORK_TYPE_DESC_4G;

            default:
                return NETWORK_TYPE_DESC_UNKNWON;
        }

    }


    public static int getNetworkType4Monitor(Context ctx) {
        int type = getNetworkType(ctx);
        switch (type) {
            case NETWORK_TYPE_NOT_AVAILABLE:
                return 5;

            case NETWORK_TYPE_WIFI:
                return 2;

            case NETWORK_TYPE_2G:
                return 1;

            case NETWORK_TYPE_3G:
                return 3;

            case NETWORK_TYPE_4G:
                return 4;

            default:
                return 5;
        }

    }

    public static int getNetworkType4BI(Context context) {
        int type = getNetworkType(context);
        switch (type) {
            case NETWORK_TYPE_NOT_AVAILABLE:
                return 0;

            case NETWORK_TYPE_WIFI:
                return 1;

            case NETWORK_TYPE_2G:
                return 2;

            case NETWORK_TYPE_3G:
                return 3;

            case NETWORK_TYPE_4G:
                return 4;

            default:
                return 0;
        }
    }

    /**
     * 获取当前网络名移动网络只识别2G/3G/4G,不识别运营商
     *
     * @param context
     * @return
     */
    public static String getCurrentNetworkName(Context context) {
        String networkName = null;
        String networkTypeDesc = getNetworkTypeDesc(context);
        if (networkTypeDesc.equals(NETWORK_TYPE_DESC_WIFI)) {
            try {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        String ssid = wifiInfo.getSSID();
                        if (!TextUtils.isEmpty(ssid)) {
                            ssid = ssid.replace("\"", "").replace("'", "");
                            ssid = ssid.replace(";", "");
                        }
                        networkName = "wifi-" + ssid;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // [NormanOu] we got a NullPointerException report from
                // exception collect. Cause by
                // android.net.wifi.WifiManager.getConnectionInfo(WifiManager.java:971)
            }
        } else if (networkTypeDesc.equals(NETWORK_TYPE_DESC_UNKNWON) || networkTypeDesc.equals(NETWORK_TYPE_DESC_NOT_AVAILABLE)) {
            networkName = null;
        } else {
            networkName = networkTypeDesc;
        }

        if (null != networkName) {
            networkName = networkName.toUpperCase();
        }

        return networkName;
    }


}
