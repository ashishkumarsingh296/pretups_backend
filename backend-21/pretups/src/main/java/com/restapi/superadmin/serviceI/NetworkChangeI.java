package com.restapi.superadmin.serviceI;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public interface NetworkChangeI {

	void updateLoggedInNetworkCode(MultiValueMap<String, String> headers, String networkCode,
			HttpServletResponse response1, Connection con, String userId);

}
