package com.btsl.db.util;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * QueryFactory returns the Query class name defined in the db specific prop
 *  file for the queries written outside the DAO layer.
 *
 */
public class QueryFactory implements ObjectProducerI{

	private String className = "QueryFactory";

	private static Log log = LogFactory.getLog(QueryFactory.class.getName());

	private static QueryFactory queryFactory = new QueryFactory();

	private QueryFactory() {
	}

	public static QueryFactory getQueryFactoryInstance(){
		return queryFactory;
	}

	@Override
	public Object getSpecificInstance(String key){
		String methodname = className+"#getSpecificInstance";
		Object queryInstance = null;
		try {

			System.out.println("key >>>>> "+key);

			System.out.println("QueryConstants ");
			String daoClassName =  QueryConstants.getDAOName(key).trim();
			if(!BTSLUtil.isNullString(daoClassName))
				queryInstance = Class.forName(daoClassName).newInstance();
			else{
				queryInstance = Class.forName(QueryConstants.getDefaultDAOName(key)).newInstance();
			}
				
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			log.errorTrace(methodname, e);
			log.error( methodname, "No Query class has been defined in prop file with key "+key);
		}
		return queryInstance;
	}
	
}
