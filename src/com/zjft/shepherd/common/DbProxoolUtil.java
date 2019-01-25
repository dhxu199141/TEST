package com.zjft.shepherd.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DbProxoolUtil 
{
	private static Log log = LogFactory.getLog(DbProxoolUtil.class);
	/**
	 * 执行sql更新语句
	 * @author hk
	 * @since 2009.04.07
	 * @param sql语句 sql
	 * @return 成功:返回更新的记录数目;失败:-1
	 **/
	public static int update(String sql)
	{		
		Connection connection = null;
		Statement SQLStament = null;
		try 
		{
			log.info("sql="+sql);
			connection = DbConnectionNoContainer.getConnection(true);
			SQLStament = connection.createStatement();
			return SQLStament.executeUpdate(sql);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			DbConnectionNoContainer.closeStatement(SQLStament);
			DbConnectionNoContainer.releaseConnection(connection);
		} 
	}
	
	/**
	 * 执行sql删除语句
	 * @author hk
	 * @since 2009.04.07
	 * @param sql语句 sql
	 * @return 成功:返回删除的记录数目;失败:-1
	 **/
	public static int delete(String sql)
	{		
		Connection connection = null;
		Statement SQLStament = null;
		try 
		{
			log.info("sql="+sql);
			connection = DbConnectionNoContainer.getConnection(true);
			SQLStament = connection.createStatement();
			return SQLStament.executeUpdate(sql);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			DbConnectionNoContainer.closeStatement(SQLStament);
			DbConnectionNoContainer.releaseConnection(connection);
		} 
	}
	
	/**
	 * 执行sql插入语句
	 * @author hk
	 * @since 2009.04.07
	 * @param sql语句 sql
	 * @return 成功:返回插入的记录数目;失败:-1
	 **/
	public static int insert(String sql)
	{		
		Connection connection = null;
		Statement SQLStament = null;
		try 
		{
			log.info("sql="+sql);
			connection = DbConnectionNoContainer.getConnection(true);
			SQLStament = connection.createStatement();
			return SQLStament.executeUpdate(sql);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			DbConnectionNoContainer.closeStatement(SQLStament);
			DbConnectionNoContainer.releaseConnection(connection);
		} 
	}
	
	/**
	 * 批量执行sql插入语句
	 * @author hk
	 * @since 2009.04.07
	 * @param sql语句集 sqlList
	 * @return 成功:返回插入的记录数目;失败:-1。一条插入失败所有都失败
	 * @throws SQLException 
	 **/
	public static int batchInsert(List<String> sqlList)
	{		
		Connection connection = null;
		Statement SQLStament = null;
		try 
		{
			
			connection = DbConnectionNoContainer.getConnection(false);
			SQLStament = connection.createStatement();
			for(String sql:sqlList)
			{
				log.info("sql="+sql);
				SQLStament.executeUpdate(sql);
			}
			connection.commit();
			return sqlList.size();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			DbConnectionNoContainer.rollback(connection);
			return -1;
		}
		finally
		{
			DbConnectionNoContainer.closeStatement(SQLStament);
			DbConnectionNoContainer.releaseConnection(connection);
		} 
	}
		
	/**
	 * 执行sql查询语句
	 * @author hk
	 * @since 2009.04.07
	 * @param sql语句 sql
	 * @param 返回结果集类型,0:返回list结果集;1:返回hashmap结果集;其他同0 returnType
	 * @return 查询结果集 resultList元素为list<value>或HashMap<ColName,value>
	 **/
	public static List<Object> query(String sql,int returnType)
	{		
		Connection connection = null;
		Statement SQLStament = null;
		ResultSet set = null;
		try 
		{
			log.info("sql="+sql);
			connection = DbConnectionNoContainer.getConnection(true);
			SQLStament = connection.createStatement();
			set = SQLStament.executeQuery(sql);

			ResultSetMetaData rsmd=set.getMetaData();
			int colCount=rsmd.getColumnCount();
			List<Object> resultList=new ArrayList<Object>();
			if(returnType==0)
			{
				while(set!=null&&set.next())
				{
					ArrayList<Object> temp=new ArrayList<Object>();
					for(int i=0;i<colCount;i++)
					{
						temp.add(set.getObject(i+1));
					}
					resultList.add(temp);
				}
			}
			else
			{
				while(set!=null&&set.next())
				{
					HashMap<String, Object> temp=new HashMap<String,Object>();
					for(int i=0;i<colCount;i++)
					{
						temp.put(rsmd.getColumnName(i+1).toUpperCase(),set.getObject(i+1));
					}
					resultList.add(temp);
				}
			}
			return resultList;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			DbConnectionNoContainer.closeResultSet(set);
			DbConnectionNoContainer.closeStatement(SQLStament);
			DbConnectionNoContainer.releaseConnection(connection);
		} 
	}
	
	/**
	 * 执行sql查询语句
	 * @author lky
	 * @since 2009.04.07
	 * @param sql语句 sql
	 * @return 0:存在,1:不存在,-1错误
	 **/
	public static int query(String sql)
	{		
		Connection connection = null;
		Statement SQLStament = null;
		ResultSet set = null;
		try 
		{
			log.info("sql="+sql);
			connection = DbConnectionNoContainer.getConnection(true);
			SQLStament = connection.createStatement();
			set = SQLStament.executeQuery(sql);

			ResultSetMetaData rsmd=set.getMetaData();
			int colCount=rsmd.getColumnCount();
			return colCount==0?0:1;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			DbConnectionNoContainer.closeResultSet(set);
			DbConnectionNoContainer.closeStatement(SQLStament);
			DbConnectionNoContainer.releaseConnection(connection);
		} 
	}
	
	
	/**
	 * 获取当前可插入表中的记录编号
	 * @author hk
	 * @since 2009.04.09
	 * @param table  表名
	 * @param idName 记录主键
	 * @param baseId 记录基本值
	 * @return 对于原来记录主键为按数字型排序的返回当前最大记录+1，如果为-1表示不能获取最大记录
	 * */
	public static int getId(String table,String idName,int baseId)
	{
		if(table==null||table.equals("")||idName==null||idName.equals(""))
		{
			return -1;
		}
		
		Connection connection = null;
		Statement SQLStament = null;
		ResultSet set = null;
		
		try
		{
			String sql="SELECT MAX("+idName+") tempvalue FROM "+table;
			connection = DbConnectionNoContainer.getConnection(true);
			SQLStament = connection.createStatement();
			set = SQLStament.executeQuery(sql);
			if(set!=null&&set.next())
			{
				return Integer.parseInt(set.getString("tempvalue"))+1;
			}
			return baseId+1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			DbConnectionNoContainer.closeResultSet(set);
			DbConnectionNoContainer.closeStatement(SQLStament);
			DbConnectionNoContainer.releaseConnection(connection);
		} 
	}
	
	/**
	 * 从数据库查询到的结果集中获取指定列的值
	 * @author hk
	 * @since 2009.04.09
	 * @param 查询结果list resultList
	 * @param 第几条数据 rowNum
	 * @param 列的名称 colName
	 * @return 列的值 value
	 **/
	public static Object getValue(List resultList,int rowNum,String colName)
	{
		try
		{
			HashMap<String, Object> tempMap = (HashMap<String, Object>)resultList.get(rowNum);
			return tempMap.get(colName.toUpperCase());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 从数据库查询到的结果集中获取指定列的值
	 * @author hk
	 * @since 2009.04.09
	 * @param 查询结果list resultList
	 * @param 第几条数据 rowNum
	 * @param 第几列 colNum
	 * @return 列的值 value
	 **/
	public static Object getValue(List resultList,int rowNum,int colNum)
	{
		try
		{
			ArrayList<String> tempList = (ArrayList)resultList.get(rowNum);
			return tempList.get(colNum);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		List<String> temp=new ArrayList();
		String sql="SELECT max(subversion) maxsubversion FROM rvc_versioninfo WHERE projectid = '10001'";
		
		query(sql,1);
		
	}
}
