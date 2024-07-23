package com.inter.comverse;

import java.util.ArrayList;
import java.util.Hashtable;

public class Node {
    public boolean HasAttribute;
    public boolean HasChild;
    private Hashtable<String, String> attList;
    private ArrayList<Node> nodeList;
    private String nodeName;
    private String nodeValue;

    public Node() {
        HasAttribute = false;
        HasChild = false;
        attList = null;
        nodeList = null;
        nodeName = "";
        nodeValue = "";
    }

    public void setNodeValue(String name) {
        if (name != null)
            nodeValue = name;
    }

    public String getNodeValue() {
        if (nodeValue != null)
            return nodeValue;
        else
            return "";
    }

    public void setNodeName(String name) {
        if (name != null)
            nodeName = name;
    }

    public String getNodeName() {
        if (nodeName != null)
            return nodeName;
        else
            return "";
    }

    public void setAttributeList(Hashtable<String, String> table) {
        attList = table;
    }

    public Hashtable<String, String> getAttributeList() {
        return attList;
    }

    public void setChildNodeList(ArrayList<Node> table) {
        nodeList = table;
    }

    public ArrayList<Node> getChildNodeList() {
        return nodeList;
    }
}
