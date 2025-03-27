package com.example.cmtProject.repository.erp.saleMgt;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cmtProject.entity.comm.CommoncodeDetail;

public interface CommoncodeDetailRepository extends JpaRepository<CommoncodeDetail, String> {

	//공통코드에서 부서명, 직급명 가져오기
	List<CommoncodeDetail> findByCmnCode(String cmnCode);

}
