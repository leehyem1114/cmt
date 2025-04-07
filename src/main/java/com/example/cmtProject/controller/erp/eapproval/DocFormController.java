package com.example.cmtProject.controller.erp.eapproval;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.service.erp.eapproval.DocFormService;
import com.example.cmtProject.comm.exception.DocFormNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.List;

/**
 * 문서 양식 관리 컨트롤러
 * 양식 관리 화면 처리
 */
@Controller
@RequestMapping(PathConstants.APPROVAL_BASE + PathConstants.FORM_BASE)
@RequiredArgsConstructor
@Slf4j
public class DocFormController {

    private final DocFormService docFormService;

    /**
     * 문서 양식 목록 페이지
     */
    @GetMapping
    public String formList(Model model) {
        log.info("문서 양식 목록 페이지 접속");
        
        try {
            // 문서 양식 목록 조회
            List<DocFormDTO> forms = docFormService.getAllDocForms();
            model.addAttribute("forms", forms);
            log.info("문서 양식 목록 페이지 접속" + forms);
            
            return PathConstants.VIEW_FORM_LIST;
        } catch (Exception e) {
            log.error("문서 양식 목록 조회 중 오류 발생", e);
            model.addAttribute("errorMessage", "문서 양식 목록을 불러오는 중 오류가 발생했습니다.");
            return PathConstants.VIEW_FORM_LIST;
        }
    }

    /**
     * 문서 양식 등록 페이지
     */
    @GetMapping(PathConstants.FORM_NEW)
    public String newForm(Model model) {
        log.info("문서 양식 등록 페이지 접속");
        
        model.addAttribute("docForm", new DocFormDTO());
        model.addAttribute("isNew", true);
        
        return PathConstants.VIEW_FORM_EDIT;
    }

    /**
     * 문서 양식 수정 페이지
     */
    @GetMapping(PathConstants.FORM_EDIT + "/{formId}")
    public String editForm(@PathVariable String formId, Model model, RedirectAttributes redirectAttributes) {
        log.info("문서 양식 수정 페이지 접속: {}", formId);
        
        try {
            DocFormDTO form = docFormService.getDocFormById(formId);
            model.addAttribute("docForm", form);
            model.addAttribute("isNew", false);
            
            return PathConstants.VIEW_FORM_EDIT;
        } catch (DocFormNotFoundException e) {
            log.warn("문서 양식을 찾을 수 없음: {}", formId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return PathConstants.REDIRECT_FORM_LIST;
        } catch (Exception e) {
            log.error("문서 양식 조회 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "문서 양식을 불러오는 중 오류가 발생했습니다.");
            return PathConstants.REDIRECT_FORM_LIST;
        }
    }

    /**
     * 문서 양식 상세 조회 페이지
     */
    @GetMapping(PathConstants.FORM_VIEW + "/{formId}")
    public String viewForm(@PathVariable String formId, Model model, RedirectAttributes redirectAttributes) {
        log.info("문서 양식 상세 조회 페이지 접속: {}", formId);
        
        try {
            DocFormDTO form = docFormService.getDocFormById(formId);
            model.addAttribute("docForm", form);
            
            return PathConstants.VIEW_FORM_VIEW;
        } catch (DocFormNotFoundException e) {
            log.warn("문서 양식을 찾을 수 없음: {}", formId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return PathConstants.REDIRECT_FORM_LIST;
        } catch (Exception e) {
            log.error("문서 양식 조회 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "문서 양식을 불러오는 중 오류가 발생했습니다.");
            return PathConstants.REDIRECT_FORM_LIST;
        }
    }
}