package com.example.cmtProject.controller.erp.eapproval;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.comm.response.ResponseCode;
import com.example.cmtProject.constants.ApprovalStatus;
import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.service.erp.eapproval.DocumentService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.eapproval.DocFormService;
import com.example.cmtProject.service.erp.eapproval.ApprovalProcessService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 전자결재 REST API 컨트롤러
 * 클라이언트에 데이터를 JSON 형태로 제공하는 API 컨트롤러
 */
@RestController
@RequestMapping("/api/eapproval")
@RequiredArgsConstructor
@Slf4j
public class EapprovalRestController {

    private final DocumentService documentService;
    private final DocFormService docFormService;
    private final ApprovalProcessService approvalProcessService;
    private final ObjectMapper objectMapper;
    private final EmployeesService empService;
    
    /**
     * 문서 양식 조회 API
     */
    @GetMapping("/form/{formId}")
    public ApiResponse<DocFormDTO> getFormContent(@PathVariable(name = "formId") String formId) {
        log.debug("문서 양식 조회 요청: {}", formId);
        try {
            DocFormDTO form = docFormService.getDocFormById(formId);
            if (form == null) {
                log.warn("양식을 찾을 수 없음: {}", formId);
                return ApiResponse.error("양식을 찾을 수 없습니다.", ResponseCode.NOT_FOUND);
            }
            return ApiResponse.success(form);
        } catch (Exception e) {
            log.error("양식 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("양식 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 상세 조회 API
     */
    @GetMapping("/document/{docId}")
    public ApiResponse<DocumentDTO> getDocumentDetail(@PathVariable String docId) {
        log.debug("문서 상세 조회 요청: {}", docId);
        try {
            DocumentDTO document = documentService.getDocumentDetail(docId);
            if (document == null) {
                log.warn("문서를 찾을 수 없음: {}", docId);
                return ApiResponse.error("문서를 찾을 수 없습니다.", ResponseCode.NOT_FOUND);
            }
            return ApiResponse.success(document);
        } catch (Exception e) {
            log.error("문서 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 저장 API (임시저장/결재요청)
     * FormData를 통한 파일 업로드 지원
     */
    @PostMapping("/document")
    public ApiResponse<DocumentDTO> saveDocument(
            @RequestParam(value = "docId", required = false) String docId,
            @RequestParam(value = "docNumber", required = false) String docNumber,
            @RequestParam("formId") String formId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("isTempSave") boolean isTempSave,
            @RequestParam("approvalLinesJson") String approvalLinesJson,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        log.debug("문서 저장 요청: 임시저장={}, 제목={}", isTempSave, title);
        try {
            // 현재 로그인 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();
            
            // 문서 DTO 생성
            DocumentDTO documentDTO = new DocumentDTO();
            
            // 문서 ID가 없으면 새로 생성
            if (docId == null || docId.isEmpty()) {
                docId = UUID.randomUUID().toString();
            }
            
            documentDTO.setDocId(docId);
            documentDTO.setDocNumber(docNumber);
            documentDTO.setFormId(formId);
            documentDTO.setTitle(title);
            documentDTO.setContent(content);
            documentDTO.setDrafterId(currentUserId);
            
            // 부서 정보는 추후 사용자 서비스에서 조회
            // 현재는 DocumentMapper에서 사용자 정보 조회 후 설정
            Integer drafterId = documentService.getEmployeeNoByEmpId(currentUserId);
            String draftDeptCode = getDraftDeptCode(drafterId);
            documentDTO.setDraftDept(draftDeptCode);
            
            // 결재선 정보 설정
            List<ApprovalLineDTO> approvalLines = parseApprovalLines(approvalLinesJson);
            documentDTO.setApprovalLines(approvalLines);
            
            // 문서 저장
            DocumentDTO savedDocument = documentService.saveDocument(documentDTO, isTempSave);
            
            // 첨부파일 처리 (현재는 파일 개수만 로깅)
            if (files != null && !files.isEmpty()) {
                log.debug("첨부파일 {}개 있음 (현재 처리 로직 없음)", files.size());
                // TODO: 파일 업로드 처리 로직 구현
                // attachmentService.saveAttachments(docId, files);
            }
            
            String message = isTempSave ? "문서가 임시저장되었습니다." : "결재요청이 완료되었습니다.";
            return ApiResponse.success(message, savedDocument);
        } catch (Exception e) {
            log.error("문서 저장 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 기안자 부서 코드 조회 (임시 메서드)
     * EmployeesService 구현 후에는 해당 서비스로 대체 예정
     */
    private String getDraftDeptCode(Integer drafterId) {
        // TODO: EmployeesService 구현 후에는 해당 서비스에서 부서 코드 조회
        // 현재는 임시로 기본 부서 코드 반환
        return "DEPT";
    }
    
    /**
     * 결재선 JSON 파싱 헬퍼 메서드
     */
    private List<ApprovalLineDTO> parseApprovalLines(String approvalLinesJson) throws Exception {
        if (approvalLinesJson == null || approvalLinesJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        return objectMapper.readValue(approvalLinesJson, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, ApprovalLineDTO.class));
    }

    /**
     * 결재 처리 API (승인/반려)
     */
    @PostMapping("/document/{docId}/process")
    public ApiResponse<Map<String, Object>> processApproval(
            @PathVariable String docId,
            @RequestParam Integer approverId,
            @RequestParam String decision,
            @RequestParam(required = false) String comment) {
        
        log.debug("결재 처리 요청: 문서={}, 결재자={}, 의사결정={}", docId, approverId, decision);
        try {
            if (DocumentStatus.REJECTED.equals(decision)) {
                approvalProcessService.reject(docId, approverId, comment);
            } else {
                approvalProcessService.approve(docId, approverId, comment);
            }
            
            String message = ApprovalStatus.APPROVED.equals(decision) ? "결재가 승인되었습니다." : "결재가 반려되었습니다.";
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("decision", decision);
            
            return ApiResponse.success(message, result);
        } catch (RuntimeException e) {
            log.warn("결재 처리 중 비즈니스 오류: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            log.error("결재 처리 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("결재 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 기안 문서 목록 조회 API
     */
    @GetMapping("/drafts")
    public ApiResponse<List<DocumentDTO>> getDrafterDocuments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        
        log.debug("기안 문서 목록 조회 요청: 기안자={}", currentUserId);
        try {
            List<DocumentDTO> documents = documentService.getDrafterDocumentsByEmpId(currentUserId);
            return ApiResponse.success(documents);
        } catch (Exception e) {
            log.error("문서 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 결재 대기 문서 목록 조회 API
     */
    @GetMapping("/pending")
    public ApiResponse<List<DocumentDTO>> getPendingDocuments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        
        log.debug("결재 대기 문서 목록 조회 요청: 결재자={}", currentUserId);
        try {
            List<DocumentDTO> documents = documentService.getPendingDocumentsByEmpId(currentUserId);
            return ApiResponse.success(documents);
        } catch (Exception e) {
            log.error("대기 문서 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("대기 문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 상태별 문서 목록 조회 API
     */
    @GetMapping("/documents/status/{status}")
    public ApiResponse<List<DocumentDTO>> getDocumentsByStatus(@PathVariable String status) {
        log.debug("상태별 문서 목록 조회 요청: 상태={}", status);
        try {
            List<DocumentDTO> documents = documentService.getDocumentsByStatus(status);
            return ApiResponse.success(documents);
        } catch (Exception e) {
            log.error("문서 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 결재자 목록 조회 API (조직도)
     */
    @GetMapping("/approvers")
    public ApiResponse<List<EmpListPreviewDTO>> getApprovers() {
        log.debug("결재자 목록 조회 요청");
        try {
            // DB에서 직원 목록 조회
            List<EmpListPreviewDTO> employees = empService.getEmplist();
            
            if (employees == null) {
                log.error("직원 목록이 null입니다");
                return ApiResponse.error("결재자 목록을 가져올 수 없습니다", ResponseCode.SERVER_ERROR);
            }
            
            return ApiResponse.success(employees);
        } catch (Exception e) {
            log.error("결재자 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("결재자 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 문서 삭제 API (임시저장 문서만 삭제 가능)
     */
    @DeleteMapping("/document/{docId}")
    public ApiResponse<Boolean> deleteDocument(@PathVariable String docId) {
        log.debug("문서 삭제 요청: {}", docId);
        try {
            boolean result = documentService.deleteDocument(docId);
            if (result) {
                return ApiResponse.success("문서가 삭제되었습니다.", true);
            } else {
                log.warn("임시저장 문서가 아니어서 삭제 불가: {}", docId);
                return ApiResponse.error("임시저장 문서만 삭제할 수 있습니다.", ResponseCode.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("문서 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}