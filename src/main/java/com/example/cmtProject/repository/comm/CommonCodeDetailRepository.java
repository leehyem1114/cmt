package com.example.cmtProject.repository.comm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.entity.comm.CommoncodeDetail;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommoncodeDetail, String> {

	CommonCodeDetailDTO findByCmnDetailCodeAndCmnCode(String group, String code);
	

}
