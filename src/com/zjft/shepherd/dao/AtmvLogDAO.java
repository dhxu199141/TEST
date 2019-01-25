package com.zjft.shepherd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.DbConnection;

/** 
* <p>Title:日志操作DAO</p> 
* <p>Description: </p> 
* <p>Copyright: Copyright (c) 2016 </p> 
* <p>Company: zjft </p> 
* @version 1.0 
*/
public class AtmvLogDAO {
	private static Log log = LogFactory.getLog(AtmvLogDAO.class);
	private static final String ATMV_LOG_PATH_SELECT= "SELECT a.log_path,a.file_type FROM Atmv_Log_Path a where a.log_type =? ";
	
	/**
	 * 获取amtc_log_path 所有信息,并返回
	 * @param devNo 设备号
	 **/
	public static List<AtmvLogPathInfo>  getAtmcLogPathInfo(String appType)
	{	
		List<AtmvLogPathInfo> list = new ArrayList<AtmvLogPathInfo>();
		if(appType == null || appType.equals(""))
		{
			return null;
		}
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try 
		{
			connection = DbConnection.getConnection(true);
		
			pst = connection.prepareStatement(ATMV_LOG_PATH_SELECT);
			pst.setString(1, appType);
			
			rs = pst.executeQuery();

			while(rs!=null && rs.next())
			{	
				AtmvLogPathInfo atmvLogPathInfo = new AtmvLogPathInfo();
				atmvLogPathInfo.setLogPath(rs.getString("log_path"));
				atmvLogPathInfo.setFileType(rs.getString("file_type"));
				list.add(atmvLogPathInfo);
			}
		} 
		
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("获取设备端日志失败");
			return null;
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return list;
	}
	
	
}
