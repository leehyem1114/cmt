package com.example.cmtProject.controller.mes.warehouse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.warehouse.WarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
/**
 *  창고 화면 컨트롤러
 *  사용자에게 보여질 화면을 처리하는 컨트롤러
 * 
 */
@Controller
@RequestMapping(PathConstants.WAREHOUSE_BASE)
@Slf4j
public class WarehouseController {
    
    @Autowired
    private WarehouseService whs;
    
    /**
     * 
     * 창고 관리 메인 페이지 
     * 초기 정보를 모델에 담아 뷰로 전달 
     * 
     */
    @GetMapping(PathConstants.WAREHOUSE_VIEW)
    public String warehouseListGET(Model model) {
        
        Map<String,Object> findMap = new HashMap<>();
        List<Map<String, Object>> wareHouseList = whs.warehouseList(findMap);
        model.addAttribute("wareHouseList",wareHouseList);
        
//        // 타임스탬프 객체 처리를 위해 직렬화 가능한 형태로 변환
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//            String jsonString = mapper.writeValueAsString(wareHouseList);
//            List<Map<String, Object>> sanitizedList = mapper.readValue(jsonString, 
//                    mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
//            model.addAttribute("wareHouseList", sanitizedList);
//        } catch (Exception e) {
//            log.error("데이터 변환 중 오류 발생: ", e);
//            model.addAttribute("wareHouseList", new HashMap<>());
//        }
//        
        return PathConstants.VIEW_WAREHOUSE_VIEW;
    }
    
    /**
     * 창고 목록 조회 API
     * 검색어를 기반으로 창고 목록을 조회합니다.
     * 
     * @param keyword 검색어 (선택)
     * @return 창고 목록
     */
    @GetMapping("/api/warehouses/list")
    @ResponseBody
    public ApiResponse<List<Map<String, Object>>> getWarehouses(
            @RequestParam(name = "keyword", required = false) String keyword) {
        log.info("창고 목록 조회 API 요청. 검색어: {}", keyword);
        
        Map<String, Object> findMap = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        List<Map<String, Object>> warehouses = whs.warehouseList(findMap);
        
        // 직렬화 문제 해결을 위해 변환
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String jsonString = mapper.writeValueAsString(warehouses);
            List<Map<String, Object>> sanitizedList = mapper.readValue(jsonString, 
                    mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            return ApiResponse.success(sanitizedList);
        } catch (Exception e) {
            log.error("데이터 변환 중 오류 발생: ", e);
            return ApiResponse.error("데이터 변환 중 오류가 발생했습니다.", e.getMessage());
        }
    }
}