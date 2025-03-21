package com.example.cmtProject.repository.erp.salaries;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.SalaryItem;

@Repository
public interface SalaryItemRepository extends JpaRepository<SalaryItem, Long> {

	// 예: 가장 첫 번째 항목 하나만 조회
    Optional<SalaryItem> findFirstBySalItemType(String salItemType);
}
