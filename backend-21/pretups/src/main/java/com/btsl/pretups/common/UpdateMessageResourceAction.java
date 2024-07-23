package com.btsl.pretups.common;

import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.btsl.common.ApplicationContextProvider;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;

public class UpdateMessageResourceAction /*extends Action*/ {
    
     public static final Log log = LogFactory
			.getLog(UpdateMessageResourceAction.class.getName());
}
