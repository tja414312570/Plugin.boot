package com.yanan.billing.utils;

import java.util.Date;

import com.yanan.billing.model.UserAccountModel;
import com.yanan.billing.model.UserTokenModel;
import com.yanan.frame.jdb.DBInterface.LOGIC_STATUS;
import com.yanan.frame.jdb.operate.Insert;
import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.autowired.property.Property;
import com.yanan.frame.servlets.session.Token;

@Register
public class UserUtils {
	@Property("user_token_available")
	private int tokenAvailableTimes;
	public boolean isAvailable(Date diffDate){
		Date now = new Date();
		return (now.getTime() - diffDate.getTime())/1000 < tokenAvailableTimes;
	}
	public String recodeToken(UserAccountModel user) {
		String tokenId = Token.getToken().getTokenId();
		UserTokenModel userTokenModel = new UserTokenModel();
		userTokenModel.setCreateTime(new Date());
		userTokenModel.setStatus(LOGIC_STATUS.NORMAL);
		userTokenModel.setUser(user.getUid());
		userTokenModel.setToken(tokenId);
		Insert insert = new Insert(userTokenModel);
		insert.insert();
		return tokenId;
	}
}