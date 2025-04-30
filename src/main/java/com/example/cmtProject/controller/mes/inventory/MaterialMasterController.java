package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.inventory.MaterialMasterService;

import lombok.extern.slf4j.Slf4j;

@Controller
//@RequestMapping()
@Slf4j
public class MaterialMasterController {
    
    @Autowired
    private MaterialMasterService mms;
    
    /**
     * 원자재 기준정보 메인페이지
     */
   // @GetMapping()
    public String materialInventoryGet(Model model) {
        Map<String,Object> findMap = new HashMap<>();
        List<Map<String,Object>> materialList = mms.materialList(findMap);
        model.addAttribute("materialList", materialList);
        
        return "";
    }
}