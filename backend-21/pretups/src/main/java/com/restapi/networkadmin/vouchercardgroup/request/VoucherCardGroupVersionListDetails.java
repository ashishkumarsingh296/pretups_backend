package com.restapi.networkadmin.vouchercardgroup.request;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class VoucherCardGroupVersionListDetails {
	 private String cardGroupSetID;
	    private String version;
	    private Date applicableFrom;
	    private String createdBy;
	    private Date creadtedOn;
	    private String modifiedBy;
	    private Date modifiedOn;
	    private String applicableFromAsString;
	    private String oldApplicableFrom;
	    private String createdOnAsString;
	    private String cardGroupSetCombinedID;
	    private String modifiedOnAsString;
}
