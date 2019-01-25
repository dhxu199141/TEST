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
 * Title:*通过proxool数据库连接池访问数据库(本程序为不通过web容器加载proxool)
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
				System.out.println("获取用户名失败");
			}
			 userName =  acc.getUserName();
	
		} catch (Exception e) {
			System.out.println("获取用户名失败");
			e.printStackTrace();
		}
		return userName ;
	}
	
	private static String getPassWord(){
		String passWord = "" ;
		try {
			CybAccount acc = CyberArkUtil.getInstance().getDbAccount();
			if (acc == null) {
				System.out.println("获取密码失败");
			}
			passWord =  acc.getPassWord();
	
		} catch (Exception e) {
			System.out.println("获取密码失败");
			e.printStackTrace();
		}
		return passWord ;
	}

	/**
	 * 创建poxool连接池
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
				//sjxu 苏州oci无法获取参数 v1.9.0
//				pool.setDelegateProperties("user=" + SystemCons.getdbUser() + ",password=" + SystemCons.getdbPwd());
			}
			pool.setMinimumConnectionCount(SystemCons.getminConn());
			pool.setMaximumConnectionCount(SystemCons.getmaxConn());
			pool.setHouseKeepingTestSql(SystemCons.getHouseKeepingTestSql());
        	pool.setTestBeforeUse(true);
		} catch (Exception e) {
			log.error("连接池获取数据库连接失败",e);
		}
	}

	/**
	 * 从数据库连接池中获取一个连接
	 * 
	 * @since 2009.04.08
	 * @return 成功:数据库连接;失败:null
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
				//sjxu 苏州oci无法获取参数 v1.9.0
//				pool.setDelegateProperties("user=" + SystemCons.getdbUser() + ",password=" + SystemCons.getdbPwd());
			}
			pool.setMinimumConnectionCount(SystemCons.getminConn());
			pool.setMaximumConnectionCount(SystemCons.getmaxConn());
			pool.setHouseKeepingTestSql(SystemCons.getHouseKeepingTestSql());
		} catch (Exception e) {
			log.error("重新加载poxool连接池出现异常："+e.getMessage());
		}
	}

	/**
	 * 从数据库连接池中获取一个连接
	 * @since 2009.04.08
	 * @param autoCommit 是否自动commit
	 * @return 成功:数据库连接;失败:null
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
	 * 数据库连接rollback
	 * @since 2009.04.08
	 * @param p_connection 数据库连接
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
	 * 数据库连接commit
	 * @since 2009.04.08
	 * @param p_connection 数据库连接
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
	 * 数据库连接release
	 * @since 2009.04.08
	 * @param p_connection 数据库连接
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
	 * 关闭resultset
	 * @since 2009.04.08
	 * @param a_resultSet 结果集
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
	 * 关闭数据库操作声明(Statement)
	 * @since 2009.04.08
	 * @param a_pstmSQL 数据库操作声明 
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
	 * 关闭数据库操作声明(Statement)
	 * @since 2009.06.15
	 * @param a_pstmSQL 数据库操作声明 
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
	 * 获取proxool连接池属性并在后台打印(此函数仅供参考)
	 * @since 2009.04.08
	 **/      
    public static void showSnapshotInfo()
    {           
        try
        {           
            SnapshotIF snapshot = ProxoolFacade.getSnapshot(SystemCons.getdbAlias(), true); 
            int curActiveCount = snapshot.getActiveConnectionCount();//获得活动连接数 
            int availableCount = snapshot.getAvailableConnectionCount();//获得可得到的连接数 
            int maxCount = snapshot.getMaximumConnectionCount() ;//获得总连接数
            if(curActiveCount!=activeCount)//当活动连接数变化时输出的信息 
            {           
            	log.debug("活动连接数:"+curActiveCount+"(active)  可得到的连接数:"+availableCount+"(available)  总连接数:"+maxCount);                       
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