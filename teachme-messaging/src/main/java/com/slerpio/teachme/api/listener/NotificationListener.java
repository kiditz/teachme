//package com.slerpio.teachme.api.listener;
//
//import org.slerp.core.Domain;
//import org.slerp.core.business.BusinessTransaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
///**
// * @author kiditz
// * */
//@Component
//public class NotificationListener {
//    private Logger log = LoggerFactory.getLogger(getClass());
//    @Autowired
//    BusinessTransaction addNotification;
//
//    @KafkaListener(topics = "${topic.notification.add}", containerGroup = "${topic.notification.group}")
//    public void addNotification(Domain domain){
//        log.info("Notification Message : ", domain);
//        domain.put("status", "D");
//        addNotification.handle(domain);
//    }
//
//}
