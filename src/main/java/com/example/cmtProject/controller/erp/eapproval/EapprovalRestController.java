package com.example.cmtProject.controller.erp.eapproval;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.comm.response.ResponseCode;
import com.example.cmtProject.constants.ApprovalStatus;
import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.service.erp.eapproval.DocumentService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.eapproval.DocFormService;
import com.example.cmtProject.service.erp.eapproval.ApprovalProcessService;
import com.example.cmtProject.comm.exception.DocumentAccessDeniedException;
import com.example.cmtProject.comm.exception.DocumentNotFoundException;

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
@RequestMapping(PathConstants.API_BASE)
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
    @GetMapping(PathConstants.API_FORM + "/{formId}")
    public ApiResponse<DocFormDTO> getFormContent(@PathVariable(name = "formId") String formId) {
        log.debug("문서 양식 조회 요청: {}", formId);
        try {
            DocFormDTO form = docFormService.getDocFormById(formId);
            if (form == null) {
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
    @GetMapping(PathConstants.API_DOCUMENT + "/{docId}")
    public ApiResponse<DocumentDTO> getDocumentDetail(@PathVariable String docId) {
        log.debug("문서 상세 조회 요청: {}", docId);
        try {
            DocumentDTO document = documentService.getDocumentDetail(docId);
            return ApiResponse.success(document);
        } catch (DocumentNotFoundException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (Exception e) {
            log.error("문서 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 저장 API (임시저장/결재요청)
     * FormData를 통한 파일 업로드 지원
     */
    @PostMapping(PathConstants.API_DOCUMENT)
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
            
            // 사용자 및 부서 정보 설정
            Integer drafterId = documentService.getEmployeeNoByEmpId(currentUserId);
            String draftDeptCode = getDraftDeptCode(drafterId);
            documentDTO.setDraftDept(draftDeptCode);
            
            // 결재선 정보 설정
            List<ApprovalLineDTO> approvalLines = parseApprovalLines(approvalLinesJson);
            documentDTO.setApprovalLines(approvalLines);
            
            // 문서 저장
            DocumentDTO savedDocument = documentService.saveDocument(documentDTO, isTempSave);
            
            // 첨부파일 처리 (향후 구현)
            if (files != null && !files.isEmpty()) {
                log.debug("첨부파일 {}개 있음", files.size());
                // TODO: 파일 업로드 처리 로직 구현
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
     */
    private String getDraftDeptCode(Integer drafterId) {
        // TODO: EmployeesService 구현 후에는 해당 서비스에서 부서 코드 조회
        return "DEPT001";
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
    @PostMapping(PathConstants.API_DOCUMENT + "/{docId}/process")
    public ApiResponse<Map<String, Object>> processApproval(
            @PathVariable String docId,
            @RequestParam Integer approverId,
            @RequestParam String decision,
            @RequestParam(required = false) String comment) {
        
        log.debug("결재 처리 요청: 문서={}, 결재자={}, 의사결정={}", docId, approverId, decision);
        try {
            if (ApprovalStatus.REJECTED.equals(decision)) {
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
    @GetMapping(PathConstants.API_DOCUMENTS + "/status/{status}")
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
    @GetMapping(PathConstants.API_APPROVERS)
    public ApiResponse<List<EmpListPreviewDTO>> getApprovers() {
        log.debug("결재자 목록 조회 요청");
        try {
            List<EmpListPreviewDTO> employees = empService.getEmpList();
            
            if (employees == null) {
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
    @DeleteMapping(PathConstants.API_DOCUMENT + "/{docId}")
    public ApiResponse<Boolean> deleteDocument(@PathVariable String docId) {
        log.debug("문서 삭제 요청: {}", docId);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();
            
            boolean result = documentService.deleteDocument(docId, currentUserId);
            return ApiResponse.success("문서가 삭제되었습니다.", true);
        } catch (DocumentAccessDeniedException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.FORBIDDEN);
        } catch (DocumentNotFoundException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (Exception e) {
            log.error("문서 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}