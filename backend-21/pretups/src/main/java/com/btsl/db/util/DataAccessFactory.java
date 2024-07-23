package com.btsl.db.util;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;


/**
 * DataAccessFactory returns the DAO class name defined in the db specific prop file
 * If any dao is missing in prop file, it 'll return Oracle Dao class as a default
 *
 */
public class DataAccessFactory implements ObjectProducerI{

	private String className = "DataAccessFactory";

	private static Log log = LogFactory.getLog(DataAccessFactory.class.getName());

	private static DataAccessFactory dataAccessFactory = new DataAccessFactory();

	private DataAccessFactory() {
	}

	public static DataAccessFactory getDataAccessFactoryInstance(){
		return dataAccessFactory;
	}

	public Object getSpecificInstance(String daoKey){
		String methodname = className+"#getSpecificInstance";
		Object specificDao = null;
		try {
			String daoClassName =  QueryConstants.getDAOName(daoKey).trim();
			if(!BTSLUtil.isNullString(daoClassName))
				specificDao = Class.forName(daoClassName).newInstance();
			else{
				specificDao = Class.forName(QueryConstants.getDefaultDAOName(daoKey)).newInstance();
			}
				
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			log.errorTrace(methodname, e);
			log.error(methodname, "No Dao calss has been defined in prop file with key "+daoKey);
		}
		return specificDao;
	}



}