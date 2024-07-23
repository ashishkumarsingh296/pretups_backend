package com.restapi.networkadmin.commissionprofile.requestVO;

import java.util.ArrayList;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStatusForCommissionProfileRequestVO {
	 ArrayList<ChangeStatusForCommissionProfileVO> changeStatusListForCommissionProfile = new ArrayList<>();
}
