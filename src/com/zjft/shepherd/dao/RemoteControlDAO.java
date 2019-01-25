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
 * Զ�̿���dao��ӿ�
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
	 * ��¼Զ�̿�����Ϣ
	 * @param cmdName Զ�̿�������
	 * @param opNo ������
	 * @param devNo �豸��
	 * @param result �������
	 * @param opContent ����
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
			log.error("��¼Զ�̿�����Ϣ����:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return null;
	}
	
	/**
	 * ��¼Զ�̿�����Ϣ
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
			log.error("��¼Զ�̿�����Ϣ����:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}

	
	/**
	 * ����Զ�̿��ƽ����¼
	 * @param logicId Զ�̿��Ʋ�����¼���
	 * @param status ���
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
			log.error("����Զ�̿��ƽ��ʧ��:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * �����ļ����ؼ�¼��Ϣ
	 * @param tracelogicId �ļ�����Զ�̿��Ʊ��
	 * @param remoteFile Զ���ļ�
	 * @param locfilename �����ļ�
	 * @param compressfile ѹ���ļ�
	 * @param progress ����
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
			log.error("����Զ�̿��ƽ��ʧ��:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return null;
	}
	
	
	/**
	 * �����ļ����ؽ���
	 * @param logicId ���ر��
	 * @param propress ����
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
			log.error("����Զ�̿��ƽ��ʧ��:",e);
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
			log.error("����Զ�̿��ƽ��ʧ��:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		
		
		return null;
	}
	
	/**
	 * ����Զ�̿��ƹ��ֺŻ��幦��
	 * 
	 * @param devNo �豸��
	 * @param status ״̬
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
			log.error("����Զ�̿��ƹ��ֺŻ��幦��:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * ����Զ�̿����ֽ��ױ��Ļ��幦��
	 * 
	 * @param devNo �豸��
	 * @param status ״̬
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
			log.error("����Զ�̿����ֽ��ױ��Ļ��幦��ʧ��:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}
	
	/**
	 * ����Զ�̿��ƴ�ȡ����ֺ�ƾ����ӡ����״̬
	 * 
	 * @param devNo �豸��
	 * @param colName ������ֶ�
	 * @param status ״̬
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
			log.error("����Զ�̿��ƴ�ȡ����ֺ�ƾ����ӡ����״̬ʧ��:",e);
		} finally {
			DbConnection.closeResultSet(rs);
			DbConnection.closePreparedStatement(pst);
			DbConnection.releaseConnection(connection);
		}
		return false;
	}

}
