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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.btsl.common.BTSLBaseException;



/**
 * Repository of GeographicalDomainRepositoryImpl interface.
 *
 * @author VENKATESAN.S
 */
@Component
@CacheConfig(cacheNames = "systemdata_cache")
public class GeographicalDomainRepositoryImpl implements GeographicalDomainRepository {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeographicalDomainRepositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method for loading User Assigned Geographies List.(That are assigned to
     * the user) From the table userGeographies
     * 
     * Used for Users(UsersAction)
     * 
     * @author VENKATESAN.S
     * 
     * @param userId
     *            - String
     * @param networkCode
     *            - String
     */
    @SuppressWarnings("unchecked")
    @Override
    @Cacheable
    public ArrayList<UserGeographies> loadUserGeographyList(String userId, String networkCode) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MessageFormat.format("loadUserGeographyList , Entered userId={0}, networkCode={1} ", userId,
                    networkCode));
        }
        ArrayList<UserGeographies> list = new ArrayList<>();
        try {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append(
                    "SELECT ug.id.grfDomainCode, gd.grphdomainName, gdt.grphDomainTypeName, gdt.sequenceNo, gdt.grphDomainType");
            sqlQuery.append(" FROM UserGeographies ug, GeographicalDomains gd, GeographicalDomainTypes gdt");
            sqlQuery.append(
                    " WHERE ug.id.userId =:userId AND gd.networkCode =:networkCode AND gd.grphDomainCode = ug.id.grfDomainCode");
            sqlQuery.append(" AND gdt.grphDomainType = gd.grphDomainType AND gd.status='Y'");
            sqlQuery.append(" ORDER BY gd.grphdomainName");
            Query query = entityManager.createQuery(sqlQuery.toString());
            query.setParameter("userId", userId);
            query.setParameter("networkCode", networkCode);
            List<Object[]> menuObject = query.getResultList();
            if (!CommonUtils.isNullorEmpty(menuObject)) {
                for (Object[] obj : menuObject) {
                    UserGeographies userGeographies = constructGeographies(obj);
                    list.add(userGeographies);
                }
            }
        } catch (Exception e) {
        	
            LOGGER.error("Exception occurs at loadUserGeographyList {}", e);
            e.printStackTrace();
            //throw new Exception(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return list;
    }

    /**
     * Construct the UserGeographies data
     */
    private static UserGeographies constructGeographies(Object[] objects) {
        UserGeographies userGeographies = new UserGeographies();
        userGeographies.setGraphDomainCode((String) objects[NumberConstants.ZERO.getIntValue()]);
        userGeographies.setGraphDomainName((String) objects[NumberConstants.ONE.getIntValue()]);
        userGeographies.setGraphDomainTypeName((String) objects[NumberConstants.TWO.getIntValue()]);
        userGeographies.setGraphDomainSequenceNumber(((Long) objects[NumberConstants.THREE.getIntValue()]).intValue());
        userGeographies.setGraphDomainType((String) objects[NumberConstants.FOUR.getIntValue()]);
        return userGeographies;
    }

}
