package com.example.cmtProject.entity.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
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
@Table(name = "WORK_TIMES")
public class WorkTime {

    @Id
    @Column(name = "WKT_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wktNo; // 근무시간NO (WKT_NO)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_NO", nullable = false)
    private Employees empNo; // 사원번호 (EMP_NO), 사원 테이블 참조

    @Column(name = "WKT_DATE", nullable = false)
    private LocalDate workDate; // 근무일자 (WKT_DATE)

    @Column(name = "WKT_START_TIME")
    private LocalTime startTime; // 출근시간 (WKT_START_TIME)

    @Column(name = "WKT_END_TIME")
    private LocalTime endTime; // 퇴근시간 (WKT_END_TIME)

    @Enumerated(EnumType.STRING)
    @Column(name = "WKT_STATUS", nullable = false)
    private WorkStatus workStatus; // 근무상태 (WKT_STATUS)

    @Enumerated(EnumType.STRING)
    @Column(name = "WKT_TYPE", nullable = false)
    private WorkType workType; // 기준근무유형 (WKT_TYPE)

    @Column(name = "WKT_REMARKS", length = 200)
    private String remarks; // 비고 (WKT_REMARKS)
    
    public static WorkTime toEntity(WorkTimeDTO dto, Employees employee) {
        return WorkTime.builder()
            .empNo(employee)
            .workDate(dto.getWorkDate())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .workStatus(dto.getWorkStatus())
            .workType(dto.getWorkType())
            .remarks(dto.getRemarks())
            .build();
    }

}
