package com.irelandken.sql;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

/**
 * Schema Free Sql Template
 *  
 * @author irelandKen
 * @since 2013-11-12
 * TODO: queryOne? selectOne ?
 */

public interface SqlOperations
{
	/**
	 * INSERT INTO table (key1, key2..) VALUES ('value1', 'value2'..)
	 * <br>
	 * @param table
	 * @param data
	 * @return id
	 */
	Number insert(String table, Map<String, Object> data);
	
	/**
	 * INSERT INTO table (key1, key2..) VALUES ('value1', 'value2'..);
	 * <br>
	 * @param table
	 * @param data
	 * @return success/fail
	 */
	boolean insert2(String table, Map<String, Object> data);
	

	/**
	 * SELECT field1,field2.. FROM table WHERE where;
	 * <br>
	 * @param table
	 * @param fields
	 * @param where 
	 * @return affert rows count
	 */
	List<Map<String, Object>> select(String table,String[] fields,String where);
	
	/**
	 * SELECT field1,field2.. FROM table WHERE where ORDER BY orderBy LIMIT start,limit;
	 * 
	 * @param table
	 * @param fields
	 * @param where
	 * @param orderBy EX:"field1 DESC, field2 ASC"
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Map<String, Object>> select(String table,String[] fields,String where,String orderBy,int start,int limit);
	
	/**
	 * SELECT field1,field2.. FROM table WHERE where;
	 * <br>
	 * @param table
	 * @param fields
	 * @param where 
	 * @return a map as a row OR null 
	 */
	Map<String, Object> selectOne(String table,String[] fields,String where);
	
	
	/**
	 * SELECT field1,field2.. FROM table WHERE key1 = 'value1' AND key2 = 'value2' ..;
	 * 
	 * @param table
	 * @param fields
	 * @param where 
	 * @return affert rows count
	 */
	List<Map<String, Object>> select(String table,String[] fields,Map<String,Object> where);
	
	/**
	 * SELECT field1,field2.. FROM table WHERE key1 = 'value1' AND key2 = 'value2'.. ORDER BY orderBy LIMIT start,limit;
	 * 
	 * @param table
	 * @param fields
	 * @param where
	 * @param orderBy EX:"field1 DESC, field2 ASC"
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Map<String, Object>> select(String table,String[] fields,Map<String,Object> where,String orderBy,int start,int limit);
	
	/**
	 * SELECT field1,field2.. FROM table WHERE key1 = 'value1' AND key2 = 'value2' ..;
	 * 
	 * @param table
	 * @param fields
	 * @param where 
	 * @return a map as a row OR null
	 */
	Map<String, Object> selectOne(String table,String[] fields,Map<String,Object> where);
	
	
	/**
	 * UPDATE table SET field1 = 'value1', field2 = 'value2'.. WHERE where;
	 * <br>
	 * @param table
	 * @param data
	 * @param where 
	 * @return affert rows count
	 */
	int update(String table,Map<String, Object> data,String where);
	
	/**
	 * UPDATE table SET field1 = 'value1', field2 = 'value2'.. WHERE key1 = 'value1' AND key2 = 'value2' ..;
	 * <br>
	 * @param table
	 * @param data
	 * @param where 
	 * @return affert rows count
	 */
	int update(String table,Map<String, Object> data,Map<String,Object> where);
	
	
	/**
	 * DELETE FROM table WHERE where;
	 * 
	 * @param table
	 * @param where 
	 * @return affert rows count
	 */
	int delete(String table, String where);
	
	/**
	 * DELETE FROM table WHERE key1 = 'value1' AND key2 = 'value2' ..;
	 *  
	 * @param table
	 * @param where 
	 * @return affert rows count
	 */
	int delete(String table, Map<String,Object> where);
	
	/**
	 * SELECT COUNT(*) FROM table WHERE where;
	 * 
	 * @param table
	 * @param where 
	 * @return rows count
	 */
	int count(String table, String where);
	
	
	/**
	 * SELECT COUNT(*) FROM table WHERE key1 = 'value1' AND key2 = 'value2' ..;
	 * 
	 * @param table
	 * @param where 
	 * @return affert rows count
	 */
	int count(String table, Map<String,Object> where);
	
	
	
	//-----------------------------------------------------------
	//-----以下为通用的SQL操作 -----------------------------------------
	//-----------------------------------------------------------
	
	
	/**
	 * Execute a query for a result list, given static SQL.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * {@code queryForList} method with {@code null} as argument array.
	 * <p>The results will be mapped to a List (one entry for each row) of
	 * Maps (one entry for each column using the column name as the key).
	 * Each element in the list will be of the form returned by this interface's
	 * queryForMap() methods.
	 * @param sql SQL query to execute
	 * @return an List that contains a Map per row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForList(String, Object[])
	 */
	List<Map<String, Object>> query(String sql) throws DataAccessException;
	
	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * list of arguments to bind to the query, expecting a result list.
	 * <p>The results will be mapped to a List (one entry for each row) of
	 * Maps (one entry for each column, using the column name as the key).
	 * Each element in the list will be of the form returned by this interface's
	 * queryForMap() methods.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @return a List that contains a Map per row
	 * @throws DataAccessException if the query fails
	 * @see #queryForList(String)
	 */
	List<Map<String, Object>> query(String sql, Object... args) throws DataAccessException;

	/**
	 * Execute a query for a result Map, given static SQL.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * {@link #queryForMap(String, Object...)} method with {@code null}
	 * as argument array.
	 * <p>The query is expected to be a single row query; the result row will be
	 * mapped to a Map (one entry for each column, using the column name as the key).
	 * @param sql SQL query to execute
	 * @return the result Map (one entry for each column, using the
	 * column name as the key) OR null
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForMap(String, Object[])
	 * @see ColumnMapRowMapper
	 */
	Map<String, Object> queryOne(String sql) throws DataAccessException;
	
	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * list of arguments to bind to the query, expecting a result Map.
	 * The queryForMap() methods defined by this interface are appropriate
	 * when you don't have a domain model. Otherwise, consider using
	 * one of the queryForObject() methods.
	 * <p>The query is expected to be a single row query; the result row will be
	 * mapped to a Map (one entry for each column, using the column name as the key).
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @return the result Map (one entry for each column, using the
	 * column name as the key) OR null
	 * @throws DataAccessException if the query fails
	 * @see #queryForMap(String)
	 * @see ColumnMapRowMapper
	 */
	Map<String, Object> queryOne(String sql, Object... args) throws DataAccessException;

	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement).
	 * @param sql static SQL to execute
	 * @return the number of rows affected
	 */
	int queryUpdate(String sql);
	
	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement)
	 * via a prepared statement, binding the given arguments.
	 * @param sql SQL containing bind parameters
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @return the number of rows affected
	 */
	int queryUpdate(String sql, Object... args);
	
}
