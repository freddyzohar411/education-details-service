package com.avensys.rts.educationdetailsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.educationdetailsservice.entity.EducationDetailsEntity;

import java.util.List;

public interface EducationDetailsRepository extends JpaRepository<EducationDetailsEntity, Integer> {
    List<EducationDetailsEntity> findByEntityTypeAndEntityId(String entityType, Integer entityId);
}
