package com.slerpio.teachme.security;

import com.slerpio.teachme.model.UserCredentials;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserSecurityService extends UserDetailsService {
	UserCredentials loadUserByUsername(String username);
}
