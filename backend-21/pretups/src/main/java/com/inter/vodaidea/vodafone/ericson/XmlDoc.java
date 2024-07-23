/**
 * @FileName: XmlDoc.java
 * @Copyright: All Rights Reserved for Comviva Tech Ltd. @2011 
 */
package com.inter.vodaidea.vodafone.ericson;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is use for parsing xml file 
 */

class XmlDoc
{
	File xmlFile;
	Vector<String> element;
	StringBuffer con; 
	BufferedReader in;
	public boolean isValidate;
	public Hashtable<String,String> ht1 = new Hashtable<String,String>();
	public CommonFunc comon;
	public StringBuffer res=new StringBuffer();

	public XmlDoc(String f){
		xmlFile=new File(f);
		element=null;
		con=null;
		comon=new CommonFunc();
		isValidate=false;
		isValidate=parse(xmlFile);		
	}//end of constructor

	public XmlDoc(InputStream IS){
		element=null;
		con=null;
		isValidate=false;
		comon=new CommonFunc();
		in=new BufferedReader(new InputStreamReader(IS));
		isValidate=parse(in);	 	
	}

	public XmlDoc(BufferedReader IS){

		element=null;
		con=null;
		isValidate=false;
		comon=new CommonFunc();
		in=IS;
		isValidate=parse(in);
	}

	/*
	 * getParentElement() method return parent element
	 */
	/*public String getFirstChildElement(String tag){
		if(tag!=null && con!=null && con.length()>0){
			return comon.getFirstChildElement(con.toString(), tag);
		}else{
			return "";
		}		
	}*/

	public ArrayList<Node> getAllChildElement(String tag){
		if(tag!=null && con!=null && con.length()>0){
			return comon.getAllChildElement(con.toString(), tag);
		}else{
			return null;
		}
	}

	/*public String getXmlDocument(){
		if(con!=null)
			return con.toString();
		else
			return "";
	}*/

	/*public String getParentElement(String element){
		if(element!=null && con!=null && con.length()>0){
			return comon.getParentElement(con.toString(), element);
		}else{
			return "";
		}
	}//end of getParentElement()
*/
	/*
	 * getElementValue() returns element value
	 */
	/*public String getElementValue(String element){
		if(element!=null && con!=null && con.length()>0){
			return comon.getElementValue(con.toString(), element);
		}else{
			return "";
		}
	}//end of getElementValue();
*/
	/*public ArrayList<String> getElementValues(String tag){
		if(tag!=null && con!=null && con.length()>0){
			return comon.getElementValues(con.toString(), tag);
		}else{
			return null;
		}
	}*/

	/*public Hashtable<String,String> getAttributes(String tag){
		if(tag!=null)
			return comon.getAttbutes(tag);
		else
			return null;
	}*/

	public String getRootElement(){
		String root="";
		if(con!=null)
			root=comon.getRootElement(con.toString());
		return root;
	}

	/* private void tempDisplayDoc(BufferedReader br){
		 if(br!=null){
			 int i=0;			 
			 try{
				while((i=br.read())!=-1){					 
				}
			 }catch(Exception ie){				 
			 }			 
		 }
	 }*/

	/*
	 * parse() method parse xml file and store content of file in string buffer 
	 * if valid xml else give error message
	 */	
	public boolean parse(BufferedReader in){
		boolean memberFlag=false;
		boolean nameFlag=false;
		boolean valueFlag=false;
		boolean structFlag=false;
		boolean arrayFlag=false;
		int structCounter=0;





		boolean flag=false;
		con=new StringBuffer();
		element=new Vector<String>();	 
		String tag="";
		if(in!=null){
			try{
				int i=0;
				flag=true;
				while((i=in.read())!=-1){
					char ch=(char)i;
					if(ch=='<'){
						tag="";
						tag=comon.getTag(in);

						if(tag.equals("<member>")){
							memberFlag=true;
						}else if(tag.equals("<name>")){
							nameFlag=true;

						}else if(tag.equals("<value>")){
							valueFlag=true;

						}else if(tag.equals("<array>")){
							arrayFlag=true;
							res.append("<");
						}else if(tag.equals("<struct>")){
							structFlag=true;
							structCounter++;

							if(structCounter==2){
								res.append("[");
							}else if(structCounter==3){
								res.append("{");
							}else if(structCounter==4){
								res.append("(");
							}
						}else if(tag.equals("</member>")){
							memberFlag=false;

						}else if(tag.equals("</name>")){
							nameFlag=false;
							if(structCounter==1){
								res.append("=");
							}else if(structCounter==2){
								res.append("@");
							}else if(structCounter==3){
								res.append("#");
							}else if(structCounter==4){
								res.append("*");
							}

						}else if(tag.equals("</value>")){
							valueFlag=false;
							if(structCounter==1 && !arrayFlag){
								res.append("&");
							}else if(structCounter==2){
								res.append("|");
							}else if(structCounter==3){
								res.append("^");
							}else if(structCounter==4){
								res.append("~");
							}

						}else if(tag.equals("</array>")){
							arrayFlag=false;
							res.append(">");
						}else if(tag.equals("</struct>")){
							structFlag=false;

							if(structCounter==2){
								res.append("]");
							}else if(structCounter==3){
								res.append("}");
							}else if(structCounter==4){
								res.append(")");
							}
							if(arrayFlag)
								res.append(",");

							structCounter--;
						}

						if(!tag.startsWith("<?xml") && !tag.startsWith("</") && !tag.startsWith("<!")&& !tag.endsWith("/>")){
							element.add(tag);
							con.append(tag);
						}
						else if(tag.indexOf("##EXCEPTION##")!=-1){
							flag=false;
							con=new StringBuffer();
							break;
						}					
						else if(tag.startsWith("</")){
							if(element.size()>=1){
								String sTag=(String)element.get(element.size()-1);
								String eTag=tag;
								tag=tag.replaceAll(">", "");
								tag=tag.replaceAll("</", "<");
								if(sTag.startsWith(tag)){
									element.remove(element.size()-1);
									con.append(eTag);
								}else{
									flag=false;
									con=new StringBuffer();
									break;
								}
							}else{
								flag=false;
								con=new StringBuffer();
								break;
							}
						}else{	
							if(tag.endsWith("/>")){
								String eTag="</";
								if(tag.indexOf(" ")!=-1){
									eTag+=tag.substring(1, tag.indexOf(" "))+">";
								}else{
									eTag+=tag.substring(1, tag.indexOf("/>"))+">";
								}
								tag=tag.trim().substring(0, tag.trim().length()-2);
								tag=tag.trim()+">";							
								tag=tag.replace(" >", ">");
								con.append(tag+eTag);
							}else if(tag.indexOf("<!--")==-1)
								con.append(tag);
						}
					}else if(ch=='>'){
						con=new StringBuffer();
						flag=false;
						break;
					}else if(ch=='&'){
						tag="";
						tag=comon.getEntity(in);
						if(tag.indexOf("##EXCEPTION##")!=-1){
							flag=false;
							con=new StringBuffer();
							break;
						}else{
							con.append(tag);
						}
					}else{
						con.append(ch);
						res.append(ch);
					}
				}//end of while
				String temp=""+res;
				temp=temp.replaceAll("\\|\\|", "|");
				temp=temp.replaceAll("\\^\\^", "^");
				temp=temp.replaceAll("\\~\\~", "~");
				temp=temp.replaceAll("\\&\\&", "&");
				temp=temp.replaceAll("\\|\\]", "]");
				temp=temp.replaceAll("\\^}", "}");
				temp=temp.replaceAll("\\~\\)", ")");
				temp=temp.replaceAll("\\,>", ">");
				temp=temp.replaceAll("\\,\\|>", ">");
				temp=temp.replaceAll("\\,\\&>", ">");
				temp=temp.replaceAll("\\,\\^>", ">");

				res=new StringBuffer(temp);
				//System.out.println("STRING:::"+res);
				in.close();
			}catch(Exception e){
				e.printStackTrace();
			}//end of catch
		}//end of outer if
		return flag;
	}//end of parse
	public Hashtable getXMLContents(){
		Hashtable ht=new Hashtable();
		CommonFunc comonFunc=new CommonFunc();
		if(res !=null)
			ht=comonFunc.getHashtableFromString(res.toString());
		return ht;
	}

	public boolean parse(File xmlFile){
		boolean flag=false;	 
		con=new StringBuffer();
		element=new Vector<String>();	 
		String tag="";
		if(xmlFile!=null){
			try{
				int i=0;
				RandomAccessFile fin=new RandomAccessFile(xmlFile, "r");
				flag=true;
				while((i=fin.read())!=-1){
					char ch=(char)i;
					if(ch=='<'){
						tag="";
						tag=comon.getTag(fin);
						if(!tag.startsWith("<?xml") && !tag.startsWith("</") && !tag.startsWith("<!")&& !tag.endsWith("/>")){
							element.add(tag);
							con.append(tag);
						}
						else if(tag.indexOf("##EXCEPTION##")!=-1){
							flag=false;
							con=new StringBuffer();
							break;
						}					
						else if(tag.startsWith("</")){
							if(element.size()>=1){
								String sTag=(String)element.get(element.size()-1);
								String eTag=tag;
								tag=tag.replaceAll(">", "");
								tag=tag.replaceAll("</", "<");
								if(sTag.startsWith(tag)){
									element.remove(element.size()-1);
									con.append(eTag);
								}else{
									flag=false;
									con=new StringBuffer();
									break;
								}
							}else{
								flag=false;
								con=new StringBuffer();
								break;
							}
						}else{
							if(tag.endsWith("/>")){
								String eTag="</";
								if(tag.indexOf(" ")!=-1){
									eTag+=tag.substring(1, tag.indexOf(" "))+">";
								}else{
									eTag+=tag.substring(1, tag.indexOf("/>"))+">";
								}
								tag=tag.trim().substring(0, tag.trim().length()-2);
								tag=tag.trim()+">";
								tag=tag.replace(" >", ">");
								con.append(tag+eTag);														
							}else if(tag.indexOf("<!--")==-1)
								con.append(tag);
						}					
					}else if(ch=='>'){
						flag=false;
						con=new StringBuffer();
						break;
					}else if(ch=='&'){
						tag="";
						tag=comon.getEntity(fin);
						if(tag.indexOf("##EXCEPTION##")!=-1){
							flag=false;
							con=new StringBuffer();
							break;
						}else{
							con.append(tag);
						}
					}else{
						con.append(ch);
					}
				}//end of while
				fin.close();			
			}catch(Exception e){
				e.printStackTrace();
			}
		}//end of if
		return flag;
	}//end of parse() 	

	//added by abhay on 5/25/2007
	public void processRetrieveSubscriberLiteResult(Node n)
	{
		ArrayList<Node> arr=null;
		if(n.HasChild && n.getNodeName().equals("<RetrieveSubscriberLiteResult>"))
		{
			arr = n.getChildNodeList();
			for(int i=0; i<arr.size();i++)
			{
				Node n2 = (Node)arr.get(i);
				ht1.put(n2.getNodeName(), n2.getNodeValue());	
			}			
			/*Enumeration en=ht1.keys();
			while(en.hasMoreElements())
			{
				String key=(String)en.nextElement();			
			}*/	
		}
	}

	public void processSubscriberData(Node n){

		ArrayList<Node> arr=null;
		if(n.HasChild && !n.getNodeName().equals("<Balances>")){
			arr=n.getChildNodeList();
			for(int i=0; i<arr.size();i++){
				Node nl=(Node)arr.get(i);
				processSubscriberData(nl);			
			}
		}
		else if(n.getNodeName().equals("<CurrentState>")){
			ht1.put(n.getNodeName(),n.getNodeValue());

		}else if(n.getNodeName().equals("<Balances>")){
			ArrayList<Node> BalArr=n.getChildNodeList();
			for(int i=0;i<BalArr.size();i++){
				Node n2=(Node)BalArr.get(i);
				Hashtable<String,String> ht=processBalance(n2);
				if(ht!=null){
					String str=null;
					if(ht.containsKey("<BalanceName>")){
						str=(String)ht.get("<BalanceName>");
					}
					if(str!=null && str.equalsIgnoreCase("CORE BALANCE")){
						Enumeration<String> en=ht.keys();
						while(en.hasMoreElements()){
							String key=(String)en.nextElement();
							ht1.put(key,ht.get(key).toString());						
						}
					}
				}
			}
		} 	 	
	}//end processSubscriberData	 

	public void processError(Node n){
		ArrayList<Node> arr=null;
		if(n.HasChild && !n.getNodeName().equals("<detail>")){
			arr=n.getChildNodeList();
			for(int i=0; i<arr.size();i++){
				Node nl=(Node)arr.get(i);
				processError(nl);	 	    
			}
		}
		else if (n.HasChild && n.getNodeName().equals("<detail>"))
		{

			ArrayList<Node> BalArr=n.getChildNodeList();
			for(int i=0;i<BalArr.size();i++){

				Node n2=(Node)BalArr.get(i);	 	    
				ht1.put(n2.getNodeName(),n2.getNodeValue());	 	   
			}
		}	 	  
	}//end processError(

	public Hashtable<String,String> processBalance(Node n){
		Hashtable<String,String> ht = new Hashtable<String,String>();
		ArrayList<Node> BalArr=n.getChildNodeList();
		for(int i=0;i<BalArr.size();i++){	
			Node n2=(Node)BalArr.get(i);
			ht.put(n2.getNodeName(),n2.getNodeValue());
		} 	
		return ht; 	
	}

	public Hashtable<String,String> forDisp(Node n){		
		if(n.getNodeName().equals("<RetrieveSubscriberLiteResult>"))
		{
			processRetrieveSubscriberLiteResult(n);
		}
		else if(n.getNodeName().equals("<soap:Fault>")){
			processError(n);	    
		}	
		else if(n.HasChild){
			ArrayList<Node> ar=null;
			ar=n.getChildNodeList();
			for(int i=0; i<ar.size();i++){
				Node nl=(Node)ar.get(i);
				forDisp(nl);	
			}
		}
		else if(n.getNodeName().equals("<responseCode>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<originTransactionID>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<serviceClassCurrent>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<currency1>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<accountValue1>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<currency2>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<accountValue2>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<supervisionExpiryDate>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<serviceFeeExpiryDate>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<creditClearanceDate>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<serviceRemovalDate>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<languageIDCurrent>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}else if(n.getNodeName().equals("<temporaryBlockedFlag>")){
			ht1.put(n.getNodeName(),n.getNodeValue());		
		}
		return ht1;
	}

}//end of class
