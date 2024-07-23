package com.restapi.channeluser.service;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import java.sql.Connection;

@Service
public interface ReprintVoucherServiceI {
	
	public ReprintVoucherResponseVO loadVoucherDetails(MultiValueMap<String, String> headers,String productId,HttpServletResponse responseSwag,Connection con);
}
