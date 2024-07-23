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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.btsl.user.businesslogic.entity.SystemPreferences;



/**
 * Data base operations for SystemPreferences
 * 
 * @author subesh KCV
 * @date : 19-Dec-2019
 */
@Repository
public interface SystemPreferencesRepository extends JpaRepository<SystemPreferences, String> {

    /**
     * Get the SystemPreference from preferenceCode
     * 
     * @param preferenceCode
     *            - preferenceCode
     * @return SystemPreferences
     */
    @Query("SELECT s FROM SystemPreferences s WHERE s.preferenceCode =:preferenceCode")
    SystemPreferences getSystemPreferencesByCode(@Param("preferenceCode") String preferenceCode);

    /**
     * Get the Lists of SystemPreference from List of preference Codes
     * 
     * @param preferenceCodeList
     *            - List<String>
     * @return List<SystemPreferences>
     */
    @Query("FROM SystemPreferences s WHERE s.preferenceCode  in(:preferenceCode)")
    List<SystemPreferences> getSystemPreferencesByPreferenceCodes(
            @Param("preferenceCode") List<String> preferenceCodeList);

    /**
     * Get the Lists of SystemPreference from preference Type
     * 
     * @param preferenceType
     *            - String
     * @return SystemPreferences
     */
    @Query("FROM SystemPreferences s WHERE s.preferenceType =:preferenceType")
    List<SystemPreferences> getSystemPreferencesByPreferenceType(@Param("preferenceType") String preferenceType);

    /**
     * Update System Preferences.
     * 
     * @param defaultValue
     *            - defaultValue
     * @param preferenceCodeList
     *            - List<String>
     * @return int
     */
    @Modifying
    @Query("UPDATE SystemPreferences s SET s.defaultValue =:defaultValue WHERE s.preferenceCode in(:preferenceCode)")
    int updateSystemPreferences(@Param("defaultValue") String defaultValue,
            @Param("preferenceCode") List<String> preferenceCodeList);

    /**
     * Update TimeIntervel System Preferences.
     * 
     * @param defaultValue
     *            - defaultValue
     * @param preferenceCode
     *            - String
     * @return int
     */
    @Modifying
    @Query("UPDATE SystemPreferences s SET s.defaultValue =:defaultValue WHERE s.preferenceCode =:preferenceCode")
    int updateTimeIntervelSystemPreference(@Param("defaultValue") String defaultValue,
            @Param("preferenceCode") String preferenceCode);
    
    
    /**
     * Load All SystemPreference 
     * 
     * 
     * @return List<SystemPreferences>
     */
    @Query("SELECT s FROM SystemPreferences s")
    List<SystemPreferences> loadAllSystemPreferences();

    
    

}
