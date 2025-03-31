package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.mapper.erp.eapproval.DocFormMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocFormService {

    private final DocFormMapper docFormMapper;
    
    /**
     * 전체 양식 목록 조회
     */
    public List<DocFormDTO> getAllDocForms() {
        return docFormMapper.selectAllDocForms();
    }
    
    /**
     * 특정 양식 조회
     */
    public DocFormDTO getDocFormById(String formId) {
        return docFormMapper.selectDocFormById(formId);
    }
    
    /**
     * 양식 저장
     */
    @Transactional
    public DocFormDTO saveDocForm(DocFormDTO docFormDTO) {
        if (docFormMapper.selectDocFormById(docFormDTO.getFormId()) == null) {
            docFormMapper.insertDocForm(docFormDTO);
        } else {
            docFormMapper.updateDocForm(docFormDTO);
        }
        return docFormMapper.selectDocFormById(docFormDTO.getFormId());
    }
    
    /**
     * 양식 삭제
     */
    @Transactional
    public boolean deleteDocForm(String formId) {
        docFormMapper.deleteDocForm(formId);
        return docFormMapper.selectDocFormById(formId) == null;
    }
    
    
} //DocFormService