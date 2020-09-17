package com.zvision.net.okhttp;

public class RuntimeHelper {
    public static String DOMAIN = "";

    public static boolean DEBUG = false;

    public static void logDebug(String msg){
        if(RuntimeHelper.DEBUG){
            System.out.println("NetClient："+msg);
        }
    }

}
