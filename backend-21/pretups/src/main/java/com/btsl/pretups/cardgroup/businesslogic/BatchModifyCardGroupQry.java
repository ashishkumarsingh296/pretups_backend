package com.btsl.pretups.cardgroup.businesslogic;

import java.util.ArrayList;

public interface BatchModifyCardGroupQry {
	public String loadCardGroupDetailsListByDateQry(String p_serviceType,ArrayList cgSetselectedList);

	String loadCardGroupDetailsListByDateQryFile(String pServiceType, ArrayList pCgSetselectedList);
}
