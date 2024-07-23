package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.ArrayList;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class VoucherCardGroupSetDetails {
	private String cardGroupSetName;
    private String cardGroupSubServiceName;;
    private String serviceTypedesc;
    private String setTypeName;
    private String defaultCardGroupRequired;
    private String selectCardGroupSetVersionId;
    private ArrayList cardGroupSetVersionList;
    private String cardGroupSetstatus;

}
