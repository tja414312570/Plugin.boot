package com.yanan.billing.service.user;


import com.yanan.billing.model.UserAccountModel;
import com.yanan.frame.jdb.annotation.Sql;
/**
 * 用户登录服务
 * @author yanan
 *
 */
import com.yanan.frame.plugin.annotations.Service;
@Service
@Sql
public interface UserService {
	/**
	 * 通过电话号码查询登录用户
	 * @param phone
	 * @return
	 */
	UserAccountModel queryUserByPhone(String phone);
	/**
	 * 添加新的用户
	 * @param user
	 * @return
	 */
	int insertUser(UserAccountModel user);
	/**
	 * 根据用户Id查询用户
	 * @param user
	 * @return
	 */
	UserAccountModel queryUserById(String user);
}