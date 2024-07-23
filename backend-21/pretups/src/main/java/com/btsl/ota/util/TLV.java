/*
 * Created on Dec 16, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.ota.util;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author gaurav.garg
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TLV {
	private StringBuffer tlvStorage = new StringBuffer();
    private static final Log _log = LogFactory.getLog(TLV.class.getName());


    public void setTag(String tag) {
        tlvStorage.append(tag);
    }

    public void setData(String data) {
        tlvStorage.append(data);
    }

    public void setLength() throws Exception {
        int length = (tlvStorage.length() - 2) / 2;// -2 is for the tag which is
                                                   // of two bytes divide by 2
                                                   // is as it takes one byte
        tlvStorage.insert(2, SimUtil.lengthConverter(length));
    }

    public String getTLV() {
        return tlvStorage.toString();
    }

    /**
	 * 
	 */
    public TLV() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args){
    	try{
        TLV tlv = new TLV();
        tlv.setTag("51");
        tlv.setData("51");
        tlv.setData("51");
        tlv.setData("51");
        tlv.setLength();
        tlv.getTLV();
       }
    	catch(Exception e){
   		 _log.errorTrace("main", e);
   		}	
    }
}
