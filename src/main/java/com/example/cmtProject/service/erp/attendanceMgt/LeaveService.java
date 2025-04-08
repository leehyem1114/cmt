package com.example.cmtProject.service.erp.attendanceMgt;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.attendanceMgt.LeaveMapper;

@Service
public class LeaveService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);
	
	@Autowired
	LeaveMapper leaveMapper;

	// ADMIN은 모든 휴가정보 조회
	public List<LeaveDTO> getAllLeaves() {
		logger.info("@@@@@@@@@@@@@" + leaveMapper.getAllLeaves());
		return leaveMapper.getAllLeaves();
	}

	// 휴가 일정 관리 저장
	@Transactional
	public void insertLeave(LeaveDTO dto, Employees loginUser) {
		dto.setEmpId(loginUser.getEmpId());
		leaveMapper.insertLeave(dto, dto.getEmpId());	
	}
	
// 결재 연동을 위한 메서드 추가
    
    /**
     * 결재 문서ID로 휴가 정보 조회
     */
    public LeaveDTO getLeaveByDocId(String docId) {
        logger.info("결재 문서ID로 휴가 정보 조회: {}", docId);
        return leaveMapper.getLeaveByDocId(docId);
    }
    
    /**
     * 결재 문서ID와 함께 휴가 저장
     */
    @Transactional
    public void insertLeaveWithDocId(LeaveDTO dto, Employees employee, String docId) {
        logger.info("결재 문서ID와 함께 휴가 저장: 직원={}, 문서ID={}", employee.getEmpId(), docId);
        
        // 이미 처리된 휴가가 있는지 확인
        LeaveDTO existingLeave = leaveMapper.getLeaveByDocId(docId);
        if (existingLeave != null) {
            logger.info("이미 처리된 휴가 신청: {}", docId);
            return;
        }
        
        // 휴가 정보 저장
        leaveMapper.insertLeaveWithDocId(dto, employee.getEmpId(), docId);
        
        logger.info("휴가 정보 저장 완료: {}", employee.getEmpId());
    }
    
    /**
     * 휴가 상태 업데이트
     */
    @Transactional
    public boolean updateLeaveStatus(Long levNo, String status, String remarks) {
        logger.info("휴가 상태 업데이트: 휴가번호={}, 상태={}", levNo, status);
        int result = leaveMapper.updateLeaveStatus(levNo, status, remarks);
        return result > 0;
    }

    
}
    
    
    
    
    

