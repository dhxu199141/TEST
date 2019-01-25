package com.zjft.shepherd.business.control;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zjft.shepherd.common.StringUtil;
import com.zjft.shepherd.common.SystemCons;
import com.zjft.shepherd.dao.DevInfoDAO;

/**  
 *   
 * 类名称： AtmLogRule
 * 类描述： 
 * 创建人：  
 * 修改信息：zhangdd 2017-04-13 v1.4.3
*   
*/ 
public class AtmLogRule {

	/**  
	 * 函数名： getAtmLogRule
	 * 函数功能：针对规则对设备路径进行匹配   同时支持两种ATMC软件类型：WSAP  WSAPPlus
	 * 处理过程：
	 * 输入参数描述： logDate   dev_no
	 * 输出参数描述： 设备ATMC应用日志文件路径
	 * 异常处理描述： 
	 * 创建人：  
	 * 修改信息：zhangdd 2017-04-14 V1.4.3
	*/ 
	public static String getAtmLogRule(HashMap<String, Object> param) {
		String logDate = StringUtil.parseString(param.get("logDate").toString());
		String devNo = StringUtil.parseString(param.get("devNo").toString());
		//zjmod by zhangdd 2017-04-13 根据设备上送的ATMC软件类型，判断设备对应的日志文件路径。start
		String atmcSoft = StringUtil.parseString(DevInfoDAO.getAtmcSoft(devNo));

		//设备端日志路径
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
		//zjmod by zhangdd 2017-04-13 根据设备上送的ATMC软件类型，判断设备对应的日志文件路径。end
				
		return atmcLogFile;
	}

	/**
	 * 针对规则对设备路径进行匹配
	 * @param logDate
	 * @return 设备ATMC应用日志文件路径
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
