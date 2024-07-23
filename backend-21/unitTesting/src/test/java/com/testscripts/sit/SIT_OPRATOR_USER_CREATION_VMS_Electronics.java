package com.testscripts.sit;

import java.io.IOException;
import java.sql.SQLException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.VomsOperatorUsers;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils._masterVO;
public class SIT_OPRATOR_USER_CREATION_VMS_Electronics extends BaseTest  {

	String sheetToRefer = ExcelI.ELC_OPERATOR_USERS_HIERARCHY_SHEET;
	String voucherType = "Electronic";
	static String voucherTypeAllowed;
	
	
	 @Test
		public void A_01_operatorUserFile() throws SQLException {
			voucherTypeAllowed = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE),
					CONSTANT.USER_VOUCHERTYPE_ALLOWED);
			if (voucherTypeAllowed.equalsIgnoreCase("TRUE"))
				new VomsOperatorUsers()._01_fetchOperatorUsers_custom(voucherType);
		}

	     @Test(dataProvider = "optusrcreationdataElc")
		public void A_02_operatorUserCreation(int RowNum, String ParentUser, String LoginUser, String sheetTorefer,
				String vouchertype) throws InterruptedException {
			new VomsOperatorUsers()._02_operatorUserCreation(RowNum, ParentUser, LoginUser, sheetTorefer, vouchertype);
		}

		@DataProvider(name = "optusrcreationdataElc")
		public Object[][] optdataElc() throws IOException {
			if (voucherTypeAllowed.equalsIgnoreCase("TRUE")) {
				Object[][] dataOpt = new VomsOperatorUsers().DomainCategoryProvider(sheetToRefer, voucherType);
				return dataOpt;
			}
			return null;
		}

}
