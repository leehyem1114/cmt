package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.mapper.erp.eapproval.DocFormMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocFormService {

    private final DocFormMapper docFormMapper;
    
    /**
     * 전체 양식 목록 조회
     */
    public List<DocFormDTO> getAllDocForms() {
        log.debug("전체 양식 목록 조회");
        return docFormMapper.selectAllDocForms();
    }
    
    /**
     * 특정 양식 조회
     */
    public DocFormDTO getDocFormById(String formId) {
        log.debug("양식 조회: {}", formId);
        return docFormMapper.selectDocFormById(formId);
    }
    
    /**
     * 양식 저장
     */
    @Transactional
    public DocFormDTO saveDocForm(DocFormDTO docFormDTO) {
        log.info("양식 저장: {}", docFormDTO.getFormId());
        
        try {
            if (docFormMapper.selectDocFormById(docFormDTO.getFormId()) == null) {
                log.debug("신규 양식 저장");
                // 생성 시간, 생성자 ID 설정
                docFormDTO.setCreateDate(LocalDateTime.now());
                docFormMapper.insertDocForm(docFormDTO);
            } else {
                log.debug("기존 양식 수정");
                // 수정 시간, 수정자 ID 설정
                docFormDTO.setUpdateDate(LocalDateTime.now());
                docFormMapper.updateDocForm(docFormDTO);
            }
            
            return docFormMapper.selectDocFormById(docFormDTO.getFormId());
        } catch (Exception e) {
            log.error("양식 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("양식 저장 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 양식 삭제
     * 
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteDocForm(String formId) {
        log.info("양식 삭제: {}", formId);
        
        try {
            docFormMapper.deleteDocForm(formId);
            
            // 삭제 확인
            boolean isDeleted = docFormMapper.selectDocFormById(formId) == null;
            log.debug("양식 삭제 결과: {}", isDeleted ? "성공" : "실패");
            
            return isDeleted;
        } catch (Exception e) {
            log.error("양식 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("양식 삭제 중 오류가 발생했습니다.", e);
        }
    }
}