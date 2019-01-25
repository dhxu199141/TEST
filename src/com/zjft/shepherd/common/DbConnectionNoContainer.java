package com.zjft.shepherd.common;

/**
 *ͨ��proxool���ݿ����ӳط������ݿ�
 *������Ϊ��ͨ��web��������proxool
 * */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.admin.SnapshotIF;

import com.zjft.shepherd.common.SystemCons;
public class DbConnectionNoContainer 
{
	
	private static Log log = LogFactory.getLog(DbConnectionNoContainer.class);	
	private static ProxoolDataSource pool=null;
	private static int activeCount = 0; 

	private static String dbAlias=SystemCons.getdbAlias();
	private static String dbDriver=SystemCons.getdbDriver();
	private static String dbUrl=SystemCons.getdbUrl();
	private static String dbUser=SystemCons.getdbUser();
	private static String dbPwd=SystemCons.getdbPwd();
	private static int minConn=SystemCons.getminConn();
	private static int maxConn=SystemCons.getmaxConn();
	
	
	/**
	 * ����poxool���ӳ�
	 * @author hk
	 * @since 2009.04.08
	 **/
	static 
	{
		try 
		{
        	pool=new ProxoolDataSource();
        	pool.setAlias(dbAlias);
        	pool.setDriver(dbDriver);
        	pool.setDriverUrl(dbUrl);
        	pool.setUser(dbUser);
        	pool.setPassword(dbPwd);
			//sjxu ����oci�޷���ȡ���� v1.9.0
//        	pool.setDelegateProperties("user="+dbUser+",password="+dbPwd);
        	pool.setMinimumConnectionCount(minConn);
        	pool.setMaximumConnectionCount(maxConn);
        	pool.setHouseKeepingTestSql(SystemCons.getHouseKeepingTestSql());
        	pool.setHouseKeepingSleepTime(1000);
        	pool.setTestBeforeUse(true);
        }
        catch(Exception e) 
        {
        	pool=null;
        	e.printStackTrace();
        }
	}
	
	/**
	 * �����ݿ����ӳ��л�ȡһ������
	 * @author hk
	 * @since 2009.04.08
	 * @return �ɹ�:���ݿ�����;ʧ��:null
	 **/
    public static Connection getConnection()
    {
    	Connection conn=null;

        try 
        {
        	conn=pool.getConnection();
        }
        catch(SQLException e) 
        {
        	e.printStackTrace();
        } 
 		return conn;
    }	
     
	/**
	 * �����ݿ����ӳ��л�ȡһ������
	 * @author hk
	 * @since 2009.04.08
	 * @param �Ƿ��Զ�commit autoConmmit
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
	 * @author hk
	 * @since 2009.04.08
	 * @param ���ݿ����� p_connection
	 * @return void
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
	 * @author hk
	 * @since 2009.04.08
	 * @param ���ݿ����� p_connection
	 * @return void
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
	 * @author hk
	 * @since 2009.04.08
	 * @param ���ݿ����� p_connection
	 * @return void
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
	 * @author hk
	 * @since 2009.04.08
	 * @param ����� a_resultSet
	 * @return void
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
	 * @author hk
	 * @since 2009.04.08
	 * @param ���ݿ�������� a_pstmSQL
	 * @return
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
	 * ��ȡproxool���ӳ����Բ��ں�̨��ӡ(�˺��������ο�)
	 * @author hk
	 * @since 2009.04.08
	 * @return void
	 **/      
    public static void showSnapshotInfo()
    {           
        try
        {           
            SnapshotIF snapshot = ProxoolFacade.getSnapshot(dbAlias,true); 
            int curActiveCount=snapshot.getActiveConnectionCount();//��û������ 
            int availableCount=snapshot.getAvailableConnectionCount();//��ÿɵõ��������� 
            int maxCount=snapshot.getMaximumConnectionCount() ;//�����������
            if(curActiveCount!=activeCount)//����������仯ʱ�������Ϣ 
            {           
             log.info("�������:"+curActiveCount+"(active)  �ɵõ���������:"+availableCount+"(available)  ��������:"+maxCount+" (max)");                       
             activeCount=curActiveCount;           
            }           
        }
        catch(ProxoolException e)
        {           
            e.printStackTrace();           
        }           
    } 
    
	/**
	 * ���Ա�����
	 * @author hk
	 * @since 2009.04.08
	 * @return void
	 **/
    public static void main(String[] args)
    {
    	Connection conn=null;
    	try 
    	{
    		conn=getConnection();
    		System.out.println("get connection");
			showSnapshotInfo();
		} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
		}
    	finally
    	{
    		releaseConnection(conn);
    		System.out.println("release connection");
    	}
    }
}