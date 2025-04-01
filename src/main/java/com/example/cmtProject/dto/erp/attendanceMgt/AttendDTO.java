package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDateTime;

import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class AttendDTO {

    private Long atdNo; // 출결NO
	
    private Long empNo; // 사원번호
    
    private String empName; // 사원이름
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime attendDate; // 출근일자
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime attendLeave; // 퇴근일자
    
    private String attendType; // 출결유형
    
    private String attendStatus; // 출결상태

    private String remarks; // 비고
    
    public Attend toEntity() {
        return Attend.builder()
        	.atdNo(atdNo)
            .empNo(empNo)
            .empName(empName)
            .attendDate(attendDate)
            .attendLeave(attendLeave)
            .attendType(attendType)
            .attendStatus(attendStatus)
            .remarks(remarks)
            .build();
    }

    @Builder
	public AttendDTO(Long atdNo, Long empNo, String empName, LocalDateTime attendDate, LocalDateTime attendLeave, 
			String attendType, String attendStatus, String remarks) {
    	this.atdNo = atdNo;
		this.empNo = empNo;
		this.empName = empName;
		this.attendDate = attendDate;
		this.attendLeave = attendLeave;
		this.attendType = attendType;
		this.attendStatus = attendStatus;
		this.remarks = remarks;
	}
    


    
}
















