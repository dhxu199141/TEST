/**
 * 
 */
package com.zjft.shepherd.key;

/**
 * @author Administrator
 *
 */
public class BinaryTransfer {

	/**
	 * 
	 */
	public BinaryTransfer() {
		super();
	}
	
	public static byte[] ascToBin(String val){
		byte interByte[] = val.toUpperCase().getBytes();
		byte[] result = new byte[interByte.length/2];
		for (int i = 0; i < result.length; i++) {
			byte hi = interByte[i * 2];
			byte lo = interByte[i * 2 + 1];
			int h = hi > 0x40 ? 10 + hi - 0x41 : hi - 0x30;
			int l = lo > 0x40 ? 10 + lo - 0x41 : lo - 0x30;
			result[i] = (byte) (h << 4 | l);
		}
		return result;
	}
	
	 public static String binToAsc(byte[] val){
		 StringBuffer str = new StringBuffer(val.length * 2);
		 for (int i = 0; i < val.length; i++) {
			 char hi = Character.forDigit((val[i] >> 4) & 0x0F, 16);
			 char lo = Character.forDigit(val[i] & 0x0F, 16);
			 str.append(Character.toUpperCase(hi));
			 str.append(Character.toUpperCase(lo));
		 }		
		 return str.toString();
	}
		

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		System.out.println(binToAsc(ascToBin("8a8a")));

	}

}
