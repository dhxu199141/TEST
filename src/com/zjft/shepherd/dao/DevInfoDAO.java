package com.zjft.shepherd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.DbConnection;
import com.zjft.shepherd.service.tcp.message.DevBaseInfoMessage;
import com.zjft.shepherd.vo.ChkDevInfo;
import com.zjft.shepherd.vo.DevAtmpInfo;
import com.zjft.shepherd.vo.DevBaseInfo;
/** 
* <p>Title:文件传输DAO</p> 
* <p>Description: </p> 
* <p>Copyright: Copyright (c) 2009 </p> 
* <p>Company: zjft </p> 
* @version 1.0 
*/ 
/**  
 *   
 * 类名称： DevInfoDAO
 * 类描述： 设备信息DAO层
 * 创建人：  
 * 修改信息：zhangdd 2017-04-10  V1.4.3
*   
*/ 
public class DevInfoDAO 
{
	private static Log log = LogFactory.getLog(DevInfoDAO.class);
	private final static String DEV_ATMP_SELECT = "select t.*,a.no,a.org_no,c.no,c.org_type from org_atmp_conf t ,dev_base_info a,org_table b,org_table c where a.org_no=b.no and (b.no=t.org_no or b.upper1=t.org_no or b.upper2=t.org_no or b.upper3=t.org_no or b.upper4=t.org_no or b.upper5=t.org_no) and c.no=t.org_no and a.no=? order by a.no,c.org_grade_no desc";
	private final static String CHK_DEV_INFO_QUERY = "select t.no,t.ip,t.away_flag,t.serial,a.name,b.name,c.groupvalue,d.name,t.org_no from dev_base_info t,dev_type_table a,dev_catalog_table b,rvc_project_type c,dev_vendor_table d where t.no=? and t.dev_type=a.no and t.dev_catalog=b.no and t.atmc_soft=c.groupid and t.dev_vendor=d.no";
	private final static String DEV_DIFF_INFO_EXISTS = "select * from dev_diff_info where dev_no=?";
	private final static String DEV_DIFF_INFO_DELETE = "delete from dev_diff_info where dev_no=?";
	private final static String DEV_DIFF_INFO_INSERT = "insert into dev_diff_info(dev_no,diff_date,c_ip,v_ip,c_org_no,v_org_no,c_dev_type,v_dev_type,c_dev_vendor,v_dev_vendor,c_dev_catalog,v_dev_catalog,c_atmc_soft,v_atmc_soft,c_install_type,v_install_type,c_serial_no,v_serial_no) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DEVBASEINFO_SELECT_ENCODED = "SELECT DEK_ENCODED FROM dev_base_info WHERE no=?";
	private static final String DEVBASEINFO_SELECT = "SELECT * FROM dev_base_info WHERE no=?";
	private static final String DEVBASEINFO_SELECT_LOG ="SELECT a.no,a.ip,a.comm_packet,a.zip_type,a.serial FROM dev_base_info a WHERE a.no = ?";
	
	
	/**
	 * 获取dev_base_info 所有信息,并返回
	 * @param devNo 设备号
	 **/
	public static DevBaseInfo getDevBaseInfo(String devNo)
	{
		if(devNo == null || devNo.equals(""))
		{
			return null;
		}
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try 
		{
			connection = DbConnection.getConnection(true);
		
			pst = connection.prepareStatement(DEVBASEINFO_SELECT_LOG);
			pst.setString(1, devNo);

			rs = pst.executeQuery();
			
			if(rs!=null&&rs.next())
			{	
				 DevBaseInfo devBaseInfo = new DevBaseInfo();
				 devBaseInfo.setDevNo(rs.getString("no"));
				 devBaseInfo.setIp(rs.getString("ip"));
				 devBaseInfo.setPacketLen(Integer.valueOf(rs.getString("comm_packet")));
				 devBaseInfo.setVizpType(Integer.valueOf(rs.getString("zip_type")));
				 devBaseInfo.setSerial(rs.getString("serial"));
				 return devBaseInfo;
			}
			else
			{
				return null;
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("获取设备号为【"+devNo+"】的设备信息错误");
			return null;
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
	}
	
	
	/**
	 * 获取ATM设备的atmp信息,并返回
	 * @param devNo 设备号
	 **/
	public static DevAtmpInfo getDevAtmpInfo(String devNo)
	{
		if(devNo == null || devNo.equals(""))
		{
			return null;
		}
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try 
		{
			connection = DbConnection.getConnection(true);
		
			pst = connection.prepareStatement(DEV_ATMP_SELECT);
			pst.setString(1, devNo);

			rs = pst.executeQuery();
			
			if(rs!=null&&rs.next())
			{	
				DevAtmpInfo devAtmpInfo = new DevAtmpInfo();
				devAtmpInfo.setAtmpIp(rs.getString("atmp_ip"));
				devAtmpInfo.setAtmpPort(rs.getInt("atmp_port"));
				return devAtmpInfo;
			}
			else
			{
				return null;
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("获取设备号为【"+devNo+"】的atmp配置信息错误");
			return null;
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
	}
	
	/**
	 * 获取需要校验的ATM设备信息,并返回
	 * @param devNo 设备号
	 **/
	public static ChkDevInfo getChkDevInfo(String devNo)
	{
		if(devNo == null || devNo.equals(""))
		{
			return null;
		}
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try 
		{
			connection = DbConnection.getConnection(true);
		
			pst = connection.prepareStatement(CHK_DEV_INFO_QUERY);
			pst.setString(1, devNo);

			rs = pst.executeQuery();
			
			if(rs != null && rs.next())
			{	
				ChkDevInfo chkDevInfo = new ChkDevInfo();
				chkDevInfo.setDevNo(devNo);
				chkDevInfo.setIp(rs.getString(2));
				chkDevInfo.setInstallType(rs.getString(3));
				chkDevInfo.setSerialNo(rs.getString(4));
				chkDevInfo.setDevType(rs.getString(5));
				chkDevInfo.setDevCatalog(rs.getString(6));
				chkDevInfo.setAtmcSoft(rs.getString(7));
				chkDevInfo.setDevVendor(rs.getString(8));
				chkDevInfo.setOrgNo(rs.getString(9));
				return chkDevInfo;
			}
			else
			{
				return null;
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("获取需要校验的设备号为【"+devNo+"】的ATM设备信息错误");
			return null;
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
	}
	
	/**
	 * 判断设备差异信息是否已经存在
	 * @param devNo 设备号
	 **/
	public static boolean isDiffInfoExists(String devNo)
	{
		if(devNo == null || devNo.equals(""))
		{
			return false;
		}
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try 
		{
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(DEV_DIFF_INFO_EXISTS);
			pst.setString(1, devNo);
			rs = pst.executeQuery();
			if(rs != null && rs.next())
			{	
				return true;
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("判断设备号为【"+devNo+"】的差异信息是否已经存在错误");
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * 删除设备差异信息
	 * @param devNo 设备号
	 **/
	public static boolean delDiffInfo(String devNo)
	{
		if(devNo == null || devNo.equals(""))
		{
			return false;
		}
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try 
		{
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(DEV_DIFF_INFO_DELETE);
			pst.setString(1, devNo);
			pst.executeUpdate();
			return true;
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("删除设备号为【"+devNo+"】的差异信息错误");
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * 保存设备差异信息
	 **/
	public static boolean saveDiffInfo(ChkDevInfo chkDevInfo, DevBaseInfoMessage message)
	{
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try 
		{
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(DEV_DIFF_INFO_INSERT);
			pst.setString(1, message.getChkTermno());
			pst.setString(2, CalendarUtil.getSysTimeYMDHMS());
			pst.setString(3, message.getChkIp());
			pst.setString(4, chkDevInfo == null ? "" : chkDevInfo.getIp());
			pst.setString(5, message.getChkOrgNo());
			pst.setString(6, chkDevInfo == null ? "" : chkDevInfo.getOrgNo());
			pst.setString(7, message.getChkDevType());
			pst.setString(8, chkDevInfo == null ? "" : chkDevInfo.getDevType());
			pst.setString(9, message.getChkDevVendor());
			pst.setString(10, chkDevInfo == null ? "" : chkDevInfo.getDevVendor());
			pst.setString(11, message.getChkDevCatalog());
			pst.setString(12, chkDevInfo == null ? "" : chkDevInfo.getDevCatalog());
			pst.setString(13, message.getChkAtmcSoft());
			pst.setString(14, chkDevInfo == null ? "" : chkDevInfo.getAtmcSoft());
			pst.setString(15, message.getChkInstallType());
			pst.setString(16, chkDevInfo == null ? "" : chkDevInfo.getInstallType());
			pst.setString(17, message.getChkSerialNo());
			pst.setString(18, chkDevInfo == null ? "" : chkDevInfo.getSerialNo());
			
			if(pst.executeUpdate() > 0)
			{	
				return true;
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("保存设备差异信息错误");
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * 根据设备号获取某台设备的密钥
	 **/
	public static HashMap<String,String> getDevDekEncodedBydevNo(String devNo)
	{
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String,String> map = new HashMap<String,String>();
		
		try 
		{
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(DEVBASEINFO_SELECT_ENCODED);
			pst.setString(1, devNo);

			rs = pst.executeQuery();
			
			if(rs!=null&&rs.next())
			{	
				map.put("encoded", rs.getString(1));
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("获取设备号为【"+devNo+"】的设备密钥错误") ;
		}
		finally
		{
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return map;
	}
	
	/**
	 * 根据设备号获取某台设备的日志路径
	 **/
	public static String getDevLogPathBydevNo(String devNo) {
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(DEVBASEINFO_SELECT);
			pst.setString(1, devNo);

			rs = pst.executeQuery();
			
			if(rs != null && rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				for(int i = 1; i <= rsmd.getColumnCount(); i++){
					if (rsmd.getColumnName(i).equalsIgnoreCase("dev_log_path")) {
						return rs.getString(i);
					}
				}
			}
		} catch (SQLException e) {
			log.error("获取设备号为【"+devNo+"】的日志路径：" + e.getMessage(), e) ;
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return null;
	}
	
	/**
	 * 获取文件传输速度
	 * trans_rate字段可能不存在，不存在时不限制传输速度
	 * @param devNo 设备号
	 * @author cqluo
	 * @return 文件传输速度单位Kb/s，为空时不限速
	 */
	public static String getTransRate(String devNo) {

		Connection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);

			pst = connection.prepareStatement(DEVBASEINFO_SELECT);
			pst.setString(1, devNo);

			rs = pst.executeQuery();

			if (rs != null && rs.next()) {
				return rs.getString("trans_rate");
			}
		} catch (SQLException e) {
			
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return null;
	}
	
	/**
	 * 获取设备端ATMC软件类型
	 * 10002：WSAP(C/S)  10003：WSAPPlus(C/S)
	 * @param devNo 设备号
	 * @author zhangdd
	 * @since 2017-04-10
	 * @version V1.4.3
	 * @return ATMC软件类型
	 */
	public static String getAtmcSoft(String devNo) {

		Connection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);

			pst = connection.prepareStatement(DEVBASEINFO_SELECT);
			pst.setString(1, devNo);

			rs = pst.executeQuery();

			if (rs != null && rs.next()) {
				return rs.getString("atmc_soft");
			}
		} catch (SQLException e) {
			
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return null;
	}
	
	
}
