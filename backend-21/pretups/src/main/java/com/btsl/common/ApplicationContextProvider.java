package com.btsl.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author ayush.abhijeet
 * This class provides application context to spring
 *
 */

@Component
@Lazy(false)
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	public ApplicationContext getApplicationContext() {
		return context;
	}

	public static ApplicationContext getApplicationContext(String test) {
		return context;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

}
