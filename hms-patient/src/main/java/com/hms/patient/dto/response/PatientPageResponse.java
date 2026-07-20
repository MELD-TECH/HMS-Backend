package com.hms.patient.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientPageResponse {

    private List<PatientSummaryResponse> content;

    private long totalElements;

    private int totalPages;

    private int page;

    private int size;

}
