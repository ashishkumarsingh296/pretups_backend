package com.restapi.networkadmin.vouchercardgroup.response;

import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DenominationDetails  {
private String denomination;
private VomsCategoryVO vomsCategoryVO;
}
