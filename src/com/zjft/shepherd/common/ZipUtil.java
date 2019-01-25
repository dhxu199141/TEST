package com.zjft.shepherd.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * zip,gzip操作实用类
 * 
 * @author hpshen
 * @since 2007.04.10
 * @since 2009.04.15 ykliu 增加gzip处理
 */

public class ZipUtil {

	private static Log log = LogFactory.getLog(ZipUtil.class);

	/**
	 * 将输入的字节流进行解压缩
	 * @param zipBytes  压缩后的字节流
	 * @return 解压后的字节流
	 * @throws IOException
	 * @throws ZipException
	 */
	public static byte[] unzip(byte[] zipBytes) throws IOException,
			ZipException {

		if (zipBytes == null) {
			throw new NullPointerException("unzip传入参数为null");
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
		ZipInputStream zis = new ZipInputStream(bais);
		zis.getNextEntry();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final int BUFSIZ = 102400;
		byte inbuf[] = new byte[BUFSIZ];
		int n;
		while ((n = zis.read(inbuf, 0, BUFSIZ)) != -1) {
			baos.write(inbuf, 0, n);
		}
		byte[] data = baos.toByteArray();
		baos.close();
		zis.close();
		bais.close();
		return data;
	}

	/**
	 * 对输入的字节流进行zip压缩
	 * @param data 需要压缩的字节流
	 * @return zip压缩后的字节流
	 * @throws IOException
	 * @throws ZipException
	 */
	public static byte[] zip(byte[] data) throws IOException, ZipException {
		if (data == null) {
			throw new NullPointerException("zip传入参数为null");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipEntry ze = new ZipEntry("servletservice");
		ZipOutputStream zos = new ZipOutputStream(baos);
		zos.putNextEntry(ze);
		zos.write(data, 0, data.length);
		zos.closeEntry();
		zos.close();
		byte[] zipBytes = baos.toByteArray();
		baos.close();
		return zipBytes;
	}

	/**
	 * 压缩文件或目录下的所有文件(注意：生成的ZIP文件不能存放在压缩目录中，否则容易产异常)
	 * @param inputFileName 文件或文件夹名称
	 * @param outputFileName 压成的文件压缩文件名称
	 * @return true:成功,false:失败
	 * */
	public static boolean zipFileDoc(String inputFileName, String outputFileName)
			throws Exception {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(outputFileName));

			ZipUtil.zipFiles(out, new File(inputFileName), "");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	/**
	 * 递归压缩函数(私有)
	 * @author hjtang
	 * */
	private static void zipFiles(ZipOutputStream out, File f, String base)
			throws Exception {
		FileInputStream in = null;
		try {
			if (f.isDirectory()) {
				File[] fl = f.listFiles();

				out.putNextEntry(new ZipEntry(base + "/"));
				base = base.length() == 0 ? "" : base + "/";

				for (int i = 0; i < fl.length; i++) {
					ZipUtil.zipFiles(out, fl[i], base + fl[i].getName());
				}
			} else {
				out.putNextEntry(new ZipEntry(base));
				in = new FileInputStream(f);
				int b;

				while ((b = in.read()) != -1) {
					out.write(b);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}

		}
	}

	/**
	 * 解压缩标准的ZIP文件
	 * @param srcZipName 原ZIP文件名(绝对路径) 
	 * @param desZipDoc 目标加压文件夹(绝对路径) 
	 * @return 解压后的文件列表
	 * */

	public static List unZipFiles(String srcZipName, String desZipDoc) {
		ZipFile zfile = null;

		Enumeration zList = null;

		ZipEntry ze = null;

		byte[] buf = new byte[1024];

		List fileList = new ArrayList();

		int fileNum = 0;

		String zfiles = null;

		OutputStream os = null;

		InputStream is = null;

		new File(desZipDoc).mkdirs();

		try {
			zfile = new ZipFile(srcZipName);

			zList = zfile.entries();

			while (zList.hasMoreElements()) {
				ze = (ZipEntry) zList.nextElement();

				if (ze.isDirectory()) {
					File dir = new File(desZipDoc
							+ System.getProperty("file.separator")
							+ ze.getName().toString());

					if (!dir.exists()) {
						dir.mkdirs();
					}
				} else {
					fileNum++;
					zfiles = desZipDoc + ze.getName().toString();

					fileList.add(new String[] { String.valueOf(fileNum),
							ze.getName().toString() });

					File ret = new File(zfiles);

					/*以ZipEntry为参数得到一个InputStream，并写到OutputStream中*/
					os = new BufferedOutputStream(new FileOutputStream(ret));
					is = new BufferedInputStream(zfile.getInputStream(ze));
					int readLen = 0;
					while ((readLen = is.read(buf, 0, 1024)) != -1) {
						os.write(buf, 0, readLen);
					}
					if (is != null) {
						is.close();
					}
					if (os != null) {
						os.close();
					}
				}
			}

			return fileList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (zfile != null) {
					zfile.close();
				}
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解压缩标准的ZIP文件中的指定文件
	 * 
	 * @param srcZipName 原ZIP文件名(绝对路径) 
	 * @param unZipFile 需要解压的文件
	 * @param desZipDoc 需要解压的目标文件
	 * @return true：解压成功，false：解压失败
	 * */

	public static boolean unZipFile(String srcZipName, String desZipDoc,
			String unZipFile) {
		ZipFile zfile = null;

		Enumeration zList = null;

		ZipEntry ze = null;

		byte[] buf = new byte[1024];

		String zfiles = null;

		OutputStream os = null;

		InputStream is = null;

		new File(desZipDoc).mkdirs();

		try {

			zfile = new ZipFile(srcZipName);

			zList = zfile.entries();

			while (zList.hasMoreElements()) {
				ze = (ZipEntry) zList.nextElement();

				if (ze.getName().equals(unZipFile)) {

					zfiles = desZipDoc + ze.getName().toString();

					File ret = new File(zfiles);

					/*以ZipEntry为参数得到一个InputStream，并写到OutputStream中*/
					os = new BufferedOutputStream(new FileOutputStream(ret));
					is = new BufferedInputStream(zfile.getInputStream(ze));
					int readLen = 0;
					while ((readLen = is.read(buf, 0, 1024)) != -1) {
						os.write(buf, 0, readLen);
					}
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (zfile != null) {
					zfile.close();
				}
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//----------------------
	/**
	 * 将输入的字节流进行解压缩
	 * @param gzipBytes  压缩后的字节流
	 * @return 解压后的字节流
	 * @throws IOException
	 * @throws ZipException
	 */
	public static byte[] ungzip(byte[] gzipBytes) throws IOException {

		if (gzipBytes == null) {
			throw new NullPointerException("ungzip传入参数为null");
		}
//		System.out.println("---------------------");
		log.debug("返回报文gzipBytes.length=" + gzipBytes.length);
		log.debug("返回报文gzipBytes=" + new String(gzipBytes));
		log.debug("返回报文bytesToHexString(gzipBytes)="+bytesToHexString(gzipBytes));
//		System.out.println("---------------------");
		ByteArrayInputStream bais = new ByteArrayInputStream(gzipBytes);
		GZIPInputStream gzis = new GZIPInputStream(bais);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		final int BUFSIZ = gzipBytes.length;
		byte inbuf[] = new byte[gzipBytes.length];
		int n;
		while ((n = gzis.read(inbuf, 0, BUFSIZ)) != -1) {
			baos.write(inbuf, 0, n);
		}
		byte[] data = baos.toByteArray();
		baos.close();
		gzis.close();
		bais.close();
		return data;
	}

	/**
	 * 对输入的字节流进行gzip压缩
	 * 
	 * @param data
	 *            需要压缩的字节流
	 * @return gzip压缩后的字节流
	 * @throws IOException
	 * @throws ZipException
	 */
	public static byte[] gzip(byte[] data) throws IOException, ZipException {
		if (data == null) {
			throw new NullPointerException("gzip传入参数为null");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//ZipEntry ze = new ZipEntry("servletservice");
		GZIPOutputStream zos = new GZIPOutputStream(baos);
		//zos.putNextEntry(ze);
		zos.write(data, 0, data.length);
		//zos.closeEntry();
		zos.close();
		byte[] gzipBytes = baos.toByteArray();
		baos.close();
		return gzipBytes;
	}

	/**
	 * 压缩文件(注意：生成的GZIP文件不能存放在压缩目录中，否则容易产异常)
	 * 
	 * @param inputFileName
	 *            文件或文件夹名称
	 * @param outputFileName
	 *            压成的文件压缩文件名称
	 * @return true:成功,false:失败
	 */
	public static boolean gzipFile(String inputFileName, String outputFileName)
			throws Exception {
		GZIPOutputStream out = null;
		FileInputStream in = null;
		try {
			out = new GZIPOutputStream(new FileOutputStream(outputFileName));
			File infile = new File(inputFileName);
			if (infile.isDirectory()) {
				// 不支持文件夹
				return false;
			} else {
				in = new FileInputStream(infile);
				int b;

				while ((b = in.read()) != -1) {
					out.write(b);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 解压文件(注意：生成的GZIP文件不能存放在压缩目录中，否则容易产异常)
	 * 
	 * @param inputFileName
	 *            压缩文件名称
	 * @param outputFileName
	 *            解压后的文件名称
	 * @return true:成功,false:失败
	 */
	public static boolean ungzipFile(String inputFileName, String outputFileName)
			throws Exception {
		GZIPInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new GZIPInputStream(new FileInputStream(
					new File(inputFileName)));
			out = new FileOutputStream(new File(outputFileName));

			final int BUFSIZ = 102400;
			byte inbuf[] = new byte[BUFSIZ];
			int n;
			while ((n = in.read(inbuf, 0, BUFSIZ)) != -1) {
				out.write(inbuf, 0, n);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			// ZipUtil.unZipFile("c:\\TDDOWNLOAD\\SQL2000_olap.rar","c:\\","sql_200_olap.pdf");
			//			String a = "abcd";
			//			
			//			byte[] b = a.getBytes();  System.out.println("1="+new String(b));
			//			byte[] c = ZipUtil.zip(b);  System.out.println("2="+new String(c));
			//			byte[] d = ZipUtil.unzip(c);  System.out.println("3="+new String(d));
			//			
			StringBuffer inputXML = new StringBuffer();
			inputXML.append("<?xml version=\"1.0\"?>").append("<root>").append(
					"<cmdid value=\"200018\" />").append("<msgid value=\"")
					.append(1).append("\" />").append("<cmddatetime date=\"")
					.append(2).append("\" time=\"").append(3).append("\" />")
					.append("<operatorinfo userid=\"zjft\"/>").append(
							"<actioninfo time=\"").append(4).append(
							"\" filename=\"").append(5 + ".flst").append(
							"\" viewpath=\"").append(6).append("\" />").append(
							"<remote ipaddress=\"").append(7).append(
							"\" termno=\"").append(8).append("\" serialno=\"")
					.append(9).append("\" />").append("</root>");
			String a1 = inputXML.toString();
			//System.out.println("a1_length="+a1.length());
			byte[] b1 = a1.getBytes();
			//System.out.println("zip="+zip(b1).length);
			//System.out.println("gzip="+gzip(b1).length);
//
//			String a1="15";
//			byte[] b1 = a1.getBytes();
//			System.out.println("4=" + new String(b1));
//			byte[] c1 = ZipUtil.gzip(b1);
//			System.out.println("5=" + new String(c1));
//			byte[] d1 = ZipUtil.ungzip(c1);
//			System.out.println("6=" + new String(d1));
////			log.info("--------");
//			ZipUtil.gzipFile("d://readme.txt", "c://readme.txt.gz");
//			ZipUtil.ungzipFile("c://dddxxx.txt.gz", "c://dddxxx.txt");
//			System.out.print("ok!");
//						DataInputStream dis = new DataInputStream(new FileInputStream(new File("c://dddxxx.txt.gz"))); // 读
//						byte[] gzip = new byte[1024];
//						dis.read(gzip);
//						dis.close();
//						
//						System.out.println(new String(ZipUtil.ungzip(gzip)));
//
//			log.info("--------");
//			String a1="0x1f0x8b0x080x000x000x000x000x000x000x0b0x8d0x510xc10x4e0x840x300x140x3c0x6b0xe20x3f0x340xbd0xc30x160x640x650xbb0x010xf70xe80x690x2f0xea0xd10x4b0xa50x0f0xac0xa10x940xb40x6f0x370xea0xd70xfb0x4a0x8a0x1b0x3d0x790xa00xd00x990x760x980x990xd70x1c0x3e0xec0xc80xce0xe00x830x710x530xcb0x8b0x5c0xf00xc30xfd0xcd0x750xe30x9d0x430x7a0x5f0x350x6f0xa00x340x430xaf0xa60x600x0d0xf60xa30x1a0x5a0x2e0x380xb30x100x820x1a0x000x3f0x670x680xf90xf10xe90x810xb30xe00x4e0xbe0x4b0xc00xe30xf10xf90x070x000x6f0x270x470xc20x520x4a0x210x440x450x4f0xb10x720x660x260x5c0xe40x650x5e0xe70x050xa10x9b0xe50x7f0x9d0xd50x460xb30xb30x1a0x4f0xa40x5c0xc60xe30xbb0xc40xd80x300x5c0x180xad0x6a0x550xbf0xde0xca0xac0xda0xd50x770x590xa50xe00x350xdb0x950xb40xad0x0b0x900x450x590x740x120xaa0xed0x450x510x2b0x040x340x160x580xfc0x580x640x650x260xaa0xac0xac0x380x8b0x300xd90x280xf70xdb0x7a0x2f0x440xba0xe20x660xf00x0a0x9d0x370x530xef0xd80x290x800x370xba0xe50x5f0xef0x3d0x260xde0x830x750x080x0c0xd70x700x640x330x3a0xe50xcc0xcc0x4a0x6b0x4f0xe50xfc0x4a0xc60xa20x820x1a0x630x0f0x490x400x750x480x7d0x2f0xf20xbd0x190x610x520xd10x040xda0xf90x050x810x960xff0x840xcb0xfb0x310xe00x6a0x9f0x7a0x4a0x810xfe0x040xf10x800x9d0xd30xb00xf60x490x830x210x9f0xc90x830x1d0xbc0x990x570x660xc10x9a0xcd0x320xf60x6f";
//			byte[] inputb=ZipUtil.hexStringToByte(a1);
//			System.out.print(new String(inputb));
//			byte[] outputb=ZipUtil.ungzip(inputb);
//			log.info(new String(outputb));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final String bytesToHexString(byte[] bArray) {
	    StringBuffer sb = new StringBuffer(bArray.length);
	    String sTemp;
	    for (int i = 0; i < bArray.length; i++) {
	     sTemp = Integer.toHexString(0xFF & bArray[i]);
	     sb.append("0x");
	     if (sTemp.length() < 2)
	      sb.append(00);
//	     sb.append(sTemp.toUpperCase());
	     sb.append(sTemp);
	    }
	    return sb.toString();
	}
	
	public static byte[] hexStringToByte(String hex) {
	    int len = (hex.length() / 2);
	    byte[] result = new byte[len];
	    char[] achar = hex.toCharArray();
	    for (int i = 0; i < len; i++) {
	     int pos = i * 2;
	     result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
	    }
	    return result;
	}

	private static byte toByte(char c) {
	    byte b = (byte) "0123456789ABCDEF".indexOf(c);
	    return b;
	}
	
}