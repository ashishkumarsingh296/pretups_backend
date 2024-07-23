/*
 * Created on Dec 12, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cs3cp6;

import java.text.ParseException;

import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @author chetan.kothari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class TestDate {

    public static void main(String[] args) {

        try {

            System.out.println(InterfaceUtil.getDateFromDateString("00000000", "yyyy/MM/dd"));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
