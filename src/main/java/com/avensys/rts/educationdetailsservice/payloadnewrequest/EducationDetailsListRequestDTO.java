package com.avensys.rts.educationdetailsservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EducationDetailsListRequestDTO {
	private List<EducationDetailsRequestDTO> educationDetailsList;
}
