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
	 * ִ��sql�������
	 * @author hk
	 * @since 2009.04.07
	 * @param sql��� sql
	 * @return �ɹ�:���ظ��µļ�¼��Ŀ;ʧ��:-1
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
	 * ִ��sqlɾ�����
	 * @author hk
	 * @since 2009.04.07
	 * @param sql��� sql
	 * @return �ɹ�:����ɾ���ļ�¼��Ŀ;ʧ��:-1
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
	 * ִ��sql�������
	 * @author hk
	 * @since 2009.04.07
	 * @param sql��� sql
	 * @return �ɹ�:���ز���ļ�¼��Ŀ;ʧ��:-1
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
	 * ����ִ��sql�������
	 * @author hk
	 * @since 2009.04.07
	 * @param sql��伯 sqlList
	 * @return �ɹ�:���ز���ļ�¼��Ŀ;ʧ��:-1��һ������ʧ�����ж�ʧ��
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
	 * ִ��sql��ѯ���
	 * @author hk
	 * @since 2009.04.07
	 * @param sql��� sql
	 * @param ���ؽ��������,0:����list�����;1:����hashmap�����;����ͬ0 returnType
	 * @return ��ѯ����� resultListԪ��Ϊlist<value>��HashMap<ColName,value>
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
	 * ִ��sql��ѯ���
	 * @author lky
	 * @since 2009.04.07
	 * @param sql��� sql
	 * @return 0:����,1:������,-1����
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
	 * ��ȡ��ǰ�ɲ�����еļ�¼���
	 * @author hk
	 * @since 2009.04.09
	 * @param table  ����
	 * @param idName ��¼����
	 * @param baseId ��¼����ֵ
	 * @return ����ԭ����¼����Ϊ������������ķ��ص�ǰ����¼+1�����Ϊ-1��ʾ���ܻ�ȡ����¼
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
	 * �����ݿ��ѯ���Ľ�����л�ȡָ���е�ֵ
	 * @author hk
	 * @since 2009.04.09
	 * @param ��ѯ���list resultList
	 * @param �ڼ������� rowNum
	 * @param �е����� colName
	 * @return �е�ֵ value
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
	 * �����ݿ��ѯ���Ľ�����л�ȡָ���е�ֵ
	 * @author hk
	 * @since 2009.04.09
	 * @param ��ѯ���list resultList
	 * @param �ڼ������� rowNum
	 * @param �ڼ��� colNum
	 * @return �е�ֵ value
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
