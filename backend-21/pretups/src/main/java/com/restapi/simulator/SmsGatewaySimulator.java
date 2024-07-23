package com.restapi.simulator;

import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${SmsGatewaySimulator.name}", description = "${SmsGatewaySimulator.desc}")//@Api(tags="Simulator")
@RestController
@RequestMapping(value = "/v1/simulate")
public class SmsGatewaySimulator {

	@GetMapping(value= "/sendSms", produces = MediaType.APPLICATION_JSON)
	public String processSendSms(){
		return "200";
	}
}
