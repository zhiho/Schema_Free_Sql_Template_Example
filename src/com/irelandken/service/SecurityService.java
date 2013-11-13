package com.irelandken.service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.irelandken.sql.SqlTemplate;


@Component
public class SecurityService {
	
	@Autowired
	private SqlTemplate template;
	
	//Jusy For share the JDBC Connection, not open Transaction in fact;
	//@Transactional(propagation=Propagation.SUPPORTS)
	public boolean login(String username,String password) {
		
		//密码验证
		Map<String,Object> where = new HashMap<String,Object>(1);
		where.put("username", username);
		
		Map<String, Object> row = template.selectOne("users", new String[]{"password"}, where);
		
		String pwd = (row == null) ? null : (String)row.get("password");
		
		if(pwd == null || ! pwd.equals(password)) {
			return false;
		}
		
		//更新最后访问时间
		Map<String, Object> data = new HashMap<String, Object>(1);
		data.put("last_access_time", new Date());
		template.update("users", data, where);
		
		return true;
	}
	
	
	public Map<String, Object> getUseinfo(String username) {
		
		return template.selectOne("users", null, "username='"+username +"'");
	}

}
