package com.btsl.qryfactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordVO;

public interface ServiceKeywordDaoI {
  
	public ArrayList<?> loadServiceTypeList(Connection con) throws BTSLBaseException ;

    public ArrayList<?> loadServiceTypeData(Connection con, String serviceType) throws BTSLBaseException ;

    public int addServiceType(Connection con, ServiceKeywordVO serviceKeywordVO) throws BTSLBaseException ;
 
    public int updateServiceType(Connection con, ServiceKeywordVO serviceKeywordVO) throws BTSLBaseException ;

    public boolean isRecordModified(Connection con, long oldlastModified, String key) throws BTSLBaseException ;

    public boolean isServiceKeywordExist(Connection con, ServiceKeywordVO serviceKeywordVO, boolean flag) throws BTSLBaseException ;

    public HashMap<?,?> loadServiceCache() throws BTSLBaseException ;

    public ArrayList<?> loadServiceTypeListForNetworkServices(Connection con) throws BTSLBaseException ;

    public HashMap<?,?> loadServiceTypeCache() throws BTSLBaseException ;

    public ArrayList<?> loadAllServiceTypeList(Connection con, String status) throws BTSLBaseException ;
 
    public int updateServiceTypeStatus(Connection con, ArrayList<?> voList) throws BTSLBaseException ;
 
    public boolean recordModified(Connection con, String serviceTypCode, String module, String networkCode, long oldLastModified) throws BTSLBaseException ;

    public ArrayList<?> loadServiceTypeListForServiceSelector(Connection con) throws BTSLBaseException ;
  
    public ArrayList<?> loadServiceCache(Connection con) throws BTSLBaseException ;
   
	public HashMap<?, ?> loadWebServiceTypeCache() throws BTSLBaseException;
}
