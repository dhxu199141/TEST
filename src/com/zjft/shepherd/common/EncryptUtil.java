package com.zjft.shepherd.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author shp
 * @since 2005.12.12
 */

public final class EncryptUtil {

	private static final String DES = "DES";//DES
	
	private static final String DESede = "DESede";//DESede
	
	/**
	 * MD5�����㷨
	 * @param arg0 - Ҫ���ܵ��ַ���
	 * @return ���ܹ�����ַ���
	 */

	public static String MD5(String arg0) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = arg0.getBytes();
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

	/**
	 * ���ַ�byte����ת��Ϊ16�����ַ���
	 * @param bytes  byte����
	 * @return ת�����16�����ַ���
	 */
	public static String byte2hex(byte bytes[]) {
		StringBuffer retString = new StringBuffer();
		for (int i = 0; i < bytes.length; ++i) {
			retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF))
					.substring(1).toUpperCase());
		}
		return retString.toString();
	}

	/**
	 * ��16�����ַ���ת��Ϊ�ַ�byte����
	 * @param hex 16�����ַ���
	 * @return ת������ַ�byte����
	 */
	public static byte[] hex2byte(String hex) {
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
					16);
		}
		return bts;
	}

	public static byte[] getMABBytes(String macString) {
		int DES_BLOCK_SIZE = 8;
		byte[] tmpBytes = macString.getBytes();

		byte[] retnBytes;
		int iOrigLen = tmpBytes.length;
		int iMode = iOrigLen % DES_BLOCK_SIZE;
		int iAppend = 0;

		if (iMode != 0) {
			iAppend = DES_BLOCK_SIZE - iMode;
		}

		retnBytes = new byte[iOrigLen + iAppend];
		System.arraycopy(tmpBytes, 0, retnBytes, 0, iOrigLen);
		for (int i = iOrigLen; i < iOrigLen + iAppend; i++) {
			retnBytes[i] = (byte) 0x00;
		}
		return retnBytes;
	}

	public static byte[] getMABBytes(byte[] macBytes) {
		int DES_BLOCK_SIZE = 8;
		byte[] retnBytes;
		int iOrigLen = macBytes.length;
		int iMode = iOrigLen % DES_BLOCK_SIZE;
		int iAppend = 0;

		if (iMode != 0) {
			iAppend = DES_BLOCK_SIZE - iMode;
		}

		retnBytes = new byte[iOrigLen + iAppend];
		System.arraycopy(macBytes, 0, retnBytes, 0, iOrigLen);
		for (int i = iOrigLen; i < iOrigLen + iAppend; i++) {
			retnBytes[i] = (byte) 0x00;
		}
		return retnBytes;
	}
	
	/**
	 * �����㷨
	 * @param plainText ���ܶ���
	 * @param desKey �ܳ�����
	 * @return ���ܺ���ַ�����
	 */
	private static byte[] doEncrypt(byte[] plainText, byte[] desKey) {
		try {

			// DES�㷨Ҫ����һ�������ε������Դ
			//SecureRandom sr = new SecureRandom();
			/* ��ĳ�ַ�������ܳ����� */
			byte rawKeyData[] = desKey;
			// ��ԭʼ�ܳ����ݴ���DESKeySpec����
			DESKeySpec dks = new DESKeySpec(rawKeyData);
			// ����һ���ܳ׹�����Ȼ��������DESKeySpecת����
			// һ��SecretKey����
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey key = keyFactory.generateSecret(dks);
			// Cipher����ʵ����ɼ��ܲ���
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding"); //  DES/CBC/NoPadding");	;;;PKCS5Padding

			cipher.init(Cipher.ENCRYPT_MODE, key); //, sr);
			// ���ڣ���ȡ���ݲ�����

			// ��ʽִ�м��ܲ���
			byte[] encryptedData = cipher.doFinal(plainText);

			return encryptedData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * AnsiX9.9
	 */
	public static byte[] generateMacBytes99(byte[] mabBytes,String desKey) throws Exception{
		int DES_BLOCK_SIZE = 8;
		
		mabBytes = getMABBytes(mabBytes);
		/* ��MAB�е�ÿ8���ֽڷ�Ϊһ�飨����˲���8���ӽڣ����Ҳ�0x00��,��MAK��ΪDES��Կ���ζԳ����һ�����ÿһ��������²�����
		 a)	����DES����
		 b)	�������������һ���8���ֽ���򣬽��ȡ����һ�飬�������в�����
		 �����һ�����DES���㣬�ó�8���ֽڵļ���ֵ
		 */
		// The DES/CBC method is just as above
		//byte[] desKey = hex2byte("0123456789ABCDEF");
		//byte[] retnBytes = encoder.encode( mabBytes );
		byte[] retnBytes = new byte[mabBytes.length];
		byte[] procBytes = new byte[mabBytes.length];

		System.arraycopy(mabBytes, 0, procBytes, 0, mabBytes.length);

		int iBlkCnt = mabBytes.length / DES_BLOCK_SIZE;
		byte[] nextBlock = new byte[DES_BLOCK_SIZE];

		byte[] curBlock = new byte[DES_BLOCK_SIZE];
		for (int i = 0; i < iBlkCnt - 1; i++) {
			// get current block
			System.arraycopy(procBytes, i * DES_BLOCK_SIZE, curBlock, 0,
					DES_BLOCK_SIZE);
			// get next block
			System.arraycopy(procBytes, (i + 1) * DES_BLOCK_SIZE, nextBlock, 0,
					DES_BLOCK_SIZE);

			// ��MAK��ΪDES��Կ���ζԳ����һ�����ÿһ����н���DES����
			//byte[] encryptBlock = encoder.encode( curBlock );
			byte[] encryptBlock = encryptBy3DES(curBlock, desKey);
			// �������������һ���8���ֽ���򣬽��ȡ����һ��
			for (int j = 0; j < DES_BLOCK_SIZE; j++) {
				nextBlock[j] = (byte) (encryptBlock[j] ^ nextBlock[j]);
			}
			System.arraycopy(nextBlock, 0, procBytes, (i + 1) * DES_BLOCK_SIZE,
					DES_BLOCK_SIZE);

			// ��ÿһ����н���DES�����Ľ�����ȥ 
			System.arraycopy(encryptBlock, 0, retnBytes, i * DES_BLOCK_SIZE,
					DES_BLOCK_SIZE);
		}

		// added by qzou for the MAB with only 1 block, 20060911 begin
		if (iBlkCnt < 2) {
			System.arraycopy(mabBytes, 0, nextBlock, 0, DES_BLOCK_SIZE);
		}
		// added by qzou for the MAB with only 1 block, 20060911 end

		//�������һ�����DES���㣬�ó�8���ֽڵļ���ֵ
		//byte[] encryptLastBlock = encoder.encode( nextBlock );
		byte[] encryptLastBlock = encryptBy3DES(nextBlock, desKey);
		// �����һ�����DES�����Ľ�����ȥ 
		System.arraycopy(encryptLastBlock, 0, retnBytes, mabBytes.length
				- DES_BLOCK_SIZE, DES_BLOCK_SIZE);

		return encryptLastBlock;
	}

	/*
	 * AnsiX9.19
	 */
	public static byte[] generateMacBytes919(byte[] mabBytes,String desKey) throws Exception{
		int DES_BLOCK_SIZE = 8;
		
		mabBytes = getMABBytes(mabBytes);

		/* ��MAB�е�ÿ8���ֽڷ�Ϊһ�飨����˲���8���ӽڣ����Ҳ�0x00��,��MAK��ΪDES��Կ���ζԳ����һ�����ÿһ��������²�����
		 a)	����DES����
		 b)	�������������һ���8���ֽ���򣬽��ȡ����һ�飬�������в�����
		 �����һ�����DES���㣬�ó�8���ֽڵļ���ֵ
		 */
		// The DES/CBC method is just as above
		//byte[] desKey = hex2byte("0123456789ABCDEF");
		//byte[] retnBytes = encoder.encode( mabBytes );
		String makLeft = desKey.substring(0, 16);
		String makRight = desKey.substring(16, 32);
		
		byte[] retnBytes = new byte[mabBytes.length];
		byte[] procBytes = new byte[mabBytes.length];

		System.arraycopy(mabBytes, 0, procBytes, 0, mabBytes.length);

		int iBlkCnt = mabBytes.length / DES_BLOCK_SIZE;
		byte[] nextBlock = new byte[DES_BLOCK_SIZE];

		byte[] curBlock = new byte[DES_BLOCK_SIZE];
		for (int i = 0; i < iBlkCnt - 1; i++) {
			// get current block
			System.arraycopy(procBytes, i * DES_BLOCK_SIZE, curBlock, 0,
					DES_BLOCK_SIZE);
			// get next block
			System.arraycopy(procBytes, (i + 1) * DES_BLOCK_SIZE, nextBlock, 0,
					DES_BLOCK_SIZE);

			// ��MAK��ΪDES��Կ���ζԳ����һ�����ÿһ����н���DES����
			//byte[] encryptBlock = encoder.encode( curBlock );
			byte[] encryptBlock = encryptByDES(curBlock, makLeft);
			// �������������һ���8���ֽ���򣬽��ȡ����һ��
			for (int j = 0; j < DES_BLOCK_SIZE; j++) {
				nextBlock[j] = (byte) (encryptBlock[j] ^ nextBlock[j]);
			}
			System.arraycopy(nextBlock, 0, procBytes, (i + 1) * DES_BLOCK_SIZE,
					DES_BLOCK_SIZE);

			// ��ÿһ����н���DES�����Ľ�����ȥ 
			System.arraycopy(encryptBlock, 0, retnBytes, i * DES_BLOCK_SIZE,
					DES_BLOCK_SIZE);
		}

		// added by qzou for the MAB with only 1 block, 20060911 begin
		if (iBlkCnt < 2) {
			System.arraycopy(mabBytes, 0, nextBlock, 0, DES_BLOCK_SIZE);
		}
		// added by qzou for the MAB with only 1 block, 20060911 end

		//�������һ�����DES���㣬�ó�8���ֽڵļ���ֵ
		//byte[] encryptLastBlock = encoder.encode( nextBlock );
		byte[] encryptLastBlock = encryptByDES(nextBlock, makLeft);
		// �����һ�����DES�����Ľ�����ȥ 
		System.arraycopy(encryptLastBlock, 0, retnBytes, mabBytes.length
				- DES_BLOCK_SIZE, DES_BLOCK_SIZE);
		
		//�����㷨������˫������Կ���һ���ּ����mac����ansiX99
		//Ȼ�����ұߵ���Կ���ܣ�������߼��ܣ������mac,��ΪansiX919
		byte[] rightBlock = decryptByDES(encryptLastBlock, makRight);
		byte[] ansiX919Block = encryptByDES(rightBlock, makLeft);

		return ansiX919Block;
	}
	
	
	//������Կ����������ָ�����ܺͽ����㷨ΪRSA
	public static String[] Skey_RSA(int keylen) {//������Կ����      
		String[] output = new String[5]; //�����洢��Կ��e n d p q       
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(keylen); //ָ����Կ�ĳ��ȣ���ʼ����Կ��������          
			KeyPair kp = kpg.generateKeyPair(); //������Կ��
			RSAPublicKey puk = (RSAPublicKey) kp.getPublic();
			RSAPrivateCrtKey prk = (RSAPrivateCrtKey) kp.getPrivate();
			BigInteger e = puk.getPublicExponent();
			BigInteger n = puk.getModulus();
			BigInteger d = prk.getPrivateExponent();
			BigInteger p = prk.getPrimeP();
			BigInteger q = prk.getPrimeQ();
			output[0] = e.toString();
			output[1] = n.toString();
			output[2] = d.toString();
			output[3] = p.toString();
			output[4] = q.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(ZipUtil.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return output;
	}

	//������RSA��Կ�а���������������Ϣ��e��n��������������m,�������ĵĹ�ʽ��m��e�η�����n��ģ��
	public static String Enc_RSA(String mingwen, String eStr, String nStr) {
		String miwen = new String();
		try {
			BigInteger e = new BigInteger(eStr);
			BigInteger n = new BigInteger(nStr,16);   //�˴�nStr����Ϊ16�����ַ���  ת��Ϊ10����
			byte[] ptext = mingwen.getBytes("UTF8"); //��ȡ���ĵĴ�����
			BigInteger m = new BigInteger(ptext);
			BigInteger c = m.modPow(e, n);
			miwen = c.toString();
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(ZipUtil.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return miwen;
	}

	//����
	public static String Dec_RSA(String miwen, String dStr, String nStr) {
		StringBuffer mingwen = new StringBuffer();
		BigInteger d = new BigInteger(dStr,16);//��ȡ˽Կ�Ĳ���d,n  //�˴�dStr����Ϊ16�����ַ���  ת��Ϊ10����
		BigInteger n = new BigInteger(nStr,16);    //�˴�nStr����Ϊ16�����ַ���  ת��Ϊ10����
		BigInteger c = new BigInteger(miwen);
		BigInteger m = c.modPow(d, n);//��������
		byte[] mt = m.toByteArray();//�������Ķ�Ӧ���ַ��������
		for (int i = 0; i < mt.length; i++) {
			mingwen.append((char) mt[i]);
		}
		return mingwen.toString();
	}

	/*
	 * RSA����   ��ATMC����������JDK API�ļ��ܷ�ʽ
	 * @param:String mingwen ����
	 * @param:String e ָ��  10�����ַ���
	 * @param:String n ��Կ  16�����ַ���
	 * @return byte ����
	 */
	public static byte[] encrypt_RSA(String mingwen, String e,String n) {  
        try {  
            BigInteger modulus = new BigInteger(n,16);  
            BigInteger exponent = new BigInteger(e);  

            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);  
            KeyFactory fact = KeyFactory.getInstance("RSA");  
            PublicKey pubKey = fact.generatePublic(rsaPubKey);  
  
            Cipher cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);  
  
            byte[] cipherData = cipher.doFinal(mingwen.getBytes());  
            return cipherData;  
        } catch (Exception e1) {  
            e1.printStackTrace();  
        }  
        return null;  
  
    }  
  
	/*
	 * RSA����   ��ATMC����������JDK API�ļ��ܷ�ʽ
	 * @param:String mingwen ����
	 * @param:String e ָ��  10�����ַ���
	 * @param:String n ��Կ  16�����ַ���
	 * @return byte ����
	 */
	public static byte[] encrypt_RSA(byte[] mingwen, String e,String n) {  
        try {  
            BigInteger modulus = new BigInteger(n,16);  
            BigInteger exponent = new BigInteger(e);  

            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);  
            KeyFactory fact = KeyFactory.getInstance("RSA");  
            PublicKey pubKey = fact.generatePublic(rsaPubKey);  
  
            Cipher cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);  
  
            byte[] cipherData = cipher.doFinal(mingwen);  
            return cipherData;  
        } catch (Exception e1) {  
            e1.printStackTrace();  
        }  
        return null;  
  
    }  
	
	/*
	 * RSA����   ��ATMC����������JDK API�ļ��ܷ�ʽ
	 * @param:String mingwen ����
	 * @param:String e ָ��  10�����ַ���
	 * @param:String n ��Կ  16�����ַ���
	 * @return byte ����
	 */
	public static String encrypt_RSA_toHexString(String mingwen, String e,String n) {  
        return byte2hex(encrypt_RSA(mingwen,e, n));
    }
	
	/*
	 * RSA����   ��ATMC����������JDK API�ļ��ܷ�ʽ
	 * @param:String mingwen ����
	 * @param:String e ָ��  10�����ַ���
	 * @param:String n ��Կ  16�����ַ���
	 * @return byte ����
	 */
	public static String encrypt_RSA_BytetoHexString(byte[] mingwen, String e,String n) {  
        return byte2hex(encrypt_RSA(mingwen,e, n));
    }
	
	/*
	 * RSA����    ��ATMC����������JDK API�Ľ��ܷ�ʽ
	 * @param:String miwen ����
	 * @param:String d ˽Կ  16�����ַ���
	 * @param:String n ��Կ  16�����ַ���
	 * @return byte ����
	 */
    public static byte[] dencrypt_RSA(byte[] miwen,String d, String n) {  
        try {  
            BigInteger modules = new BigInteger(n,16);  
            BigInteger exponent = new BigInteger(d,16);  

            KeyFactory factory = KeyFactory.getInstance("RSA");  
            Cipher cipher = Cipher.getInstance("RSA");  
  
            RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules, exponent);  
            PrivateKey privKey = factory.generatePrivate(privSpec);  
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] decrypted = cipher.doFinal(miwen);  
            return decrypted;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }
    
    
	/** 
	 * �������DESkey ��ŵ�DesKey.txt��
	 * @param 
	 * @return 
	 */
	public static void saveDesKey() {
		try {
			SecureRandom sr = new SecureRandom();
			// Ϊ����ѡ���DES�㷨����һ��KeyGenerator����
			KeyGenerator kg = KeyGenerator.getInstance(DES);
			kg.init(sr);
			// ���·�� ��Ҫ�½� conf �ļ���
			// String fileName = "conf/DesKey.xml";
			// ����·��
			String fileName = System.getProperty("user.dir") + "/DesKey.txt";
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			// ������Կ
			Key key = kg.generateKey();
			System.out.println(key.getAlgorithm());
			System.out.println(key.getEncoded().length);
			for (int i = 0; i < key.getEncoded().length; i++) {
				System.out.println(key.getEncoded()[i]);
			}

			System.out.println(key.getEncoded());

			System.out.println(key.getFormat());

			System.out.println(ZipUtil.bytesToHexString(key.getEncoded()));
			oos.writeObject(key);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * ���ļ��л�ȡkey
	 * @param 
	 * @return key
	 */
	public static Key getKey() {
		Key kp = null;
		try {
			// ���·�� ��Ҫ�½� conf �ļ���
			// String fileName = "conf/DesKey.xml";
			// InputStream is = Encrypt.class.getClassLoader().getResourceAsStream(fileName);
			// ����·��
			String fileName = System.getProperty("user.dir") + "/DesKey.txt";
			;
			FileInputStream is = new FileInputStream(fileName);

			ObjectInputStream oos = new ObjectInputStream(is);
			kp = (Key) oos.readObject();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kp;
	}

	/** 
	 * file�д�����ģ����ܺ��ŵ�dest�ļ���
	 * @param  file dest
	 * @return 
	 */
	public static void encrypt(String file, String dest) throws Exception {
		Cipher cipher = Cipher.getInstance(DES);
		cipher.init(Cipher.ENCRYPT_MODE, getKey());
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(dest);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = cis.read(buffer)) > 0) {
			out.write(buffer, 0, r);
		}
		cis.close();
		is.close();
		out.close();
	}

	/** 
	 * file�д�����ģ����ܺ��ŵ�file�ļ���
	 * @param  file dest
	 * @return 
	 */
	public static void decrypt(String file, String dest) throws Exception {
		Cipher cipher = Cipher.getInstance(DES);
		cipher.init(Cipher.DECRYPT_MODE, getKey());
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(dest);
		CipherOutputStream cos = new CipherOutputStream(out, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = is.read(buffer)) >= 0) {
			cos.write(buffer, 0, r);
		}
		cos.close();
		out.close();
		is.close();
	}

	/** 
	 * ����String��������,String������� 
	 * @param strMing 
	 * @return 
	 */
	public static String getEncString(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		try {
			return byte2hex(getEncCode(strMing.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	/** 
	 * ���� ��String��������,String������� 
	 * @param strMi 
	 * @return 
	 */
	public static String getDesString(String strMi) {

		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			return new String(getDesCode(hex2byte(strMi)));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	/** 
	 * ������byte[]��������,byte[]������� 
	 * @param byteS 
	 * @return 
	 */

	private static byte[] getEncCode(byte[] byteS) {

		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(DES);
			cipher.init(Cipher.ENCRYPT_MODE, EncryptUtil.getKey());
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/** 
	 * ������byte[]��������,��byte[]������� 
	 * 
	 * @param byteD 
	 * @return 
	 */

	private static byte[] getDesCode(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance(DES);
			cipher.init(Cipher.DECRYPT_MODE, EncryptUtil.getKey());
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/*
	 * ��ȡ�����Կ
	 */
	public static String getDESKeyHex()
	{
		try
		{
			return byte2hex(getDESKeyByte());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}
	
	/*
	 * ��ȡ�����Կ
	 */
	public static byte[] getDESKeyByte()
	{
		try
		{
			SecureRandom sr = new SecureRandom();
			// Ϊ����ѡ���DES�㷨����һ��KeyGenerator����
			KeyGenerator kg = KeyGenerator.getInstance(DESede);
			kg.init(112,sr);  //DES �� 56��  3DES  ��112 168 ȫ��24���ֽڣ�����˫���� K1=K3
			// ������Կ
			Key key = kg.generateKey();

			return key.getEncoded();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}
	
	/** 
	 
	* ���� 
	* @param src ����Դ 
	* @param key ��Կ�����ȱ�����8�ı��� 
	* @return  ���ؼ��ܺ������ 
	* @throws Exception 
	 
	*/  
	  
	public static byte[] encrypt(byte[] src, byte[] key, String algorithm)throws Exception {
	       //DES�㷨Ҫ����һ�������ε������Դ  
	       //SecureRandom sr = new SecureRandom();
	       // ��ԭʼ�ܳ����ݴ���DESKeySpec����   
	       SecretKeySpec dks = new SecretKeySpec(key,algorithm);
	       // ����һ���ܳ׹�����Ȼ��������DESKeySpecת���� 
	       // һ��SecretKey����  
//	       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES); 
//	       SecretKey securekey = keyFactory.generateSecret(dks);
	       // Cipher����ʵ����ɼ��ܲ���  
	       Cipher cipher = Cipher.getInstance(algorithm+"/ECB/NoPadding");
	       // ���ܳ׳�ʼ��Cipher����  
	       cipher.init(Cipher.ENCRYPT_MODE, dks);
	       // ���ڣ���ȡ���ݲ�����  
	       // ��ʽִ�м��ܲ���  
	       return cipher.doFinal(src);
	}  
	/**
	* 
	* ����
	* @param src����Դ
	* @param key ��Կ�����ȱ�����8�ı���
	* @return ���ؽ��ܺ��ԭʼ����
	* @throws Exception
	*/

	public static byte[] decrypt(byte[] src, byte[] key, String algorithm) throws Exception
	{
	  
	   // DES�㷨Ҫ����һ�������ε������Դ
	   //SecureRandom sr = new SecureRandom();
	   // ��ԭʼ�ܳ����ݴ���һ��DESKeySpec����
	   SecretKeySpec dks = new SecretKeySpec(key,algorithm);
	   // ����һ���ܳ׹�����Ȼ��������DESKeySpec����ת����
	   // һ��SecretKey����
//	   SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
//	   SecretKey securekey = keyFactory.generateSecret(dks);
	   // Cipher����ʵ����ɽ��ܲ���
	   Cipher cipher = Cipher.getInstance(algorithm+"/ECB/NoPadding");
	   // ���ܳ׳�ʼ��Cipher����
	   cipher.init(Cipher.DECRYPT_MODE, dks);
	   // ���ڣ���ȡ���ݲ�����
	   // ��ʽִ�н��ܲ���
	   return cipher.doFinal(src);
	  
	}
	
	//����
	public static byte[] encryptByDES(byte[] src, String key)throws Exception {
		return encrypt(src , hex2byte(key), DES);
	}
	
	//����
	public static byte[] decryptByDES(byte[] src, String key)throws Exception {
		return decrypt(src , hex2byte(key),DES);
	}
	
	//����
	public static byte[] encryptBy3DES(byte[] src, String key)throws Exception {
		return encrypt(src , hex2byte(key), DESede);
	}
	
	//����
	public static byte[] decryptBy3DES(byte[] src, String key)throws Exception {
		return decrypt(src , hex2byte(key), DESede);
	}
	
	public static void main(String[] args) {

		try {

			//String key = getDESKeyHex();
			String key = "2FC4BC32E019A8EADA8F64AEDA94CD682FC4BC32E019A8EA";
			String clearText="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root><cmdid value=\"900006\"/><cmddatetime date=\"2011-09-01 10:20:51\" time=\"2011-09-01 10:20:51\"/><msgid value=\"b1c638af-70a6-4931-ab3a-b88174516bb9\"/><remote ipaddress=\"10.2.7.62\" termno=\"000100\"/><projinfo/></root>";
			byte[] macBytes = generateMacBytes919(getMABBytes(clearText),key);
			System.out.println("111111111");
			System.out.println(macBytes.length);
			String result=byte2hex(macBytes);
			System.out.println(result);
			
//			System.out.println("==" + EncryptUtil.MD5("admin"));
//			System.out.println("==" + EncryptUtil.MD5("admin"));
//			System.out.println("==" + EncryptUtil.MD5("admin"));
//
//			System.out.println("test1");
//			String a[] = Skey_RSA(512);
//			System.out.println(a[0]);
//			System.out.println(a[1]);
//			System.out.println(a[2]);
//			System.out.println(a[3]);
//			System.out.println(a[4]);
//			System.out.println("test2");
//			String a = "65537";
//			String b = "EDDF00327874E75C5AD0FE5C821E68FEFD4D6BE9343BB34CB8C064D81EAAB249A24B3BD78A591E0EAC88CA295AFE13810905E0858458CB302FA66243FD7CDDA41FB1E9828399BF87F0C7083222992ED43CAC8AC6C271D71045A41EADDB377E40F7A9D7D0C020448C22915AAFD3992D8860AB86C7F66D90840C2FF5DE398654D3";
//			String c = "9E5DC4FB3BB9C4079EAB760D01DAB9CDE30B91832076F8F4";
//			String rsajm = Enc_RSA(c, a, b);
//			System.out.println("1["+rsajm+"]");
//			System.out.println("2["+Dec_RSA(rsajm, a, b)+"]");
//			System.out.println("test3");
			
//			EncryptUtil.saveDesKey();
//			System.out.println("����key");
//			EncryptUtil.getKey().toString();
//			System.out.println("��ȡkey");
//			EncryptUtil.encrypt("d:\\hello.txt", "d:\\encrypt.txt");
//		    System.out.println("����");
//			EncryptUtil.decrypt("d:\\encrypt.txt", "d:\\decrypt.txt");
//			System.out.println("����");
//			
//			byte[] strEnc = encrypt(getMABBytes("1234567812345678"),key); //�����ַ���,����String������ 
//			System.out.println("["+byte2hex(strEnc)+"]"); 
//			byte[] strDes = decrypt(strEnc,key); //��String ���͵����Ľ��� 
//			System.out.println("["+new String(strDes)+"]"); 
		}

		catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
	}

}