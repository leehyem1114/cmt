package com.example.cmtProject.repository.erp.saleMgt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.salesMgt.PurchasesOrder;

@Repository
public interface PurchasesOrderRepository extends JpaRepository<PurchasesOrder, Long> {

}
