package com.restapi.networkadmin.cardgroup.requestVO;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class C2SAddCardGroupSaveRequestVO {

	private String cardGroupSetID;
	private String cardGroupSetName;
	
	private Date createdOn;
	private String createdBy;
    private String version;
    private Date modifiedOn;
    private String modifiedBy;
    private String moduleCode;
    private String status;
    private String subServiceType;
    private String serviceTypeID;
   
	private String setType;
    private String defaultCardGroup;
    private String applicableFromHour;
    private String applicableFromDate;
    private ArrayList origCardGroupSetNameList;
    private ArrayList cardGroupSetNameList;
    private ArrayList cardGroupSetVersionList;
    private String selectCardGroupSetId;
	private String selectCardGroupSetVersionId;
    
    private ArrayList<CardGroupDetailsVO> cardGroupList;
    private String cardGroupSubServiceID;
    private String oldApplicableFromDate;
    private String oldApplicableFromHour;
    
	@Override
	public String toString() {
		StringBuilder sbd= new StringBuilder();
		sbd.append("C2SAddCardGroupSaveRequestVO [cardGroupSetID=");
		sbd.append(cardGroupSetID);
		sbd.append(", cardGroupSetName=");
		sbd.append(cardGroupSetName);
		sbd.append(", createdOn=");
		sbd.append(createdOn);
		sbd.append(", createdBy=");
		sbd.append(createdBy);
		sbd.append(", version=");
		sbd.append(version);
		sbd.append(", modifiedOn=");
		sbd.append(modifiedOn);
		sbd.append(", modifiedBy=");
		sbd.append(modifiedBy);
		sbd.append(", moduleCode=");
		sbd.append(moduleCode);
		sbd.append(", status=");
		sbd.append(status);
		sbd.append(", subServiceType=");
		sbd.append(subServiceType);
		sbd.append(", serviceTypeID=");
		sbd.append(serviceTypeID);
		sbd.append(", setType=");
		sbd.append(setType);
		sbd.append(", defaultCardGroup=");
		sbd.append(defaultCardGroup);
		sbd.append( ", applicableFromHour=");
		sbd.append(applicableFromHour);
		sbd.append(", applicableFromDate=");
		sbd.append(applicableFromDate);
		sbd.append(", origCardGroupSetNameList=");
		sbd.append(origCardGroupSetNameList);
		sbd.append(", cardGroupSetNameList=");
		sbd.append(cardGroupSetNameList);
		sbd.append(", cardGroupSetVersionList=");
		sbd.append(cardGroupSetVersionList);
		sbd.append( ", selectCardGroupSetId=");
		sbd.append(selectCardGroupSetId);
		sbd.append( ", selectCardGroupSetVersionId=");
		sbd.append(selectCardGroupSetVersionId);
		sbd.append( ", cardGroupList=" );
		sbd.append( cardGroupList );
		sbd.append( ", cardGroupSubServiceID=" );
		sbd.append( cardGroupSubServiceID );
		sbd.append( "]" );
		
		
		return sbd.toString();
		}
	  
   
}
