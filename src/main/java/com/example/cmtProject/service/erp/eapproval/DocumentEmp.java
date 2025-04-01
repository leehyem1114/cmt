//package com.example.cmtProject.service.erp.eapproval;
//
//import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
//import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
//import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
//import com.example.cmtProject.mapper.erp.eapproval.ApprovalLineMapper;
//import com.example.cmtProject.service.erp.employees.EmployeesService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class DocumentEmp {
//
//    /**
//     * 직원 ID로 직원 번호 조회
//     * DocumentService 등에서 활용
//     * 
//     * @param empId 직원 ID (사번)
//     * @return 직원 번호 (EMP_NO)
//     */
//    public Integer getEmployeeNoByEmpId(String empId) {
//        log.info("직원 번호 조회 시작: {}", empId);
//        
//        try {
//            // empMapper를 통해 직원 번호 조회
//            Integer empNo = DocumentMapper.selectEmployeeNoByEmpId(empId);
//
//            if (empNo == null) {
//                log.warn("직원을 찾을 수 없음: {}", empId);
//                throw new RuntimeException("직원 정보를 찾을 수 없습니다: " + empId);
//            }
//
//            log.debug("직원 번호 조회 성공: {} -> {}", empId, empNo);
//            return empNo;
//        } catch (Exception e) {
//            log.error("직원 번호 조회 중 오류 발생: {}", empId, e);
//            throw new RuntimeException("직원 정보 조회 중 오류가 발생했습니다.", e);
//        }
//    }
//    
//}