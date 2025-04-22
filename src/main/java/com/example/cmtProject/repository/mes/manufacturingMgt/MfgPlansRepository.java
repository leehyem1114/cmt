package com.example.cmtProject.repository.mes.manufacturingMgt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.mes.manufacturingMgt.MfgPlan;

@Repository
public interface MfgPlansRepository extends JpaRepository<MfgPlan, Long> {

	@Query(value = "SELECT COUNT(MP_CREATED_AT) FROM MFG_PLANS WHERE TRUNC(MP_CREATED_AT) = TO_DATE(:data, 'YYYY-MM-DD')", nativeQuery = true)
	int getNextMpCode(@Param("data") String data);

}
