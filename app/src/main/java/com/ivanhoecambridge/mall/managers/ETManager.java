package com.ivanhoecambridge.mall.managers;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.data.Attribute;

import java.util.ArrayList;

/**
 * Created by Kay on 2017-03-02.
 */

public class ETManager {

    public static ArrayList<Attribute> getETAttributes(){
        try {
            return ETPush.getInstance().getAttributes();
        } catch (ETException e) {
            e.printStackTrace();
        }
        return new ArrayList<Attribute>();
    }

    public static boolean addETAttribute(String key, String value){
        try {
            return ETPush.getInstance().addAttribute(key, value);
        } catch (ETException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateET(){
        try {
            ETPush.getInstance().updateEt();
        } catch (ETException e) {
            e.printStackTrace();
        }
    }

}
