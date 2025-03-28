package com.example.cmtProject.service.erp.salaries;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.salaries.SalaryItemDTO;
import com.example.cmtProject.entity.erp.salaries.PayMent;
import com.example.cmtProject.entity.erp.salaries.SalaryItem;
import com.example.cmtProject.repository.erp.salaries.SalaryItemRepository;

@Service
public class SalaryItemService {

	@Autowired
	private SalaryItemRepository salaryItemRepository;

    // 급여 유형 목록 조회
    public List<SalaryItemDTO> getSalaryItems() {
    	
        return salaryItemRepository.findAll().stream()
                         .map(SalaryItem::toDto)
                         .collect(Collectors.toList());
    }

    

//	public String getFirstItemNameByType(SalaryItemType enumType) {
//	    return salaryItemRepository.findFirstBySalItemType(enumType)
//                .map(SalaryItem::getSalItemName)
//                .orElse("");
//	}

	// 급여 유형 추가
	public void registerSalaryItem(SalaryItemDTO salaryItemDTO) {
//		SalaryItem salaryItem = SalaryItem.builder()
//                .salItemType(salaryItemDTO.getSalItemType())  // Enum 타입 변환
//                .salItemName(salaryItemDTO.getSalItemName())
//                .salItemDesc(salaryItemDTO.getSalItemDesc())
//                .salItemCalc(salaryItemDTO.getSalItemCalc())
//                .salItemImportance(salaryItemDTO.getSalItemImportance())
//                .salItemApplyYear(salaryItemDTO.getSalItemApplyYear())
//                .salItemUpdate(LocalDate.now()) // 최종 수정일 현재 날짜로 설정
//                .build();

		salaryItemRepository.save(salaryItemDTO.toEntity());
		
	}



	public List<PayMent> getAllSalaries() {
		// TODO Auto-generated method stub
		return null;
	}


	@Transactional
	public void deleteSalItem(Long sliNo) throws Exception {
		// Item 1개 조회
		Optional<SalaryItem> optionalsalItem = salaryItemRepository.findById(sliNo);
		
		if(optionalsalItem.isPresent()) { // 조회 결과가 존재할 경우
			// 삭제 요청
			salaryItemRepository.delete(optionalsalItem.get()); // delete()가 더 안전 : 엔티티에서 조회하므로, deleteById는 조회할 대상없으면 exception 발생
		} else {
			throw new Exception("해당 상품이 존재하지 않습니다!"); // true false return 으로 해도 됨
		}
		
	}

//	@Transactional
//	public void modifySalaryItem(SalaryItemDTO salaryItemDTO) {
//		SalaryItem salaryItem = salaryItemRepository.findById(salaryItemDTO.getSalItemNo()).get();
//		
//		salaryItem.changeSalaryItem(salaryItemDTO);
//	}
}