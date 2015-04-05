package com.example.gracelu.myproject1;

/**
 * Created by Grace.Lu on 2015/3/21.
 */
public class SqlCommands {
    public static String createSpendString = "create table spendrecord"
            + "(id integer primary key autoincrement,int spendmoney, submittime date,iscalculate int)";

    public static String SetToCalculate = "update spendcord set iscalculate=1";

}
