package com.zjft.shepherd.key;

import java.util.HashMap;
import java.util.StringTokenizer;

public class Document {
	private boolean uniqueMode = true;//默认唯一节点模式 
	private Node root = null;
	private Node currentNode = null;
	private int offset = 0;
	private String xml = "";
	private int xmlLength = 0;
	private String version = "1.1";
	private String encoding = "UTF-8";

	private HashMap<String, Node> fastDom = new HashMap<String, Node>();
	
	public Document(String xml) {
		this.xml = xml;
		this.xmlLength = xml.length();
	}
	
	public Document() {
	}

	/**
	 * 获取下个字符
	 * 
	 * @return
	 */
	public char getNextCharacter() {
		char rt = xml.charAt(offset);
		offset++;
		return rt;

	}

	/**
	 * 判断下一字符是否为指定字符token
	 * 
	 * @param token
	 * @return
	 */
	private boolean match(char token) {
		for (int i = offset; i < this.xmlLength; i++) {
			char tc = xml.charAt(i);
			if (tc != ' ') {
				if (tc == token) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	private String getDescription() {
		skipSpace();
		StringBuffer desc = new StringBuffer();
		while (offset < this.xmlLength - 2) {
			char tc1 = this.getNextCharacter();
			if (tc1 == '-') {
				if ((xml.charAt(offset) == '-')
						&& (xml.charAt(offset + 1) == '>')) {
					offset += 2;
					return desc.toString();
				}
			} else {
				desc.append(tc1);
			}
		}
		return null;

	}

	/**
	 * 获取Node名称
	 * 
	 * @return
	 */
	private String getNodeName() {
		skipSpace();
		char[] name = new char[120];// 
		int i = 0;
		while (i < 120) {
			char tc = getNextCharacter();

			if ((tc == ' ') || (tc == '>') || (tc == '/')) {
				if (i > 0)
					return new String(name).trim();
			} else {
				name[i] = tc;
				i++;
				if (i > 120) {
					System.err.println("NODE NAME长度只能小于120");
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取属性信息
	 * 
	 */
	private void getAttributes() {
		skipSpace();
		StringBuffer name = new StringBuffer();
		StringBuffer value = new StringBuffer();
		boolean isAttribute = false;
		while (offset < this.xmlLength) {
			char tc1 = this.getNextCharacter();
			if (tc1 == '=') {
				skipSpace();
				char tc2 = this.getNextCharacter();
				if (tc2 == '"') {// 获取属性值
					isAttribute = true;
					while (offset < this.xmlLength) {
						char tc3 = this.getNextCharacter();
						if (tc3 == '"') {
							this.currentNode.setAttribute(name.toString(),
									value.toString());
							this.skipSpace();
							value.delete(0, value.length());
							name.delete(0, name.length());
							break;
						} else
							value.append(tc3);
					}
				}
			} else if (tc1 == '/') {
				skipSpace();
				char tc2 = this.getNextCharacter();
				if (tc2 != '>') {
					System.err.println("/必须使用>来封闭");
				} else {
					this.currentNode = this.currentNode.getParent();
					//System.out.println("11Node Name=["+currentNode.getName()+"] value=["+currentNode.getValue()+"]");
					break;
				}
			} else if (tc1 == '>') {
				break;
			} else {
				name.append(tc1);
			}
		}
	}

	private int skipSpace() {
		int skipCount = 0;
		while (offset < xml.length()) {
			char tc = xml.charAt(offset);
			if ((tc != ' ') && (tc != '\r') && (tc != '\n')) {
				return skipCount;
			} else {
				offset++;
				skipCount++;
			}
		}
		return skipCount;
	}

	private String getValue() {
		StringBuffer value = new StringBuffer();
		value.append(xml.charAt(offset - 1));
		if (xml.charAt(offset) == '<') {
			return value.toString().trim();
		}
		while (offset < xml.length()) {
			char tc = this.getNextCharacter();
			value.append(tc);
			if (xml.charAt(offset) == '<') {
				return value.toString().trim();
			}
		}
		return null;
	}

	private void skipBOM() {
		try{
			byte[] buf = xml.getBytes("utf-8");		
			if (buf.length >= 3) {
				if(buf[0] == (byte)0xEF && buf[1] == (byte)0xBB && buf[2] == (byte)0xBF){
					byte[] aBuf = new byte[buf.length-3];
					System.arraycopy(buf, 3, aBuf, 0, buf.length-3);				
//					System.out.println(aBuf[0]&0xFF);//60
//					System.out.println(aBuf[1]&0xFF);//63
//					System.out.println(aBuf[2]&0xFF);//120
					this.xml = new String(aBuf,"utf-8");
					this.xmlLength = xml.length();
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void getXMLHeader() {
		this.skipBOM();
		this.skipSpace();
		if ((this.xml.charAt(offset) == '<')
				&& (this.xml.charAt(offset + 1) == '?')) {
			int idx = this.xml.indexOf("version");
			if (idx > 0) {
				boolean start = false;
				StringBuffer tmp = new StringBuffer();
				for (int i = idx + 8; i < this.xmlLength; i++) {
					char tc = this.xml.charAt(i);
					if (tc == '"') {
						if (start == false) {
							start = true;
						} else {
							break;
						}
					} else {
						if (start)
							tmp.append(tc);
					}
				}
				this.version = tmp.toString();
			}
			idx = this.xml.indexOf("encoding");
			if (idx > 0) {
				boolean start = false;
				StringBuffer tmp = new StringBuffer();
				for (int i = idx + 9; i < this.xmlLength; i++) {
					char tc = this.xml.charAt(i);
					if (tc == '"') {
						if (start == false) {
							start = true;
						} else {
							break;
						}
					} else {
						if (start)
							tmp.append(tc);
					}
				}
				this.encoding = tmp.toString();
			}
			int end = this.xml.indexOf("?>");
			offset = end + 2;
		}
	}

	public Node parse(String xml) {
		return parse(xml,true);
	}
	
	public Node parse(String xml,boolean uniqueMode) {
		root = null;
		currentNode = null;
		offset = 0;
		fastDom.clear();
		
		this.xml = xml;
		this.uniqueMode = uniqueMode;
		if(this.xml == null){
			return null;
		}else{
			this.xmlLength = xml.length();
			if(this.xmlLength == 0) {
				return null;
			}
		}

		String name = "";
		String absName = "" ;
		Node newNode = null;
		
		getXMLHeader();
		while (offset < this.xmlLength) {
			this.skipSpace();
			char token = getNextCharacter();
			if (token == '<') {
				if (match('!')) {
					getNextCharacter();
					char tc = getNextCharacter();
					if (tc == '-') {
						tc = getNextCharacter();
						if (tc == '-') {
							String desc = getDescription();
							if (this.currentNode != null)
								this.currentNode.setDescription(desc);
						} else {
							System.err.println("语法错误在" + offset);
							return null;
						}
					}
				} else if (match('/')) {

					//String nodeName = this.getNodeName();
					//System.out.println(currentNode.getName().substring(currentNode.getName().lastIndexOf(".")+1));
					if (currentNode.getName().equalsIgnoreCase(this.getNodeName()))
						currentNode = currentNode.getParent();
					else {
						System.err.println("expected tag:"
								+ currentNode.getName() + ",in fact tag is :"
								+ this.getNodeName());
						return null;
					}
				} else {
					name = this.getNodeName();
					newNode = new Node();
					
					if (root == null) {
						root = newNode;
						newNode.setName(name);
						newNode.setAbsName(name);
						currentNode = root;
						fastDom.put(name, root);
					} else {
						absName = currentNode.getAbsName()+"."+name;
						
						fastDom.put(absName, newNode);
						newNode.setName(name);
						newNode.setAbsName(absName);
						if(this.isUniqueMode()){
							currentNode.addChild(newNode);	
						}else{
							currentNode.addDuplicateChild(newNode);
						}
						currentNode = newNode;
					}
					char tc = this.xml.charAt(offset - 1);

					if (tc == ' ') {
						getAttributes();
					} else if (tc == '/') {
						tc = this.xml.charAt(offset);
						if (tc != '>') {
							System.err.println("tag:" + currentNode.getName()
									+ " not match");
						} else {
							offset = offset + 1;
							currentNode.setValue("");
							currentNode = currentNode.getParent();
					//	System.out.println("Node Name=["+currentNode.getName()+"] value=["+currentNode.getValue()+"]");
						}
					} else {
						if (tc != '>') {
							System.err.println("tag:" + currentNode.getName()
									+ " not match");
						}
					}
				}
			} else {
				currentNode.setValue(getValue());
				//System.out.println("Node Name=["+currentNode.getName()+"] value=["+currentNode.getValue()+"]");
			}
		}
		return root;
	}
    
	public HashMap<String, Node> getFastDom() {
		return fastDom;
	}

	public void setFastDom(HashMap<String, Node> fastDom) {
		this.fastDom = fastDom;
	}
	
	/**
	 * 根据路径查询节点，如果没有返回null；
	 * 存在返回该节点
	 * @param path
	 * @return
	 */
	public Node qryAndCheckFastNode(String path){
		return this.getFastDom().get(path);
	}
	
	/**
	 * 根据路径查询节点，如果没有就自动创建节点，并返回该节点；
	 * 存在返回该节点
	 * @param path
	 * @return
	 */
	public Node qryFastNode(String path){
		Node currentNode = this.getFastDom().get(path);
		if(currentNode != null){
			return currentNode;
		}else{
			StringTokenizer st = new StringTokenizer( path, "." );
			String nodeName = "";
			String nodePath = "";
			int i = -1;
			Node childNode;
			while ( st.hasMoreTokens() ) {
				nodeName = st.nextToken();
				
				i++;
				if(i == 0){//创建根节点
					nodePath = nodeName;
					currentNode = this.getFastDom().get(nodeName);
					if(currentNode != null){
						root = currentNode;
						continue;
					}else{
						currentNode = new Node();
						currentNode.setName(nodeName);
						
						currentNode.setAbsName(nodeName);
						fastDom.put(nodeName, currentNode);
						root = currentNode;
					}
				}else{ 
					nodePath = nodePath + "." + nodeName;
				
					childNode = this.getFastDom().get(nodePath);
					if(childNode != null){
						currentNode = childNode;
						continue;
					}else{
						childNode = new Node();
						childNode.setName(nodeName);
						childNode.setValue("");
						
						this.addFastNode(currentNode, childNode);
						currentNode = childNode;
					}
				}
			}
			return currentNode;
		}
	}
	
	public void addFastNode(Node aCurrentNode,Node childNode){
		aCurrentNode.addChild(this.getFastDom(),childNode);
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	public String asXML(){
		if(this.root == null) {
			return "";
		}else{
			return this.root.asXML(this.isUniqueMode());	
		}
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public int getXmlLength() {
		return xmlLength;
	}

	public void setXmlLength(int xmlLength) {
		this.xmlLength = xmlLength;
	}

	public boolean isUniqueMode() {
		return uniqueMode;
	}

	public void setUniqueMode(boolean uniqueMode) {
		this.uniqueMode = uniqueMode;
	}

}