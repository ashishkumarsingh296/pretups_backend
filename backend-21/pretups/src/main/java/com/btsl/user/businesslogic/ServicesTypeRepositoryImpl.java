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


/**
 * Repository of ServicesTypeRepositoryImpl interface.
 *
 * @author VENKATESAN.S
 */
@Component
@CacheConfig(cacheNames = "systemdata_cache")
public class ServicesTypeRepositoryImpl implements ServicesTypeCustomRepository {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesTypeRepositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method for loading Users Assigned Services List(means Services that are
     * assigned to the user). From the table USER_SERVICES
     * 
     * Used in(userAction, ChannelUserAction)
     * 
     * @author VENKATESAN
     * 
     * @param userId
     *            String
     * @return java.util.ArrayList
     * 
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    @Override
    @Cacheable
    public ArrayList loadUserServicesList(String userId) {
        ArrayList list = new ArrayList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MessageFormat.format("loadUserServicesList, Entered in the userId={0}", userId));
        }

        Query query;
        try {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append(
                    "SELECT US.serviceType, ST.name FROM UserServices US, ServiceTypetable ST, Users U, CategoryServiceType CST");
            sqlQuery.append(
                    " WHERE US.userId =:userId AND US.serviceType = ST.serviceType AND CST.networkCode=U.networkCode");
            sqlQuery.append(
                    " AND U.userId=US.userId AND U.categoryCode=CST.categoryCode AND CST.serviceType=US.serviceType");
            query = entityManager.createQuery(sqlQuery.toString());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format("loadUserServicesList, creating QUERY: query={0}", query));
            }
            query.setParameter("userId", userId);
            List<Object[]> serviceObject = query.getResultList();
            if (!CommonUtils.isNullorEmpty(serviceObject)) {
                for (Object[] obj : serviceObject) {
                    ListValues listValues = constructServiceType(obj);
                    list.add(listValues);
                }
            }

        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at loadUserServicesList {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }

        return list;
    }

    /**
     * Construct the MenuItem data
     */
    private static ListValues constructServiceType(Object[] objects) {
        ListValues listValues = new ListValues();
        listValues = new ListValues((String) objects[NumberConstants.ONE.getIntValue()],
                (String) objects[NumberConstants.ZERO.getIntValue()]);
        return listValues;
    }
}
