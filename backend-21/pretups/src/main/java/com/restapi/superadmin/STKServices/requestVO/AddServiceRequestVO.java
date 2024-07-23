package com.restapi.superadmin.STKServices.requestVO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AddServiceRequestVO {
    private String[] categoriesList;
    private String serviceSetID;
    private String label1;
    private String label2;
    private String wmlCode;
    private String bytecode;
    private String description;
    private String status;
}
