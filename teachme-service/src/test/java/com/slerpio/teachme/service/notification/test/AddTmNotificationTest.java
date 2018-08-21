package com.slerpio.teachme.service.notification.test;

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
public class AddTmNotificationTest
		extends
			AbstractTransactionalJUnit4SpringContextTests {

	static private Logger log = LoggerFactory
			.getLogger(AddTmNotificationTest.class);
	@Autowired
	private BusinessTransaction addTmNotification;

	@Before
	public void prepare() {
		executeSqlScript(
				"classpath:com/slerpio/teachme/service/notification/test/AddTmNotificationTest.sql",
				false);
	}

	@Test
	public void testSuccess() {
		String text = "This is test";
		String message = "Test Notification";
		Long userId = 1L;
		String title = "Rifky Aditya Bastara";
		Domain tmNotificationDomain = new Domain();
		tmNotificationDomain.put("text", text);
		tmNotificationDomain.put("message", message);
		tmNotificationDomain.put("userId", userId);
		tmNotificationDomain.put("title", title);
		tmNotificationDomain.put("sent", false);
		Domain outputTmNotification = addTmNotification.handle(tmNotificationDomain);
		log.info("Result Test {}", outputTmNotification);
		Assertions.assertThat(outputTmNotification.get("text")).isEqualTo(text);
		Assertions.assertThat(outputTmNotification.get("message")).isEqualTo(message);
		Assertions.assertThat(outputTmNotification.get("title")).isEqualTo(title);
	}
}