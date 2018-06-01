package com.slerpio.teachme.repository;

import com.slerpio.teachme.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
	
	Client getByClientId(String clientId);
}