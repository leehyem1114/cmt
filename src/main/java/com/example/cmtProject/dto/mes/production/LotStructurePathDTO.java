package com.example.cmtProject.dto.mes.production;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotStructurePathDTO {

	private String bomLevel;
    private String childItemCode;
    private String parentPdtCode;
    private String itemType;
    private String bomPrcType;
    private String bomQty;
    private String bomUnit;
    private String path;
}
