package com.zjft.shepherd.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.admin.SnapshotIF;

import com.zjft.shepherd.common.cyberark.CybAccount;
import com.zjft.shepherd.common.cyberark.CyberArkUtil;

/**
 * <p>
 * Title:*ͨ��proxool���ݿ����ӳط������ݿ�(������Ϊ��ͨ��web��������proxool)
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: zjft
 * </p>
 * 
 * @version 1.0
 */
public class DbConnection {

	private static Log log = LogFactory.getLog(DbConnection.class);
	private static int activeCount = 0;
	private static ProxoolDataSource pool = null;

        //mod by yyhe 2012-05-30
	private static String getUserName(){
		String userName = "" ;
		try {
			CybAccount acc = CyberArkUtil.getInstance().getDbAccount();
			if (acc == null) {
				System.out.println("��ȡ�û���ʧ��");
			}
			 userName =  acc.getUserName();
	
		} catch (Exception e) {
			System.out.println("��ȡ�û���ʧ��");
			e.printStackTrace();
		}
		return userName ;
	}
	
	private static String getPassWord(){
		String passWord = "" ;
		try {
			CybAccount acc = CyberArkUtil.getInstance().getDbAccount();
			if (acc == null) {
				System.out.println("��ȡ����ʧ��");
			}
			passWord =  acc.getPassWord();
	
		} catch (Exception e) {
			System.out.println("��ȡ����ʧ��");
			e.printStackTrace();
		}
		return passWord ;
	}

	/**
	 * ����poxool���ӳ�
	 * 
	 * @since 2009.04.08
	 */
	static {
		try {
			pool = new ProxoolDataSource();
			pool.setAlias(SystemCons.getdbAlias());
			pool.setDriver(SystemCons.getdbDriver());
			pool.setDriverUrl(SystemCons.getdbUrl());
			log.info("cyberarkKey=["+SystemCons.getCyberarkKey()+"]");
			if(SystemCons.getCyberarkKey()==1){
                pool.setUser(getUserName());
                pool.setPassword(getPassWord());
                pool.setDelegateProperties("user=" + getUserName() + ",password=" + getPassWord());
			}else{
				pool.setUser(SystemCons.getdbUser());
				pool.setPassword(SystemCons.getdbPwd());
				//sjxu ����oci�޷���ȡ���� v1.9.0
//				pool.setDelegateProperties("user=" + SystemCons.getdbUser() + ",password=" + SystemCons.getdbPwd());
			}
			pool.setMinimumConnectionCount(SystemCons.getminConn());
			pool.setMaximumConnectionCount(SystemCons.getmaxConn());
			pool.setHouseKeepingTestSql(SystemCons.getHouseKeepingTestSql());
        	pool.setTestBeforeUse(true);
		} catch (Exception e) {
			log.error("���ӳػ�ȡ���ݿ�����ʧ��",e);
		}
	}

	/**
	 * �����ݿ����ӳ��л�ȡһ������
	 * 
	 * @since 2009.04.08
	 * @return �ɹ�:���ݿ�����;ʧ��:null
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			showSnapshotInfo();
		} catch (SQLException e) {
            reConnection() ;
			getConnection() ;
		}
		return conn;
	}

        private static void reConnection(){
		try {
			pool = new ProxoolDataSource();
			pool.setAlias(SystemCons.getdbAlias());
			pool.setDriver(SystemCons.getdbDriver());
			pool.setDriverUrl(SystemCons.getdbUrl());
			log.info("cyberarkKey1=["+SystemCons.getCyberarkKey()+"]");
			if(SystemCons.getCyberarkKey()==1){
                pool.setUser(getUserName());
                pool.setPassword(getPassWord());
                pool.setDelegateProperties("user=" + getUserName() + ",password=" + getPassWord());
			}else{
				pool.setUser(SystemCons.getdbUser());
				pool.setPassword(SystemCons.getdbPwd());
				//sjxu ����oci�޷���ȡ���� v1.9.0
//				pool.setDelegateProperties("user=" + SystemCons.getdbUser() + ",password=" + SystemCons.getdbPwd());
			}
			pool.setMinimumConnectionCount(SystemCons.getminConn());
			pool.setMaximumConnectionCount(SystemCons.getmaxConn());
			pool.setHouseKeepingTestSql(SystemCons.getHouseKeepingTestSql());
		} catch (Exception e) {
			log.error("���¼���poxool���ӳس����쳣��"+e.getMessage());
		}
	}

	/**
	 * �����ݿ����ӳ��л�ȡһ������
	 * @since 2009.04.08
	 * @param autoCommit �Ƿ��Զ�commit
	 * @return �ɹ�:���ݿ�����;ʧ��:null
	 **/
	public static Connection getConnection( boolean autoCommit ) 
	{	
		Connection conn = null;
		try 
		{
			conn = getConnection();
			conn.setAutoCommit( autoCommit );
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * ���ݿ�����rollback
	 * @since 2009.04.08
	 * @param p_connection ���ݿ�����
	 **/
	public static void rollback(Connection p_connection ) 
	{
		try 
		{
			p_connection.rollback();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * ���ݿ�����commit
	 * @since 2009.04.08
	 * @param p_connection ���ݿ�����
	 **/
	public static void commit( Connection p_connection ) 
	{
		try 
		{
			p_connection.commit();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * ���ݿ�����release
	 * @since 2009.04.08
	 * @param p_connection ���ݿ�����
	 **/
	public static void releaseConnection( Connection p_connection ) 
	{
		try 
		{
			if( p_connection != null ) 
			{
				p_connection.close();
			}
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * �ر�resultset
	 * @since 2009.04.08
	 * @param a_resultSet �����
	 **/
	public static void closeResultSet(ResultSet a_resultSet) 
	{
		try
		{
			if (a_resultSet != null)
			{
				a_resultSet.close();
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * �ر����ݿ��������(Statement)
	 * @since 2009.04.08
	 * @param a_pstmSQL ���ݿ�������� 
	 **/
	public static void closeStatement(Statement a_pstmSQL)
	{
		try 
		{
			if (a_pstmSQL != null)
			{
				a_pstmSQL.close();
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}    
	
	/**
	 * �ر����ݿ��������(Statement)
	 * @since 2009.06.15
	 * @param a_pstmSQL ���ݿ�������� 
	 **/
	public static void closePreparedStatement(PreparedStatement a_pstmSQL)
	{
		try 
		{
			if (a_pstmSQL != null)
			{
				a_pstmSQL.close();
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}   
	
	/**
	 * ��ȡproxool���ӳ����Բ��ں�̨��ӡ(�˺��������ο�)
	 * @since 2009.04.08
	 **/      
    public static void showSnapshotInfo()
    {           
        try
        {           
            SnapshotIF snapshot = ProxoolFacade.getSnapshot(SystemCons.getdbAlias(), true); 
            int curActiveCount = snapshot.getActiveConnectionCount();//��û������ 
            int availableCount = snapshot.getAvailableConnectionCount();//��ÿɵõ��������� 
            int maxCount = snapshot.getMaximumConnectionCount() ;//�����������
            if(curActiveCount!=activeCount)//����������仯ʱ�������Ϣ 
            {           
            	log.debug("�������:"+curActiveCount+"(active)  �ɵõ���������:"+availableCount+"(available)  ��������:"+maxCount);                       
            	activeCount=curActiveCount;           
            }           
        }
        catch(ProxoolException e)
        {           
            e.printStackTrace();           
        }           
    } 
    
    public static void main(String[] args)
    {
    	Connection conn=null;
    	try 
    	{
    		conn=getConnection();
			showSnapshotInfo();
		} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
		}
    	finally
    	{
    		releaseConnection(conn);
    	}
    }
}