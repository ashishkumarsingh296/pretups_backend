package com.btsl.pretups.configuration.businesslogic;

/** ConfigurationI.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Sanjay Kumar Bind1            		May 7, 2017     Initital Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Interface for storing the configurations
 */
	
/**
 * @author sanjay.bind1
 *
 */
public interface ConfigurationI {

	public String DEFAULT_LANGUAGE = "DEFAULT_LANGUAGE";//For Default locale
	public String DEFAULT_COUNTRY="DEFAULT_COUNTRY";//For Default Country
	
	public String TYPE_INTEGER="INT";
	public String TYPE_LONG="NUMBER";
	public String TYPE_BOOLEAN="BOOLEAN";
	public String TYPE_AMOUNT="AMOUNT";
	public String TYPE_DATE="DATE";
	public String TYPE_STRING="STRING";
}
