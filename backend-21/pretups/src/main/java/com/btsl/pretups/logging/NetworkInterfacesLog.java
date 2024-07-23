package com.btsl.pretups.logging;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/*
 * @# NetworkInterfacesLog.java
 * 
 * Created by 
 * ------------------------------------------------------------------------------
 * --
 * Finalatix MAY 02, 2023 
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2023 Finalatix Technologies Pvt Ltd.
 */
public class NetworkInterfacesLog {
    private static Log _Filelogger = LogFactory.getLog(NetworkInterfacesLog.class.getName());

    /**
	 * ensures no instantiation
	 */
    private NetworkInterfacesLog() {
        
    }

    public static void log(InterfaceNetworkMappingVO interfaceMappingVO) {
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" [ Network Code : " + interfaceMappingVO.getNetworkCode() + "]");
        strBuff.append(" [Interface CategoryID : " + interfaceMappingVO.getInterfaceCategoryID() + "]");
        strBuff.append(" [Interface ID : " + interfaceMappingVO.getInterfaceID() + "]");
        strBuff.append(" [Queue Size : " + interfaceMappingVO.getQueueSize() + "]");
        strBuff.append(" [Queue Time Out : " + interfaceMappingVO.getQueueTimeOut() + "]");
        strBuff.append(" [Request Time Out : " + interfaceMappingVO.getRequestTimeOut() + "]");
        strBuff.append(" [Next Check Queue Req Sec : " + interfaceMappingVO.getNextCheckQueueReqSec() + "]");
        strBuff.append(" [Created On : " + interfaceMappingVO.getCreatedOn() + "]");
        strBuff.append(" [Created By : " + interfaceMappingVO.getCreatedBy() + "]");
        strBuff.append(" [Modified On : " + interfaceMappingVO.getModifiedOn() + "]");
        strBuff.append(" [Modified By : " + interfaceMappingVO.getModifiedBy() + "]");
        strBuff.append(" [Last Modified On : " + interfaceMappingVO.getLastModifiedOn() + "]");
        _Filelogger.info(" ", strBuff.toString());
    }
    
    public static void arrayListlog( ArrayList<InterfaceNetworkMappingVO> interfaceNetworkMappingList ) {
    	for(InterfaceNetworkMappingVO interfaceMappingVO : interfaceNetworkMappingList) {
    		 StringBuilder strBuff = new StringBuilder();
    	        strBuff.append(" [ Network Code : " + interfaceMappingVO.getNetworkCode() + "]");
    	        strBuff.append(" [Interface CategoryID : " + interfaceMappingVO.getInterfaceCategoryID() + "]");
    	        strBuff.append(" [Interface ID : " + interfaceMappingVO.getInterfaceID() + "]");
    	        strBuff.append(" [Queue Size : " + interfaceMappingVO.getQueueSize() + "]");
    	        strBuff.append(" [Queue Time Out : " + interfaceMappingVO.getQueueTimeOut() + "]");
    	        strBuff.append(" [Request Time Out : " + interfaceMappingVO.getRequestTimeOut() + "]");
    	        strBuff.append(" [Next Check Queue Req Sec : " + interfaceMappingVO.getNextCheckQueueReqSec() + "]");
    	        strBuff.append(" [Created On : " + interfaceMappingVO.getCreatedOn() + "]");
    	        strBuff.append(" [Created By : " + interfaceMappingVO.getCreatedBy() + "]");
    	        strBuff.append(" [Modified On : " + interfaceMappingVO.getModifiedOn() + "]");
    	        strBuff.append(" [Modified By : " + interfaceMappingVO.getModifiedBy() + "]");
    	        strBuff.append(" [Last Modified On : " + interfaceMappingVO.getLastModifiedOn() + "]");
    	        _Filelogger.info(" ", strBuff.toString()+"\n");
    	}
       
    }

}
