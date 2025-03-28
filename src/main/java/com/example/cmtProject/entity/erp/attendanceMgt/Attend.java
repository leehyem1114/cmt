package com.example.cmtProject.entity.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ATTENDS")
public class Attend {

    @Id
    @Column(name = "ATD_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long atdNo; // 출결NO (ATD_NO)
  
    @Column(name = "EMP_NO")
    private Long empNo; // 사원번호 (EMP_NO)
    
    @Column(name = "EMP_NAME", nullable = false)
    private String empName; // 사원이름 (EMP_NAME)

    @Column(name = "ATD_DATE", nullable = false)
    private LocalDateTime attendDate; // 출근일자 (ATD_DATE)
    
    @Column(name = "ATD_LEAVE")
    private LocalDateTime attendLeave; // 퇴근일자 (ATD_LEAVE)
    
    @Column(name = "ATD_TYPE", nullable = false)
    private String attendType; // 출결유형 (ATD_TYPE)

    @Column(name = "ATD_STATUS", nullable = false)
    private String attendStatus; // 출결상태 (ATD_STATUS)

    @Column(name = "ATD_REMARKS", length = 200)
    private String remarks; // 비고 (ATD_REMARKS)
    
    @Builder
    public Attend(Long atdNo, Long empNo, String empName, LocalDateTime attendDate, LocalDateTime attendLeave, String attendType, String attendStatus,
			String remarks) {
    	this.atdNo = atdNo;
		this.empNo = empNo;
		this.empName = empName;
		this.attendDate = attendDate;
		this.attendLeave = attendLeave;
		this.attendType = attendType;
		this.attendStatus = attendStatus;
		this.remarks = remarks;
	}
    
    public AttendDTO toDto() {
        return AttendDTO.builder()
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



	
    
}
