package com.zjft.shepherd.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.key.ProxoolUtil;
import com.zjft.shepherd.property.ProperFile;
import com.zjft.shepherd.rmi.RmiShepherdServer;
import com.zjft.shepherd.common.StringUtil;

/**  
 *   
 * �����ƣ� SystemCons
 * �������� ϵͳ���в���
 * �����ˣ�  
 * �޸���Ϣ��zhangdd 2017-04-13 V1.4.3
*   
*/ 
public class  SystemCons
{
	private static Log log = LogFactory.getLog(SystemCons.class);	
	
	
	private static String localPort="50003";//V��ҵ������˿�
	private static String systemLanguage="ATTR_zh_CN.properties";
	
	private static int activeCount=0;
	private static String dbAlias="";
	private static String dbDriver="";
	private static String dbUrl="";
	private static String dbUser="";
	private static String dbPwd="";
	private static int minConn=0;
	private static int maxConn=0;

	private static String rmiip="";
	private static String rmiport="";
	private static String rmiserver="";
	private static String remotePort="";//Remoteҵ������˿�
	
	private static String localFilePort="50004";
	private static String encrypt="0";
	private static String houseKeepingTestSql = null;
	
	private static String bankVersion = null;
	private static String atmvIp = null;
	private static String atmvWebIp = null;

	private static boolean messageEncrypt=false;  //�����Ƿ�ӽ���
	
	private static String versionResultPort; //���պ�̨Jar����Ϣ�����������˿�
	
	private static int cyberarkKey = 0;
	
	private static int uploadBytesBuffer = 8;//�ϴ��ļ���С����,Ĭ��8kb�����500kb
	
	private static int dowloadBytesBuffer = 1024;//�����ļ���С����,Ĭ����1M,���ֵ2M
	
	private static String defaultAtmLogRule;
	//  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ�ATMC��־�ļ�·���䶯 start
	private static String wsntAtmLogPath;
		
	private static String isPushMessage;
			
	static
	{
		SystemCons.loadSysParam();
	}
	/**  
	 * �������� loadSysParam
	 * �������ܣ�����ϵͳ���� 
	 * ������̣�
	 * ������������� 
	 * ������������� 
	 * �쳣���������� 
	 * �����ˣ�  
	 * �޸���Ϣ��zhangdd 2017-04-13 V1.4.3
	*/ 
	public  static void loadSysParam() {
		try{
			//ZIJINMOD ��ȫ�� 2013-12-23 �޸ļ���ϵͳ������·��
			String filepath = RmiShepherdServer.Shepherd_Path;
			log.info("�����ļ�·����"+filepath);
			ProperFile proper = new ProperFile(filepath);
			
			localPort = StringUtil.parseString(proper.getProper("localPort"));
			
			activeCount = StringUtil.ch2Int(StringUtil.parseString(proper.getProper("activeCount")));
			dbAlias = StringUtil.parseString(proper.getProper("dbAlias"));
			dbDriver = StringUtil.parseString(proper.getProper("dbDriver"));
			dbUrl = StringUtil.parseString(proper.getProper("dbUrl"));
			dbUser = StringUtil.parseString(proper.getProper("dbUser"));
			dbPwd = StringUtil.parseString(proper.getProper("dbPwd"));
			minConn = StringUtil.ch2Int(StringUtil.parseString(proper.getProper("minConn")));
			maxConn = StringUtil.ch2Int(StringUtil.parseString(proper.getProper("maxConn")));
			rmiport = StringUtil.parseString(proper.getProper("rmiport"));
			rmiip = StringUtil.parseString(proper.getProper("rmiip"));
			rmiserver = StringUtil.parseString(proper.getProper("rmiserver"));
			remotePort = StringUtil.parseString(proper.getProper("remotePort"));
			localFilePort = StringUtil.parseString(proper.getProper("localFilePort"));
			encrypt = StringUtil.parseString(proper.getProper("encrypt"));
			houseKeepingTestSql = proper.getProper("houseKeepingTestSql");
			bankVersion = proper.getProper("bankVersion");
			atmvIp = proper.getProper("atmvIp");
			atmvWebIp = proper.getProper("atmvWebIp");
			isPushMessage = proper.getProper("IsPushMessage");

			if (StringUtil.isNullorEmpty(isPushMessage)) {
				isPushMessage = "true";
			}
			if (StringUtil.isNullorEmpty(atmvWebIp)) {
				atmvWebIp = atmvIp;
			}
			String language = StringUtil.parseString(proper.getProper("systemLanguage"));
			systemLanguage = (language == null || language.equals("")) ? systemLanguage : language;
			messageEncrypt = StringUtil.parseString(proper.getProper("message.encrypt")).equals("1")?true:false;
			versionResultPort = StringUtil.parseString(proper.getProper("versionResultPort"));
			
			if (encrypt.equals("1")){
				dbUser=ProxoolUtil.dncryptData(dbUser);
				dbPwd=ProxoolUtil.dncryptData(dbPwd);
			}
			
			try {
				cyberarkKey = Integer.parseInt(StringUtil.parseString(proper.getProper("cyberarkKey")));
			} catch (Exception e) {
				log.debug("��ȡcyberarkKeyʧ��,ʹ��Ĭ��ֵ");
			}
			
			try {
				uploadBytesBuffer = Integer.parseInt(StringUtil.parseString(proper.getProper("uploadBytesBuffer")));
				
				uploadBytesBuffer = uploadBytesBuffer > 500 ? 500 : uploadBytesBuffer;
				
				uploadBytesBuffer = uploadBytesBuffer < 4 ? 4 : uploadBytesBuffer;
			} catch (Exception e) {
				log.debug("�ϴ��ļ�ʱÿ�ζ�ȡ�ļ���С��������ʧ��,ʹ��Ĭ��ֵ");
			}
			log.debug("uploadBytesBuffer=" + uploadBytesBuffer);
									
			try {
				dowloadBytesBuffer = Integer.parseInt(StringUtil.parseString(proper.getProper("dowloadBytesBuffer")));
				
				dowloadBytesBuffer = dowloadBytesBuffer > 2048 ? 2048 : dowloadBytesBuffer;
				
				dowloadBytesBuffer = dowloadBytesBuffer < 1024 ? 1024 : dowloadBytesBuffer;
				
			} catch (Exception e) {
				log.debug("�������ļ����豸��ÿ�η����ļ���С��������ʧ��,ʹ��Ĭ��ֵ");
			}
			
			log.debug("dowloadBytesBuffer=" + dowloadBytesBuffer);
			
			defaultAtmLogRule = StringUtil.parseString(proper.getProper("DefaultAtmLogPath"));
			//  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ�ATMC��־�ļ�·���䶯 
			wsntAtmLogPath = StringUtil.parseString(proper.getProper("WsntAtmLogPath"));
			if("".equals(defaultAtmLogRule)) {
				defaultAtmLogRule = "C:\\WSAP\\DATA\\{log_date}.J";
			}
			log.debug("defaultAtmLogRule=" + defaultAtmLogRule);
			//  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ�ATMC��־�ļ�·���䶯 start
			if("".equals(wsntAtmLogPath)) {
				wsntAtmLogPath = "C:\\WSAPNT\\PUBLIC\\data\\{log_date}.J";
			}
			log.debug("wsntAtmLogPath=" + wsntAtmLogPath);
		//  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ�ATMC��־�ļ�·���䶯 end
			proper.close();
		} catch(Exception e) {
			
		}

	}

	public static String getSystemLanguage() {
		return systemLanguage;
	}

	public static void setSystemLanguage(String systemLanguage) {
		SystemCons.systemLanguage = systemLanguage;
	}

	public static String getEncrypt() {
		return encrypt;
	}

	public static void setEncrypt(String encrypt) {
		SystemCons.encrypt = encrypt;
	}

	public static String getLocalPort() {
		return localPort;
	}

	public static void setLocalPort(String localPort) {
		SystemCons.localPort = localPort;
	}

	public static int getactiveCount() {
		return activeCount;
	}

	public static void setactiveCount(int activeCount) {
		SystemCons.activeCount = activeCount;
	}

	public static String getdbAlias() {
		return dbAlias;
	}

	public static void setdbAlias(String dbAlias) {
		SystemCons.dbAlias = dbAlias;
	}

	public static String getdbDriver() {
		return dbDriver;
	}

	public static void setdbDriver(String dbDriver) {
		SystemCons.dbDriver = dbDriver;
	}

	public static String getdbUrl() {
		return dbUrl;
	}

	public static void setdbUrl(String dbUrl) {
		SystemCons.dbUrl = dbUrl;
	}

	public static String getdbUser() {
		return dbUser;
	}

	public static void setdbUser(String dbUser) {
		SystemCons.dbUser = dbUser;
	}

	public static String getdbPwd() {
		return dbPwd;
	}

	public static void setdbPwd(String dbPwd) {
		SystemCons.dbPwd = dbPwd;
	}

	public static int getminConn() {
		return minConn;
	}

	public static void setminConn(int minConn) {
		SystemCons.minConn = minConn;
	}

	public static int getmaxConn() {
		return maxConn;
	}

	public static void setmaxConn(int maxConn) {
		SystemCons.maxConn = maxConn;
	}

	public static String getRmiport() {
		return rmiport;
	}

	public static void setRmiport(String rmiport) {
		SystemCons.rmiport = rmiport;
	}

	public static String getRmiserver() {
		return rmiserver;
	}

	public static void setRmiserver(String rmiserver) {
		SystemCons.rmiserver = rmiserver;
	}

	public static String getRmiip() {
		return rmiip;
	}

	public static void setRmiip(String rmiip) {
		SystemCons.rmiip = rmiip;
	}

	public static String getLocalFilePort() {
		return localFilePort;
	}

	public static void setLocalFilePort(String localFilePort) {
		SystemCons.localFilePort = localFilePort;
	}

	public static String getHouseKeepingTestSql() {
		return houseKeepingTestSql;
	}

	public static void setHouseKeepingTestSql(String houseKeepingTestSql) {
		SystemCons.houseKeepingTestSql = houseKeepingTestSql;
	}

	public static String getBankVersion() {
		return bankVersion;
	}

	public static void setBankVersion(String bankVersion) {
		SystemCons.bankVersion = bankVersion;
	}

	public static String getAtmvIp() {
		return atmvIp;
	}

	public static void setAtmvIp(String atmvIp) {
		SystemCons.atmvIp = atmvIp;
	}

	public static String getAtmvWebIp() {
		return atmvWebIp;
	}

	public static void setAtmvWebIp(String atmvWebIp) {
		SystemCons.atmvWebIp = atmvWebIp;
	}

	public static boolean isMessageEncrypt() {
		return messageEncrypt;
	}

	public static String getVersionResultPort() {
		return versionResultPort;
	}

	public static void setVersionResultPort(String versionResultPort) {
		SystemCons.versionResultPort = versionResultPort;
	}

	public static int getCyberarkKey() {
		return cyberarkKey;
	}

	public static void setCyberarkKey(int cyberarkKey) {
		SystemCons.cyberarkKey = cyberarkKey;
	}

	public static int getDowloadBytesBuffer() {
		return dowloadBytesBuffer;
	}

	public static void setDowloadBytesBuffer(int dowloadBytesBuffer) {
		SystemCons.dowloadBytesBuffer = dowloadBytesBuffer;
	}

	public static int getUploadBytesBuffer() {
		return uploadBytesBuffer;
	}

	public static void setUploadBytesBuffer(int uploadBytesBuffer) {
		SystemCons.uploadBytesBuffer = uploadBytesBuffer;
	}

	public static String getRemotePort() {
		return remotePort;
	}

	public static void setRemotePort(String remotePort) {
		SystemCons.remotePort = remotePort;
	}

	public static String getDefaultAtmLogRule() {
		return defaultAtmLogRule;
	}

	public static void setDefaultAtmLogRule(String defaultAtmLogRule) {
		SystemCons.defaultAtmLogRule = defaultAtmLogRule;
	}

	//  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ�ATMC��־�ļ�·���䶯 start
	public static String getWsntAtmLogPath() {
		return wsntAtmLogPath;
	}

	public static void setWsntAtmLogPath(String wsntAtmLogPath) {
		SystemCons.wsntAtmLogPath = wsntAtmLogPath;
	}
	//  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ�ATMC��־�ļ�·���䶯 end

	public static String getIsPushMessage() {
		return isPushMessage;
	}

	public static void setIsPushMessage(String isPushMessage) {
		SystemCons.isPushMessage = isPushMessage;
	}
	
}
