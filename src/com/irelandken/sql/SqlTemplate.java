package com.irelandken.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

/**
 * Data Oriented Sql Template
 * 
 * @author irelandKen
 * @since 2013-12-07
 * @version 0.3.0
 * @see https://github.com/irelandKen/Schema_Free_Sql_Template
 * 
 * TODO: 重构SQL拼接工具
 * TODO: String sql => StringBuilder sql
 */

public class SqlTemplate extends JdbcTemplate implements SqlOperations 
{
	public SqlTemplate(DataSource dataSource)
	{
		super(dataSource);
	}

	/**
	 * Determine whether the given array is empty:
	 * i.e. {@code null} or of zero length.
	 * @param array the array to check
	 */
	private static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}
	
	/**
	 * Return {@code true} if the supplied Collection is {@code null}
	 * or empty. Otherwise, return {@code false}.
	 * @param collection the Collection to check
	 * @return whether the given Collection is empty
	 */
	private static boolean isEmpty(Collection collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
	 * Return {@code true} if the supplied Map is {@code null}
	 * or empty. Otherwise, return {@code false}.
	 * @param map the Map to check
	 * @return whether the given Map is empty
	 */
	private static boolean isEmpty(Map map) {
		return (map == null || map.isEmpty());
	}
	
	private static String link(String glue,Collection<String> strs) {
		
		StringBuilder builder = new StringBuilder();
		
		int len = strs.size();
		int cur = 0;
		for(String str : strs) {
			cur++;
			builder.append(str);
			if(cur < len) {
				builder.append(glue);
			}
		}
		
		return builder.toString();
	}
	
	private static String link(String glue, String[] strs) {
		
		StringBuilder builder = new StringBuilder();
		
		int len = strs.length;
		int cur = 0;
		for(String str : strs) {
			cur++;
			builder.append(str);
			if(cur < len) {
				builder.append(glue);
			}
		}
		
		return builder.toString();
	}
	
	private static String placeholders(int num) {
		
		if(num<=0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder(2*num-1);
		
		for(int i=0; i<num-1; i++) {
			builder.append("?,");
		}
		builder.append("?");
		
		return builder.toString();
	}
	
	/**
	 * sql segment with Prepared args ,used for prepared statement
	 * @author irelandKen
	 */
	private static class PreparedSqlSegment {
		
		String segment;
		Object[] args;
		
		public PreparedSqlSegment(String segment, Object[] args){
			this.segment = segment;
			this.args    = args;
		}
	}
	
	/**
	 * where sql segment with Prepared args ,used for prepared statement
	 * @param where not null
	 * @return
	 */
	private static PreparedSqlSegment preparedWhereString(Map<String, Object> where) {
		String[] condictions = new String[where.size()];
		Object[] args = new Object[where.size()];
		int index = 0;
		
		for(Entry<String, Object> entry : where.entrySet()) {
			condictions[index] = entry.getKey() + " = ? ";
			args[index] = entry.getValue();
			index++;
		}
		
		String where_segment = link(" AND ",condictions);
		
		return new PreparedSqlSegment(where_segment, args);
	}
	
	/**
	 * update sql segment with Prepared args ,used for prepared statement
	 * @param data not null
	 * @return
	 */
	private static PreparedSqlSegment preparedUpdateString(Map<String, Object> data) {
		String[] fieldStrs = new String[data.size()];
		Object[] args = new Object[data.size()];
		int index = 0;
		
		for(Entry<String, Object> entry : data.entrySet()) {
			fieldStrs[index] = entry.getKey() + " = ? ";
			args[index] = entry.getValue();
			index++;
		}

		String update_segment = link(", ",fieldStrs);
		
		return new PreparedSqlSegment(update_segment, args);
	}
	
	private static void addAll(Collection<Object> list , Object[] array) {
		for (Object item : array){
			list.add(item);
		}
	}
	
	@Override
	public Number insert(final String sql)
	{
		KeyHolder keyHolder = new GeneratedKeyHolder();
		super.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		            
		            return ps;
		        }
		    },
		    keyHolder
		);

		return keyHolder.getKey();
	}

	@Override
	public Number insert(final String sql, final Object... args)
	{
		KeyHolder keyHolder = new GeneratedKeyHolder();
		super.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		            
		            (new ArgumentPreparedStatementSetter(args)).setValues(ps);
		            
		            return ps;
		        }
		    },
		    keyHolder
		);

		return keyHolder.getKey();
	}
	
	@Override
	public Number insert(String table, final Map<String, Object> data)
	{
		Assert.notNull(table);
		Assert.notEmpty(data);
		
		final String sql = "INSERT INTO " + table + " (" + link(",",data.keySet()) + ") VALUES (" + placeholders(data.size()) + ")";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		super.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		            
		            (new ArgumentPreparedStatementSetter(data.values().toArray())).setValues(ps);
		            
		            return ps;
		        }
		    },
		    keyHolder
		);

		return keyHolder.getKey();
	}
	
	@Override
	public boolean insert2(String table, Map<String, Object> data)
	{
		Assert.notNull(table);
		Assert.notEmpty(data);
		
		String sql = "INSERT INTO " + table + " (" + link(",",data.keySet()) + ") VALUES (" + placeholders(data.size()) + ")";
		
		return super.update(sql, data.values().toArray()) >= 1;
	}
	
	@Override
	public int insert2(String sql)
	{
		return queryUpdate(sql);
	}

	@Override
	public int insert2(String sql, Object... args)
	{
		return queryUpdate(sql, args);
	}


	@Override
	public List<Map<String, Object>> select(String sql) throws DataAccessException
	{
		return query(sql);
	}

	@Override
	public List<Map<String, Object>> select(String sql, Object... args) throws DataAccessException
	{
		return query(sql, args);
	}
	
	@Override
	public Map<String, Object> selectOne(String sql) throws DataAccessException
	{
		return queryOne(sql);
	}

	@Override
	public Map<String, Object> selectOne(String sql, Object... args) throws DataAccessException
	{
		return queryOne(sql, args);
	}
	
	@Override
	public List<Map<String, Object>> select(String table, String[] fields, String where)
	{
		//SELECT field1,field2.. FROM table WHERE where; 
		
		return select(table, fields, where, null, null, null);
	}
	
	@Override
	public List<Map<String, Object>> select(String table, String[] fields, String where, String orderBy, Integer start, Integer limit)
	{
		//SELECT field1,field2.. FROM table WHERE where ORDER BY orderBy LIMIT start,limit;
		
		Assert.notNull(table);
		
		String sql = null;
		if(isEmpty(fields)) {
			sql  = "SELECT * FROM " + table;
		} else {
			sql  = "SELECT " + link(",",fields) + " FROM " + table;
		}
		
		if(where != null) {
			sql += " WHERE " + where;
		}
		
		if(orderBy != null) {
			sql += " ORDER BY " + orderBy;
		}
		
		if(start != null && limit != null && start >=0 && limit > 0) {
			sql += " LIMIT " + start + "," + limit;
		}
		
		return super.queryForList(sql);
	}
	
	@Override
	public Map<String, Object> selectOne(String table, String[] fields, String where)
	{
		List<Map<String, Object>> rows = select(table, fields, where, null, 0, 1);
		
		return rows.isEmpty() ? null : rows.get(0);
	}
	

	@Override
	public List<Map<String, Object>> select(String table, String[] fields, Map<String, Object> where)
	{
		//SELECT field1,field2.. FROM table WHERE key1 = ? AND key2 = ? ..;

		return select(table, fields, where, null, null, null);
	}

	@Override
	public List<Map<String, Object>> select(String table, String[] fields, Map<String, Object> where, String orderBy, Integer start, Integer limit)
	{
		//SELECT field1,field2.. FROM table WHERE key1 = ? AND key2 = ?.. ORDER BY orderBy LIMIT start,limit;

		Assert.notNull(table);
		
		String sql = null;
		if(isEmpty(fields)) {
			sql  = "SELECT * FROM " + table;
		} else {
			sql  = "SELECT " + link(",",fields) + " FROM " + table;
		}
		
		Object[] args = null;
		
		//WHERE
		if(! isEmpty(where)) {
			PreparedSqlSegment whereSegment = preparedWhereString(where);
			
			args = whereSegment.args;
			
			sql += " WHERE " + whereSegment.segment;
		}
		
		if(orderBy != null) {
			sql += " ORDER BY " + orderBy;
		}
		
		if(start != null && limit != null && start >=0 && limit > 0) {
			sql += " LIMIT " + start + "," + limit;
		}
		
		return super.queryForList(sql, args);
	}

	@Override
	public Map<String, Object> selectOne(String table, String[] fields, Map<String, Object> where)
	{
		List<Map<String, Object>> rows = select(table, fields, where, null, 0, 1);
		
		return rows.isEmpty() ? null : rows.get(0);
	}
	

	@Override
	public int update(String table, Map<String, Object> data, String where)
	{
		//UPDATE table SET field1 = ?, field2 = ?.. WHERE where; 
		
		Assert.notNull(table);
		Assert.notEmpty(data);

		PreparedSqlSegment updateSegment = preparedUpdateString(data);

		String sql = "UPDATE " + table + " SET " + updateSegment.segment;
		
		if(where != null) {
			sql += " WHERE " + where;
		}
		
		return super.update(sql, updateSegment.args);
	}

	@Override
	public int update(String table, Map<String, Object> data, Map<String, Object> where)
	{
		//UPDATE table SET field1 = ?, field2 = ?.. WHERE key1 = ? AND key2 = ?..; ; 
		
		Assert.notNull(table);
		Assert.notEmpty(data);
		
		int argCnt = data.size();
		if(where != null) {
			argCnt += where.size();
		}

		List<Object> args      = new ArrayList<Object>(argCnt);
		
		PreparedSqlSegment updateSegment = preparedUpdateString(data);
		
		addAll(args,updateSegment.args);

		String sql = "UPDATE " + table + " SET " + updateSegment.segment;
		
		//WHERE
		if(! isEmpty(where)) {
			PreparedSqlSegment whereSegment = preparedWhereString(where);
			
			addAll(args,whereSegment.args);
			
			sql += " WHERE " + whereSegment.segment;
		}
		
		return super.update(sql, args.toArray());
	}


	@Override
	public int delete(String sql)
	{
		return queryUpdate(sql);
	}

	@Override
	public int delete(String sql, Object... args)
	{
		return queryUpdate(sql, args);
	}
	
	@Override
	public int delete(String table, String where)
	{
		//DELETE FROM table WHERE where;
		
		Assert.notNull(table);
		
		String sql = "DELETE FROM " + table;
		
		if(where != null) {
			sql += " WHERE " + where;
		}
		
		return super.update(sql);
	}

	@Override
	public int delete(String table, Map<String, Object> where)
	{
		//DELETE FROM table WHERE key1 = ? AND key2 = ? ..;
		
		Assert.notNull(table);
		
		String sql = "DELETE FROM " + table;
		
		Object[] args = null;
		
		//WHERE
		if(! isEmpty(where)) {
			PreparedSqlSegment whereSegment = preparedWhereString(where);
			
			args = whereSegment.args;
			
			sql += " WHERE " + whereSegment.segment;
		}
		
		return super.update(sql,args);
	}

	@Override
	public int count(String sql)
	{
		return super.queryForInt(sql);
	}

	@Override
	public int count(String sql, Object... args)
	{
		return super.queryForInt(sql,args);
	}
	
	@Override
	public int count(String table, String where)
	{
		//SELECT COUNT(*) FROM table WHERE where;
		
		Assert.notNull(table);
		
		String sql = "SELECT COUNT(*) FROM " + table;
		
		if(where != null) {
			sql += " WHERE " + where;
		}

		return super.queryForInt(sql);
	}

	@Override
	public int count(String table, Map<String, Object> where)
	{
		//SELECT COUNT(*) FROM table WHERE key1 = ? AND key2 = ? ..;
		
		Assert.notNull(table);
		
		String sql = "SELECT COUNT(*) FROM " + table;
		
		Object[] args = null;
		
		//WHERE
		if(! isEmpty(where)) {
			PreparedSqlSegment whereSegment = preparedWhereString(where);
			
			args = whereSegment.args;
			
			sql += " WHERE " + whereSegment.segment;
		}

		return super.queryForInt(sql,args);
	}
	
	
	//-----------------------------------------------------------
	//-----以下为通用的SQL操作 -----------------------------------------
	//-----------------------------------------------------------
	
	
	@Override
	public List<Map<String, Object>> query(String sql) throws DataAccessException
	{
		return super.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> query(String sql, Object... args) throws DataAccessException
	{
		return super.queryForList(sql, args);
	}
	
	@Override
	public Map<String, Object> queryOne(String sql) throws DataAccessException
	{
		List<Map<String, Object>> rows = super.queryForList(sql);
		
		return rows.isEmpty() ? null : rows.get(0);
	}

	@Override
	public Map<String, Object> queryOne(String sql, Object... args) throws DataAccessException
	{
		List<Map<String, Object>> rows = super.queryForList(sql, args);
		
		return rows.isEmpty() ? null : rows.get(0);
	}

	@Override
	public int queryUpdate(String sql)
	{
		return super.update(sql);
	}

	@Override
	public int queryUpdate(String sql, Object... args)
	{
		return super.update(sql, args);
	}
}