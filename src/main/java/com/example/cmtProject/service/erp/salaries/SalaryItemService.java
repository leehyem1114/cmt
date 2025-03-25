package com.example.cmtProject.service.erp.salaries;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.erp.SalaryItemDTO;
import com.example.cmtProject.entity.Salary;
import com.example.cmtProject.entity.SalaryItem;
import com.example.cmtProject.entity.SalaryItemType;
import com.example.cmtProject.repository.erp.salaries.SalaryItemRepository;

@Service
public class SalaryItemService {

	@Autowired
	private SalaryItemRepository repository;

    // 서비스 메서드: 전체 급여 항목 조회
    public List<SalaryItemDTO> getAllSalaryItems() {
        return repository.findAll().stream()
                         .map(SalaryItem::toDto)
                         .collect(Collectors.toList());
    }

    

	public String getFirstItemNameByType(SalaryItemType enumType) {
	    return repository.findFirstBySalItemType(enumType)
                .map(SalaryItem::getSalItemName)
                .orElse("");
	}

	public void saveSalaryItem(SalaryItemDTO salaryItemDTO) {
		SalaryItem salaryItem = SalaryItem.builder()
                .salItemType(salaryItemDTO.getSalItemType())  // Enum 타입 변환
                .salItemName(salaryItemDTO.getSalItemName())
                .salItemDesc(salaryItemDTO.getSalItemDesc())
                .salItemCalc(salaryItemDTO.getSalItemCalc())
                .salItemImportance(salaryItemDTO.getSalItemImportance())
                .salItemApplyYear(salaryItemDTO.getSalItemApplyYear())
                .salItemUpdate(LocalDate.now()) // 최종 수정일 현재 날짜로 설정
                .build();

		repository.save(salaryItem);
		
	}



	public List<Salary> getAllSalaries() {
		// TODO Auto-generated method stub
		return null;
	}
}