package com.zjft.shepherd.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Iterator;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * 此类包含一些常用的方法   
 * 
 * @author hsxu
 * @since 2008.4.18
 */


public class Tool {
	

	private static Log log = LogFactory.getLog(Tool.class);
    
	
	/**
	 * 中文转码方法
	 * 
	 * @param str - 要转换编码的字符串
	 * @return 转换编码后的字符串
	 */
	
	public static String toGBK(String str){

		try{			
			str = (str == null) ? "" : new String(str.getBytes("ISO-8859-1"),"ZHS16GBK");
		}
		catch(UnsupportedEncodingException e){
			log.info("Tool::toGBK(String)运行时出错："+e);
		}					
		return str;
	}
	
	/**
	 * 文件名编码
	 * 
	 * @param str - 要编码的字符串
	 * @return 编码后的字符串
	 * @throws UnsupportedEncodingException 
	 */
	
	public static String fileEncoding(String str,String charSet){
		try{
			str = new String(str.getBytes("GBK"),charSet);	
		}catch(UnsupportedEncodingException e){
			log.info("Tool::fileEncoding(String)运行时出错："+e);
		}
						
		return str;
	}
	
	/**
	 * 解释字符串方法
	 * 
	 * @param str - 要解释的字符串
	 * @return str为null返回""；否则返回str除去前后空格之后的值
	 */
	
	
	public static String parseString(String str){
		return str == null ? "" : str.trim();
	}
	
	/**
	 * 获取系统时间方法(时：分：秒)
	 * 
	 * @return 返回系统当前时间字符串，字符串格式为：yyyy-mm-dd hh:mm:ss
	 */
	
	public static String getSysTimeYMDHMS(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 获取系统时间方法(时：分：秒)
	 * 
	 * @return 返回系统当前时间字符串，字符串格式为：yyyy-mm-dd hh:mm:ss
	 */
	
	public static String getSysTimeHMS(){
		return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
	}
	/**
	 * 获取系统时间方法(时：分)
	 * @return
	 */
	public static String getSysTimeYMDHM(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 获取系统时间方法
	 * 
	 * @return 返回系统当前时间字符串，字符串格式为：yyyy-mm-dd
	 */
	
	public static String getSysTimeYMD(){
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
	}
	

	/**
	 * 编码
	 * */
	public static String  escape (String src)
	{  		
		char ch;  
		StringBuffer tmpStr = new StringBuffer(); 
		
		for (int i=0;i<src.length() ;i++ )  
		{  
			ch = src.charAt(i); 
			if(ch=='\\')
			{
				tmpStr.append("\\").append(ch);
			}
			else
			{
				tmpStr.append(ch); 
			}			
		}  
		return tmpStr.toString();
	} 

	
	/**
	 *根据给定的字符串获得相应的文件或磁盘大小
	 */
	public static String getSize(String src) 
	{  
	   double temp=Double.parseDouble(src);					
	   double B=temp/1024;
	   double KB=B/1024;
	   double MB=KB/1024;
	   double GB=MB/1024;
	   double TB=GB/1024;
	   //NumberFormat.getNumberInstance().setMaximumFractionDigits(1);
	   if(B<1)
	   {							   
		   src=temp+" B";		   
	   }							   					   
	   else if(KB<1)
	   {							   
		   src=NumberFormat.getNumberInstance().format(B)+" KB";
	   }							   						   
	   else if(MB<1)
	   {							   
		   src=NumberFormat.getNumberInstance().format(KB)+" MB";
	   }	
	   else if(GB<1)
	   {							   
		   src=NumberFormat.getNumberInstance().format(MB)+" GB";
	   }
	   else if(TB<1)
	   {							   
		   src=NumberFormat.getNumberInstance().format(GB)+" TB";
	   }
	   return src;
	}
	
	/**
	 * MD5加密算法
	 * @param s - 要加密的字符串
	 * @return 加密过后的字符串
	 */
	
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
//	创建文件夹
//	param folderPath 文件夹完整绝对路径

	public static void createFolder(String folderPath) {
	     try {
	    	java.io.File myFilePath=new java.io.File(folderPath);
    		if(!myFilePath.exists())
    		{	
    			myFilePath.mkdirs();
    		}
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}
	
	
	
	
//	删除文件夹
//	param folderPath 文件夹完整绝对路径

	public static void delFolder(String folderPath) {
	     try {
	        delAllFile(folderPath); //删除完里面所有内容
	        String filePath = folderPath;
	        filePath = filePath.toString();
	        java.io.File myFilePath = new java.io.File(filePath);
	        myFilePath.delete(); //删除空文件夹
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}

//	删除指定文件夹下所有文件
//	param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
       boolean flag = false;
       File file = new File(path);
       if (!file.exists()) {
         return flag;
       }
       if (!file.isDirectory()) {
         return flag;
       }
       String[] tempList = file.list();
       File temp = null;
       for (int i = 0; i < tempList.length; i++) {
          if (path.endsWith(File.separator)) {
             temp = new File(path + tempList[i]);
          } else {
              temp = new File(path + File.separator + tempList[i]);
          }
          if (temp.isFile()) {
             temp.delete();
          }
          if (temp.isDirectory()) {
             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
             delFolder(path + "/" + tempList[i]);//再删除空文件夹
             flag = true;
          }
       }
       return flag;
     }
		
	//日期格式化 
		public static String formatDate(String date){
			if(date==null)
			{
				date="";
			}			
			int len=date.length();			
			if(len==8){
				return date;
			}else if(len>8){
				date=date.substring(0, 9);
				date=date.replaceAll("/", "-");
				date=date.replaceAll(":", "-");
				StringTokenizer t = new StringTokenizer(date," ");
				String time1=t.nextToken();//获取日期串
//				String time2=t.nextToken();//获取时间串
				StringTokenizer t1 = new StringTokenizer(time1,"-");
				String year=t1.nextToken();//获取年份
				if(year.length()==2){
					year="20"+year;
				}
				String month=t1.nextToken();//获取月份
				if(month.length()==1){
					month="0"+month;
				}
				String day=t1.nextToken();//获取日期
				if(day.length()==1){
					day="0"+day;
				}
				date=year+month+day;
//				StringTokenizer t2 = new StringTokenizer(time2,"-");
//				String hour=t2.nextToken();//获取小时
//				String minute=t2.nextToken();//获取分钟
//				String second=t2.nextToken();//获取秒数
//				
//				String time=hour+minute+second;
			}else if(len==6){
				date="20"+date;
			}
			return date;
			
		}
}
