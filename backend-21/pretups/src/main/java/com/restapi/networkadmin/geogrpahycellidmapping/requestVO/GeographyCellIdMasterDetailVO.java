package com.restapi.networkadmin.geogrpahycellidmapping.requestVO;//package com.restapi.networkadmin.geogrpahycellidmapping.requestVO;

import com.btsl.pretups.master.businesslogic.GeographicalDomainCellsVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class GeographyCellIdMasterDetailVO {
    private ArrayList<GeographicalDomainCellsVO> geogCodeDetailsList = null;
    private ArrayList<GeographicalDomainCellsVO> geogCellIdList = null;


}
