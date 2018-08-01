package com.slerpio.teachme.service.material;

import org.slerp.core.business.DefaultBusinessFunction;
import com.slerpio.teachme.entity.TmMaterialComment;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import com.slerpio.teachme.repository.TmMaterialCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
@NumberValidation({"id", "page", "size"})
public class GetTmMaterialComment extends DefaultBusinessFunction {

	@Autowired
	private TmMaterialCommentRepository tmMaterialCommentRepository;

	@Override
	public Domain handle(Domain input) {
		int page = input.getInt("page");
		int size = input.getInt("size");
		Page<TmMaterialComment> comment = tmMaterialCommentRepository.getTmMaterialComment(input.getLong("id"), new PageRequest(page, size));
		return new Domain().put("comment", comment);
	}
}