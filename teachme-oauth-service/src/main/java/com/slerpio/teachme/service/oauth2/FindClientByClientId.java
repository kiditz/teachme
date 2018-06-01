package com.slerpio.teachme.service.oauth2;

import com.slerpio.teachme.entity.Client;
import com.slerpio.teachme.repository.ClientRepository;
import org.slerp.core.business.DefaultBusinessFunction;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@KeyValidation("clientId")
@NumberValidation({})
@NotBlankValidation({})
public class FindClientByClientId extends DefaultBusinessFunction {

	@Autowired
	private ClientRepository clientRepository;

	@Override
	public Domain handle(Domain clientDomain) {
		Client client = clientRepository.getByClientId(clientDomain.getString("clientId"));
		return new Domain().put("client", client);
	}
}