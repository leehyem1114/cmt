package com.example.cmtProject.dto.mes.production;

import java.time.LocalDate;
import java.util.List;

import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotBomPathBindingDTO {
	
	private List<BomInfoDTO> bomList;
	private List<LotStructurePathDTO> pathList;

}
