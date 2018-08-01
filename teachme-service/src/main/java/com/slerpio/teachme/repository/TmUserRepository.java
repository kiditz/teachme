package com.slerpio.teachme.repository;

import com.slerpio.teachme.entity.TmUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TmUserRepository extends JpaRepository<TmUser, Long> {
}