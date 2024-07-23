package com.btsl.user.businesslogic;

import java.util.Map;

import org.springframework.stereotype.Repository;



/**
 * Data base operations for SystemPreferences
 * 
 * @author venkatesans
 * @date : 16-Mar-2020
 */
@Repository
public interface MessagesCustomRepository {

    Map<String, Object> loadMessageByLocale(String plocalLng) throws VMSBaseException;

}
