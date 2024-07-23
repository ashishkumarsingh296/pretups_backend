package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class GroupSetDetails  {

	private String cardGroupSetName;
    private String cardGroupSubServiceName;;
    private String serviceTypedesc;
    private String setTypeName;
    private String defaultCardGroupRequired;
    private String selectCardGroupSetVersionId;
    private ArrayList cardGroupSetVersionList;
    private String cardGroupSetstatus;
   
}
