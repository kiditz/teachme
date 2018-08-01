package com.slerpio.teachme.api.controller;

import org.slerp.core.Domain;
import org.slerp.core.business.BusinessFunction;
import org.slerp.core.business.BusinessTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.logging.Logger;

@RestController
public class MaterialCommentController {

	@Autowired
	private BusinessTransaction addTmMaterialComment;

	@Autowired
	private BusinessFunction getTmMaterialComment;
	@Autowired
	SimpMessagingTemplate template;

	@MessageMapping("/comment.material/add")
	public void addCommentMaterial(@RequestBody Domain serviceDomain) {
		serviceDomain.put("createdAt", new Date());
		serviceDomain.put("updateAt", new Date());
		Domain result =  addTmMaterialComment.handle(serviceDomain);
		template.convertAndSend("/topic/comment.material." + result.getLong("material_id"),result);
	}

	@SubscribeMapping("/comment.material/get_comment/{id}/{page}/{size}")
	public Domain getLearningMaterialComment(@DestinationVariable long id, @DestinationVariable long page, @DestinationVariable  long size){
		Domain input = new Domain();
		input.put("id", id);
		input.put("page", page);
		input.put("size", size);
		return getTmMaterialComment.handle(input);
	}
}