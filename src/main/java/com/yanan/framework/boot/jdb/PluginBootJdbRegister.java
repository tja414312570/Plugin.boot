package com.yanan.framework.boot.jdb;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;

import com.yanan.framework.jdb.datasource.DefaultDataSource;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.plugin.autowired.enviroment.Variable;
import com.yanan.utils.string.StringUtil;

/**
 * Plugin jdb 引导服务
 * 添加plugin.jdb.driver环境激活引导
 * @author tja41
 *
 */
@Variable("plugin.jdb")
@Register(layInit = false)
public class PluginBootJdbRegister {
	@Service
	private Logger logger;
	
	private String url;
	
	private String name;
	
	private String password;
	//数据源ID
	private String id;
	
	private String driver;
	//框架本身属性
	private int max_connection = 4;
	private int min_connection = 2;
	private int add_connection = 2;
	private boolean test_connection = false;
	private String test_sql = "";
	private int wait_times;
	private int login_timeout;

	@PostConstruct
	public void start() {
		logger.info("start plugin jdb service");
		DefaultDataSource source = new DefaultDataSource();
		source.setId(this.id);
		source.setUrl(this.url);
		source.setUsername(this.name);
		source.setPassword(this.password);
		source.setDriver(this.driver);
		source.setMax_connection(max_connection);
		source.setAdd_connection(add_connection);
		source.setMin_connection(min_connection);
		source.setTest_connection(test_connection);
		source.setTest_sql(test_sql);
		source.setWait_times(wait_times);
		try {
			if(StringUtil.isNotBlank(this.driver)) {
				logger.info("booting jdb plugin");
				source.setLoginTimeout(login_timeout);
				source.init();
			}else {
				logger.info("not plugin jdb config");
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public String toString() {
		return "PluginBootJdbRegister [logger=" + logger + ", url=" + url + ", name=" + name + ", password=" + password
				+ ", id=" + id + ", driver=" + driver + ", max_connection=" + max_connection + ", min_connection="
				+ min_connection + ", add_connection=" + add_connection + ", test_connection=" + test_connection
				+ ", test_sql=" + test_sql + ", wait_times=" + wait_times + ", login_timeout=" + login_timeout + "]";
	}
}
