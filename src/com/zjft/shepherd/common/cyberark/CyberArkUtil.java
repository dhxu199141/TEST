package com.zjft.shepherd.common.cyberark;

import java.util.Properties;

import javapasswordsdk.PSDKPassword;
import javapasswordsdk.PSDKPasswordRequest;

/**
 * @author YANGDAN009
 * 
 */
public class CyberArkUtil {

	// propertise参数

	private String urlKey = "java.naming.provider.url";

	private String url;

	private String contextFactoryKey = "java.naming.factory.initial";

	// 默认使用这个
	private String contextFactory = "weblogic.jndi.WLInitialContextFactory";

	private String principalKey = "java.naming.security.principal";

	private String principal;

	private String credentialsKey = "java.naming.security.credentials";

	private String credentials = "";

	// CyberArk参数
	private String cyberArkCredFilePath;

	private String cyberArkSafe;

	private String cyberArkFolder;

	private String testObject;
	
	private String dbObject;
	
	private String ftpsObject;

	private boolean enableCyberArk = false;

	private boolean debugMode = false;

//	public static Log log = LogFactory.getLog(CyberArkUtil.class);
	/**
	 * 配置文件路径
	 */
	private static String Context_Properties = "D:/20120508/代码移交20120508/BOPA_SHEP/WEB-INF/context-int-brdis.properties";

	public static String getContext_Properties() {
		return Context_Properties;
	}

	public static void setContext_Properties(String context_Properties) {
		Context_Properties = context_Properties;
	}

	private static CyberArkUtil cyberArk = null;
	
	public static CyberArkUtil getInstance() {
		if(cyberArk == null) {
			try {
				Context_Properties ="/bankapp/bddc-atmv/conf/context-stg-brdis.properties" ;
				System.out.println("Context_Properties="+Context_Properties) ;
				cyberArk = new CyberArkUtil(new ProperFile(Context_Properties));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//log.error("初始化CyberArk异常: ", e);
				System.out.println("初始化CyberArk异常!") ;
			}
		}
		return cyberArk;
	}
	
	private CyberArkUtil(ProperFile proper) {
		// TODO Auto-generated constructor stub
//		this.contextFactory = proper.getProper("weblogic.jndi.WLInitialContextFactory");
//		this.url = proper.getProper("mis-brdis.jndi.url");
		this.principal = proper.getProper("mis-brdis.jndi.security.principal");
		this.credentials = proper.getProper("mis-brdis.jndi.security.credentials");

		this.cyberArkCredFilePath = proper.getProper("mis-brdis.cyberArk.CredFilePath");
		this.cyberArkSafe = proper.getProper("mis-brdis.cyberArk.Safe");
		this.cyberArkFolder = proper.getProper("mis-brdis.cyberArk.Folder");
		
		this.testObject = proper.getProper("mis-brdis.cyberArk.testuser.Object");
		this.dbObject = proper.getProper("mis-brdis.cyberArk.dbuser.Object");
		this.ftpsObject = proper.getProper("mis-brdis.cyberArk.ftpsuser.Object");
		
		this.enableCyberArk = proper.getProper("mis-brdis.cyberArk.Enable").equals("true") ? true : false;
		this.debugMode = proper.getProper("mis-brdis.cyberArk.debugMode").equals("true") ? true : false;

	}

	private Object getObject() throws Exception {
		// TODO Auto-generated method stub
		Properties plainProps = new Properties();
//		plainProps.put(urlKey, url);
//		plainProps.put(contextFactoryKey, contextFactory);
		plainProps.put(principalKey, principal);
		plainProps.put(credentialsKey, credentials);

		Properties encryptProps = getEncryptedEnvironment(plainProps);
		if (encryptProps != null) {
			return encryptProps;
		} else {
			return plainProps;
		}

	}

	private Properties getEncryptedEnvironment(Properties props) {
		System.out.println("CyberArk Enable:" + this.enableCyberArk);
		if (this.enableCyberArk == false) {
			return props;
		}
		String credential = "";
		try {
			PSDKPassword password = null;
			System.out.println("Start cyberArk processing!");
			PSDKPasswordRequest passRequest = new PSDKPasswordRequest();
			
			System.out.println("cyberArkCredFilePath:" + this.cyberArkCredFilePath);
			passRequest.setCredFilePath(this.cyberArkCredFilePath);
			
			System.out.println("cyberArkSafe:" + this.cyberArkSafe);
			passRequest.setSafe(this.cyberArkSafe);
			
			System.out.println("cyberArkFolder:" + this.cyberArkFolder);
			passRequest.setFolder(this.cyberArkFolder);
			
			// 获取cyberArkObject,默认为虚拟用户名称
			String cyberArkObject = props.getProperty(principalKey);
			//System.out.println("cyberArkObject:" + cyberArkObject);
			
			passRequest.setObject(cyberArkObject);

			// 获取密码
			password = javapasswordsdk.PasswordSDK.getPassword(passRequest);
			String user = password.getUserName();
		    String pwd = password.getContent();
		   // System.out.println("user=[" + user + "]    pwd=[" + pwd + "]");
			credential = password.getContent();
			//System.out.println("Credential get by CyberArk:[" + credential + "]");
			System.out.println("End cyberArk processing!");

			// 设置密码
			props.setProperty(credentialsKey, credential);

			System.out.println("JNDI Property Info Start!");
			//System.out.println(props.toString());
			System.out.println("JNDI Property Info End.");
			return props;
		} catch (Exception e) {
			System.out.println("CyperArch Exception !");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取测试用户的账号及密码
	 * @return
	 */
	public CybAccount getTestAccount() {
		CybAccount acc = null;
		CyberArkUtil cyb = CyberArkUtil.getInstance();
		cyb.principal = cyb.testObject;
		if(cyb == null) {
			System.out.println("获取帐户出错, [CyberArkUtil] 实例化失败");
		}
		else {
			try {
				Properties pro = ((Properties) cyb.getObject());
				acc = new CybAccount();
				acc.setUserName(pro.getProperty(cyb.principalKey));
				acc.setPassWord(pro.getProperty(cyb.credentialsKey));
				//System.out.println("TestAccount: " + acc.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("出现异常: ");
				e.printStackTrace();
			}
		}
		return acc;
	}

	/**
	 * 获取DB用户的账号及密码
	 * @return
	 */
	public CybAccount getDbAccount() {
		CybAccount acc = null;
		CyberArkUtil cyb = CyberArkUtil.getInstance();
		cyb.principal = cyb.dbObject;
		if(cyb == null) {
			System.out.println("获取帐户出错, [CyberArkUtil] 实例化失败");
		}
		else {
			try {
				Properties pro = ((Properties) cyb.getObject());
				acc = new CybAccount();
				acc.setUserName(pro.getProperty(cyb.principalKey));
				acc.setPassWord(pro.getProperty(cyb.credentialsKey));
				//System.out.println("DbAccount: " + acc.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("出现异常: ");
				e.printStackTrace();
			}
		}
		return acc;
	}


	/**
	 * 获取FTPS用户的账号及密码
	 * @return
	 */
	public CybAccount getFtpsAccount() {
		CybAccount acc = null;
		CyberArkUtil cyb = CyberArkUtil.getInstance();
		cyb.principal = cyb.ftpsObject;
		if(cyb == null) {
			System.out.println("获取帐户出错, [CyberArkUtil] 实例化失败");
		}
		else {
			try {
				Properties pro = ((Properties) cyb.getObject());
				acc = new CybAccount();
				acc.setUserName(pro.getProperty(cyb.principalKey));
				acc.setPassWord(pro.getProperty(cyb.credentialsKey));
				//System.out.println("FtpsAccount: " + acc.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("出现异常: ");
				e.printStackTrace();
			}
		}
		return acc;
	}
	
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getContextFactory() {
		return contextFactory;
	}

	public void setContextFactory(String contextFactory) {
		this.contextFactory = contextFactory;
	}

	public String getContextFactoryKey() {
		return contextFactoryKey;
	}

	public void setContextFactoryKey(String contextFactoryKey) {
		this.contextFactoryKey = contextFactoryKey;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getCredentialsKey() {
		return credentialsKey;
	}

	public void setCredentialsKey(String credentialsKey) {
		this.credentialsKey = credentialsKey;
	}

	public String getCyberArkCredFilePath() {
		return cyberArkCredFilePath;
	}

	public void setCyberArkCredFilePath(String cyberArkCredFilePath) {
		this.cyberArkCredFilePath = cyberArkCredFilePath;
	}

	public String getCyberArkFolder() {
		return cyberArkFolder;
	}

	public void setCyberArkFolder(String cyberArkFolder) {
		this.cyberArkFolder = cyberArkFolder;
	}

	public String getCyberArkObject() {
		return testObject;
	}

	public void setCyberArkObject(String cyberArkObject) {
		this.testObject = cyberArkObject;
	}

	public String getCyberArkSafe() {
		return cyberArkSafe;
	}

	public void setCyberArkSafe(String cyberArkSafe) {
		this.cyberArkSafe = cyberArkSafe;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public boolean isEnableCyberArk() {
		return enableCyberArk;
	}

	public void setEnableCyberArk(boolean enableCyberArk) {
		this.enableCyberArk = enableCyberArk;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getPrincipalKey() {
		return principalKey;
	}

	public void setPrincipalKey(String principalKey) {
		this.principalKey = principalKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlKey() {
		return urlKey;
	}

	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}

	public static void main(String[] args) {
//		ProperFile proper = new ProperFile("D:/context-BOPA-brdis.properties");
//		ProperFile proper = new ProperFile("D:/20120508/代码移交20120508/BOPA_SHEP/WEB-INF/context-BOPA-brdis.properties");

		CybAccount acc1 = CyberArkUtil.getInstance().getTestAccount();
		System.out.println(acc1);
		CybAccount acc2 = CyberArkUtil.getInstance().getDbAccount();
		System.out.println(acc2);
		CybAccount acc3 = CyberArkUtil.getInstance().getFtpsAccount();
		System.out.println(acc3);
//		if(ark == null)
//			return;
//		try {
//			Properties pro = ((Properties) ark.getObject());
//			String username = pro.getProperty(ark.principalKey);
//			String passwd = pro.getProperty(ark.credentialsKey);
//			System.out.println("username=[" + username + "]");
//			System.out.println("passwd=[" + passwd + "]");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
