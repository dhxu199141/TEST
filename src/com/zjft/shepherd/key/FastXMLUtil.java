package com.zjft.shepherd.key;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class FastXMLUtil {
	   
	private static Document getDocument(){
		return new Document();
	}
	
	public static String convertDOMToText(Document dom) {	
		return dom.asXML();
	}
	
	public static Document convertTextToDOM( String xmlText ) {
		if (xmlText == null) {
			throw new NullPointerException("createDocument参数为null");
		}
		try {
			Document dom = getDocument();
			dom.parse(xmlText);
			return dom;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document convertTextToDOMs( String xmlText ) {
		if (xmlText == null) {
			throw new NullPointerException("createDocument参数为null");
		}
		try {
			Document dom = getDocument();
			dom.parse(xmlText,false);
			return dom;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document getEmptyDocument() {
		try {
			Document dom = getDocument();
			return dom;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document loadXMLs(String fileName){
		try {
			String xml = readFile(fileName);
			Document dom = getDocument();
			dom.parse(xml,false);
			return dom;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document loadXML(String fileName){
		try {
			String xml = readFile(fileName);
			Document dom = getDocument();
			dom.parse(xml);
			return dom;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String readFile(String fileName) {
		String output = "";

		File file = new File(fileName);

		if (file.exists()) {
			if (file.isFile()) {
				try {
					InputStreamReader read = new InputStreamReader (new FileInputStream(file),"UTF-8");
					BufferedReader reader=new BufferedReader(read);
//					BufferedReader reader = new BufferedReader(new FileReader(file));
					StringBuffer buffer = new StringBuffer();
					String text;

					while ((text = reader.readLine()) != null)
						buffer.append(text);

					output = buffer.toString();
				} catch (IOException ioException) {
					System.err.println("File Error!");

				}
			} else if (file.isDirectory()) {
				String[] dir = file.list();
				output += "Directory contents:\n";

				for (int i = 0; i < dir.length; i++) {
					output += dir[i] + "\n";
				}
			}
		} else {
			System.err.println("Does not exist!");
		}
		return output;
	}
}
