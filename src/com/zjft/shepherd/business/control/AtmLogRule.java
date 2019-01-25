package com.zjft.shepherd.business.control;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zjft.shepherd.common.StringUtil;
import com.zjft.shepherd.common.SystemCons;
import com.zjft.shepherd.dao.DevInfoDAO;

/**  
 *   
 * �����ƣ� AtmLogRule
 * �������� 
 * �����ˣ�  
 * �޸���Ϣ��zhangdd 2017-04-13 v1.4.3
*   
*/ 
public class AtmLogRule {

	/**  
	 * �������� getAtmLogRule
	 * �������ܣ���Թ�����豸·������ƥ��   ͬʱ֧������ATMC������ͣ�WSAP  WSAPPlus
	 * ������̣�
	 * ������������� logDate   dev_no
	 * ������������� �豸ATMCӦ����־�ļ�·��
	 * �쳣���������� 
	 * �����ˣ�  
	 * �޸���Ϣ��zhangdd 2017-04-14 V1.4.3
	*/ 
	public static String getAtmLogRule(HashMap<String, Object> param) {
		String logDate = StringUtil.parseString(param.get("logDate").toString());
		String devNo = StringUtil.parseString(param.get("devNo").toString());
		//zjmod by zhangdd 2017-04-13 �����豸���͵�ATMC������ͣ��ж��豸��Ӧ����־�ļ�·����start
		String atmcSoft = StringUtil.parseString(DevInfoDAO.getAtmcSoft(devNo));

		//�豸����־·��
		String  atmcLogFile = "";
		if("10003".equals(atmcSoft)){
			atmcLogFile = SystemCons.getWsntAtmLogPath();
			atmcLogFile = atmcLogFile.replace("{log_date}", logDate);
			atmcLogFile = atmcLogFile.replace("{dev_no}", devNo);
		}else {
			atmcLogFile = SystemCons.getDefaultAtmLogRule();
			atmcLogFile = atmcLogFile.replace("{log_date}", logDate);
			atmcLogFile = atmcLogFile.replace("{dev_no}", devNo);
		}
		//zjmod by zhangdd 2017-04-13 �����豸���͵�ATMC������ͣ��ж��豸��Ӧ����־�ļ�·����end
				
		return atmcLogFile;
	}

	/**
	 * ��Թ�����豸·������ƥ��
	 * @param logDate
	 * @return �豸ATMCӦ����־�ļ�·��
	 * */
	public static String getAtmLogRule(String logDate) {
		String  atmcLogFile = SystemCons.getDefaultAtmLogRule();
		Pattern pattern = Pattern.compile("\\{log_date\\}");
		Matcher matcher = pattern.matcher(atmcLogFile);
		return matcher.replaceAll(logDate);
	}
	
	public static String getAtmcLogPorp() {
		String  atmcLogFile = SystemCons.getDefaultAtmLogRule();
		return atmcLogFile.substring(atmcLogFile.indexOf("."));
	}
	
}
