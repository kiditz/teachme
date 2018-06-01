package com.slerpio.teachme.repository;

import com.slerpio.teachme.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findUserPrincipalByPhoneNumberOrUsername(String phoneNumber, String username);
}