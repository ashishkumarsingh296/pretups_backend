package com.btsl.util;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.OracleCodec;

public class SqlParameterEncoder {
	
	/**
     * ensures no instantiation
     */
    private SqlParameterEncoder(){
    	
    }
	/**
	 * 
	 * @param param - String to be encoded
	 * @return
	 */
	public static String encodeParams(String param)
	{
		
		String paramNew = null;
		String modifyResult = null;
			if(param!=null)
			{
				Codec codec = new OracleCodec();
					paramNew = ESAPI.encoder().encodeForSQL(codec, param);
					modifyResult = paramNew.replace("''", "'");
		
			}
		return modifyResult;
	}
	/*public static void main(String[] args)
	{
		
		String param = "";
		String paramNew = null;
		String modifyResult = null;
			
				Codec codec = new OracleCodec();
					paramNew = ESAPI.encoder().encodeForSQL(codec, param);
					modifyResult = paramNew.replace("''", "'");
		
			
		System.out.println(modifyResult);
	}*/
	
}
