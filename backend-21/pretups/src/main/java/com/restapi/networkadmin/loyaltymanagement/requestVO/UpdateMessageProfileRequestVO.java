package com.restapi.networkadmin.loyaltymanagement.requestVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateMessageProfileRequestVO {
    private String welcomeMesage1;
    private String welcomeMesage2;
    private String successMessage1;
    private String successMessage2;
    private String failureMessage1;
    private String failureMessage2;
    private String profileName;
    private String setID;
    private String version;
}
