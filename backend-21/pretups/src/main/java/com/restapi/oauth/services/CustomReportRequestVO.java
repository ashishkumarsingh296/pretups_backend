package com.restapi.oauth.services;

import lombok.Data;

import java.util.Set;

@Data
public class CustomReportRequestVO {
    Set<String> roleCodeSet;
}
