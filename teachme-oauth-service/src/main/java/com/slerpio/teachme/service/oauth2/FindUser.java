package com.slerpio.teachme.service.oauth2;

import com.slerpio.teachme.OauthConstant;
import com.slerpio.teachme.entity.User;
import com.slerpio.teachme.repository.UserRepository;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessFunction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@KeyValidation("query")
@NumberValidation({})
@NotBlankValidation({ "query" })
public class FindUser extends DefaultBusinessFunction {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Domain handle(Domain userPrincipalDomain) {
		super.handle(userPrincipalDomain);
		String query = userPrincipalDomain.getString("query");
		User userPrincipal = userRepository.findUserPrincipalByPhoneNumberOrUsername(query, query);
		if (userPrincipal == null)
			throw new CoreException(OauthConstant.USER_NOT_FOUND);
		return new Domain().put("user", userPrincipal);
	}
}