package com.btsl.pretups.businesslogic.interfaceManagement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;

public class InterfaceManagementBL {
	
	private static final Log LOG = LogFactory.getLog(InterfaceManagementBL.class.getName());
	
	
	private InterfaceDAO interfaceDAO;
	 
	 
	 
	public InterfaceVO getIntCatListOnLookUp(InterfaceVO obj1) throws BTSLBaseException {
		 String catType;
	try{	 
	 if(obj1.getInterfaceCategoryType()!=null)
			catType=obj1.getInterfaceCategoryType();
		else
			catType=PretupsI.INTERFACE_CATEGORY;
		
     ArrayList<ListValueVO> interfaceCategoryList = LookupsCache.loadLookupDropDown(catType,true);
     obj1.setInterfaceCategoryList(interfaceCategoryList);
     
	}
	catch(Exception e){
		    LOG.error("getIntCatListOnLookUp", "Due to some technical reasons");
	    	LOG.errorTrace("getIntCatListOnLookUp", e);      
	}
	return obj1;
	 }
	 	 
	 public ArrayList<InterfaceVO> loadInterfaceDetails(String interfaceCategoryCode, String categoryCode, String networkCode)throws BTSLBaseException{
		 ArrayList<InterfaceVO> interfaceDetailList=null;
		 final String methodName="loadInterfaceDetails";
		 Connection con=null;MComConnectionI mcomCon = null;
		 try{
			 mcomCon = new MComConnection();try{con=mcomCon.getConnection();}catch(SQLException e){
				 LOG.error(methodName, "Exception=" + e);
				 LOG.errorTrace(methodName, e);
			 }
		 interfaceDAO= new InterfaceDAO();
		 interfaceDetailList = interfaceDAO.loadInterfaceDetails(con, interfaceCategoryCode, categoryCode, networkCode);
		 }catch(BTSLBaseException be){
			 throw be;
		 }finally {
			 if(mcomCon != null){mcomCon.close("InterfaceManagementBL#loadInterfaceDetails");mcomCon=null;}
	            if (LOG.isDebugEnabled()) {
	            	LOG.debug(methodName, "Exiting Size() " + interfaceDetailList);
	            	
	            }
	        }
		 return interfaceDetailList;
	 }
	 
	 public InterfaceVO addInterfaceDetails(InterfaceVO p_inputVO) throws  BTSLBaseException 
	 {
		 ArrayList interfaceTypeIdList=null;
		 InterfaceVO vo= new InterfaceVO();
		 final String methodName="addInterfaceDetails";
		 Connection con=null;MComConnectionI mcomCon = null;
		 try{
			 mcomCon = new MComConnection();try{con=mcomCon.getConnection();}catch(SQLException e){
				 LOG.error(methodName, "SQLException=" + e);
				 LOG.errorTrace(methodName, e);
			 }
		 interfaceDAO= new InterfaceDAO();
		 interfaceTypeIdList = interfaceDAO.loadInterfaceTypeId(con, p_inputVO.getInterfaceCategoryCode());
		 vo.setInterfaceCategoryList (interfaceTypeIdList);
		 HashMap<String, String> details = interfaceDAO.getRequiredDetails(con, p_inputVO.getInterfaceCategoryCode());
         if (details != null && details.size() > 0) {
        	 vo.setUriReq(details.get("uri_req"));
         }
         vo.setSingleStateTransaction(PretupsI.INTERFACE_SINGLE_STATE_TRANASACTION);
		 }catch(BTSLBaseException be){
			 throw be;
		 }finally {
			 if(mcomCon != null){mcomCon.close("InterfaceManagementBL#addInterfaceDetails");mcomCon=null;}                                                                            
	            if (LOG.isDebugEnabled()) {                                                     
	            	LOG.debug(methodName, "Exiting  " +vo.toString());
	            }
	        }
		 return vo;
	 }
}
