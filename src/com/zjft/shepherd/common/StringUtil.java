package com.zjft.shepherd.common;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author shp
 * @since 2005.12.12
 */


public final class StringUtil {

       
    /**
     * 处理字符串
     * @param arg0 要处理的字符串
     * @return 若arg0为空(null)则返回"",否则返回arg0除去前后空格之后的值
     */
    
    public static String parseString(String arg0){
       	return arg0 == null ? "" : arg0.trim();
    }

    /**
     * 处理字符串
     * @param arg0 要处理的对象
     * @return 若obj为空(null)则返回"",否则返回obj转换成字符串且除去该字符前后空格之后的值
     */
    
    public static String parseString(Object arg0){
        return arg0 == null ? "" : arg0.toString().trim();
    }
    
	/**
	 * 将字符串转换为int型整数。
	 * @return int 整数
	 */
	public static int ch2Int(String str){
	    try{
		    return(Integer.parseInt(str));
	    }
	    catch(NumberFormatException e){
		    return(-1);
	    }
	}    
	/**
	 * 将字符串转换为double型。
	 * @return double 长整型
	 */
	public static double ch2Double(String str){
	    try{
		    return(Double.parseDouble(str));
	    }
	    catch(NumberFormatException e){
		    return(-1);
	    }
	}    
	/**
	 * 中文转码方法
	 * @param arg0 要转换编码的字符串
	 * @return 转换编码后的字符串
	 */
	
	public static String toGBK(String arg0){

		try{			
			arg0 = (arg0 == null) ? "" : new String(arg0.getBytes("ISO-8859-1"),"GBK");
		}
		catch(UnsupportedEncodingException e){
            System.out.println(e);
		}					
		return arg0;
	}
	
	/**
	 * 过滤字符串方法
	 * @param arg0 需要过滤的字符串
	 * @param arg1 要过滤掉的字符串
	 * @return 返回过滤后的字符串
	 */
	public static String filterString(String arg0,String arg1){
		
		int pos;
		String str1 = StringUtil.parseString(arg0);
		String str2 = StringUtil.parseString(arg1);
		String str = str1;	
		
		while(str.indexOf(str2) >= 0){
			pos = str.indexOf(str2);
			str = str.substring(0,pos)+str.substring(pos+str2.length());
		}
		return str;
	}
	
	/**
	 * 发送之前的预处理
	 * 将传入的byte数组内容进行zip压缩，并在前面加上长度，转化成新的byte数组返回
	 * @param byteArray 传入的参数
	 * @param zipId 压缩方式 0：非压缩,2：标准ZIP压缩,3:gzip压缩
	 * @return byte[]：byte数组
	 */
	public static byte[] preProcess(byte[] byteArray,int zipId)
	{
		if(byteArray==null)
		{
			throw new IllegalArgumentException("preProcess 传入参数为空");
		}
		
		byte[] byteZip=null;
		
		try
		{
			if(zipId==2)
			{
				byteZip=ZipUtil.zip(byteArray);
			}
			else if(zipId==3)
			{
				byteZip=ZipUtil.gzip(byteArray);
			}
			else
			{
				byteZip=byteArray;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();			
			return null;
		}
		
		int zipLength=byteZip.length;
		int lastLength=zipLength+8;
		System.out.println("lastLength="+lastLength);
		byte[] byteReturn=new byte[lastLength];
		
		System.arraycopy(String.format("%04d",lastLength).getBytes(),0,byteReturn,0,4);
		
		if(zipId==2)
		{
			System.arraycopy(new String("T200").getBytes(),0,byteReturn,4,4);
		}
		else if (zipId==3)
		{
			System.arraycopy(new String("T300").getBytes(),0,byteReturn,4,4);
		}
		else
		{
			System.arraycopy(new String("T000").getBytes(),0,byteReturn,4,4);
		}
		
		System.arraycopy(byteZip,0,byteReturn,8,zipLength);  //压缩后的数据
				
		return byteReturn;
		
	}	
	/**
	 * 将HashMap转换成带","分割的string List
	 * 
	 * @param hm  要转换的HashMap
	 * @param ifkey  是否转换key:	1转换key,0转换value
	 * @return 转换后的字符串
	 */
	
	public static String hmtoList(HashMap hm,int ifkey){
		StringBuffer listBuff=new StringBuffer();
		int i=0;
		if( hm==null) {
			return null;
		}
		Iterator it=hm.keySet().iterator();
		while(it.hasNext()){
			if(ifkey==1){
				listBuff.append("'").append(it.next()).append("'").append(",");
			}else{
				listBuff.append("'").append(hm.get(it.next())).append("'").append(",");
			}
			
			i++;
		}
		if(i>0){
			listBuff.deleteCharAt(listBuff.length()-1);	//去掉最后一个'号
		}
		//System.out.println("list="+listBuff.toString());
		return listBuff.toString();
	}
	
	/**
	 * 字符转换ISO-8859-1 To GBK
	 * */
	public static String isoToGBK(String src)
	{
		try
		{ 
			if(src!=null)
			{
				src =new String(src.getBytes("iso-8859-1"),"gbk");
			}
		}
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        return src;
	}
	/**
	 * 编码"\"
	 * */
	public static String  escapea (String src)
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
	 * 编码"\"
	 * */
	public static String  escapeb (String src)
	{  		
		char ch;  
		StringBuffer tmpStr = new StringBuffer(); 
		
		for (int i=0;i<src.length() ;i++ )  
		{  
			ch = src.charAt(i); 
			if(ch=='/')
			{
				tmpStr.append("\\/").append(ch);
			}
			else
			{
				tmpStr.append(ch); 
			}			
		}  
		return tmpStr.toString();
	} 
	
	/**
	 * 字符串替换,替换所有的数据
	 * content 需替换字符串(String replace)
	 * rep1 原字符串
	 * rep2 替换后的字符串
	 * return String NULL 表示替换失败
	 * */
	public static String replaceAllForUnix(String content, String rep1, String rep2)
    {
		try
		{            
            String newStr=content; //替换后的字符串
            
            int index = content.lastIndexOf(rep1);  
            
            StringBuffer rep=new StringBuffer();
            
            while(index>=0)
            {            	
            	rep.append(newStr.substring(0,index));
            	
            	rep.append(rep2);
            	
            	rep.append(newStr.substring(index+rep1.length(),newStr.length()));
            	
            	newStr = rep.toString();            	
            	
            	index = (newStr).lastIndexOf(rep1);
            	
            	rep.delete(0, rep.length());
            }
            
            return newStr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * 是否为空判断
	 * @param id
	 * @return
	 */
	public static boolean isNullorEmpty(String id) {
        if(id == null || id.trim().equals("")) {
        	return true;
        }
        return false;
	}
	
	public static void main(String[] args){	
		
		String s=StringUtil.hmtoList(null, 1);
		System.out.println("s="+s);	   
		System.out.println(StringUtil.replaceAllForUnix(null,null,null));

		System.out.println(StringUtil.replaceAllForUnix("交行CASE生成通知：设备编号:${aevNo},所属机构:${aevNo},故障类型:${catalogNo},设备模块:${devModule},case当前状态:${status},故障代码:${faultCode},厂商故障码:${vendorCode},case描述:${description},设备品牌:${devVendor},设备型号:${devType},设备地址:${address},case编号:${caseNo},生成时间:${openTime},预期到场时间:${preOnsiteTime},预期关闭时间:${preCloseTime},实际关闭时长:${useCloseTime}。","${adevNo}","test"));
	}	

}