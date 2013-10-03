package com.volarvideo.demoapp.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**XML parsing helper
 * 
 * @author Chris Allen on Mar 21, 2013
 */
public class XMLParser {

	public static Element getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
 
        } catch (ParserConfigurationException e) {
        	e.printStackTrace();
        } catch (SAXException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        if(doc != null)
        	return doc.getDocumentElement();
        return null;
	}
	
	public static List<VVNode> getChildNodes(Element p) {
		List<VVNode> els = new ArrayList<VVNode>();
		

		NodeList nodes = p.getChildNodes();
		for(int index=0; index < nodes.getLength(); index++) {
			if(nodes.item(index).getNodeType() == Node.ELEMENT_NODE)
				els.add(new VVNode(nodes.item(index)));
		}
		
		return els;
	}
	
	public static String localName(Node n) {
		String[] split = n.getNodeName().split(":");
		return split[split.length-1];
	}
	
	public static String getValue(Element e) {
		Node child;
		if( e != null){
			if (e.hasChildNodes()){
				for( child = e.getFirstChild(); child != null; child = child.getNextSibling() ){
					if( child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE){
						String value = child.getNodeValue();
						if(value == null) value = "";
						value = value.trim();
						if(!VVUtils.isEmpty(value))
							return value;
					}
				}
			}
		}
		return "";
	}
	
}
