package com.zjft.shepherd.key;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.List;

public class ProxoolUtil {
	private static String key = "0102030405060708";
	private static DESEncryptArith des= new DESEncryptArith();

	/**
	 * 加密数据
	 * @param data
	 * @param key
	 * @return 两位数据长度+加密后的数据
	 */
	public static String encryptData(String data){
		int len = data.getBytes().length;
		byte[] tmp = new byte[(len/8+1)*8];
		System.arraycopy(data.getBytes(), 0, tmp, 0, len);
		try{
			byte[] result = des.doEncrypt(tmp, BinaryTransfer.ascToBin(key));
			return String.format("%02d", len)+ BinaryTransfer.binToAsc(result);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 解密数据
	 * @param data
	 * @param key
	 * @return 解密后的数据
	 */
	public static String dncryptData(String data){
		int len = Integer.parseInt(data.substring(0,2));
		try{
			byte[] result = des.doDecrypt(BinaryTransfer.ascToBin(data.substring(2)), BinaryTransfer.ascToBin(key));
			return new String(result,0,len);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 获取数据
	 * @param file
	 * @return 获取当前文件中的数据
	 */
	public static String[] getData(String file){
		String[] result = new String[2];
		Document doc = FastXMLUtil.loadXMLs( file );
		Node root = doc.getRoot();		
		List<Node> nodeList = root.findChilds("proxool").get(0).getChildList();
		
		for(int i=0; i<nodeList.size(); i++ ){
			Node node = nodeList.get(i);
			if(node.getName().equals("driver-properties")){
				List<Node> properties = node.findChilds("property");
				for(int j=0;j<properties.size();j++){
					Node property = properties.get(j);
					if(property.getAttribute("name").equals("user")){
						result[0] = property.getAttribute("value");
					}else if(property.getAttribute("name").equals("password")){
						result[1] = property.getAttribute("value");
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 替换当前文件中的数据
	 * @param file
	 * @param username
	 * @param pwd
	 * @return 
	 */
	public static void replaceFileData(String file,String username, String pwd) throws Exception{
		Document doc = FastXMLUtil.loadXMLs( file );
		Node root = doc.getRoot();		
		List<Node> nodeList = root.findChilds("proxool").get(0).getChildList();
		
		for(int i=0; i<nodeList.size(); i++ ){
			Node node = nodeList.get(i);
			if(node.getName().equals("driver-properties")){
				List<Node> properties = node.findChilds("property");
				for(int j=0;j<properties.size();j++){
					Node property = properties.get(j);
					if(property.getAttribute("name").equals("user")){
						property.setAttribute("value", username);
					}else if(property.getAttribute("name").equals("password")){
						property.setAttribute("value", pwd);
					}
				}
			}
		}
		
		DataOutputStream out = null;
		try{
			out = new DataOutputStream(new FileOutputStream(file,false));
			String outString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+doc.asXML();
			out.writeBytes(outString);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Save To File Error!");
		}finally{
			out.close();
		}
	}
	
	/**
	 * 替换数据
	 * @param file
	 * @return 替换当前Document中的数据
	 */
	public static String replaceDocData(String file,String username, String pwd) throws Exception{
		Document doc = FastXMLUtil.loadXMLs( file );
		Node root = doc.getRoot();		
		List<Node> nodeList = root.findChilds("proxool").get(0).getChildList();
		
		for(int i=0; i<nodeList.size(); i++ ){
			Node node = nodeList.get(i);
			if(node.getName().equals("driver-properties")){
				List<Node> properties = node.findChilds("property");
				for(int j=0;j<properties.size();j++){
					Node property = properties.get(j);
					if(property.getAttribute("name").equals("user")){
						property.setAttribute("value", username);
					}else if(property.getAttribute("name").equals("password")){
						property.setAttribute("value", pwd);
					}
				}
			}
		}
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+doc.asXML();
	}
	
}
