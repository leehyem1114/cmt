package com.example.cmtProject.controller.erp.eapproval;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.service.erp.eapproval.DocumentService;
import com.example.cmtProject.service.erp.eapproval.DocFormService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

/**
 * 전자결재 화면 컨트롤러
 * 사용자에게 보여질 화면을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/eapproval")
@RequiredArgsConstructor
public class eapprovalController {

    private final DocumentService documentService;
    private final DocFormService docFormService;

    /**
     * 전자결재 메인 페이지
     */
    @GetMapping("/approvalList")
    public String approvalList(Model model, Principal principal) {
        Integer currentUserId = getCurrentUserId(principal);
        
        // 기안 문서 목록
        List<DocumentDTO> draftDocuments = documentService.getDrafterDocuments(currentUserId);
        // 대기 문서 목록
        List<DocumentDTO> pendingDocuments = documentService.getPendingDocuments(currentUserId);
        
        model.addAttribute("draftDocuments", draftDocuments);
        model.addAttribute("pendingDocuments", pendingDocuments);
        
        return "erp/eapproval/approvalList";
    }

    /**
     * 문서 작성 폼 페이지
     */
    @GetMapping("/document/new")
    public String newDocumentForm(Model model) {
        // 문서 양식 목록 조회
        List<DocFormDTO> forms = docFormService.getAllDocForms();
        
        model.addAttribute("documentDTO", new DocumentDTO());
        model.addAttribute("forms", forms);
        
        return "erp/eapproval/document-form";
    }

    /**
     * 임시저장 문서 수정 폼 페이지
     */
    @GetMapping("/document/edit/{docId}")
    public String editDocumentForm(@PathVariable String docId, Model model) {
        DocumentDTO document = documentService.getDocumentDetail(docId);
        
        // 문서 양식 목록 조회
        List<DocFormDTO> forms = docFormService.getAllDocForms();
        
        model.addAttribute("documentDTO", document);
        model.addAttribute("forms", forms);
        
        return "erp/eapproval/document-form";
    }

    /**
     * 문서 저장 (임시저장 또는 결재요청)
     */
    @PostMapping("/document/save")
    public String saveDocument(DocumentDTO documentDTO, 
                             @RequestParam boolean isTempSave,
                             Principal principal) {
        
        // 현재 로그인한 사용자 정보 설정
        Integer currentUserId = getCurrentUserId(principal);
        documentDTO.setDrafterId(currentUserId);
        
        // 부서 정보 설정 (실제로는 사용자 정보에서 가져와야 함)
        documentDTO.setDraftDept("부서코드");
        
        DocumentDTO savedDocument = documentService.saveDocument(documentDTO, isTempSave);
        
        if (isTempSave) {
            return "redirect:/eapproval/approvalList";
        } else {
            return "redirect:/eapproval/document/view/" + savedDocument.getDocId();
        }
    }

    /**
     * 문서 조회 페이지
     */
    @GetMapping("/document/view/{docId}")
    public String viewDocument(@PathVariable String docId, Model model, Principal principal) {
        DocumentDTO document = documentService.getDocumentDetail(docId);
        Integer currentUserId = getCurrentUserId(principal);
        
        // 현재 사용자가 결재 대기 중인 결재자인지 확인
        boolean isCurrentApprover = document.getApprovalLines().stream()
            .anyMatch(line -> line.getApproverNo().equals(currentUserId) && "대기".equals(line.getApprovalStatus()));
        
        model.addAttribute("document", document);
        model.addAttribute("isCurrentApprover", isCurrentApprover);
        
        return "erp/eapproval/document-view";
    }

    /**
     * 결재 처리 (승인/반려)
     */
    @PostMapping("/document/{docId}/approve")
    public String approveDocument(@PathVariable String docId,
                                @RequestParam String decision,
                                @RequestParam(required = false) String comment,
                                Principal principal) {
        
        Integer currentUserId = getCurrentUserId(principal);
        documentService.processApproval(docId, currentUserId, decision, comment);
        
        return "redirect:/eapproval/pending";
    }

    /**
     * 결재 대기 문서함
     */
    @GetMapping("/pending")
    public String pending(Model model, 
                         Principal principal,
                         @RequestParam(required = false) String approvalType,
                         @RequestParam(required = false) String keyword) {
        Integer currentUserId = getCurrentUserId(principal);
        List<DocumentDTO> documents = documentService.getPendingDocuments(currentUserId);
        
        model.addAttribute("documents", documents);
        model.addAttribute("approvalType", approvalType);
        model.addAttribute("keyword", keyword);
        
        return "erp/eapproval/pending-list";
    }

    /**
     * 완료 문서함
     */
    @GetMapping("/completed")
    public String completed(Model model, @RequestParam(required = false) String keyword) {
        List<DocumentDTO> documents = documentService.getDocumentsByStatus("완료");
        
        model.addAttribute("documents", documents);
        model.addAttribute("keyword", keyword);
        
        return "erp/eapproval/completed-list";
    }

    /**
     * 반려 문서함
     */
    @GetMapping("/rejected")
    public String rejected(Model model, @RequestParam(required = false) String keyword) {
        List<DocumentDTO> documents = documentService.getDocumentsByStatus("반려");
        
        model.addAttribute("documents", documents);
        model.addAttribute("keyword", keyword);
        
        return "erp/eapproval/rejected-list";
    }
    
    /**
     * 현재 사용자 ID 가져오기 (임시 메소드)
     */
    private Integer getCurrentUserId(Principal principal) {
        // 실제 구현에서는 Principal에서 사용자 정보를 가져와 ID를 반환
        // 임시로 1 반환
        return 1;
    }
}