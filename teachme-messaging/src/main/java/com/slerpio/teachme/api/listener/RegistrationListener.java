// package org.slerpio.api.listener;

// import org.slerp.core.Domain;
// import org.slerp.core.business.BusinessTransaction;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Component;

// import io.reactivex.Single;

// @Component
// public class RegistrationListener {
// 	Logger log = LoggerFactory.getLogger(getClass());
// 	@Value("${kafka.messages.rm_user_principal}")
// 	String topicRemovePrincipal;
// 	@Value("${kafka.messages.mail_regiter}")
// 	String topicMailRegistration;
// 	@Autowired
// 	BusinessTransaction addProfile;
// 	@Autowired
// 	KafkaTemplate<String, Domain> kafkaTemplate;

// 	@KafkaListener(topics = "${kafka.messages.register}", containerGroup = "${kafka.groupId}")
// 	public void listenRegistration(Domain userDomain) {
// 		log.info("Domain {}", userDomain);
// 		String username = userDomain.getString("username");
// 		String address = "";
// 		String email = userDomain.getString("email");
// 		String phoneNumber = "";
// 		String imagePath = "";
// 		String fullname = "";
// 		Domain profileDomain = new Domain();
// 		profileDomain.put("schoolId", userDomain.getDomain("schoolId"));
// 		profileDomain.put("username", username);
// 		profileDomain.put("address", address);
// 		profileDomain.put("email", email);
// 		profileDomain.put("phoneNumber", phoneNumber);
// 		profileDomain.put("imagePath", imagePath);
// 		profileDomain.put("fullname", fullname);
// 		profileDomain.put("address", address);
// 		Single.just(profileDomain).map(profile -> {
// 			log.info("Processing Profile to Save : {}", profile);
// 			Domain outputDomain = addProfile.handle(profile);
// 			return outputDomain;
// 		}).doOnError(t -> {
// 			log.error("CoreException : {}", t);
// 			log.info("Send back to tell oauth should be reverse user by removing it self");
// 			kafkaTemplate.send(topicRemovePrincipal, userDomain);
// 		}).subscribe(profile -> {
// 			log.info("Profile Saved : {}", profile);
// 			log.info("Will be send email into");
// 			kafkaTemplate.send(topicMailRegistration, userDomain);
// 		});
// 	}
// }