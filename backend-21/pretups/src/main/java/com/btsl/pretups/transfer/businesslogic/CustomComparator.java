/* @# CustomComparator.java
 *
 *	   Created on 				Created by					History
 *	--------------------------------------------------------------------------------
 * 		Apr 13, 2017			  Hargovind Karki		   Initial creation
 *	--------------------------------------------------------------------------------
 *  Copyright(c) 2017 Comviva Technologies Ltd.
 */

package com.btsl.pretups.transfer.businesslogic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public class CustomComparator {
	
	private static Method targetMethod = null;
	private static int sortOrder = 0;
	private static final Log log = LogFactory.getLog(CustomComparator.class.getName());
	
	
	private CustomComparator() {
	  }
	
	 /**
     * Returns the comparator based on class name, field name and sort order
     * Comparator returned can be used to sort the collections of objects of the class
     * @param   className   Name of the class whose objects are present in the collection.
     * @param   fieldName   Name of the field on which the collection should be sorted.
     * @param   order   Sorting order (0 for ascending and 1 for descending.
     * @return  An implementation of comparator based upon the arguments.
     */
	@SuppressWarnings("unchecked")
	public static synchronized Comparator<TransferRulesVO> getComparator(String className, String fieldName, int order) throws ClassNotFoundException,IllegalArgumentException{
		if(!validateArgs(className, fieldName, order)){
			throw new IllegalArgumentException();
		}
		
		
		//Initialise local and static variables
		Comparator<TransferRulesVO> resultComparator = null;
		Method[] methods = null;
		targetMethod = null;
		sortOrder = order;
		//Load the class in the memory
		Class targetClass = Class.forName(className);
		//Fetch the methods
		methods = targetClass.getMethods();
		//Iterate through methods array to find the required getter method
		for(Method method : methods){
			if(method.getName().toLowerCase().matches("get*" + fieldName.toLowerCase())){
				targetMethod = method;
				break;
			}
		}
		//If getter method is not present then throw the exception
		if(null == targetMethod){
			throw new NoSuchMethodError();
		}
		//Instantiate Comparator with anonymous inner class
		resultComparator = new Comparator() {
			
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				  final String METHOD_NAME = "compare";
				int retVal = 0;
				try {
					Object value1 = targetMethod.invoke(o1);
					Object value2 = targetMethod.invoke(o2);
					//Implement the sorting based on the supplied arguments
					if(sortOrder == 0){
						retVal = compareValues(value1, value2);
					}
					else{
						retVal = compareValues(value2, value1);
					}
				} catch ( IllegalAccessException e ) {
						
						log.errorTrace(METHOD_NAME, e);
					}
				catch ( InvocationTargetException e ) {
					log.errorTrace(METHOD_NAME, e);
				}
				catch (  NoSuchMethodException e) {
					log.errorTrace(METHOD_NAME, e);
				}
				
				return retVal;
			}
		};
		return resultComparator;
	}
	/**
     * Returns the boolean result of whether the arguments supplied are valid
     * @param   className   Name of the class whose objects are present in the collection.
     * @param   fieldName   Name of the field on which the collection should be sorted.
     * @param   order   Sorting order (0 for ascending and 1 for descending.
     * @return  status of validation.
     */
	public static boolean validateArgs(String className, String fieldName, int order){
		boolean result = false;
		if(null != className && 0 != className.length() && null != fieldName && 0 != fieldName.length())
		{
			 if(0 <= order || 1 >= order)
				 result = true;
		}
		return result;
	}
	/**
     * Returns the comparison of two values by calling the compareTo methods for their classes
     * @param   value1   Value to be compared.
     * @param   value2   Value to be compared.
     * @return  Outcome of comparison
     */
	public static int compareValues(Object value1, Object value2) throws SecurityException, NoSuchMethodException, 
	IllegalArgumentException, IllegalAccessException, InvocationTargetException{

		int result = 0;
		Method method1 = null;
		//If either of the values is null then, return the result accordingly 
		if(null == value1 && null == value2){
			result= 0;
		}
		else if(null == value1){
			result= -1;
		}
		else if(null == value2){
			result= 1;
		}
		else{
		//else, call the compareTo method of respective class
		method1 = value1.getClass().getMethod("compareTo",new Class[]{value2.getClass()});
		result = (Integer)method1.invoke(value1, new Object[]{value2});
		//return result;
		}
		return result;
		
	}
}
