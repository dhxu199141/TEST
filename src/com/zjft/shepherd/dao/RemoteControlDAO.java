package com.zjft.shepherd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.DbConnection;
import com.zjft.shepherd.common.SystemLanguage;

/**
 * 远程控制dao层接口
 * @author cqluo
 *
 */
public class RemoteControlDAO {
	
	private static Log log = LogFactory.getLog(RemoteControlDAO.class);
	
	private static final String REMOTE_TRACE_INSERT = "INSERT INTO REMOTE_TRACE (LOGIC_ID,COMMAND_ID,DEV_NO,OP_NO,OP_TIME,STATUS,CONTENT) VALUES (?,?,?,?,?,?,?)";
	private static final String REMOTE_TRACE_STATUS_UPDATE = "UPDATE REMOTE_TRACE SET STATUS = ? WHERE LOGIC_ID = ?";
	private static final String REMOTE_FILE_DOWN_INFO_INSERT = "INSERT INTO REMOTE_FILE_DOWN_INFO (LOGIC_ID,TRACE_LOGIC_ID,REMOTE_FILE,LOCAL_FILE,REMOTE_HANDLE_FILE,PROGRESS) VALUES (?,?,?,?,?,?)";
	private static final String REMOTE_FILE_DOWN_INFO_PROGRESS_UPDATE = "UPDATE REMOTE_FILE_DOWN_INFO SET PROGRESS = ? WHERE LOGIC_ID = ?";
	private static final String REMOTE_FILE_DOWN_SELECT1 = "SELECT LOGIC_ID,TRACE_LOGIC_ID,REMOTE_FILE,LOCAL_FILE,REMOTE_HANDLE_FILE,PROGRESS FROM REMOTE_FILE_DOWN_INFO WHERE TRACE_LOGIC_ID = ? ";
	private static final String REMOTE_SERIALNOINFO_UPDATE = "UPDATE DEV_SOFTWARE_INFO SET SERIAL_NO_INFO_STATUS = ? WHERE DEV_NO = ?";
	private static final String REMOTE_TXTINFO_UPDATE = "UPDATE DEV_SOFTWARE_INFO SET TXT_INFO_STATUS = ? WHERE DEV_NO = ?";
	private static final String NEW_REMOTE_TRACE_INSERT = "INSERT INTO REMOTE_TRACE (LOGIC_ID,COMMAND_ID,DEV_NO,OP_NO,OP_TIME,STATUS,CONTENT,CACHE_FALG,COUNT_ALL_FLAG,CASHUNIT_COUNT,CASHUNIT_LIST,CHECK_TYPE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	
	/**
	 * 记录远程控制信息
	 * @param cmdName 远程控制命令
	 * @param opNo 操作人
	 * @param devNo 设备号
	 * @param result 操作结果
	 * @param opContent 详情
	 * @return
	 */
	public static RemoteTrace saveRemoteTrace(String cmdName,String opNo,String devNo,String result,String opContent){
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(REMOTE_TRACE_INSERT);
			String logicId = UUID.randomUUID().toString();
			pst.setString(1, logicId);
			pst.setString(2, cmdName);
			pst.setString(3, devNo);
			pst.setString(4, opNo);
			String opTime = CalendarUtil.getSysTimeYMDHMS();
			pst.setString(5, opTime);
			pst.setString(6, result);
			pst.setString(7, SystemLanguage.getMainDev_no() + "|" +devNo+ "|" + SystemLanguage.getSrcOperateAction() + "|" + opContent);
			if(pst.executeUpdate() > 0) {
				RemoteTrace remoteTrace = new RemoteTrace();
				remoteTrace.setLogicId(logicId);
				remoteTrace.setCommandId(cmdName);
				remoteTrace.setDevNo(devNo);
				remoteTrace.setOpNo(opNo);
				remoteTrace.setOpTime(opTime);
				remoteTrace.setStatus(result);
				remoteTrace.setContent(SystemLanguage.getMainDev_no() + "|" +devNo+ "|" + SystemLanguage.getSrcOperateAction() + "|" + opContent);
				return remoteTrace;
			}
		} catch (SQLException e) {
			log.error("记录远程控制信息错误:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return null;
	}
	
	/**
	 * 记录远程控制信息
	 * @return
	 */
	public static Boolean saveRemoteTrace(Map<String, Object> paraMap){
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String cmdName=paraMap.get("cmdName").toString();
			String msgId=paraMap.get("msgId").toString();
			String opNo=paraMap.get("userId").toString();
			String devNo=paraMap.get("devNo").toString();
			String opContent=paraMap.get("opContent").toString();
			String result=paraMap.get("result").toString();
			String commandCache=paraMap.get("commandCache").toString();
			String cimCountAll=paraMap.get("cimCountAll").toString();
			String cimCashUnitCount=paraMap.get("cimCashUnitCount").toString();
			String cashUnitList=paraMap.get("cashUnitList").toString();
			String checkType=paraMap.get("checkType").toString();

			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(NEW_REMOTE_TRACE_INSERT);
//			String logicId = UUID.randomUUID().toString();
			pst.setString(1, msgId);
			pst.setString(2, cmdName);
			pst.setString(3, devNo);
			pst.setString(4, opNo);
			String opTime = CalendarUtil.getSysTimeYMDHMS();
			pst.setString(5, opTime);
			pst.setString(6, result);
			pst.setString(7, SystemLanguage.getMainDev_no() + "|" +devNo+ "|" + SystemLanguage.getSrcOperateAction() + "|" + opContent);
			pst.setString(8, commandCache);
			pst.setString(9, cimCountAll);
			pst.setString(10, cimCashUnitCount);
			pst.setString(11, cashUnitList);
			pst.setString(12, checkType);
			if(pst.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			log.error("记录远程控制信息错误:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}

	
	/**
	 * 更新远程控制结果记录
	 * @param logicId 远程控制操作记录编号
	 * @param status 结果
	 */
	public static Boolean updateRemoteTraceStatus(String logicId, String status) {
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(REMOTE_TRACE_STATUS_UPDATE);
			pst.setString(1, status);
			pst.setString(2, logicId);
			if(pst.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			log.error("更新远程控制结果失败:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * 保存文件下载记录信息
	 * @param tracelogicId 文件下载远程控制编号
	 * @param remoteFile 远程文件
	 * @param locfilename 本地文件
	 * @param compressfile 压缩文件
	 * @param progress 进度
	 * @return
	 */
	public static RemoteFileDownInfo saveFileDownInfo(String tracelogicId,
			String remoteFile, String locfilename, String compressfile, double progress) {
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(REMOTE_FILE_DOWN_INFO_INSERT);
			String logicId = UUID.randomUUID().toString();
			pst.setString(1, logicId);
			pst.setString(2, tracelogicId);
			pst.setString(3, remoteFile);
			pst.setString(4, locfilename);
			pst.setString(5, compressfile);
			pst.setDouble(6, progress);
			if(pst.executeUpdate() > 0) {
				RemoteFileDownInfo downInfo = new RemoteFileDownInfo();
				downInfo.setLogicId(logicId);
				downInfo.setRemoteFile(remoteFile);
				downInfo.setLocalFile(locfilename);
				downInfo.setTraceLogicId(tracelogicId);
				downInfo.setRemoteHandleFile(compressfile);
				downInfo.setProgress(progress);
				return downInfo;
			}
		} catch (SQLException e) {
			log.error("更新远程控制结果失败:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return null;
	}
	
	
	/**
	 * 更新文件下载进度
	 * @param logicId 下载编号
	 * @param propress 进度
	 * @return
	 */
	public static Boolean updateRemoteFileDownPropress(String logicId, Double progress) {
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(REMOTE_FILE_DOWN_INFO_PROGRESS_UPDATE);
			pst.setDouble(1, progress);
			pst.setString(2, logicId);
			if(pst.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			log.error("更新远程控制结果失败:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}

	public static RemoteFileDownInfo getFileDownInfoByRemoteLogicId(
			String logicId) {
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(REMOTE_FILE_DOWN_SELECT1);
			pst.setString(1, logicId);
			
			rs = pst.executeQuery();
			if(rs.next()) {
				RemoteFileDownInfo downInfo = new RemoteFileDownInfo();
				downInfo.setLogicId(rs.getString("LOGIC_ID"));
				downInfo.setTraceLogicId(logicId);
				downInfo.setRemoteFile(rs.getString("REMOTE_FILE"));
				downInfo.setRemoteHandleFile(rs.getString("REMOTE_HANDLE_FILE"));
				downInfo.setLocalFile(rs.getString("LOCAL_FILE"));
				downInfo.setProgress(rs.getDouble("PROGRESS"));
				return downInfo;
			}
			
		} catch (SQLException e) {
			log.error("更新远程控制结果失败:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		
		
		return null;
	}
	
	/**
	 * 更新远程控制冠字号缓冲功能
	 * 
	 * @param devNo 设备号
	 * @param status 状态
	 */
	public static Boolean updateRemoteSerialNoInfo(String devNo, int status) {
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(REMOTE_SERIALNOINFO_UPDATE);
			pst.setInt(1, status);
			pst.setString(2, devNo);
			if(pst.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			log.error("更新远程控制冠字号缓冲功能:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * 更新远程控制现金交易报文缓冲功能
	 * 
	 * @param devNo 设备号
	 * @param status 状态
	 */
	public static Boolean updateRemoteTxNoInfo(String devNo, int status) {
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement(REMOTE_TXTINFO_UPDATE);
			pst.setInt(1, status);
			pst.setString(2, devNo);
			if(pst.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			log.error("更新远程控制现金交易报文缓冲功能失败:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * 更新远程控制存取款冠字号凭条打印开关状态
	 * 
	 * @param devNo 设备号
	 * @param colName 需更新字段
	 * @param status 状态
	 */
	public static Boolean updateRemoteCimsrpCdmsrpFlag(String devNo, String colName, int status) {
		
		Connection connection = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = DbConnection.getConnection(true);
			pst = connection.prepareStatement("UPDATE DEV_SOFTWARE_INFO SET " + colName + " = ? WHERE DEV_NO = ?");
			pst.setInt(1, status);
			pst.setString(2, devNo);
			if(pst.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			log.error("更新远程控制存取款冠字号凭条打印开关状态失败:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}

}
