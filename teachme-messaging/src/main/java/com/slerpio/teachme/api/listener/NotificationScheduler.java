//package com.slerpio.teachme.api.listener;
//
//import io.reactivex.Observable;
//import org.slerp.core.Domain;
//import org.slerp.core.business.BusinessFunction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//
//@Service
//public class NotificationScheduler implements ApplicationListener<BrokerAvailabilityEvent> {
//	private Logger log = LoggerFactory.getLogger(getClass());
//	@Autowired
//	private BusinessFunction getNotificationByStatus;
//	@Autowired
//	private SimpMessagingTemplate template;
//	private AtomicBoolean brokerAvailable = new AtomicBoolean();
//
//	@Scheduled(fixedRate = 5000)
//	public void schedule() {
//		long page = 0L;
//		long size = 10;
//		Domain input = new Domain().put("status", "D").put("page", page).put("size", size);
//		Domain domain = getNotificationByStatus.handle(input).getDomain("notificationPage");
//		if(domain.getLong("numberOfElements") > 0) {
//			Observable.fromIterable(domain.getList("content")).filter(content -> brokerAvailable.get() && content != null).subscribe(payload -> {
//				String path = "/topic/notification/".concat(payload.getString("receiverPhoneNumber")).trim();
//				log.info("Send message to : {}", payload.getString("receiverPhoneNumber"));
//				template.convertAndSend(path, payload);
//			});
//		}
//	}
//
//	@Override
//	public void onApplicationEvent(BrokerAvailabilityEvent event) {
//		this.brokerAvailable.set(event.isBrokerAvailable());
//	}
//}
