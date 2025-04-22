package com.example.cmtProject.service.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.production.LotDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.mapper.mes.production.WorkOrderMapper;

import jakarta.transaction.Transactional;

@Service
public class WorkOrderService {
	@Autowired WorkOrderMapper orderMapper;
	//작업지시 리스트
	public List<WorkOrderDTO> getOrderList() {
		return orderMapper.selectOrderList();
	}
	//제조계획
	public List<MfgScheduleDTO> getPlanList() {
		return orderMapper.selectPlanList();
	}
	@Transactional
	public void registMsPlan(WorkOrderDTO workOrderDTO) {
		orderMapper.insertMsPlan(workOrderDTO); // 작업지시 등록
		updateMfgStatus(workOrderDTO.getMsCode()); // 제조계획 상태 변경
	}
	//작업시작 버튼 = 날짜 업데이트&진행중
	public void startWork(Long workOrderNo) {
		orderMapper.updateWorkStartTime(workOrderNo);
		
	}

	@Transactional
	public void updateMfgStatus(String msCode) {
		orderMapper.updateMfgStatus(msCode);// 제조계획 상태 변경
		deleteMfgList(msCode); //제조계획 리스트에서 제거 
	}
	//작업지시 등록시 제거됨
	private void deleteMfgList(String msCode) {
		orderMapper.deleteMfgList(msCode);
	}
	
	//로트번호로 단일제품정보 들고오기
	public WorkOrderDTO getProductDetail(String lotNo) {
		return orderMapper.selectProductDetail(lotNo);
	}
	
	//lot + workOrder 테이블
	public List<LotDTO> getLotDetail() {
		return orderMapper.selectLotDetail();
	}
	//lotNo로 상품 상세정보 들고오기
	public LotDTO getLotNoDetail(Long lotNo) {
		return orderMapper.selectLotNoDetail(lotNo);
	}
	
	//=====================================================
	public List<LotDTO> getAllLotTree() {
		return orderMapper.selectAllLotTree();
	}
	
}
