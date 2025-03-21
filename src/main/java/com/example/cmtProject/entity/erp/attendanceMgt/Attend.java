package com.example.cmtProject.entity.erp.attendanceMgt;

import java.time.LocalDate;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ATTENDS")
public class Attend {

    @Id
    @Column(name = "ATD_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long atdNo; // 출결NO (ATD_NO)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_NO", nullable = false)
    private Employees empNo; // 사원번호 (EMP_NO)

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
    
    public static Attend toEntity(AttendDTO dto, Employees employee) {
        return Attend.builder()
            .empNo(employee)
            .attendDate(dto.getAttendDate())
            .attendType(dto.getAttendType())
            .attendStatus(dto.getAttendStatus())
            .remarks(dto.getRemarks())
            .build();
    }
    
}
