package com.darfoo.darfoolauncher.support;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by zjh on 15-1-7.
 */
public class NetManager {

    /**
     * 获取板子的mac地址
     * @param context
     * @return
     */
    public static String getMacAddress(Context context){
        String macaddress = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null){
            macaddress = wifiInfo.getMacAddress();
        }else{
            macaddress = "null";
        }

        return macaddress;
    }

    /**
     * 获取板子所在网管的ip信息
     * @return
     */
    public static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().replace("%", "-");
                    }
                }
            }
        } catch (SocketException ex) {
            return "null";
        } catch (Exception e) {
            return "null";
        }
        return "null";
    }

}
