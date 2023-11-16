package com.avensys.rts.educationdetailsservice.service;

import java.util.List;

import com.avensys.rts.educationdetailsservice.payloadnewrequest.EducationDetailsRequestDTO;
import com.avensys.rts.educationdetailsservice.payloadnewresponse.EducationDetailsResponseDTO;

public interface EducationDetailsService {

    EducationDetailsResponseDTO createEducationDetails(EducationDetailsRequestDTO contactNewRequestDTO);

    EducationDetailsResponseDTO getEducationDetailsById(Integer id);

    EducationDetailsResponseDTO updateEducationDetails(Integer id, EducationDetailsRequestDTO contactNewRequestDTO);

    void deleteEducationDetails(Integer id);

    List<EducationDetailsResponseDTO> getEducationDetailsByEntityTypeAndEntityId(String entityType, Integer entityId);

    void deleteEducationDetailsByEntityTypeAndEntityId(String entityType, Integer entityId);
}
