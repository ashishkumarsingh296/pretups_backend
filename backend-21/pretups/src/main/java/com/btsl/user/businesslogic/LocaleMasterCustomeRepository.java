package com.btsl.user.businesslogic;

import java.util.HashMap;

import org.springframework.stereotype.Repository;



/**
 * Data base operations for SystemPreferences
 * 
 * @author venkatesans
 * @date : 16-Mar-2020
 */
@Repository
public interface LocaleMasterCustomeRepository {

    @SuppressWarnings("rawtypes")
    HashMap loadLocaleMasterCache() throws Exception;

    @SuppressWarnings("rawtypes")
    HashMap loadLocaleDetailsAtStartUp() throws VMSBaseException, Exception;
    
    

}
