package com.zjft.shepherd.key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Node  {
	private String name;
	private String absName;
	private Node parent;
	private String value;
	private String description;
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private HashMap<String, Node> childNodes = new HashMap<String, Node>();
	private List<Node> childList = new ArrayList<Node>();

	public Node(){
	}
    
	public Node(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setAttribute(String name, String value) {
		this.attributes.put(name, value);
	}

	public String getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return this.description;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return this.parent;
	}
	
	public void addChild(Node childNode) {
		this.childNodes.put(childNode.getName(), childNode);
		childNode.setParent(this);
	}

	public void addDuplicateChild(Node childNode) {
		this.childList.add(childNode);
		childNode.setParent(this);
	}
	
	public void addChild(HashMap<String,Node> fastDoc,Node childNode) {
		String absNamePath = this.getAbsName()+"."+childNode.getName();
		
		childNode.setAbsName(absNamePath);
		
		fastDoc.put(absNamePath, childNode);
		addChild(childNode);
	}
	
	public String asXML(boolean uniqueMode) {
		StringBuffer xml = new StringBuffer();
		if ((this.getDescription() != null)
				&& (this.getDescription().length() > 0))
			xml.append("<!--" + this.getDescription() + "--> ");
		xml.append("<");
		xml.append(this.getName());
		Iterator<String> keys = this.attributes.keySet().iterator();
		String key = "";
		String value = "";
		while (keys.hasNext()) {
			key = keys.next();
			value = this.attributes.get(key);
			xml.append(" " + key + "=\"" + value+"\"");
		}
		
		if(uniqueMode){
			if (((this.getValue() == null) || (this.getValue().length() == 0))
					&& (this.childNodes.size() == 0)) {
				xml.append("/>");
			} else {
				xml.append(">");
				if ((this.getValue() != null) && (this.getValue().length() > 0)) {
					xml.append(this.getValue());
				}
				Iterator<String> it = this.childNodes.keySet().iterator();
				while (it.hasNext()) {
					key = it.next();
					xml.append(((Node) this.childNodes.get(key)).asXML(uniqueMode));
				}

				xml.append("</" + this.getName() + ">");
			}
		}else{
			if (((this.getValue() == null) || (this.getValue().length() == 0))
					&& (this.childList.size() == 0)) {
				xml.append("/>");
			} else {
				xml.append(">");
				if ((this.getValue() != null) && (this.getValue().length() > 0)) {
					xml.append(this.getValue());
				}
				for(int i=0;i<this.childList.size();i++){
					xml.append(((Node) this.childList.get(i)).asXML(uniqueMode));
				}
				xml.append("</" + this.getName() + ">");
			}
		}
		return xml.toString();
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public HashMap<String, Node> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(HashMap<String, Node> childNodes) {
		this.childNodes = childNodes;
	}
	public String getAbsName() {
		return absName;
	}
	public void setAbsName(String absName) {
		this.absName = absName;
	}

	public List<Node> getChildList() {
		return childList;
	}

	public void setChildList(List<Node> childList) {
		this.childList = childList;
	}
	
	public List<Node> findChilds(String name){
		List<Node> list = new ArrayList<Node>();
		Node aNode = null;
		for(int i=0;i<this.childList.size();i++){
			aNode = childList.get(i);
			if(aNode.getName().equals(name)){
				list.add(aNode);
			}
		}
		return list;
	}
}
