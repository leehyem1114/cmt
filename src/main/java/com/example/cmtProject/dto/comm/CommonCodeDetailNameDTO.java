package com.example.cmtProject.dto.comm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeDetailNameDTO {
	 /** 상세코드 */
    private String cmnDetailCode;
    
    /** 상세코드명 */
    private String cmnDetailName;
}
