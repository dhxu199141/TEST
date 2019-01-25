/**
 * 
 */
package com.zjft.shepherd.key;

//import java.security.SecureRandom;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

import sun.misc.*;

//import com.zjft.zjfz.validator.TestMacKey;


/**
 * @author qzou
 *
 */
public class DESEncryptArith implements IEncryptArith{  
	private static final byte[] PASSWORD_CRYPT_KEY = {0x19,0x70,0x79,0x75,0x6E,0x33,0x26,(byte)0x89,0x44,(byte)0xBC,0x05,(byte)0x80,(byte)0xF8,0x3D,0x00,(byte)0x80}; 
	private static final byte[] plain = {0x01,0x23,0x45,0x67,(byte)0x89,(byte)0xab,(byte)0xcd,(byte)0xe7};
	private static final byte[] cipher = {(byte)0xc9,0x57,0x44,0x25,0x6a,0x5e,(byte)0xd3,0x1d};
	private final static String DES = "DES/ECB/NoPadding"; 

	
	
    public byte[] doDecrypt(byte[] encryptText,byte[] desKey) throws Exception {
    	//byte[] encryptionIV = "00000000".getBytes();
     
    	//  DES算法要求有一个可信任的随机数源
        //SecureRandom sr = new SecureRandom();
        /* 用某种方法获取原始密匙数据 */
        byte[] rawKeyData = desKey; 
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        //PKCS5Padding"); ;;
        // 用密匙初始化Cipher对象
        //IvParameterSpec spec = new IvParameterSpec(encryptionIV);

        
        // 用密匙初始化Cipher对象
        //cipher.init(Cipher.ENCRYPT_MODE, key, spec);  //, sr);

//        cipher.init(Cipher.DECRYPT_MODE, key, spec);	//, sr);
        cipher.init(Cipher.DECRYPT_MODE, key);	//, sr);
        // 现在，获取数据并解密
        byte[] encryptedData = encryptText;/* 获得经过解密的数据 */
        
        // 正式执行解密操作
        byte[] decryptedData = cipher.doFinal(encryptedData);
        
        return decryptedData;
    }
    
    public byte[] doEncrypt(byte[] plainText,byte[] desKey) throws Exception {
    	//byte[] encryptionIV = "00000000".getBytes();
    	
    	/*byte[] iv = new byte[]{
                (byte)0x8E, 0x12, 0x39, (byte)0x9C,
                0x07, 0x72, 0x6F, 0x5A
            };
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        */
        // DES算法要求有一个可信任的随机数源
        //SecureRandom sr = new SecureRandom();
        /* 用某种方法获得密匙数据 */
        byte rawKeyData[] = desKey;
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");   //  DES/CBC/NoPadding");	;;;PKCS5Padding
        
        

        // Create IvParameterSpec object with initialization vector
        //IvParameterSpec spec = new IvParameterSpec(encryptionIV);
//        IvParameterSpec spec = new IvParameterSpec();

        
        // 用密匙初始化Cipher对象
//        cipher.init(Cipher.ENCRYPT_MODE, key, spec);  //, sr);
        cipher.init(Cipher.ENCRYPT_MODE, key);  //, sr);
        // 现在，获取数据并加密
        //byte[] data = plainText;
        // 正式执行加密操作
        byte[] encryptedData = cipher.doFinal(plainText);

        return encryptedData;
    }
	
 
    @SuppressWarnings("unused")
	private String binToAsc(byte[] val)
	{
//		byte interByte[] = val.getBytes();

		StringBuffer str = new StringBuffer(val.length * 2);
		for (int i = 0; i < val.length; i++) {
			char hi = Character.forDigit((val[i] >> 4) & 0x0F, 16);
			char lo = Character.forDigit(val[i] & 0x0F, 16);
			str.append(Character.toUpperCase(hi));
			str.append(Character.toUpperCase(lo));
		}
		
		return str.toString();
	}
    
    public static void main(String[] args) throws Exception  {
    	byte[] ori = "11".getBytes();
    	byte[] pin = new byte[(ori.length/8+1)*8];
    	java.util.Arrays.fill(pin, (byte)0x00);
    	System.arraycopy(ori, 0, pin, 0, ori.length);
    	
    	DESEncryptArith des = new DESEncryptArith();
		byte[] tmp = des.doEncrypt(pin,PASSWORD_CRYPT_KEY);
		String base64 = new BASE64Encoder().encode(tmp);
		
		System.out.println(base64);
		System.out.println(new String(des.binToAsc(base64.getBytes())));
    	
		new BASE64Decoder().decodeBuffer(base64);
		System.out.println(new String(des.doDecrypt(new BASE64Decoder().decodeBuffer(base64), PASSWORD_CRYPT_KEY)));
    	
//    	byte[] key = new TestMacKey().getRootKey();
//    	byte[] data = new TestMacKey().getMacKey();
//    	byte[] dac = new TestMacKey().getDac();
//    	try {
//    		DESEncryptArith arith = new DESEncryptArith();
//			byte[] result = arith.doEncrypt(data,key);
//			byte[] dacResult = arith.doEncrypt(dac,data);
////			System.out.println(new String(arith.binToAsc(key)));
////			System.out.println(new String(arith.binToAsc(data)));
////			System.out.println(new String(arith.binToAsc(result)));
////			System.out.println(new String(arith.binToAsc(dacResult)));
//			
//			data = arith.doDecrypt(dacResult,data);
////			System.out.println(new String(arith.binToAsc(dac)));
////			System.out.println(new String(arith.binToAsc(data)));
//			data = arith.doDecrypt(result,key);
////			System.out.println(new String(arith.binToAsc(data)));
//			
//		} catch (Exception e) {	
//			e.printStackTrace();
//		}	
    }
    
	
}
