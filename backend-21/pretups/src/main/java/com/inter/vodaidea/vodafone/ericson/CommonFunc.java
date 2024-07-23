/**
 * @FileName: CommonFunc.java
 * @Copyright: All Rights Reserved for Comviva Tech Ltd. @2011 
 * @Comments:  It parse the XML and store it in HashMap, with tag as a key and tag value as Hashmap value
 * @Comments: It validate the child elements in XML.
 * @Comments: call the client to send the request to IN
 * @Comments: validate the response from IN
 */
package com.inter.vodaidea.vodafone.ericson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


//import org.apache.log4j.*;
/**
 * This class is containg general method
 */

public class CommonFunc{

	private Vector retVect = new Vector();
	private StringTokenizer st;
	/**
	 * @param f
	 * @return boolean
	 * May 10, 2011
	 * ashish.gupta
	 */
	/*public boolean isFileExist(File f){
		if(f!=null && f.exists()){
			return true;
		}else{
			return false;
		}
	}*///end of isFileExist()

	/**
	 * @param tag
	 * @return boolean
	 * May 10, 2011
	 * ashish.gupta
	 */
	public boolean hasAttribute(String tag){
		if(tag!=null && tag.length()>0 && tag.indexOf(" ")!=-1){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @param con
	 * @param tag
	 * @return boolean
	 * May 10, 2011
	 * ashish.gupta
	 */
	public boolean hasChildElement(String con, String tag){
		boolean result=false;
		if(tag!=null && con!=null && con.indexOf(tag)!=-1){
			String eTag="</"+tag.substring(1);
			if(eTag.indexOf(" ")!=-1)
				eTag=eTag.substring(0, eTag.indexOf(" "))+">";
			con=con.substring(con.indexOf(tag)+tag.length());		
			con=con.substring(0, findEndTagPosition(tag, eTag, con));
			if(con.indexOf("<")!=-1){
				String child=con.substring(con.indexOf("<"), con.indexOf(">")+1);
				if(child.startsWith("</"))
					result=false;
				else
					result=true;			
			}
		}
		return result;
	}

	/**
	 * @param tag
	 * @return Hashtable<String,String>
	 * May 10, 2011
	 * ashish.gupta
	 */
	public Hashtable<String, String> getAttbutes(String tag){
		Hashtable<String, String> attr=null;
		if(tag!=null && tag.indexOf(" ")!=-1){
			tag=tag.replaceAll(">","");
			tag=tag.replaceAll("\"","");
			attr=new Hashtable<String, String>();
			String nameValue[]=tag.split(" ");
			for(int i=1; i<nameValue.length; i++){
				String temp[]=nameValue[i].split("=");
				attr.put(temp[0], temp[1]);
			}
		}
		return attr;
	}
	/**
	 * @param con
	 * @param tag
	 * @return String
	 * May 10, 2011
	 * ashish.gupta
	 */
	/*public String getFirstChildElement(String con, String tag){
		String child="";
		if(tag!=null && con!=null && con.indexOf(tag)!=-1){
			String eTag="</"+tag.substring(1);
			if(eTag.indexOf(" ")!=-1)
				eTag=eTag.substring(0, eTag.indexOf(" "));
			con=con.substring(con.indexOf(tag)+tag.length(), con.indexOf(eTag));
			if(con.indexOf("<")!=-1){
				child=con.substring(con.indexOf("<"), con.indexOf(">")+1);
				if(child.startsWith("</"))
					child="";
			}
		}
		return child;
	}*/
	/**
	 * @param con
	 * @param tag
	 * @return ArrayList<Node>
	 * May 10, 2011
	 * ashish.gupta
	 */
	public ArrayList<Node> getAllChildElement(String con, String tag){
		ArrayList<Node> childList=null;
		String child="";
		if(tag!=null && con!=null && con.indexOf(tag)!=-1){		
			String eTag="</"+tag.substring(1);
			if(eTag.indexOf(" ")!=-1)
				eTag=eTag.substring(0, eTag.indexOf(" "))+">";
			con=con.substring(con.indexOf(tag)+tag.length());//, con.lastIndexOf(eTag)+eTag.length());
			con=con.substring(0, findEndTagPosition(tag, eTag, con));
			childList=new ArrayList<Node>();
			while(con.indexOf("<")!=-1){			
				child=con.substring(con.indexOf("<"), con.indexOf(">")+1);			
				if(child.startsWith("</"))
					child="";
				else{
					Node n=new Node();
					if(hasAttribute(child)){
						n.HasAttribute=true;
						n.setAttributeList(getAttbutes(child));
					}
					if(hasChildElement(con, child)){
						n.HasChild=true;
						n.setChildNodeList(getAllChildElement(con, child));
					}else{

						String v=getElementValue(con, child);
						n.setNodeValue(v);

					}
					n.setNodeName(child);
					childList.add(n);				
					eTag="</"+child.substring(1);
					if(eTag.indexOf(" ")!=-1)
						eTag=eTag.substring(0, eTag.indexOf(" "))+">";	
					con=con.substring(con.indexOf(child)+child.length());
					con=con.substring(findEndTagPosition(child, eTag, con)+eTag.length());				
				}
			}
		}
		return childList;
	}
	/**
	 * @param tag
	 * @param etag
	 * @param con
	 * @return int
	 * May 10, 2011
	 * ashish.gupta
	 */
	public int findEndTagPosition(String tag, String etag, String con){
		int pos=0;
		String tag1="";
		if(tag!=null && etag!=null && con!=null){				
			if(tag.indexOf(" ")!=-1){
				tag1=tag.substring(tag.indexOf(" ")+1);
				tag=tag.substring(0, tag.length()-1)+">";
			}
			else
				tag1=tag.substring(0, tag.length()-1)+" ";
			int from=0;
			int sCount=1;
			int eCount=1;
			while(con.indexOf("<", from)!=-1){
				String temp=con.substring(con.indexOf("<", from), con.indexOf(">", from)+1);
				if(temp.equalsIgnoreCase(etag)){
					sCount--;
					eCount--;
					if(sCount==0 && eCount==0){
						pos=con.indexOf(temp, from);
						break;
					}else{
						from=con.indexOf("<", from)+temp.length();
					}
				}else if(temp.equalsIgnoreCase(tag)){
					sCount++;
					eCount++;
					from=con.indexOf("<", from)+temp.length();
				}else if(temp.equalsIgnoreCase(tag1)){
					sCount++;
					eCount++;
					from=con.indexOf("<", from)+temp.length();
				}else{
					from=con.indexOf("<", from)+temp.length();
				}

			}//end of while
		}
		return pos;
	}//end findEndtagPosition()

	/**
	 * @param con
	 * @return String
	 * May 10, 2011
	 * ashish.gupta
	 */
	public String getRootElement(String con){
		String root="";
		if(con!=null){
			root=con.substring(con.lastIndexOf("</"));
			root=root.substring(0, root.indexOf(">"));
			root="<"+root.substring(2);
			root=con.substring(con.indexOf(root));
			root=root.substring(0, root.indexOf(">")+1);
		}
		return root;
	}

	/**
	 * @param con
	 * @param element
	 * @return String
	 * May 10, 2011
	 * ashish.gupta
	 */
	/*public String getParentElement(String con, String element){
		String parent="";
		if(con!=null && element!=null){
			con=con.substring(0, con.indexOf(element));
			while(true){
				if(con.indexOf("<")!=-1){
					String tTag=con.substring(con.lastIndexOf("<"), con.lastIndexOf(">")+1);
					if(tTag.startsWith("</")){
						tTag=tTag.replaceFirst("</", "<");
						tTag=tTag.replaceFirst(">", "");
						if(con.indexOf(tTag)!=-1){
							con=con.substring(0, con.lastIndexOf(tTag));
						}
					}else if(tTag.startsWith("<?")){
						break;
					}else if(tTag.startsWith("<!")){
						break;
					}else{
						parent=tTag;
						break;
					}
				}
			}
		}
		return parent;
	}//end of getParentElement()
*/
	/**
	 * @param fin
	 * @return
	 * @throws IOException String
	 * May 10, 2011
	 * ashish.gupta
	 */
	public String getEntity(RandomAccessFile fin)throws IOException{
		StringBuffer tag=new StringBuffer();
		long pos=0;
		if(fin!=null){
			pos=fin.getFilePointer();
			char ch;
			int i=0;
			tag.append("&");
			while((i=fin.read())!=';'){
				ch=(char)i;
				if(ch=='\r' || ch=='\n' || ch=='<' || ch=='>' || ch==' ')
					break;
				else if(i==-1){
					break;
				}
				else
					tag.append(ch);
			}
			ch=(char)i;
			if(ch==';')
				tag.append(ch);
			else{
				tag=new StringBuffer();
				tag.append("##EXCEPTION##");
				fin.seek(pos);
			}
		}
		return tag.toString();
	}//end of getEntity()

	/**
	 * @param fin
	 * @return
	 * @throws IOException String
	 * May 10, 2011
	 * ashish.gupta
	 */
	public String getEntity(BufferedReader fin)throws IOException{
		StringBuffer tag=new StringBuffer();
		if(fin!=null){
			char ch;
			int i=0;
			tag.append("&");
			while((i=fin.read())!=';'){
				ch=(char)i;
				if(ch=='\r' || ch=='\n' || ch=='<' || ch=='>' || ch==' ')
					break;
				else if(i==-1){
					break;
				}
				else
					tag.append(ch);
			}
			ch=(char)i;
			if(ch==';')
				tag.append(ch);
			else{
				tag=new StringBuffer();
				tag.append("##EXCEPTION##");
			}
		}
		return tag.toString();
	}//end of getEntity()

	/**
	 * @param fin
	 * @return
	 * @throws IOException String
	 * May 10, 2011
	 * ashish.gupta
	 */
	public String getTag(RandomAccessFile fin)throws IOException{
		StringBuffer tag=new StringBuffer();
		long pos=0;
		if(fin!=null){
			pos=fin.getFilePointer();
			char ch;
			int i=0;
			tag.append("<");
			while((i=fin.read())!='>'){
				ch=(char)i;
				if(ch=='\r' || ch=='\n' || ch=='<')
					break;
				else if(i==-1){
					break;
				}
				else
					tag.append(ch);
			}
			ch=(char)i;
			if(tag.toString().startsWith("<!--")){
				tag.append(ch);
				if(!tag.toString().endsWith("-->")){
					i=0;
					while((i=fin.read())!=-1){
						tag.append((char)i);
						if(tag.toString().endsWith("-->"))
							break;
					}
				}			
			}else if(ch=='>')
				tag.append(ch);
			else{
				tag=new StringBuffer();
				tag.append("##EXCEPTION##");
				fin.seek(pos);
			}
		}
		return tag.toString();
	}//end of getTag()
	/**
	 * @param fin
	 * @return
	 * @throws IOException String
	 * May 10, 2011
	 * ashish.gupta
	 */
	public String getTag(BufferedReader fin)throws IOException{
		StringBuffer tag=new StringBuffer();
		if(fin!=null){
			char ch;
			int i=0;
			tag.append("<");
			while((i=fin.read())!='>'){
				ch=(char)i;
				if(ch=='\r' || ch=='\n' || ch=='<')
					break;
				else if(i==-1){
					break;
				}
				else
					tag.append(ch);
			}
			ch=(char)i;
			if(tag.toString().startsWith("<!--")){
				tag.append(ch);
				if(!tag.toString().endsWith("-->")){
					i=0;
					while((i=fin.read())!=-1){
						tag.append((char)i);
						if(tag.toString().endsWith("-->"))
							break;
					}
				}			
			}else if(ch=='>')
				tag.append(ch);
			else{
				tag=new StringBuffer();
				tag.append("##EXCEPTION##");
			}
		}
		return tag.toString();
	}//end of getTag()

	/**
	 * @param str
	 * @param tag
	 * @return String
	 * May 10, 2011
	 * ashish.gupta
	 */
	public String getElementValue(String str, String tag){
		String val="";
		if(str!=null && tag!=null && str.indexOf(tag)!=-1){
			str=str.substring(str.indexOf(tag));
			str=str.substring(str.indexOf(">")+1);
			val=str.substring(0, str.indexOf("</"+tag.substring(1)));
		}
		return val;
	}//end of getElementValue()

	/**
	 * @param str
	 * @param tag
	 * @return ArrayList<String>
	 * May 10, 2011
	 * ashish.gupta
	 */
	/*public ArrayList<String> getElementValues(String str, String tag){
		ArrayList<String> val=new ArrayList<String>();
		if(str!=null && tag!=null){
			while(str.indexOf(tag)!=-1){
				val.add(getElementValue(str, tag));
				str=str.substring(str.indexOf("</"+tag.substring(1)) + tag.length());
			}
		}else{
			val=null;
		}
		return val;
	}//end of getElementValues()
*/
	public Hashtable getHashtableFromString(String str)
	{
		Hashtable ht=new Hashtable();
		String [] row=str.split("&");

		for(int i=0;i<row.length;i++)
		{
			String [] column=row[i].split("=");

			if(column.length == 2)
				ht.put(column[0],column[1]);
			else
				ht.put(row[i],"null");
		}
		return ht;
	}

	public String getHashToString(Hashtable hash) {
		Vector keyVect = new Vector ();
		String msg = "";

		for (Enumeration enumKey = hash.keys() ; enumKey.hasMoreElements() ;) {
			keyVect.addElement(enumKey.nextElement());
		}
		for ( int i=0; i<keyVect.size(); i++) {
			msg += keyVect.elementAt (i).toString()+"=";
			msg += hash.get(keyVect.elementAt (i).toString()) +"&";
		}
		//msg = msg.substring(0,msg.lastIndexOf("&"));
		return msg;
	}

	public Vector getStringToken(String parseStr,String dim) {
		retVect.clear();

		//Condition for check the delimiter at end.
		//If condition is satisfied then neglect end delimiter
		if (parseStr.endsWith(dim)) {
			parseStr = parseStr.substring(0,parseStr.lastIndexOf(dim));
			st = new StringTokenizer(parseStr,dim);
			while (st.hasMoreTokens()) {
				retVect.addElement(st.nextToken());
			}//close while

		}//close if
		else {
			st = new StringTokenizer(parseStr,dim);
			while (st.hasMoreTokens()) {
				retVect.addElement(st.nextToken());
			}//close while
		}
		return retVect;
	}//closing getStringToken method


}//end of class
