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
import java.util.Date;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Data base operations for LoginUsersRespository.
 *
 * @author Subesh KCV
 * @date : @date : 20-DEC-2019
 */
@Component
public class NetworkRepositoryImpl implements NetworkRepository {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkRepositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<NetworksVO> loadNetworkListForSuperOperatorUsers(String userId, String status) {
        Query query;
        List<NetworksVO> listNetworkVO = null;
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format(
                        "loadNetworkListForSuperOperatorUser, sparameter userId={0} ,status= {1}", userId, status));
            }

            StringBuilder strBuff = new StringBuilder();
            strBuff.append(" SELECT n.networkCode,n.networkName,");
            strBuff.append(" n.networkShortName,n.companyName,n.reportHeaderName,  ");
            strBuff.append(" n.erpNetworkCode,n.address1,n.address2,n.city,n.state,n.zipCode,  ");
            strBuff.append(" n.country,n.networkType,n.status,n.remarks,n.language1Message, ");
            strBuff.append("  n.language2Message,n.text1Value,n.text2Value,n.countryPrefixCode,n.serviceSetId,  ");
            strBuff.append(" n.createdBy, n.modifiedBy, n.createdOn,");
            strBuff.append(" n.modifiedOn FROM Networks n JOIN  UserGeographies ug");
            strBuff.append(" ON ug.id.grfDomainCode =n.networkCode and ug.id.userId= :userID ");
            strBuff.append(" AND n.status not in (");
            strBuff.append(status);
            strBuff.append(") ORDER BY n.networkCode ");
            query = entityManager.createQuery(strBuff.toString());
            query.setParameter("userID", userId);
            List<Object[]> channelUserObject = query.getResultList();

            listNetworkVO = getListNetworkVO(channelUserObject);

        } catch (

        PersistenceException pe) {
            LOGGER.error("Exception occurs at loadNetworkListForSuperOperatorUsers {}", pe);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }

        return listNetworkVO;
    }

    private static NetworksVO constructNetworkVO(Object[] obj) {
        NetworksVO networksVO = new NetworksVO();
        construct0to12(networksVO, obj);
        construct13to24(networksVO, obj);
        return networksVO;
    }

    private static void construct0to12(NetworksVO networksVO, Object[] obj) {

        networksVO.setNetworkCode(
                (obj[NumberConstants.ZERO.getIntValue()] != null ? obj[NumberConstants.ZERO.getIntValue()].toString()
                        : ""));
        networksVO.setNetworkName(
                (obj[NumberConstants.ONE.getIntValue()] != null ? obj[NumberConstants.ONE.getIntValue()].toString()
                        : ""));
        networksVO.setNetworkShortName(
                (obj[NumberConstants.TWO.getIntValue()] != null ? obj[NumberConstants.TWO.getIntValue()].toString()
                        : ""));
        networksVO.setCompanyName(
                (obj[NumberConstants.THREE.getIntValue()] != null ? obj[NumberConstants.THREE.getIntValue()].toString()
                        : ""));
        networksVO.setReportHeaderName(
                (obj[NumberConstants.FOUR.getIntValue()] != null ? obj[NumberConstants.FOUR.getIntValue()].toString()
                        : ""));
        networksVO.setErpNetworkCode(
                (obj[NumberConstants.FIVE.getIntValue()] != null ? obj[NumberConstants.FIVE.getIntValue()].toString()
                        : ""));
        networksVO.setAddress1(
                (obj[NumberConstants.SIX.getIntValue()] != null ? obj[NumberConstants.SIX.getIntValue()].toString()
                        : ""));
        networksVO.setAddress2(
                (obj[NumberConstants.SEVEN.getIntValue()] != null ? obj[NumberConstants.SEVEN.getIntValue()].toString()
                        : ""));
        networksVO.setCity(
                (obj[NumberConstants.EIGHT.getIntValue()] != null ? obj[NumberConstants.EIGHT.getIntValue()].toString()
                        : ""));

    }

    private static void construct13to24(NetworksVO networksVO, Object[] obj) {

        networksVO.setState(
                (obj[NumberConstants.NINE.getIntValue()] != null ? obj[NumberConstants.NINE.getIntValue()].toString()
                        : ""));
        networksVO.setZipCode(
                (obj[NumberConstants.N10.getIntValue()] != null ? obj[NumberConstants.N10.getIntValue()].toString()
                        : ""));
        networksVO.setCountry(
                (obj[NumberConstants.N11.getIntValue()] != null ? obj[NumberConstants.N11.getIntValue()].toString()
                        : ""));
        networksVO.setNetworkType(
                (obj[NumberConstants.N12.getIntValue()] != null ? obj[NumberConstants.N12.getIntValue()].toString()
                        : ""));
        networksVO.setStatus(
                (obj[NumberConstants.N13.getIntValue()] != null ? obj[NumberConstants.N13.getIntValue()].toString()
                        : ""));
        networksVO.setRemarks(
                (obj[NumberConstants.N14.getIntValue()] != null ? obj[NumberConstants.N14.getIntValue()].toString()
                        : ""));
        networksVO.setLanguage1Message(
                (obj[NumberConstants.N15.getIntValue()] != null ? obj[NumberConstants.N15.getIntValue()].toString()
                        : ""));
        constructNetworkVOSub(networksVO, obj);
    }

    private static void constructNetworkVOSub(NetworksVO networksVO, Object[] obj) {

        networksVO.setLanguage2Message(
                (obj[NumberConstants.N16.getIntValue()] != null ? obj[NumberConstants.N16.getIntValue()].toString()
                        : ""));
        networksVO.setText1Value(
                (obj[NumberConstants.N17.getIntValue()] != null ? obj[NumberConstants.N17.getIntValue()].toString()
                        : ""));
        networksVO.setText2Value(
                (obj[NumberConstants.N18.getIntValue()] != null ? obj[NumberConstants.N18.getIntValue()].toString()
                        : ""));
        networksVO.setCountryPrefixCode(
                (obj[NumberConstants.N19.getIntValue()] != null ? obj[NumberConstants.N19.getIntValue()].toString()
                        : ""));
        networksVO.setServiceSetId(
                (obj[NumberConstants.N20.getIntValue()] != null ? obj[NumberConstants.N20.getIntValue()].toString()
                        : ""));
        networksVO.setCreatedBy(
                (obj[NumberConstants.N21.getIntValue()] != null ? obj[NumberConstants.N21.getIntValue()].toString()
                        : ""));
        networksVO.setModifiedBy(
                (obj[NumberConstants.N22.getIntValue()] != null ? obj[NumberConstants.N22.getIntValue()].toString()
                        : ""));
        networksVO.setCreatedOn(
                (obj[NumberConstants.N23.getIntValue()] != null ? (Date) obj[NumberConstants.N23.getIntValue()]
                        : null));
        networksVO.setModifiedOn(
                (obj[NumberConstants.N24.getIntValue()] != null ? (Date) obj[NumberConstants.N24.getIntValue()]
                        : null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NetworksVO> loadNetworkListForSuperChannelAdm(String userId, String status) {
        Query query;

        List<NetworksVO> listNetworkVO = null;
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format(
                        "loadNetworkListForSuperChannelAdm, parameter userId= {0} ,status= {1}", userId, status));
            }

            StringBuilder strBuff = new StringBuilder();
            strBuff.append(" SELECT n.networkCode,n.networkName,");
            strBuff.append(" n.networkShortName,n.companyName,n.reportHeaderName,  ");
            strBuff.append(" n.erpNetworkCode,n.address1,n.address2,n.city,n.state,n.zipCode,  ");
            strBuff.append(" n.country,n.networkType,n.status,n.remarks,n.language1Message, ");
            strBuff.append("  n.language2Message,n.text1Value,n.text2Value,n.countryPrefixCode,n.serviceSetId,  ");
            strBuff.append(" n.createdBy, n.modifiedBy, n.createdOn,");
            strBuff.append(
                    " n.modifiedOn FROM Networks n JOIN  GeographicalDomains gd ON gd.networkCode = n.networkCode   ");
            strBuff.append(" JOIN UserGeographies ug ON ug.id.grfDomainCode =gd.grphDomainCode  ");
            strBuff.append(" and ug.id.userId= :userID ");
            strBuff.append(" and gd.status <> 'N' ");
            strBuff.append(" ORDER BY n.networkCode ");
            query = entityManager.createQuery(strBuff.toString());
            query.setParameter("userID", userId);
            List<Object[]> channelUserObject = query.getResultList();
            listNetworkVO = getListNetworkVO(channelUserObject);

        } catch (PersistenceException pe) {
            LOGGER.error("Exception occurs at loadNetworkListForSuperChannelAdm {}", pe);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }

        return listNetworkVO;
    }

    private List<NetworksVO> getListNetworkVO(List<Object[]> channelUserObject) {
        List<NetworksVO> listNetworkVO = new ArrayList<>();
        if (!CommonUtils.isNullorEmpty(channelUserObject)) {
            for (Object[] obj : channelUserObject) {
                NetworksVO networkVO = constructNetworkVO(obj);
                listNetworkVO.add(networkVO);
            }
        }

        if (!listNetworkVO.isEmpty()) {
            return listNetworkVO;
        }
        return listNetworkVO;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NetworksVO> loadNetworkList(String status) {
        Query query;
        List<NetworksVO> listNetworkVO = null;
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format("loadNetworkList, parameter  status={0} ", status));
            }
            StringBuilder strBuff = new StringBuilder();
            strBuff.append(" SELECT n.networkCode,n.networkName,");
            strBuff.append(" n.networkShortName,n.companyName,n.reportHeaderName,  ");
            strBuff.append(" n.erpNetworkCode,n.address1,n.address2,n.city,n.state,n.zipCode,  ");
            strBuff.append(" n.country,n.networkType,n.status,n.remarks,n.language1Message, ");
            strBuff.append("  n.language2Message,n.text1Value,n.text2Value,n.countryPrefixCode,n.serviceSetId,  ");
            strBuff.append(" n.createdBy, n.modifiedBy, n.createdOn,");
            strBuff.append(" n.modifiedOn FROM Networks n ");
            strBuff.append("  where n.status not in (");
            strBuff.append(status);
            strBuff.append(") ORDER BY n.networkCode ");
            query = entityManager.createQuery(strBuff.toString());
            List<Object[]> channelUserObject = query.getResultList();
            listNetworkVO = getListNetworkVO(channelUserObject);
        } catch (PersistenceException pex) {
            LOGGER.error("Exception occurs at loadNetworkList {}", pex);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return listNetworkVO;
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public List<UserGeographiesVO> loadUserGeographyList(String userId, String networkCode) {
        Query query;
        List<UserGeographiesVO> listuserGeographiesVO = new ArrayList();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format("loadUserGeographyList, parameter userID = {0}, networkcode={1}",
                        userId, networkCode));
            }
            StringBuilder strBuff = new StringBuilder();
            strBuff.append(
                    " SELECT ug.id.grfDomainCode,gd.grphdomainName,gdt.grphDomainTypeName,gdt.sequenceNo ,gdt.grphDomainType ");
            strBuff.append(
                    "  FROM UserGeographies ug join  GeographicalDomains gd   on gd.grphDomainCode = ug.id.grfDomainCode ");
            strBuff.append("  join  GeographicalDomainTypes gdt   on gdt.grphDomainType  =gd.grphDomainType ");
            strBuff.append("  where ug.id.userId =:userID ");
            strBuff.append("  and gd.networkCode =:networkCode ");
            strBuff.append("  and gd.status='Y'");
            strBuff.append("   ORDER BY gd.grphdomainName ");
            query = entityManager.createQuery(strBuff.toString());
            query.setParameter("userID", userId);
            query.setParameter("networkCode", networkCode);
            List<Object[]> channelUserObject = query.getResultList();
            if (!CommonUtils.isNullorEmpty(channelUserObject)) {
                for (Object[] obj : channelUserObject) {
                    listuserGeographiesVO.add(constructUserGeographiesVO(obj));
                }
            }
            if (!listuserGeographiesVO.isEmpty()) {
                return listuserGeographiesVO;
            }
        } catch (PersistenceException pe) {
            LOGGER.error("Exception occurs at loadNetworkList {}", pe);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return listuserGeographiesVO;

    }

    private static UserGeographiesVO constructUserGeographiesVO(Object[] obj) {
        UserGeographiesVO userGeographiesVO = new UserGeographiesVO();

        userGeographiesVO.setGraphDomainCode((obj[0] != null ? obj[0].toString() : ""));
        userGeographiesVO.setGraphDomainName((obj[1] != null ? obj[1].toString() : ""));
        userGeographiesVO.setGraphDomainTypeName(
                (obj[NumberConstants.TWO.getIntValue()] != null ? obj[NumberConstants.TWO.getIntValue()].toString()
                        : ""));
        userGeographiesVO.setGraphDomainSequenceNumber((obj[NumberConstants.THREE.getIntValue()] != null
                ? Integer.parseInt(obj[NumberConstants.THREE.getIntValue()].toString())
                : 0));
        userGeographiesVO.setGraphDomainType(
                (obj[NumberConstants.FOUR.getIntValue()] != null ? obj[NumberConstants.FOUR.getIntValue()].toString()
                        : ""));
        return userGeographiesVO;
    }

}
