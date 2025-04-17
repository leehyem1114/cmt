package com.example.cmtProject.dto.mes.standardInfoMgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BomInfoDTO {

    private Long bomNo; 		//BOM 고유번호
    private String pdtCode; 	//완성품 코드
    private String mtlCode; 	// 원자재 코드
    private int bomQty; 		// 투입 수량
    private String bomUnit; 	//투입 단위 (예: EA, TON)
    private String bomPrcType; 	// 투입 공정 단계 (예: PR, WE, PA, SA)
    private String comments; 	// 비고
    private char useYN; 		//사용 여부
}
