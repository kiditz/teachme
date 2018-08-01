// package org.slerpio.api.listener;

// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.TimeUnit;

// import org.slerp.core.Domain;
// import TemplateConfig;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Component;

// import io.reactivex.Observable;
// import io.reactivex.functions.Function;

// @Component
// public class MailRegistrationListener {
// 	@Autowired
// 	MailService mailService;
// 	@Autowired
// 	TemplateConfig cfg;
// 	@Autowired
// 	SimpMessagingTemplate simpMessagingTemplate;
// 	static Logger log = LoggerFactory.getLogger(MailRegistrationListener.class);
// 	CountDownLatch latch = new CountDownLatch(1);
// 	@Value("${kafka.messages.mail_regiter}")
// 	String topicMailRegistration;
// 	@Value("${kafka.messages.rm_user_principal}")
// 	String topicRemovePrincipal;
// 	@Autowired
// 	KafkaTemplate<String, Domain> kafkaTemplate;

// 	@KafkaListener(topics = "${kafka.messages.mail_regiter}", containerGroup = "${kafka.groupId}")
// 	public void listenToSendRegistrationMail(Domain userDomain) {
// 		log.info("Send email with domain \n{}", userDomain);

// 		Observable.just(userDomain).map(inputDomain -> {
// 			String to = userDomain.getString("email");
// 			String subject = "Verify Your Account";
// 			String text = cfg.process(userDomain, "registration.html");
// 			mailService.sendMail(to, subject, text, true);
// 			return inputDomain;
// 		}).onErrorResumeNext(t -> {
// 			log.info("Exception : {}", t);
// 			simpMessagingTemplate.convertAndSend("/queue/notify-" + userDomain.getString("username"),
// 					"Send email has been failed, our server may be down");
// 		}).subscribe(a -> {
// 			log.info("Success sending email");
// 			simpMessagingTemplate.convertAndSend("/queue/notify-" + userDomain.getString("username"),
// 					"Send email has been successed");
// 		});
// 		latch.countDown();
// 	}

// 	public static Function<Observable<? extends Throwable>, Observable<?>> exponentialBackoff(int maxRetryCount,
// 			long delay, TimeUnit unit) {
// 		return errors -> errors.zipWith(Observable.range(1, maxRetryCount), (error, retryCount) -> retryCount)
// 				.flatMap(retryCount -> Observable.timer((long) Math.pow(delay, retryCount), unit));
// 	}
// }