/**
 * ProperFile.java
 *
 * 功  能： 读取property配置文件
 * 类  名： ProperFile
 *
 *   version date        author     content
 * ──────────────────────────────────────────────────────────────
 *   V1.0.0   10-02-05   admin      初版
 *   V1.0.1   10-02-05   hk         修改不规范内容，新增读取jar包内配置文件功能         
*/
package com.zjft.shepherd.property;

import java.io.*;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 本类功能为读取property配置文件
 * @author admin
 * @version  v1.0.1 2010-02-05 修订
 */

public class ProperFile
{
    private Properties info = null;
    private File nf = null;
    private InputStream in = null;
    private String local = null;
    private static Log log = LogFactory.getLog(ProperFile.class);
    
    public ProperFile()
    {  
        try
        {
            info = new Properties(); 
        	JarFile jarFile = new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getFile().toString());
            JarEntry entry = jarFile.getJarEntry("ATTR_en_US.properties");
           	in = jarFile.getInputStream(entry);
            info.load(in);
        }
        catch(Exception e)
        {
            log.error("init ProperFile error "+e.getMessage());
        }
    }
    
    public ProperFile(String fStr) throws FileNotFoundException
    {
        info = new Properties();
        InputStream in = new FileInputStream(fStr);
        
        try
        {
            info.load(in);
        }
        catch(Exception e)
        {
            log.error("init ProperFile error "+e.getMessage());
        }
    }
    
    public ProperFile(String fStr, String local) throws FileNotFoundException
    {
        try
        {
        	this.local = local;
            info = new Properties();
            in = new FileInputStream(fStr);
            info.load(in);
        }
        catch(Exception e)
        {
            log.error("init ProperFile error "+e.getMessage());
        }
    }

    public void setProper(String PName, String PValue, String PStr)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(nf);
            info.setProperty(PName, PValue);
            info.store(fos, PStr);
            fos.close();
        }
        catch(Exception e)
        {
        	log.error(e.getMessage());
        }
    }

    public String getProper(String pName)
    {
    	String pValue = info.getProperty(pName, "");
    	if(local == null || pValue.equals(""))
    	{
    		return pValue;
    	}
    	else if(local.contains("zh_CN"))
    	{
    		try 
    		{
				return new String(pValue.getBytes("ISO-8859-1"),"GBK");
			}
    		catch (UnsupportedEncodingException e) 
    		{
				e.printStackTrace();
			} 
    	}
    	else if(local.contains("zh_TW"))
    	{
    		try 
    		{
				return new String(pValue.getBytes("ISO-8859-1"),"GB18030");
			} 
    		catch (UnsupportedEncodingException e)
    		{
				e.printStackTrace();
			} 
    	}
        return pValue;
    }

    public void close()
    {
        try
        {
            if(in != null)
                in.close();
        }
        catch(Exception e)
        {
        	log.error(e.getMessage());
        }
    }

    public static void main(String args[])
    {

    }
}
