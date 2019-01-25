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
     * �����ַ���
     * @param arg0 Ҫ������ַ���
     * @return ��arg0Ϊ��(null)�򷵻�"",���򷵻�arg0��ȥǰ��ո�֮���ֵ
     */
    
    public static String parseString(String arg0){
       	return arg0 == null ? "" : arg0.trim();
    }

    /**
     * �����ַ���
     * @param arg0 Ҫ����Ķ���
     * @return ��objΪ��(null)�򷵻�"",���򷵻�objת�����ַ����ҳ�ȥ���ַ�ǰ��ո�֮���ֵ
     */
    
    public static String parseString(Object arg0){
        return arg0 == null ? "" : arg0.toString().trim();
    }
    
	/**
	 * ���ַ���ת��Ϊint��������
	 * @return int ����
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
	 * ���ַ���ת��Ϊdouble�͡�
	 * @return double ������
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
	 * ����ת�뷽��
	 * @param arg0 Ҫת��������ַ���
	 * @return ת���������ַ���
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
	 * �����ַ�������
	 * @param arg0 ��Ҫ���˵��ַ���
	 * @param arg1 Ҫ���˵����ַ���
	 * @return ���ع��˺���ַ���
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
	 * ����֮ǰ��Ԥ����
	 * �������byte�������ݽ���zipѹ��������ǰ����ϳ��ȣ�ת�����µ�byte���鷵��
	 * @param byteArray ����Ĳ���
	 * @param zipId ѹ����ʽ 0����ѹ��,2����׼ZIPѹ��,3:gzipѹ��
	 * @return byte[]��byte����
	 */
	public static byte[] preProcess(byte[] byteArray,int zipId)
	{
		if(byteArray==null)
		{
			throw new IllegalArgumentException("preProcess �������Ϊ��");
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
		
		System.arraycopy(byteZip,0,byteReturn,8,zipLength);  //ѹ���������
				
		return byteReturn;
		
	}	
	/**
	 * ��HashMapת���ɴ�","�ָ��string List
	 * 
	 * @param hm  Ҫת����HashMap
	 * @param ifkey  �Ƿ�ת��key:	1ת��key,0ת��value
	 * @return ת������ַ���
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
			listBuff.deleteCharAt(listBuff.length()-1);	//ȥ�����һ��'��
		}
		//System.out.println("list="+listBuff.toString());
		return listBuff.toString();
	}
	
	/**
	 * �ַ�ת��ISO-8859-1 To GBK
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
	 * ����"\"
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
	 * ����"\"
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
	 * �ַ����滻,�滻���е�����
	 * content ���滻�ַ���(String replace)
	 * rep1 ԭ�ַ���
	 * rep2 �滻����ַ���
	 * return String NULL ��ʾ�滻ʧ��
	 * */
	public static String replaceAllForUnix(String content, String rep1, String rep2)
    {
		try
		{            
            String newStr=content; //�滻����ַ���
            
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
	 * �Ƿ�Ϊ���ж�
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

		System.out.println(StringUtil.replaceAllForUnix("����CASE����֪ͨ���豸���:${aevNo},��������:${aevNo},��������:${catalogNo},�豸ģ��:${devModule},case��ǰ״̬:${status},���ϴ���:${faultCode},���̹�����:${vendorCode},case����:${description},�豸Ʒ��:${devVendor},�豸�ͺ�:${devType},�豸��ַ:${address},case���:${caseNo},����ʱ��:${openTime},Ԥ�ڵ���ʱ��:${preOnsiteTime},Ԥ�ڹر�ʱ��:${preCloseTime},ʵ�ʹر�ʱ��:${useCloseTime}��","${adevNo}","test"));
	}	

}