//package com.example.cmtProject.controller.erp.eapproval;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
//import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
//import com.example.cmtProject.service.erp.eapproval.DocumentService;
//import com.example.cmtProject.service.erp.eapproval.DocFormService;
//import com.example.cmtProject.util.SecurityUtil;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import java.security.Principal;
//import java.util.List;
//
///**
// * 전자결재 화면 컨트롤러
// * 사용자에게 보여질 화면을 처리하는 컨트롤러
// */
//@Controller
//@RequestMapping("/eapproval")
//@RequiredArgsConstructor
//@Slf4j
//public class EapprovalController {
//
//    private final DocumentService documentService;
//    private final DocFormService docFormService;
//
//    /**
//     * 전자결재 메인 페이지 - 문서함으로 리다이렉트
//     */
//    @GetMapping
//    public String index() {
//        return "redirect:/eapproval/approvalList";
//    }
//
//    /**
//     * 전자결재 문서함 페이지
//     */
//    @GetMapping("/approvalList")
//    public String approvalList(Model model, Principal principal) {
//        String currentUserId = principal.getName();
//        log.info("전자결재 문서함 접속: {}", currentUserId);
//        
//        try {
//            // 기안 문서 목록
//            List<DocumentDTO> draftDocuments = documentService.getDrafterDocumentsByEmpId(currentUserId);
//            // 대기 문서 목록
//            List<DocumentDTO> pendingDocuments = documentService.getPendingDocumentsByEmpId(currentUserId);
//            // 완료 문서 목록
//            List<DocumentDTO> completedDocuments = documentService.getDocumentsByStatus("완료");
//            // 반려 문서 목록
//            List<DocumentDTO> rejectedDocuments = documentService.getDocumentsByStatus("반려");
//            
//            model.addAttribute("myDrafts", draftDocuments);
//            model.addAttribute("pendingDocs", pendingDocuments);
//            model.addAttribute("completedDocs", completedDocuments);
//            model.addAttribute("rejectedDocs", rejectedDocuments);
//            
//            return "erp/eapproval/approvalList";
//        } catch (Exception e) {
//            log.error("문서함 조회 중 오류 발생", e);
//            model.addAttribute("errorMessage", "문서 목록을 불러오는 중 오류가 발생했습니다.");
//            return "erp/eapproval/approvalList";
//        }
//    }
//
//    /**
//     * 문서 작성 폼 페이지
//     */
//    @GetMapping("/document/new")
//    public String newDocumentForm(Model model) {
//        log.info("새 문서 작성 페이지 접속");
//        
//        try {
//            // 문서 양식 목록 조회
//            List<DocFormDTO> forms = docFormService.getAllDocForms();
//            
//            model.addAttribute("documentDTO", new DocumentDTO());
//            model.addAttribute("forms", forms);
//            
//            return "erp/eapproval/documentForm";
//        } catch (Exception e) {
//            log.error("문서 작성 폼 로딩 중 오류 발생", e);
//            return "redirect:/eapproval/approvalList";
//        }
//    }
//
//    /**
//     * 임시저장 문서 수정 폼 페이지
//     */
//    @GetMapping("/document/edit/{docId}")
//    public String editDocumentForm(@PathVariable String docId, Model model, Principal principal) {
//        log.info("임시저장 문서 수정: {}", docId);
//        
//        try {
//            DocumentDTO document = documentService.getDocumentDetail(docId);
//            
//            // 문서 접근 권한 확인 (기안자만 수정 가능)
//            if (!document.getDrafterId().equals(principal.getName())) {
//                log.warn("문서 수정 권한 없음: {} (요청자: {})", docId, principal.getName());
//                return "redirect:/eapproval/approvalList";
//            }
//            
//            // 임시저장 문서만 수정 가능
//            if (!"Y".equals(document.getIsTempSaved())) {
//                log.warn("임시저장 문서가 아닙니다: {}", docId);
//                return "redirect:/eapproval/document/view/" + docId;
//            }
//            
//            // 문서 양식 목록 조회
//            List<DocFormDTO> forms = docFormService.getAllDocForms();
//            
//            model.addAttribute("documentDTO", document);
//            model.addAttribute("forms", forms);
//            
//            return "erp/eapproval/documentForm";
//        } catch (Exception e) {
//            log.error("임시저장 문서 수정 폼 로딩 중 오류 발생", e);
//            return "redirect:/eapproval/approvalList";
//        }
//    }
//
//    /**
//     * 문서 저장 처리 (임시저장 또는 결재요청)
//     * REST API를 통해 처리하므로 여기서는 폼 제출 후 리다이렉트만 처리
//     */
//    @PostMapping("/document/save")
//    public String saveDocument(@RequestParam boolean isTempSave, RedirectAttributes redirectAttributes) {
//        String message = isTempSave ? "문서가 임시저장되었습니다." : "결재요청이 완료되었습니다.";
//        redirectAttributes.addFlashAttribute("successMessage", message);
//        
//        return "redirect:/eapproval/approvalList";
//    }
//
//    /**
//     * 문서 조회 페이지
//     */
//    @GetMapping("/document/view/{docId}")
//    public String viewDocument(@PathVariable String docId, Model model, Principal principal) {
//        log.info("문서 상세 조회: {}", docId);
//        
//        try {
//            DocumentDTO document = documentService.getDocumentDetail(docId);
//            String currentUserId = principal.getName();
//            
//            // 현재 사용자가 결재 대기 중인 결재자인지 확인
//            boolean isCurrentApprover = document.getApprovalLines().stream()
//                .anyMatch(line -> line.getApproverNo().toString().equals(currentUserId) && "대기".equals(line.getApprovalStatus()));
//            
//            model.addAttribute("document", document);
//            model.addAttribute("isCurrentApprover", isCurrentApprover);
//            
//            return "erp/eapproval/document-view";
//        } catch (Exception e) {
//            log.error("문서 조회 중 오류 발생", e);
//            return "redirect:/eapproval/approvalList";
//        }
//    }
//
//    /**
//     * 결재 처리 (승인/반려)
//     * REST API를 통해 처리하므로 여기서는 폼 제출 후 리다이렉트만 처리
//     */
//    @PostMapping("/document/{docId}/approve")
//    public String approveDocument(@PathVariable String docId, RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("successMessage", "결재 처리가 완료되었습니다.");
//        return "redirect:/eapproval/pending";
//    }
//
//    /**
//     * 결재 대기 문서함
//     */
//    @GetMapping("/pending")
//    public String pending(Model model, Principal principal,
//                         @RequestParam(required = false) String approvalType,
//                         @RequestParam(required = false) String keyword) {
//        log.info("결재 대기 문서함 접속: {}", principal.getName());
//        
//        try {
//            List<DocumentDTO> documents = documentService.getPendingDocumentsByEmpId(principal.getName());
//            
//            model.addAttribute("documents", documents);
//            model.addAttribute("approvalType", approvalType);
//            model.addAttribute("keyword", keyword);
//            
//            return "erp/eapproval/pending-list";
//        } catch (Exception e) {
//            log.error("결재 대기 문서 조회 중 오류 발생", e);
//            return "redirect:/eapproval/approvalList";
//        }
//    }
//
//    /**
//     * 완료 문서함
//     */
//    @GetMapping("/completed")
//    public String completed(Model model, @RequestParam(required = false) String keyword) {
//        log.info("완료 문서함 접속");
//        
//        try {
//            List<DocumentDTO> documents = documentService.getDocumentsByStatus("완료");
//            
//            model.addAttribute("documents", documents);
//            model.addAttribute("keyword", keyword);
//            
//            return "erp/eapproval/completed-list";
//        } catch (Exception e) {
//            log.error("완료 문서 조회 중 오류 발생", e);
//            return "redirect:/eapproval/approvalList";
//        }
//    }
//
//    /**
//     * 반려 문서함
//     */
//    @GetMapping("/rejected")
//    public String rejected(Model model, @RequestParam(required = false) String keyword) {
//        log.info("반려 문서함 접속");
//        
//        try {
//            List<DocumentDTO> documents = documentService.getDocumentsByStatus("반려");
//            
//            model.addAttribute("documents", documents);
//            model.addAttribute("keyword", keyword);
//            
//            return "erp/eapproval/rejected-list";
//        } catch (Exception e) {
//            log.error("반려 문서 조회 중 오류 발생", e);
//            return "redirect:/eapproval/approvalList";
//        }
//    }
//}