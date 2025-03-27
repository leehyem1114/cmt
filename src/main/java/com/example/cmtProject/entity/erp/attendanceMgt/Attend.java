package com.example.cmtProject.entity.erp.attendanceMgt;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@AllArgsConstructor
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
    private LocalDate attendDate; // 출결일자 (ATD_DATE)

    @Enumerated(EnumType.STRING)
    @Column(name = "ATD_TYPE", nullable = false)
    private AttendType attendType; // 출결유형 (ATD_TYPE)

    @Enumerated(EnumType.STRING)
    @Column(name = "ATD_STATUS", nullable = false)
    private AttendStatus attendStatus; // 출결상태 (ATD_STATUS)

    @Column(name = "ATD_REMARKS", length = 200)
    private String remarks; // 비고 (ATD_REMARKS)
    
    @Builder
    public Attend(Long empNo, String empName, LocalDate attendDate, AttendType attendType, AttendStatus attendStatus,
			String remarks) {
		this.empNo = empNo;
		this.empName = empName;
		this.attendDate = attendDate;
		this.attendType = attendType;
		this.attendStatus = attendStatus;
		this.remarks = remarks;
	}
    
    public AttendDTO toDto() {
        return AttendDTO.builder()
            .id(atdNo)
            .empNo(empNo)
            .empName(empName)
            .attendDate(attendDate)
            .attendType(attendType)
            .attendStatus(attendStatus)
            .remarks(remarks)
            .build();
    }



	
    
}
