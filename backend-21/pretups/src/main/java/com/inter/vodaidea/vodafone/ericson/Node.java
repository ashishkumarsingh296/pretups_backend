/**
 * @FileName: Node.java
 * @Copyright: All Rights Reserved for Comviva Tech Ltd. @2011 
 * @Comments: It sends the request to IN
 * @Comments: It applies the business rules, Like: retry to all the CS4 nodes, suspend and unsuspend the CS4 Node IP
 * @Comments: validate the response code for VAL response, for 100 response retry to the next node
 */
package com.inter.vodaidea.vodafone.ericson;
import java.util.ArrayList;
import java.util.Hashtable;
public class Node {
	public boolean HasAttribute;
	public boolean HasChild;
	private Hashtable<String, String> attList;
	private ArrayList<Node> nodeList;
	private String nodeName;
	private String nodeValue;
	public Node(){
		HasAttribute=false;
		HasChild=false;
		attList=null;
		nodeList=null;
		nodeName="";
		nodeValue="";
	}
	public void setNodeValue(String name){
		if(name!=null)
			nodeValue=name;
	}
	public String getNodeValue(){
		if(nodeValue!=null)
			return nodeValue;
		else
			return "";
	}
	public void setNodeName(String name){
		if(name!=null)
			nodeName=name;
	}
	public String getNodeName(){
		if(nodeName!=null)
			return nodeName;
		else
			return "";
	}
	public void setAttributeList(Hashtable<String, String> table){
		attList=table;
	}
	public Hashtable<String, String> getAttributeList(){
		return attList;
	}
	public void setChildNodeList(ArrayList<Node> table){
		nodeList=table;
	}
	public ArrayList<Node> getChildNodeList(){
		return nodeList;
	}
}//end class Node
