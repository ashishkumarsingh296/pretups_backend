/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Repository;

import com.btsl.pretups.gateway.businesslogic.MessageGatewayMappingCacheVO;
import com.btsl.user.businesslogic.entity.ControlPreferences;
import com.btsl.user.businesslogic.entity.NetworkPreferences;
import com.btsl.user.businesslogic.entity.SystemPreferences;



/**
 * Data base operations for System tables
 * 
 * @author Subesh KCV
 * @date : 19-Dec-2019
 */
@Repository
public interface VMSCacheRepository {

    /**
     * Get the system preference based on code
     * 
     * @param value
     *            - value
     * @return Object
     */
    String getSystemPreferenceValue(String value);

    /**
     * Get the system preference based on code
     * 
     * @param messageCode
     *            - value
     * @param languageCode
     *            - value
     * @return String
     */
    String loadMessageByMessageCodeAndLangCode(String messageCode, int languageCode);

    /**
     * Get the Locale Master list
     * 
     * 
     * @return List<String>
     */
    List<LocaleMasterModal> loadLocaleMaster();

    /**
     * Get the MessageGateway based on gatewayCode
     * 
     * @param gatewayCode
     *            - value
     * @return MessageGateway
     */
    MessageGatewayVONew loadMessageGatewayCacheQry(String gatewayCode);

    /**
     * Get the LocaleMasterModal based on language code and country code
     * 
     * @param langCode
     *            - value
     * @param countryCode
     *            - value
     * 
     * @return LocaleMasterModal
     */
    LocaleMasterModal loadLocaleMasterByLangCntry(String langCode, String countryCode);

    /**
     * loadLookupDropDown
     * 
     * @param lookupType
     *            - value
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List loadLookupDropDown(String lookupType);
    
    /**
     * putModulueCodePrefMaps
     * 
     * @param moduleCode
     *            - value
     * 
     *@param mpdata
     *            - value            
     * @return List
     */
    void putModulueCodePrefMaps(String moduleCode,Map<String, String> mpdata);
    
    
    /**
     * getPreferListbyModule
     * 
     * @param moduleCode
     *            - value
     * 
     *@param mpdata
     *            - value            
     * @return HashMap
     */
    public Map<String,String> getPreferListbyModule(String moduleCode);
    
    
   /**
    * loadSystemPreferncetoRedis
    * 
    * @param List<SystemPreferences>
    *            - value
    * 
    *
    */
    public void loadSystemPreferncetoRedis(List<SystemPreferences> listsytemPreference);
    
    
    /**
     * MessageGatewayMappingCacheVO
     * 
     * @param requestCode
     *            - value
     * 
     *
     */
    
    MessageGatewayMappingCacheVO loadMessageGatewayMappingCache(String requestCode);
    
    
   
    /**
     * loadNetworkPreferncetoRedis
     * 
     * @param List<NetworkPreferences>
     *            - value
     * 
     *
     */
    
    public void loadNetworkPreferncetoRedis(List<NetworkPreferences> listNetworkPreferences);
    
    
    /**
     * getNetworkPreferenceValue
     * 
     * @param networkCode
     * @param preferenceCode  
     * 
     *
     */
    public String getNetworkPreferenceValue(String networkCode,String preferenceCode);
    
    
    /**
     * getNetworkPreferenceValue
     * 
     * @param networkCode
     * @param preferenceCode  
     * 
     *
     */
    public void loadSysMessages();
    
    
    /**
     * loadAllRegExSecurityProperties
     * 
     *
     */
    public void loadAllRegExSecurityProperties();
    
    
    /**
     * getRegExPropsinRedisCache
     * 
     *@param language
     */
    public Properties  getRegExPropsinRedisCache(String language); 

    
    
    
    /**
     * loadControlPreferncetoRedis
     * 
     *
     *
     */
    public void loadControlPreferncetoRedis(List<ControlPreferences> listControlPreferences); 
    
    

    /**
     * getControlPreferenceValue
     * 
     * @param controlCode
     * @param networkCode
     * @param preferenceCode  
     * 
     *
     */
    public String getControlPreferenceValue(String controlCode,String networkCode,String preferenceCode);
    
    
    
    /**
     * loadAllessageGatewaytocache
     * 
     *
     * 
     *
     */
    public Void loadAllMessageGateway(); 
    
    
    /**
     * getCheckSumkey
     *  
     */
    public String  getChecksumKey();
    
    /**
     * blackListJWTToken
     *  
     */
    public void blackListJWTToken(String jwtToken);
    
    /**
     * isLoggedOutToken
     *  
     */
    public boolean isLoggedOutToken(String jwtToken);
    
    /**
     * getLocaleMasterModalByLangCode
     *  
     */
    public LocaleMasterModal getLocaleMasterModalByLangCode(int languageCode);
    	
    
    
    
    
}

