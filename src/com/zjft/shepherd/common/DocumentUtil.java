/**
 * DOM²Ù×÷Àà
 */
package com.zjft.shepherd.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class DocumentUtil 
{
	private static Log log = LogFactory.getLog(DocumentUtil.class);
	private static DocumentUtil _instance = null;
	
	private DocumentUtil() 
	{
		
	}
	
	public static DocumentUtil getInstance(){
		if( _instance == null ){
			_instance = new DocumentUtil();
		}
		return _instance;
	}			
	
	
	public static Document getDocument( String file ) {
		Document doc = null;
		doc = loadDocument(file);
		return doc;
	}
	
	public static void saveToFile( String file, Document doc ) {
		
		doc.normalize();
		
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer serializer;
		try {
			serializer = tfactory.newTransformer();
			
			serializer.setOutputProperty(OutputKeys.METHOD, "xml");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			FileOutputStream fileStream = new FileOutputStream(file);
			serializer.transform(new DOMSource(doc), new StreamResult(
					fileStream));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public static String convertDOMToText(Document doc) {
		
		doc.normalize();
		
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer serializer;
		String strResult = "";
		try {
			serializer = tfactory.newTransformer();
			
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.METHOD, "xml");
			serializer.setOutputProperty(OutputKeys.INDENT, "no");
			StringWriter sw = new StringWriter();
			serializer.transform(new DOMSource(doc), 
					new StreamResult(sw));
			strResult = sw.toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return strResult;
	}
	
	public static Document getEmptyDocument() {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		Document newDocument = db.newDocument();  		
		
		return newDocument;
	}
	
	public static Document convertTextToDOM( String xmlText ) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try 
		{
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		Document doc = null;
		StringReader sr = new StringReader( xmlText );
		InputSource is = new InputSource( sr );
		try {
			doc = db.parse( is );
			doc.normalize(); 
		} catch (DOMException dom) {
			dom.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	
	private static Document loadDocument(String file) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			log.error(pce);
			pce.printStackTrace();
		}
		Document doc = null;
		try {
			doc = db.parse(file);
			doc.normalize(); 
		} catch (DOMException dom) {
			dom.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	public static Document createDocument() {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		
		return db.newDocument();
		
	}
	

	public static String getNodeContextByTagName(Document doc,String tagName){
		String value = null;
		Node item = getNodeByTagName(doc,tagName);
		if(item!=null){
			value = item.getTextContent();
		}
		return value;
	}

	public static Node getNodeByTagName(Document doc,String tagName){
		Node item = null;
		NodeList list = doc.getElementsByTagName(tagName);
		if(list!=null&&list.getLength()>0){
			for(int i=0;i<list.getLength();i++){
				item = list.item(i);
				if(item!=null&&item.getNodeType()==Node.ELEMENT_NODE){
					return item;
				}
			}
		}
		return null;
	}

	
	
}
