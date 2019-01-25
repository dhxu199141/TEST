package com.zjft.shepherd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.DbConnection;
import com.zjft.shepherd.vo.AgentPwdInfo;
/** 
* <p>Title:文件传输DAO</p> 
* <p>Description: </p> 
* <p>Copyright: Copyright (c) 2009 </p> 
* <p>Company: zjft </p> 
* @version 1.0 
*/ 
public class AgentPwdDAO 
{
	private static Log log = LogFactory.getLog(AgentPwdDAO.class);
	private final static String DEV_ORG_QUERY = "select a.no,a.upper1,a.upper2,a.upper3,a.upper4,a.upper5  from org_table a,dev_base_info b where a.no=b.org_no and b.no=?";
	private final static String AEGNT_PWD_QUERY = "select no,org_grade_no,agent_password,agent_valid_date from org_table where (org_grade_no=2 or org_grade_no=3)";
	
	/**
	 * 获取ATM设备的atmp信息,并返回
	 * @param devNo 设备号
	 **/
	public static ArrayList<AgentPwdInfo> getAgentPwd(String devNo)
	{
		if(devNo == null || devNo.equals(""))
		{
			return null;
		}
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<AgentPwdInfo> pwdList = null;
		try 
		{
			connection = DbConnection.getConnection(true);
		
			pst = connection.prepareStatement(DEV_ORG_QUERY);
			pst.setString(1, devNo);
			rs = pst.executeQuery();
			if(rs != null && rs.next())
			{	
				String orgNo = rs.getString(1);
				String upper1 = rs.getString(2);
				String upper2 = rs.getString(3);
				String upper3 = rs.getString(4);
				String upper4 = rs.getString(5);
				String upper5 = rs.getString(6);
				StringBuffer sb = new StringBuffer();
				sb.append(AEGNT_PWD_QUERY).append(" and (no='").append(orgNo);
				if(upper1 != null && !upper1.equals(""))
				{
					sb.append("' or no='").append(upper1);
				}
				if(upper2 != null && !upper2.equals(""))
				{
					sb.append("' or no='").append(upper2);
				}
				if(upper3 != null && !upper3.equals(""))
				{
					sb.append("' or no='").append(upper3);
				}
				if(upper4 != null && !upper4.equals(""))
				{
					sb.append("' or no='").append(upper4);
				}
				if(upper5 != null && !upper5.equals(""))
				{
					sb.append("' or no='").append(upper5);
				}
				sb.append("') order by org_grade_no");
				pst = connection.prepareStatement(sb.toString());
				rs = pst.executeQuery();
				pwdList = new ArrayList<AgentPwdInfo>();
				while(rs != null && rs.next())
				{
					AgentPwdInfo aPwdInfo = new AgentPwdInfo();
					aPwdInfo.setOrgNo(rs.getString("no"));
					aPwdInfo.setOrgGradeNo(rs.getInt("org_grade_no"));
					aPwdInfo.setAgentPwd(rs.getString("agent_password"));
					aPwdInfo.setAgentValidDate(rs.getString("agent_valid_date"));
					pwdList.add(aPwdInfo);
				}
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("获取设备atmp配置信息错误");
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return pwdList;
	}
}
