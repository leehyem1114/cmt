package com.example.cmtProject.controller.erp.eapproval;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.comm.response.ResponseCode;
import com.example.cmtProject.constants.ApprovalStatus;
import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.dto.erp.eapproval.AttachmentDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalRequestDTO;
import com.example.cmtProject.service.erp.eapproval.DocumentService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.eapproval.DocFormService;
import com.example.cmtProject.service.erp.eapproval.ApprovalProcessService;
import com.example.cmtProject.service.erp.eapproval.AttachmentService;
import com.example.cmtProject.comm.exception.DocumentAccessDeniedException;
import com.example.cmtProject.comm.exception.DocumentNotFoundException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private final DocumentMapper documentMapper; 
    private final DocumentService documentService;
    private final DocFormService docFormService;
    private final ApprovalProcessService approvalProcessService;
    private final AttachmentService attachmentService;
    private final ObjectMapper objectMapper;
    private final EmployeesService empService;
    
    @Value("${file.upload.dir:${user.home}/uploads}")
    private String uploadDir;
    
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
    	
        log.debug("문서 저장 요청 시작: 임시저장={}, 제목={}", isTempSave, title);
        log.debug("문서 ID: {}, 문서번호: {}, 양식ID: {}", docId, docNumber, formId);
        log.debug("내용 길이: {}", content != null ? content.length() : 0);
        log.debug("결재선 데이터: {}", approvalLinesJson);
        log.debug("첨부파일: {}", files != null ? files.size() : 0);
        
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
            String draftDeptCode = getDraftDeptCode(currentUserId);
            documentDTO.setDraftDept(draftDeptCode);
            
            // 결재선 정보 설정
            List<ApprovalLineDTO> approvalLines = parseApprovalLines(approvalLinesJson);
            documentDTO.setApprovalLines(approvalLines);
            
            // 문서 저장
            DocumentDTO savedDocument = documentService.saveDocument(documentDTO, isTempSave);
            
            // 첨부파일 처리
            if (files != null && !files.isEmpty()) {
                log.debug("첨부파일 {}개 업로드", files.size());
                processAttachments(docId, files);
            }
            
            String message = isTempSave ? "문서가 임시저장되었습니다." : "결재요청이 완료되었습니다.";
            return ApiResponse.success(message, savedDocument);
        } catch (Exception e) {
            log.error("문서 저장 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 첨부파일 처리 메서드
     * @param docId 문서 ID
     * @param files 업로드된 파일 목록
     * @throws IOException 파일 처리 오류
     */
    private void processAttachments(String docId, List<MultipartFile> files) throws IOException {
        // 디렉토리 확인 및 생성
        File uploadPath = new File(uploadDir + File.separator + docId);
        if (!uploadPath.exists()) {
            if (!uploadPath.mkdirs()) {
                throw new IOException("파일 업로드 디렉토리를 생성할 수 없습니다: " + uploadPath);
            }
        }
        
        // 각 파일 처리
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            
            // 파일 저장
            String originalFilename = file.getOriginalFilename();
            String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            File savedFile = new File(uploadPath, savedFilename);
            
            file.transferTo(savedFile);
            
            // DB에 첨부파일 정보 저장
            attachmentService.saveAttachment(docId, originalFilename, savedFilename, 
                    uploadPath.getPath(), file.getSize(), file.getContentType());
        }
    }
    
    /**
     * 기안자 부서 코드 조회
     * @param empId 직원 ID(사번)
     * @return 부서 코드
     */
    private String getDraftDeptCode(String empId) {
        log.debug("부서 코드 조회: 직원ID={}", empId);
        
        try {
            String deptCode = documentMapper.selectEmployeeDeptCodeByEmpId(empId);
            
            if (deptCode != null && !deptCode.isEmpty()) {
                return deptCode;
            }
            
            // 조회 결과가 없으면 기본값 반환
            log.warn("직원({})의 부서 정보를 찾을 수 없습니다. 기본값 사용", empId);
            return "DEPT001";
        } catch (Exception e) {
            log.error("부서 코드 조회 중 오류 발생: {}", e.getMessage(), e);
            return "DEPT001"; // 오류 시 기본값 반환
        }
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
     * 결재 처리 API (승인/반려) - JSON 방식
     */
    @PostMapping(PathConstants.API_DOCUMENT + "/{docId}/process")
    public ApiResponse<Map<String, Object>> processApproval(
            @PathVariable String docId,
            @RequestBody ApprovalRequestDTO requestData) {
        
        log.debug("결재 처리 요청: 문서={}, 결재자={}, 의사결정={}", 
                docId, requestData.getApproverId(), requestData.getDecision());
        log.debug("결재 의견: {}", requestData.getComment());
        
        try {
            if (ApprovalStatus.REJECTED.equals(requestData.getDecision())) {
                approvalProcessService.reject(docId, requestData.getApproverId(), requestData.getComment());
            } else {
                approvalProcessService.approve(docId, requestData.getApproverId(), requestData.getComment());
            }
            
            String message = ApprovalStatus.APPROVED.equals(requestData.getDecision()) ? 
                           "결재가 승인되었습니다." : "결재가 반려되었습니다.";
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("decision", requestData.getDecision());
            
            return ApiResponse.success(message, result);
        } catch (RuntimeException e) {
            log.warn("결재 처리 중 비즈니스 오류: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            log.error("결재 처리 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("결재 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
//    @PostMapping(PathConstants.API_DOCUMENT + "/{docId}/process")
//    public ApiResponse<Map<String, Object>> processApproval(
//            @PathVariable("docId") String docId,
//            @RequestParam("approverId") String approverId,
//            @RequestParam("decision") String decision,
//            @RequestParam(value = "comment", required = false) String comment) {
//        
//        log.debug("결재 처리 요청: 문서={}, 결재자={}, 의사결정={}", docId, approverId, decision);
//        log.debug("결재 의견: {}", comment);
//        
//        try {
//            if (ApprovalStatus.REJECTED.equals(decision)) {
//                approvalProcessService.reject(docId, approverId, comment);
//            } else {
//                approvalProcessService.approve(docId, approverId, comment);
//            }
//            
//            String message = ApprovalStatus.APPROVED.equals(decision) ? 
//                           "결재가 승인되었습니다." : "결재가 반려되었습니다.";
//            
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", true);
//            result.put("message", message);
//            result.put("decision", decision);
//            
//            return ApiResponse.success(message, result);
//        } catch (RuntimeException e) {
//            log.warn("결재 처리 중 비즈니스 오류: {}", e.getMessage());
//            return ApiResponse.error(e.getMessage(), ResponseCode.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("결재 처리 중 오류 발생: {}", e.getMessage(), e);
//            return ApiResponse.error("결재 처리 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
    
    /**
     * 결재 처리 API (승인/반려) - Multipart 방식 (첨부파일 지원)
     */
    @PostMapping(PathConstants.API_DOCUMENT + "/{docId}/process-with-file")
    public ApiResponse<Map<String, Object>> processApprovalWithFile(
            @PathVariable String docId,
            @RequestParam("approverId") String approverId,
            @RequestParam("decision") String decision,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        log.debug("첨부파일 포함 결재 처리 요청: 문서={}, 결재자={}, 의사결정={}", docId, approverId, decision);
        log.debug("첨부파일 수: {}", files != null ? files.size() : 0);
        
        try {
            // 결재 처리 로직
            if (ApprovalStatus.REJECTED.equals(decision)) {
                approvalProcessService.reject(docId, approverId, comment);
            } else {
                approvalProcessService.approve(docId, approverId, comment);
            }
            
            // 첨부파일 처리 (필요한 경우)
            if (files != null && !files.isEmpty()) {
                processAttachments(docId, files);
            }
            
            String message = ApprovalStatus.APPROVED.equals(decision) ? 
                           "결재가 승인되었습니다." : "결재가 반려되었습니다.";
            
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
            List<EmpListPreviewDTO> employees = empService.getEmplist();
            
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
    
    /**
     * 첨부파일 다운로드 API
     */
    @GetMapping("/download/{fileNo}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long fileNo) {
        log.debug("첨부파일 다운로드 요청: {}", fileNo);
        try {
            // 첨부파일 정보 조회
            AttachmentDTO attachment = attachmentService.getAttachmentByFileNo(fileNo);
            if (attachment == null) {
                throw new NotFoundException("첨부파일을 찾을 수 없습니다");
            }
            
            // 파일 경로 구성
            Path filePath = Paths.get(attachment.getFilePath(), attachment.getSavedName());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                throw new NotFoundException("파일을 찾을 수 없습니다: " + filePath);
            }
            
            // 다운로드 응답 구성
            String contentType = attachment.getFileType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // 파일명 인코딩
            String encodedFilename = URLEncoder.encode(attachment.getOriginalName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
            
        } catch (NotFoundException e) {
            log.warn("첨부파일 다운로드 오류: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("첨부파일 다운로드 중 오류 발생: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * NotFoundException 클래스 정의
     */
    private static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
        
        public NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}