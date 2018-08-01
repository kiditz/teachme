package com.slerpio.teachme.repository;

import com.slerpio.teachme.entity.TmMaterialComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface TmMaterialCommentRepository
		extends
			JpaRepository<TmMaterialComment, Long> {

	@Query("SELECT t FROM TmMaterialComment t WHERE t.materialId = :id")
	Page<TmMaterialComment> getTmMaterialComment(@Param("id") Long id, Pageable pageable);
}