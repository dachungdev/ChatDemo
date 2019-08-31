package com.dachung.chatdemo.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 网络连接接口工具类
 */
public class NetworkUtil {
    //获取IPV4地址
    public static String getLocalIpV4Address(){
        try{
            String ipV4;
            //将getNetworkInterfaces()得到的nilist数组，即获取本机所有的网络接口
            ArrayList<NetworkInterface> nIList = Collections.list(NetworkInterface.getNetworkInterfaces());
            //遍历每个接口
            for (NetworkInterface nI : nIList)
            {
                //这时有可能获取到多个ip，因为一个网络接口可以配置多个ip。ip的获取与InetAddress类相关
                ArrayList<InetAddress> iAList = Collections.list(nI.getInetAddresses());
                //遍历每个ip
                for(InetAddress address : iAList)
                {
                    //杜绝本地回环接口，即127.0.0.1,与通过ipv4地址
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address)
                    {
                        ipV4 = address.getHostAddress();
                        return ipV4;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
