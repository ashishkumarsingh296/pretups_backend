package com.btsl.pretups.rest.client;

import javax.ws.rs.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Parameter;

@Path("Test")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Test RestController")
@RestController
@RequestMapping(value = "/spring")
public class TestRestController  {

	
	@GetMapping("/spring/{name}")
	@ResponseBody
	//@ApiOperation(value = "Test Hello Application", response = String.class)
	public String getEmployeeByName(@Parameter(description = "This is the sample description" ) 
			@PathVariable("name") String name) {
		
		System.out.println("name "+name+" >>>>>>>>>>>>>>>>>>>>>>....");
		return "Hello - "+name;
	}
	
	
}
