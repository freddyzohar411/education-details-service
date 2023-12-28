package com.avensys.rts.educationdetailsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.educationdetailsservice.constant.MessageConstants;
import com.avensys.rts.educationdetailsservice.payloadnewrequest.EducationDetailsRequestDTO;
import com.avensys.rts.educationdetailsservice.payloadnewresponse.EducationDetailsResponseDTO;
import com.avensys.rts.educationdetailsservice.service.EducationDetailsServiceImpl;
import com.avensys.rts.educationdetailsservice.util.JwtUtil;
import com.avensys.rts.educationdetailsservice.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EducationDetailsController {

	private final Logger log = LoggerFactory.getLogger(EducationDetailsController.class);
	private final EducationDetailsServiceImpl educationDetailsService;
	private final MessageSource messageSource;

	@Autowired
	private JwtUtil jwtUtil;

	public EducationDetailsController(EducationDetailsServiceImpl educationDetailsService, MessageSource messageSource) {
		this.educationDetailsService = educationDetailsService;
		this.messageSource = messageSource;
	}

	@PostMapping("/education-details")
	public ResponseEntity<Object> createEducationDetails(@Valid @RequestBody EducationDetailsRequestDTO educationDetailsRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		log.info("Create a Education Details : Controller ");
		Long userId = jwtUtil.getUserId(token);
		educationDetailsRequestDTO.setCreatedBy(userId);
		educationDetailsRequestDTO.setUpdatedBy(userId);
		EducationDetailsResponseDTO createdWorkExperience = educationDetailsService.createEducationDetails(educationDetailsRequestDTO);
		return ResponseUtil.generateSuccessResponse(createdWorkExperience, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("/education-details/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> getEducationDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Get Education Details by entity type and entity id : Controller ");
		return ResponseUtil.generateSuccessResponse(
				educationDetailsService.getEducationDetailsByEntityTypeAndEntityId(entityType, entityId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@DeleteMapping("/education-details/{id}")
	public ResponseEntity<Object> deleteEducationDetails(@PathVariable Integer id) {
		log.info("Delete Education Details : Controller ");
		educationDetailsService.deleteEducationDetails(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PutMapping("/education-details/{id}")
	public ResponseEntity<Object> updateEducationDetails(@PathVariable Integer id,
			@Valid @RequestBody EducationDetailsRequestDTO educationDetailsRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		log.info("Update Education Details : Controller ");
		Long userId = jwtUtil.getUserId(token);
		educationDetailsRequestDTO.setUpdatedBy(userId);
		EducationDetailsResponseDTO educationDetailsResponseDTO = educationDetailsService.updateEducationDetails(id, educationDetailsRequestDTO);
		return ResponseUtil.generateSuccessResponse(educationDetailsResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This endpoint is to delete Education Details by entity type and entity id
	 * 
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	@DeleteMapping("/education-details/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> deleteEducationDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Delete Education Details by entity type and entity id : Controller ");
		educationDetailsService.deleteEducationDetailsByEntityTypeAndEntityId(entityType, entityId);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
}
