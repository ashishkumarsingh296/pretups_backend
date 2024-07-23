package com.restapi.networkadmin.commissionprofile.responseVO;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchAddUploadCommProVO {
	private String domainName;
	private String categoryCode;
	private String categoryCodeDesc;
	private String showAdditionalCommissionFlag;
	private String sequenceNo;
	private ArrayList commissionProfileList;
	private String setID;
	private ArrayList errorList;
	private String errorFlag;
	private String sheetName;
	private int length;
	private String fileName;
	private String subServiceCode;
	private String version;
	private String addtnlComStatus;
	private ArrayList domainList;
	private String networkID;

}
