package com.avensys.rts.educationdetailsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.educationdetailsservice.entity.EducationDetailsEntity;

public interface EducationDetailsRepository extends JpaRepository<EducationDetailsEntity, Integer> {
    List<EducationDetailsEntity> findByEntityTypeAndEntityId(String entityType, Integer entityId);
}
