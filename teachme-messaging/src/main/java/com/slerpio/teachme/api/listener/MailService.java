//package org.slerpio.api.listener;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Component;
//
///**
// * Service to send email message
// * 
// * @author kiditz
// * @since Sunday 17 September 2017
// */
//@Component
//public class MailService {
//
//	@Autowired
//	JavaMailSender sender;
//
//	static Logger log = LoggerFactory.getLogger(MailService.class);
//
//	public void sendMail(String to, String subject, String text, boolean useHtml) throws MessagingException {
//		MimeMessage message = sender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(message);
//		helper.setTo(to);
//		helper.setSubject(subject);
//		helper.setText(text, useHtml);
//		sender.send(helper.getMimeMessage());
//	}
//}
