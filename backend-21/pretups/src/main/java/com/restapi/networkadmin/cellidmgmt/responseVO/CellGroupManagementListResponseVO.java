package com.restapi.networkadmin.cellidmgmt.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.common.SchemaConstants;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CellGroupManagementListResponseVO extends BaseResponse {
    @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(description = "Cell id List", additionalProperties = Schema.AdditionalPropertiesValue.FALSE))
    private ArrayList<CellGroupVO> cellgroupList;
}
