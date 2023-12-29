package com.avensys.rts.educationdetailsservice.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.educationdetailsservice.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.educationdetailsservice.APIClient.UserAPIClient;
import com.avensys.rts.educationdetailsservice.customresponse.HttpResponse;
import com.avensys.rts.educationdetailsservice.entity.EducationDetailsEntity;
import com.avensys.rts.educationdetailsservice.entity.UserEntity;
import com.avensys.rts.educationdetailsservice.payloadnewrequest.EducationDetailsRequestDTO;
import com.avensys.rts.educationdetailsservice.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.educationdetailsservice.payloadnewresponse.EducationDetailsResponseDTO;
import com.avensys.rts.educationdetailsservice.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.educationdetailsservice.repository.EducationDetailsRepository;
import com.avensys.rts.educationdetailsservice.repository.UserRepository;
import com.avensys.rts.educationdetailsservice.util.MappingUtil;

import jakarta.transaction.Transactional;

@Service
public class EducationDetailsServiceImpl implements EducationDetailsService {

	private final String CANDIDATE_EDUCATIONDETAILS_TYPE = "candidate_educationdetails";

	private final Logger log = LoggerFactory.getLogger(EducationDetailsServiceImpl.class);
	private final EducationDetailsRepository educationDetailsRepository;

	@Autowired
	private UserAPIClient userAPIClient;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Autowired
	private UserRepository userRepository;

	public EducationDetailsServiceImpl(EducationDetailsRepository educationDetailsRepository,
			UserAPIClient userAPIClient, FormSubmissionAPIClient formSubmissionAPIClient) {
		this.educationDetailsRepository = educationDetailsRepository;
		this.userAPIClient = userAPIClient;
		this.formSubmissionAPIClient = formSubmissionAPIClient;
	}

	@Override
	@Transactional
	public EducationDetailsResponseDTO createEducationDetails(EducationDetailsRequestDTO educationDetailsRequestDTO) {
		log.info("Creating Education Details: service");
		System.out.println("Education Details: " + educationDetailsRequestDTO);
		EducationDetailsEntity savedEducationDetailsEntity = educationDetailsRequestDTOToEducationDetailsEntity(
				educationDetailsRequestDTO);

		// Save form data to form submission microservice
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(educationDetailsRequestDTO.getCreatedBy());
		formSubmissionsRequestDTO.setFormId(educationDetailsRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(educationDetailsRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(savedEducationDetailsEntity.getId());
		formSubmissionsRequestDTO.setEntityType(educationDetailsRequestDTO.getEntityType());
		HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		savedEducationDetailsEntity.setFormSubmissionId(formSubmissionData.getId());

		return educationDetailsEntityToEducationDetailsResponseDTO(savedEducationDetailsEntity);
	}

	@Override
	public EducationDetailsResponseDTO getEducationDetailsById(Integer id) {
		EducationDetailsEntity educationDetailsFound = educationDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Education Details not found"));
		return educationDetailsEntityToEducationDetailsResponseDTO(educationDetailsFound);
	}

	@Override
	@Transactional
	public EducationDetailsResponseDTO updateEducationDetails(Integer id,
			EducationDetailsRequestDTO educationDetailsRequestDTO) {
		EducationDetailsEntity educationDetailsFound = educationDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Education Details not found"));
		EducationDetailsEntity updatedEducationDetailsEntity = updateEducationDetailsRequestDTOToEducationDetailsEntity(
				educationDetailsFound, educationDetailsRequestDTO);

		// Update form submission
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(educationDetailsRequestDTO.getUpdatedBy());
		formSubmissionsRequestDTO.setFormId(educationDetailsRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(educationDetailsRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(updatedEducationDetailsEntity.getId());
		formSubmissionsRequestDTO.setEntityType(educationDetailsRequestDTO.getEntityType());
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.updateFormSubmission(updatedEducationDetailsEntity.getFormSubmissionId(), formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		updatedEducationDetailsEntity.setFormSubmissionId(formSubmissionData.getId());
		return educationDetailsEntityToEducationDetailsResponseDTO(updatedEducationDetailsEntity);
	}

	@Override
	@Transactional
	public void deleteEducationDetails(Integer id) {
		EducationDetailsEntity educationDetailsEntityFound = educationDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Education Details not found"));
		educationDetailsRepository.delete(educationDetailsEntityFound);
	}

	@Override
	public List<EducationDetailsResponseDTO> getEducationDetailsByEntityTypeAndEntityId(String entityType,
			Integer entityId) {
		List<EducationDetailsEntity> educationDetailsEntityList = educationDetailsRepository
				.findByEntityTypeAndEntityId(entityType, entityId);
//		List<EducationDetailsResponseDTO> educationDetailsResponseDTOList = educationDetailsEntityList.stream()
//				.map(this::educationDetailsEntityToEducationDetailsRequestDTO).toList();
		return educationDetailsEntityList.stream().map(educationDetailsEntity -> {
			return educationDetailsEntityToEducationDetailsResponseDTO(educationDetailsEntity);
		}).toList();
	}

	@Override
	@Transactional
	public void deleteEducationDetailsByEntityTypeAndEntityId(String entityType, Integer entityId) {
		List<EducationDetailsEntity> educationDetailsEntityList = educationDetailsRepository
				.findByEntityTypeAndEntityId(entityType, entityId);
		if (!educationDetailsEntityList.isEmpty()) {
			// Delete each Education Details form submission before deleting
			educationDetailsEntityList.forEach(educationDetailsEntity -> {
				formSubmissionAPIClient.deleteFormSubmission(educationDetailsEntity.getFormSubmissionId());
				educationDetailsRepository.delete(educationDetailsEntity);
			});
		}
	}


	private EducationDetailsResponseDTO educationDetailsEntityToEducationDetailsResponseDTO(
			EducationDetailsEntity educationDetailsEntity) {
		EducationDetailsResponseDTO educationDetailsResponseDTO = new EducationDetailsResponseDTO();
		educationDetailsResponseDTO.setId(educationDetailsEntity.getId());
		educationDetailsResponseDTO.setCreatedAt(educationDetailsEntity.getCreatedAt());
		educationDetailsResponseDTO.setUpdatedAt(educationDetailsEntity.getUpdatedAt());
		educationDetailsResponseDTO.setEntityType(educationDetailsEntity.getEntityType());
		educationDetailsResponseDTO.setEntityId(educationDetailsEntity.getEntityId());
		educationDetailsResponseDTO.setFormId(educationDetailsEntity.getFormId());
		educationDetailsResponseDTO.setFormSubmissionId(educationDetailsEntity.getFormSubmissionId());

		// Get created by User data from user microservice
		Optional<UserEntity> userEntity = userRepository.findById(educationDetailsEntity.getCreatedBy());
		UserEntity userData = userEntity.get();
		educationDetailsResponseDTO.setCreatedBy(userData.getFirstName() + " " + userData.getLastName());

		// Get updated by user data from user microservice
		if (educationDetailsEntity.getUpdatedBy().equals(educationDetailsEntity.getCreatedBy())) {
			educationDetailsResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
		} else {
			userEntity = userRepository.findById(educationDetailsEntity.getUpdatedBy());
			userData = userEntity.get();
			educationDetailsResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
		}

		// Get form submission data
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.getFormSubmission(educationDetailsEntity.getFormSubmissionId());
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
		educationDetailsResponseDTO
				.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));
		return educationDetailsResponseDTO;
	}

	private EducationDetailsEntity updateEducationDetailsRequestDTOToEducationDetailsEntity(
			EducationDetailsEntity educationDetailsEntity, EducationDetailsRequestDTO educationDetailsRequestDTO) {
		educationDetailsEntity.setEntityType(educationDetailsRequestDTO.getEntityType());
		educationDetailsEntity.setEntityId(educationDetailsRequestDTO.getEntityId());
		educationDetailsEntity.setUpdatedBy(educationDetailsRequestDTO.getUpdatedBy());
		educationDetailsEntity.setFormId(educationDetailsRequestDTO.getFormId());
		return educationDetailsRepository.save(educationDetailsEntity);
	}

	private EducationDetailsEntity educationDetailsRequestDTOToEducationDetailsEntity(
			EducationDetailsRequestDTO educationDetailsRequestDTO) {
		EducationDetailsEntity educationDetailsEntity = new EducationDetailsEntity();
		educationDetailsEntity.setEntityType(educationDetailsRequestDTO.getEntityType());
		educationDetailsEntity.setEntityId(educationDetailsRequestDTO.getEntityId());
		educationDetailsEntity.setCreatedBy(educationDetailsRequestDTO.getCreatedBy());
		educationDetailsEntity.setUpdatedBy(educationDetailsRequestDTO.getUpdatedBy());
		educationDetailsEntity.setFormId(educationDetailsRequestDTO.getFormId());
		return educationDetailsRepository.save(educationDetailsEntity);
	}

}
