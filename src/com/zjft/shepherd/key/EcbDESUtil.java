package com.zjft.shepherd.key;

public class EcbDESUtil
{
	private static String key = "5A494A494E524443";
	private static DESEncryptArith des = new DESEncryptArith();

	/**
	 * 加密数据
	 * @param data
	 * @param key
	 * @return 两位数据长度+加密后的数据
	 */
	public static String encryptData(String data)
	{
		if(data == null || data.equals(""))
		{
			return "";
		}
		int len = data.getBytes().length;
		byte[] tmp = new byte[(len/8+1)*8];
		System.arraycopy(data.getBytes(), 0, tmp, 0, len);
		try{
			byte[] result = des.doEncrypt(tmp, BinaryTransfer.ascToBin(key));
			return BinaryTransfer.binToAsc(result);
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
	public static String dncryptData(String data)
	{
		if(data == null || data.equals(""))
		{
			return "";
		}
		try
		{
			byte[] result = des.doDecrypt(BinaryTransfer.ascToBin(data), BinaryTransfer.ascToBin(key));
			int len = 0;
			for(len = 0 ; len < result.length ; len++)
			{
				if(result[len] == 0)
				{
					break;
				}
			}
			return new String(result, 0, len);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		String test = "zijin";
		System.out.println("加密前：" + test);
		String encrypt = encryptData(test);
		System.out.println("加密后：" + encrypt);
		System.out.println("解密后：" + dncryptData(encrypt));
	}
}
