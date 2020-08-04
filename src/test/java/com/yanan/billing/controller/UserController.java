package com.yanan.billing.controller;

import javax.validation.constraints.NotNull;

import com.yanan.billing.constant.Roles;
import com.yanan.billing.model.UserAccountModel;
import com.yanan.billing.model.UserTokenModel;
import com.yanan.billing.model.response.BaseAppResult;
import com.yanan.billing.service.sms.SMSService;
import com.yanan.billing.service.user.UserService;
import com.yanan.billing.utils.UserUtils;
import com.yanan.framework.jdb.DBInterface.LOGIC_STATUS;
import com.yanan.framework.jdb.operate.Insert;
import com.yanan.framework.jdb.operate.Query;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.plugin.autowired.exception.Error;
import com.yanan.framework.webmvc.annotations.Groups;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.parameter.HTTP.POST;
import com.yanan.framework.webmvc.parameter.annotations.RequestBody;
import com.yanan.framework.webmvc.parameter.annotations.RequestParam;
import com.yanan.framework.webmvc.response.annotations.ResponseJson;
import com.yanan.framework.webmvc.session.Token;
import com.yanan.framework.webmvc.validator.annotations.Length;

/**
 * 用户控制器
 * @author yanan
 *
 */
@Error//记录错误
@RequestMapping("/user")
public class UserController {
	@Service 
	private UserUtils userUtils;
	@Service
	private UserService userService;
	/**
	 * 判断令牌是否有效
	 * @param token
	 * @param result
	 * @return
	 */
	@ResponseJson
	@RequestMapping("/token")
	public BaseAppResult validToken(@NotNull(message="令牌不能为空") @RequestParam("token") String token){
		System.out.println(Token.getToken().getTokenId());
		Query query = new Query(UserTokenModel.class);
		query.addColumnCondition("token", token);
		query.addColumnCondition("status", LOGIC_STATUS.NORMAL);
		UserTokenModel userTokenModel = query.queryOne();
		if(userTokenModel == null || userTokenModel.getStatus() != LOGIC_STATUS.NORMAL){
			return BaseAppResult.failed(-1, "未登录");
		}else if(!userUtils.isAvailable(userTokenModel.getCreateTime())){
			return BaseAppResult.failed(-2, "登录已失效，请重新登录");
		}else {
			UserAccountModel userAccountModel = userService.queryUserById(userTokenModel.getUser());
			if(userAccountModel == null || userAccountModel.getStatus() != LOGIC_STATUS.NORMAL){
				return BaseAppResult.failed(-1, "未登录");
			}
			Token.getToken().set(UserAccountModel.class,userAccountModel);
			Token.getToken().setRoles(Roles.USER);
			return BaseAppResult.success(userTokenModel.getUser());
		}
	}
	/**
	 * 用户登录接口
	 * @param user
	 * @param result
	 * @return
	 */
    /**
     * 用户登录接口
     * @param user 用户数据
     * @param smsCode 短信验证码
     * @param result 
     * @param smsService 短信服务
     * @return
     */
	@ResponseJson
	@Groups(POST.class)
	@RequestMapping("/login")
	public BaseAppResult login(UserAccountModel user,
			@Length(value=6,message = "请填写验证码!")@RequestBody("code") String smsCode,
			@Service SMSService smsService){
		if(!smsService.valid(user.getPhone(), smsCode)){
			return BaseAppResult.failed("验证码错误!");
		}
		UserAccountModel userAccountModel = userService.queryUserByPhone(user.getPhone());
		if(userAccountModel == null){
			user.setStatus(LOGIC_STATUS.NORMAL);
			Insert insert = new Insert(user);
			if(insert.insert()){
				Token.getToken().set(UserAccountModel.class,user);
				Token.getToken().setRoles(Roles.USER);
				return BaseAppResult.success("欢迎您登录!",userUtils.recodeToken(user));
			}else{
				return BaseAppResult.failed("服务器繁忙，请稍后再试！");
			}
		}else{
			if(userAccountModel.getStatus() != LOGIC_STATUS.NORMAL){
				return BaseAppResult.failed(2,"账号已被禁用，详情请联系客服！");
			}
			Token.getToken().set(UserAccountModel.class,userAccountModel);
			Token.getToken().setRoles(Roles.USER);
			return BaseAppResult.success("欢迎您登录!",userUtils.recodeToken(userAccountModel));
		}
	}
}