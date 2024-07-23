package com.restapi.networkadmin.cardgroup.requestVO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardGroupSetVersionVODetails {
	 private String version;
     private long creadtedOn; 
     private long modifiedOn;
     private String createdBy;
     private String modifiedBy;
     private String applicableFromAsString;
     private long oldApplicableFrom;
     private String cardGroupSetID;
     private String cardGroupSetCombinedID;
     private String modifiedOnAsString;
     private String createdOnAsString;
     private Date applicableFrom;
}
