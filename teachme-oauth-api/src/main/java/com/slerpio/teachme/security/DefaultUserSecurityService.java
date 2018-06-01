package com.slerpio.teachme.security;


import com.slerpio.teachme.OauthConstant;
import com.slerpio.teachme.entity.User;
import com.slerpio.teachme.model.UserCredentials;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserSecurityService implements UserSecurityService {
	@Autowired
	private BusinessFunction findUser;
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public UserCredentials loadUserByUsername(String query) {
		Domain userPrincipalDomain = new Domain();
		userPrincipalDomain.put("query", query);
		log.debug("request : ", userPrincipalDomain);
		Domain outputUserPrincipal = findUser.handle(userPrincipalDomain);
		log.debug("response : {}", outputUserPrincipal);
		User principal = outputUserPrincipal.getDomain("user").convertTo(User.class);
		if(principal == null){
			throw new CoreException(OauthConstant.USER_NOT_FOUND);
		}
		return new UserCredentials(principal);
	}
}
