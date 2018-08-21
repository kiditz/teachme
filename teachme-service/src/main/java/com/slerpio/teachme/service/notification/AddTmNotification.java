package com.slerpio.teachme.service.notification;

import com.slerpio.teachme.TmConstant;
import com.slerpio.teachme.entity.TmNotification;
import com.slerpio.teachme.entity.TmUser;
import com.slerpio.teachme.repository.TmNotificationRepository;
import com.slerpio.teachme.repository.TmUserRepository;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessTransaction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@KeyValidation({"message", "title", "text", "userId", "sent"})
@NotBlankValidation({"message", "title", "text", "sent"})
public class AddTmNotification extends DefaultBusinessTransaction {

	@Autowired
	private TmNotificationRepository tmNotificationRepository;
	@Autowired
	private TmUserRepository tmUserRepository;

	@Override
	public void prepare(Domain tmNotificationDomain) {
		TmUser userId = tmUserRepository.findOne(tmNotificationDomain.getLong("userId"));
		if(userId == null){
			throw new CoreException(TmConstant.USER_NOT_FOUND);
		}
		tmNotificationDomain.put("userId", new Domain(userId));
	}

	@Override
	public Domain handle(Domain tmNotificationDomain) {
		super.handle(tmNotificationDomain);
		try {
			TmNotification tmNotification = tmNotificationDomain.convertTo(TmNotification.class);
			tmNotification.setCreatedAt(new Date());
			tmNotification.setUpdateAt(null);
			tmNotification = tmNotificationRepository.save(tmNotification);
			return new Domain(tmNotification);
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}
}