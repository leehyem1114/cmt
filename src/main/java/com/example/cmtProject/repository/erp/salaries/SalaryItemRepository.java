package com.example.cmtProject.repository.erp.salaries;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.SalaryItem;
import com.example.cmtProject.entity.SalaryItemType;

@Repository
public interface SalaryItemRepository extends JpaRepository<SalaryItem, Long> {


    // enum으로 찾을 때 (예: SalaryItemType.BONUS)
    Optional<SalaryItem> findFirstBySalItemType(SalaryItemType enumType);
}
