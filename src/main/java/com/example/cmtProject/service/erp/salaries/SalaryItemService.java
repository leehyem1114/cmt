package com.example.cmtProject.service.erp.salaries;

import org.springframework.stereotype.Service;

import com.example.cmtProject.entity.SalaryItem;
import com.example.cmtProject.repository.erp.salaries.SalaryItemRepository;

@Service
public class SalaryItemService {

    private final SalaryItemRepository repository;

    public SalaryItemService(SalaryItemRepository repository) {
        this.repository = repository;
    }

    public String getItemNameByType(String type) {
        return repository.findFirstBySalItemType(type)
                         .map(SalaryItem::getSalItemName)
                         .orElse("");
    }
}
