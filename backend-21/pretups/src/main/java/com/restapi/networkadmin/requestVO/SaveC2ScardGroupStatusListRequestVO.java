package com.restapi.networkadmin.requestVO;

import java.util.List;

import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SaveC2ScardGroupStatusListRequestVO {
	List<SaveCardGroupStatusRequestVO> cardGroupStatusList;

}
