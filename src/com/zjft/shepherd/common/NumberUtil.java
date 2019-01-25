package com.zjft.shepherd.common;


import java.util.Locale;
import java.text.NumberFormat;
import java.text.DecimalFormat;


/**
 * @author shp
 * @since 2005.12.12
 */


public final class NumberUtil {

 
	
	/**
	 * 数字小数化（最多保留两位小数）
	 * @param arg0 - 双精度数
	 * @return 返回小数化之后的字符串
	 */
    
    public static String decimal(double arg0){
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(arg0);
    }
    
    /**
	 * 数字小数化（保留两位小数）
	 * @param arg0 - 双精度数
	 * @return 返回小数化之后的字符串
	 */
	
	public static String decimal2(double arg0){
		return new DecimalFormat("#,##0.00").format(arg0);
	}
    
	/**
	 * 数字人民币化（保留两位小数）
	 * @param arg0 - 双精度数
	 * @return 返回人民币化之后的字符串
	 */
    
    public static String RMB(double arg0){
        NumberFormat df = NumberFormat.getCurrencyInstance(Locale.CHINA);
	    return df.format(arg0);
    }
     
    /**
	 * 数字美元化（保留两位小数）
	 * @param arg0 - 双精度数
	 * @return 返回美元化之后的字符串
	 */
    
    public static String dollar(double arg0){
        NumberFormat df = NumberFormat.getCurrencyInstance(Locale.US);
	    return df.format(arg0);
    }
     

    /**
	 * 数字百分比化（最多保留两位小数）
	 * @param arg0 - 双精度数
	 * @return 返回百分比化之后的字符串
	 */
	
	public static String percent(double arg0){
		return new DecimalFormat("#,##0.00%").format(arg0);
	}
	
	/**
	 *	二进制转换为十六进制
	 * @param binary 无符号的二进制数，转换的时候如果位数不足会右边补0
	 * @return null表示无法转换。
	 * */
	public static final String binaryToHexString(String binary)
	{		 
		if(binary==null||binary.equals(""))
		{
			return null;
		}
		try
		{
			StringBuffer Hex = new StringBuffer();
			
			int Octal=0;
			
			String tempBinary=null;
			do
			{
				if(binary.length()<=4)
				{
					Octal=Integer.parseInt(binary,2);
					
					return Hex.append(Integer.toHexString(Octal)).toString().toUpperCase();
				}				
				tempBinary = binary.substring(0,4);
				
				binary = binary.substring(4,binary.length());
				
				Octal=Integer.parseInt(tempBinary,2);				
				
				Hex.append(Integer.toHexString(Octal));
			}
			while(true);				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 *	十六进制换为二进制转
	 *@param 无符号的十六进制数
	 **/
	
	public static final String HexToBinaryString(String binary)
	{		
		if(binary==null||binary.equals(""))
		{
			return null;
		}
		try
		{
			int Hex=0;
			
			StringBuffer Binary = new StringBuffer();
			String binaryHex = null;
			for(int i=0;i<binary.length();i++)
			{
				Hex=Integer.parseInt(binary.substring(i,i+1),16);				
	
				binaryHex = "0000"+String.valueOf(Integer.toBinaryString(Hex));
				
				Binary.append(binaryHex.subSequence(binaryHex.length()-4, binaryHex.length()));				
			}
			
			return Binary.toString();  	 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		System.out.print(NumberUtil.binaryToHexString("11111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));	
		System.out.print("\n");
		System.out.print(NumberUtil.HexToBinaryString("B8000000000000000000000000000000"));	   
	}	

}