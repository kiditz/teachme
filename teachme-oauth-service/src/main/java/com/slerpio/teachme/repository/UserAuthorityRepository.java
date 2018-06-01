package com.slerpio.teachme.repository;

import com.slerpio.teachme.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

	@Query("SELECT a.authority FROM UserAuthority a WHERE a.userId.phoneNumber = :phoneNumber")
	public List<UserAuthority> getAuthorityByUsername(@Param("phoneNumber") String username);
}