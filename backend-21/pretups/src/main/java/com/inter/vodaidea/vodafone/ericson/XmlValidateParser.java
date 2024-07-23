/**
 * @FileName: XmlValidateParser.java
 * @Copyright: All Rights Reserved for Comviva Tech Ltd. @2011 
 * @Comments: It parse the XML response string
 * @Comments: It defines the method which compare the tags to validate proper ending or closing
 */
package com.inter.vodaidea.vodafone.ericson;


/*
 * Created on Nov 6, 2012
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */


import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Window - Preferences - Java - Code Style - Code Templates
 */
class XmlValidateParser {

	public Hashtable<String,String> parsevalidatexml(BufferedReader InResp)
	{
		Hashtable<String,String> ht = new Hashtable<String,String>();
		XmlDoc xmldoc=new XmlDoc(InResp);

		if(xmldoc.isValidate){ 
			String root=xmldoc.getRootElement();
			/*ArrayList<Node> arr=null;
			if(root!=null){
				arr=xmldoc.getAllChildElement(root);
			}

			if(arr!=null){
				for(int i=0;i<arr.size();i++){
					 Node n=(Node)arr.get(i);
					 if(n.getNodeName().equals("<methodResponse>")){					 	
					 	ht=xmldoc.forDisp(n);					 	
					 }else{					 	
					 }
				 }

			 }else{

			 }*/
			ht=xmldoc.getXMLContents();
		}else{

		}			
		return ht;
	}
	public Hashtable<String,String> parsevalidatexml(String InResp)
	{
		Hashtable<String,String> ht = new Hashtable<String,String>();
		XmlDoc xmldoc=new XmlDoc(InResp);

		if(xmldoc.isValidate){ 
			//System.out.println("Entered1");
			String root=xmldoc.getRootElement();
			ArrayList<Node> arr=null;
			if(root!=null){
				arr=xmldoc.getAllChildElement(root);
			}
			//System.out.println(arr);

			if(arr!=null){
				for(int i=0;i<arr.size();i++){
					//System.out.println(""+arr.get(i));
					Node n=(Node)arr.get(i);
					// System.out.println(""+n.getNodeName());
					if(n.getNodeName().equals("<params>")){					 	
						ht=xmldoc.forDisp(n);					 	
					}else{

					}
				}
			}else{

			}
		}else{

		}			
		return ht;
	}
	public static void main(String args[]){

		XmlValidateParser xmv=new XmlValidateParser();

		//String str="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountAfterRefill</name><value><struct><member><name>accountFlags</name><value><struct><member><name>activationStatusFlag</name><value><boolean>1</boolean></value></member><member><name>negativeBarringStatusFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member></struct></value></member><member><name>accountValue1</name><value><string>24900</string></value></member><member><name>serviceClassCurrent</name><value><i4>1040</i4></value></member></struct></value></member><member><name>accountBeforeRefill</name><value><struct><member><name>accountFlags</name><value><struct><member><name>activationStatusFlag</name><value><boolean>1</boolean></value></member><member><name>negativeBarringStatusFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member></struct></value></member><member><name>accountValue1</name><value><string>23900</string></value></member><member><name>serviceClassCurrent</name><value><i4>1040</i4></value></member></struct></value></member><member><name>currency1</name><value><string>INR</string></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>masterAccountNumber</name><value><string>9763001321</string></value></member><member><name>originTransactionID</name><value><string>20080925141600</string></value></member><member><name>refillInformation</name><value><struct><member><name>refillValueTotal</name><value><struct><member><name>refillAmount1</name><value><string>1000</string></value></member></struct></value></member></struct></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>transactionAmount</name><value><string>1000</string></value></member><member><name>transactionCurrency</name><value><string>INR</string></value></member><member><name>voucherGroup</name><value><string>XX</string></value></member></struct></value></param></params></methodResponse>";
		//String refillStr="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountAfterRefill</name><value><struct><member><name>accountFlags</name><value><struct><member><name>activationStatusFlag</name><value><boolean>1</boolean></value></member><member><name>negativeBarringStatusFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member></struct></value></member><member><name>accountValue1</name><value><string>12000</string></value></member><member><name>dedicatedAccountInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>1</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>5000</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>20081026T12:00:00+0000</dateTime.iso8601></value></member></struct></value></data></array></value></member><member><name>serviceClassCurrent</name><value><i4>1094</i4></value></member></struct></value></member><member><name>currency1</name><value><string>INR</string></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>masterAccountNumber</name><value><string>9763001321</string></value></member><member><name>originTransactionID</name><value><string>20080926161623</string></value></member><member><name>refillInformation</name><value><struct><member><name>progressionRefillCounter</name><value><i4>1</i4></value></member><member><name>promotionPlanProgressed</name><value><boolean>0</boolean></value></member><member><name>promotionRefillCounter</name><value><i4>0</i4></value></member><member><name>refillValuePromotion</name><value><struct><member><name>dedicatedAccountRefillInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>1</i4></value></member><member><name>refillAmount1</name><value><string>5000</string></value></member></struct></value></data></array></value></member><member><name>refillAmount1</name><value><string>0</string></value></member><member><name>serviceFeeDaysExtended</name><value><i4>0</i4></value></member><member><name>supervisionDaysExtended</name><value><i4>0</i4></value></member></struct></value></member><member><name>refillValueTotal</name><value><struct><member><name>dedicatedAccountRefillInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>1</i4></value></member><member><name>refillAmount1</name><value><string>5000</string></value></member></struct></value></data></array></value></member><member><name>refillAmount1</name><value><string>1000</string></value></member></struct></value></member></struct></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>transactionAmount</name><value><string>1000</string></value></member><member><name>transactionCurrency</name><value><string>INR</string></value></member><member><name>voucherGroup</name><value><string>60</string></value></member></struct></value></param></params></methodResponse>";

		String valrespxml="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountValue1</name><value><string>6422</string></value></member><member><name>creditClearanceDate</name><value><dateTime.iso8601>20140302T12:00:00+0000</dateTime.iso8601></value></member><member><name>currency1</name><value><string>INR</string></value></member><member><name>dedicatedAccountInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>1</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>2</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>3</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>10486</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>20110622T12:00:00+0000</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>4</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>5</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>6</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>7</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>8</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>9</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>10</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value></data></array></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>originTransactionID</name><value><string>MU110531160325</string></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>18</i4></value></member><member><name>serviceFeeExpiryDate</name><value><dateTime.iso8601>20140215T12:00:00+0000</dateTime.iso8601></value></member><member><name>serviceRemovalDate</name><value><dateTime.iso8601>20140516T12:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionExpiryDate</name><value><dateTime.iso8601>20140215T12:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";
		String toprespxml="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountValue1</name><value><string>11422</string></value></member><member><name>creditClearanceDate</name><value><dateTime.iso8601>20140302T12:00:00+0000</dateTime.iso8601></value></member><member><name>currency1</name><value><string>INR</string></value></member><member><name>dedicatedAccountInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>1</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>2</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>3</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>10486</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>20110622T12:00:00+0000</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>4</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>5</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>6</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>7</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>8</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>9</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>10</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value></data></array></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>originTransactionID</name><value><string>MU110531163729</string></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>18</i4></value></member><member><name>serviceFeeExpiryDate</name><value><dateTime.iso8601>20140215T12:00:00+0000</dateTime.iso8601></value></member><member><name>serviceRemovalDate</name><value><dateTime.iso8601>20140516T12:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionExpiryDate</name><value><dateTime.iso8601>20140215T12:00:00+0000</dateTime.iso8601></value></member><member><name>temporaryBlockedFlag</name><value><boolean>1</boolean></value></member></struct></value></param></params></methodResponse>";

		BufferedReader br=new BufferedReader(new StringReader(toprespxml));
		Hashtable htable=xmv.parsevalidatexml(br);
		System.out.println("htable  :"+htable);
		CommonFunc commonFunc = new CommonFunc();
		String pamimessage = commonFunc.getHashToString(htable);
		System.out.println("response in string :"+pamimessage);


	}
	public void comparetags(String stringoffile) {
		//System.out.println("Entered in comparetags witrh data : ");

		java.util.ArrayList<String> strarray = new java.util.ArrayList<String>();
		String temp = "";
		String _key_value = "";
		String _name = "";
		String _value = "";
		int i = 0;
		int j = 0;
		boolean flag = false;
		Hashtable valuesHash=new Hashtable();
		try {
			String _firstline = "<?xml version=\"1.0\" encoding=\"utf-8\"";


			if (stringoffile.startsWith(_firstline)) {
				int len = stringoffile.indexOf('>');
				stringoffile = stringoffile.substring(38);
			}

			while (stringoffile.length() != 0) {
				i = stringoffile.indexOf('<');
				if (i == 0) {
					j = stringoffile.indexOf('>');
					if (stringoffile.charAt(1) == '/') {
						_key_value = stringoffile.substring(2, j);
						int _strarraysize = strarray.size();
						//temp = (String) strarray.get(_strarraysize - 1);
						temp =  strarray.get(_strarraysize - 1);

						if (temp.equals(_key_value))
							strarray.remove(_strarraysize - 1);
						stringoffile = stringoffile.substring(j + 1);
					} else {
						_key_value = stringoffile.substring(1, j);
						stringoffile = stringoffile.substring(j + 1);
						strarray.add(_key_value);
					}
				} else {
					j = stringoffile.indexOf('<');

					_key_value = stringoffile.substring(0, j);
					stringoffile = stringoffile.substring(j);
					int _strarraysize = strarray.size();
					//temp = (String) strarray.get(_strarraysize - 1);
					temp =  strarray.get(_strarraysize - 1);
					// System.out.println("tag::"+temp);
					if (temp.equals("name")) {
						_name = _key_value;
					} else {
						_value = _key_value;
					}
					//System.out.println("name::"+_name+"::value::"+_value);
					valuesHash.put(_name, _value);
				}
			}

			if (strarray.size() == 0)
				flag = true;
			//  System.out.println(valuesHash);
		} catch (Exception e) {
			System.err.println("File is not valid");
		}


	}
}



