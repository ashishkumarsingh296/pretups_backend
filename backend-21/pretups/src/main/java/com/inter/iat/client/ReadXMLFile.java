package com.inter.iat.client;

import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ReadXMLFile {
    public static void main(String[] args) {
        File file = new File("C:/Documents and Settings/babu.kunwar/My Documents/RechargeRequestResponse.xml");
        String xml = "xmlns:java=" + "\"" + "java:com.wha.iah.pretups.ws" + "\"";
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList nodes = doc.getElementsByTagName("m:rechargeRequestResponse");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                String name = element.getAttribute("http://com/wha/iah/pretups/ws");
                if (!name.equals(xml))
                    continue;
                String message = "From " + " tag: " + "This is processed info. on lines";
                NodeList lines = element.getElementsByTagName("java:IatTrxId");
                for (int j = 0; j < lines.getLength(); j++) {
                    Element line = (Element) lines.item(j);
                    if (j > 0)
                        message += ",";
                    message += " ";
                    StringBuffer sb = new StringBuffer();
                    for (Node child = line.getFirstChild(); child != null; child = child.getNextSibling()) {
                        if (child instanceof CharacterData) {
                            CharacterData cd = (CharacterData) child;
                            sb.append(cd.getData());
                        }
                    }
                    String text = sb.toString().trim();
                    message += text;
                }
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
