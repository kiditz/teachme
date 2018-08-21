package com.slerpio.teachme.repository;

import com.slerpio.teachme.entity.TmNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TmNotificationRepository
		extends
			JpaRepository<TmNotification, Long> {
}