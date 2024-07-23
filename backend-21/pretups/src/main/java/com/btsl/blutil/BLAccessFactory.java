package com.btsl.blutil;

import com.btsl.db.util.ObjectProducerI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;


/**
 * DataAccessFactory returns the BusinessLogic class name defined in the db specific prop file
 * If any BusinessLogic is missing in prop file, it 'll return Oracle BusinessLogic class as a default
 *
 */
public class BLAccessFactory implements ObjectProducerI{

	private String className = "BLAccessFactory";

	private static Log log = LogFactory.getLog(BLAccessFactory.class.getName());

	private static BLAccessFactory blAccessFactory = new BLAccessFactory();

	private BLAccessFactory() {
	}

	public static BLAccessFactory getBLAccessFactoryInstance(){
		return blAccessFactory;
	}

	public Object getSpecificInstance(String blKey){
		String methodname = className+"#getSpecificInstance";
		Object specificBL = null;
		try {
			String blClassName =  BLConstants.getBLName(blKey).trim();
			if(!BTSLUtil.isNullString(blClassName))
				specificBL = Class.forName(blClassName).newInstance();
			else{
				specificBL = Class.forName(BLConstants.getDefaultBLName(blKey)).newInstance();
			}
				
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			log.errorTrace(methodname, e);
			log.error(methodname, "No BusinessLogic calss has been defined in prop file with key "+blKey);
		}
		return specificBL;
	}



}