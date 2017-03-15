package com.example.qman.myapplication.utils;

import java.text.DecimalFormat;

/**
 * Created by Qman on 2017/3/14.
 */

public class IncreasingId {
    private static int totalCount = 0;
    private static int customerID;
    public IncreasingId(){
        ++totalCount;
        customerID = totalCount;
        System.out.println("增加一个");
    }
    public static String getCustomerID() {
        DecimalFormat decimalFormat = new DecimalFormat("00000");
        return decimalFormat.format(customerID);
    }
}
