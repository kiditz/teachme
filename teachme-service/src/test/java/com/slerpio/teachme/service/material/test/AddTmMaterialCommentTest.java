package com.slerpio.teachme.service.material.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.junit.Before;
import org.junit.Test;
import org.assertj.core.api.Assertions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TestExecutionListeners(listeners = {DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class}, inheritListeners = false)
@Rollback
public class AddTmMaterialCommentTest
		extends
			AbstractTransactionalJUnit4SpringContextTests {

	static private Logger log = LoggerFactory
			.getLogger(AddTmMaterialCommentTest.class);
	@Autowired
	BusinessTransaction addTmMaterialComment;

	@Before
	public void prepare() {
		executeSqlScript(
				"classpath:com/slerpio/teachme/service/material/test/AddTmMaterialCommentTest.sql",
				false);
	}

	@Test
	public void testSuccess() {
		Long material_id = 1l;
		String message = "This is test";
		Long sender_user_id = 1l;
		Domain tmMaterialCommentDomain = new Domain();
		tmMaterialCommentDomain.put("material_id", material_id);
		tmMaterialCommentDomain.put("message", message);
		tmMaterialCommentDomain.put("sender_user_id", sender_user_id);
		Domain outputTmMaterialComment = addTmMaterialComment
				.handle(tmMaterialCommentDomain);
		log.info("Result Test {}", outputTmMaterialComment);
//		Assertions.assertThat(tmMaterialCommentDomain.get("material_id"))
//				.isEqualTo(material_id);
//		Assertions.assertThat(tmMaterialCommentDomain.get("message"))
//				.isEqualTo(message);
//		Assertions.assertThat(tmMaterialCommentDomain.get("sender_user_id"))
//				.isEqualTo(sender_user_id);
	}
}