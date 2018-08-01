package com.slerpio.teachme.service.material;

import com.slerpio.teachme.entity.TmMaterialComment;
import com.slerpio.teachme.entity.TmUser;
import com.slerpio.teachme.repository.TmMaterialCommentRepository;
import com.slerpio.teachme.repository.TmUserRepository;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessTransaction;
import org.slerp.core.validation.KeyValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@KeyValidation({"material_id", "sender_user_id", "message"})
public class AddTmMaterialComment extends DefaultBusinessTransaction {

	@Autowired
	private TmMaterialCommentRepository tmMaterialCommentRepository;
	@Autowired
	private TmUserRepository tmUserRepository;

	@Override
	public void prepare(Domain input) {
	    input.put("created_at", new Date());
		TmUser senderUserId = tmUserRepository.findOne(input.getLong("sender_user_id"));
		if (senderUserId == null) {
			throw new CoreException("user.not.found");
		}
		input.put("user", new Domain(senderUserId));
	}

	@Override
	public Domain handle(Domain input) {
		super.handle(input);
		try {
			TmMaterialComment tmMaterialComment = input.convertTo(TmMaterialComment.class);
			tmMaterialComment = tmMaterialCommentRepository.save(tmMaterialComment);
			return new Domain(tmMaterialComment);
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}
}