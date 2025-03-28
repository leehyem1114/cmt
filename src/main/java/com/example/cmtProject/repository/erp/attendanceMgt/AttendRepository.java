package com.example.cmtProject.repository.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.attendanceMgt.Attend;

@Repository
public interface AttendRepository extends JpaRepository<Attend, Long> {
	
	// 출결 목록 조회
    List<Attend> findByEmpNoOrderByAtdNoDesc(Long empNo);
    
    // 페이징 처리
    Page<Attend> findAllByOrderByAttendDateDesc(Pageable pageable);

    // 출근 조회
	boolean existsByEmpNameAndAttendDate(String empName, LocalDateTime attendDate);
    
    
    
    
}
