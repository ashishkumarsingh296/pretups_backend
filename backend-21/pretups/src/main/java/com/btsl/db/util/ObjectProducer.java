package com.btsl.db.util;

import com.btsl.blutil.BLAccessFactory;
import com.btsl.blutil.BLConstants;

/**
 * ObjectProducer produces the specific dao,query or businesslogic instance corresponding to DB connected
 *
 */
public class ObjectProducer {

	private static ObjectProducerI daoProdI,qryProdI,blProdI = null;

	/**
	 * ensures no instantiation
	 */
	private ObjectProducer(){
		
	}
	
	static {
		daoProdI = DataAccessFactory.getDataAccessFactoryInstance();
		qryProdI = QueryFactory.getQueryFactoryInstance();
		blProdI = BLAccessFactory.getBLAccessFactoryInstance();
	}

	public static Object getObject(String p_classNameKey, String p_objProducerName){
		Object specificObj = null;
		if(QueryConstants.QUERY_PRODUCER.equals(p_objProducerName)){
			specificObj =  qryProdI.getSpecificInstance(p_classNameKey);
		}
		if(QueryConstants.DAO_PRODUCER.equals(p_objProducerName)){
			specificObj =  daoProdI.getSpecificInstance(p_classNameKey);
		}
		if(BLConstants.BL_PRODUCER.equals(p_objProducerName)){
			specificObj =  blProdI.getSpecificInstance(p_classNameKey);
		}
		return specificObj;
	}

}
