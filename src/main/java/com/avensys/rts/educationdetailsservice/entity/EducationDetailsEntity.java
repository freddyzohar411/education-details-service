package com.avensys.rts.educationdetailsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "education_details")
public class EducationDetailsEntity extends BaseEntity {

	private static final long serialVersionUID = 4539091506101115672L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "entity_id")
	private int entityId;

	@Column(name = "entity_type", length = 30)
	private String entityType;

	@Column(name = "form_id")
	private Integer formId;

	@Column(name = "form_submission_id")
	private Integer formSubmissionId;

}