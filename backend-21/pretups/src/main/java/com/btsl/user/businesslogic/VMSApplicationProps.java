package com.btsl.user.businesslogic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class VMSApplicationProps {
	
	@Value("${externalizeProperties}")
    private  String externalizeProperties;

	public String getExternalizeProperties() {
		return externalizeProperties;
	}
	
	
	
	
	

}
