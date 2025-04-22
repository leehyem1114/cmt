package com.example.cmtProject.dto.mes.production;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotOriginDTO {
	private Long lotNo;
	private String childLotCode;
    private String parentLotCode;
    private String childPdtCode;
    private String parentdPdtCode;
    private LocalDate createDate;
    private String prcType;
    private String lineCode;
    private String eqpCode;
    private String woCode;
    private LocalTime startTime;
    private LocalTime finishTime;
    private String woStatusNo;
    private String useYn;
}
