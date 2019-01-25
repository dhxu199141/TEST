package com.zjft.shepherd.common;

/**
 *通过proxool数据库连接池访问数据库
 *本程序为不通过web容器加载proxool
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
	 * 创建poxool连接池
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
			//sjxu 苏州oci无法获取参数 v1.9.0
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
	 * 从数据库连接池中获取一个连接
	 * @author hk
	 * @since 2009.04.08
	 * @return 成功:数据库连接;失败:null
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
	 * 从数据库连接池中获取一个连接
	 * @author hk
	 * @since 2009.04.08
	 * @param 是否自动commit autoConmmit
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
	 * @author hk
	 * @since 2009.04.08
	 * @param 数据库连接 p_connection
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
	 * 数据库连接commit
	 * @author hk
	 * @since 2009.04.08
	 * @param 数据库连接 p_connection
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
	 * 数据库连接release
	 * @author hk
	 * @since 2009.04.08
	 * @param 数据库连接 p_connection
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
	 * 关闭resultset
	 * @author hk
	 * @since 2009.04.08
	 * @param 结果集 a_resultSet
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
	 * 关闭数据库操作声明(Statement)
	 * @author hk
	 * @since 2009.04.08
	 * @param 数据库操作声明 a_pstmSQL
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
	 * 获取proxool连接池属性并在后台打印(此函数仅供参考)
	 * @author hk
	 * @since 2009.04.08
	 * @return void
	 **/      
    public static void showSnapshotInfo()
    {           
        try
        {           
            SnapshotIF snapshot = ProxoolFacade.getSnapshot(dbAlias,true); 
            int curActiveCount=snapshot.getActiveConnectionCount();//获得活动连接数 
            int availableCount=snapshot.getAvailableConnectionCount();//获得可得到的连接数 
            int maxCount=snapshot.getMaximumConnectionCount() ;//获得总连接数
            if(curActiveCount!=activeCount)//当活动连接数变化时输出的信息 
            {           
             log.info("活动连接数:"+curActiveCount+"(active)  可得到的连接数:"+availableCount+"(available)  总连接数:"+maxCount+" (max)");                       
             activeCount=curActiveCount;           
            }           
        }
        catch(ProxoolException e)
        {           
            e.printStackTrace();           
        }           
    } 
    
	/**
	 * 测试本程序
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