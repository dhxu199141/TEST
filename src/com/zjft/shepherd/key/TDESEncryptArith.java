/**
 * 
 */
package com.zjft.shepherd.key;

//import java.security.SecureRandom;
import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author qzou
 *
 */
public class TDESEncryptArith implements IEncryptArith{  
    public byte[] doDecrypt(byte[] encryptText,byte[] desKey) throws Exception {
    	//byte[] encryptionIV = "00000000".getBytes();
     
    	//  DES算法要求有一个可信任的随机数源
        //SecureRandom sr = new SecureRandom();
        /* 用某种方法获取原始密匙数据 */
        byte[] rawKeyData = desKey; 
        // 从原始密匙数据创建一个DESKeySpec对象
        DESedeKeySpec dks = new DESedeKeySpec(rawKeyData);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        //PKCS5Padding"); ;;
        // 用密匙初始化Cipher对象
        //IvParameterSpec spec = new IvParameterSpec(encryptionIV);

        
        // 用密匙初始化Cipher对象
        //cipher.init(Cipher.ENCRYPT_MODE, key, spec);  //, sr);

        //cipher.init(Cipher.DECRYPT_MODE, key, spec);	//, sr);
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
        DESedeKeySpec dks = new DESedeKeySpec(rawKeyData);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");   //  DES/CBC/NoPadding");	;;;PKCS5Padding
        
        

        // Create IvParameterSpec object with initialization vector
        //IvParameterSpec spec = new IvParameterSpec(encryptionIV);

        
        // 用密匙初始化Cipher对象
        //cipher.init(Cipher.ENCRYPT_MODE, key, spec);  //, sr);
        cipher.init(Cipher.ENCRYPT_MODE, key);  //, sr);
        // 现在，获取数据并加密
        //byte[] data = plainText;
        // 正式执行加密操作
        byte[] encryptedData = cipher.doFinal(plainText);

        return encryptedData;
    }
	
	
}
