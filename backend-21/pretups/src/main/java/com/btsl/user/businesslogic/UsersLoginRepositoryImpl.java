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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.entity.Categories;
import com.btsl.user.businesslogic.entity.GeographicalDomainTypes;
import com.btsl.user.businesslogic.entity.UsersLoginInfo;
import com.btsl.util.BTSLUtil;
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

/**
 * Repository of UsersLoginRepositoryImpl interface.
 *
 * @author VENKATESAN.S
 */
@Component
public class UsersLoginRepositoryImpl implements UsersLoginQueryRep {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UsersLoginRepositoryImpl.class);

	/** The entity manager. */
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UsersLoginRepository usersLoginRepository;

	@Autowired
	private VMSCacheRepository vmsCacheRepository;

	@Override
	public Long getMaxUserPerNetwork(String networkCode) {
		Query queryMax;
		try {
			LOGGER.debug(
					MessageFormat.format("getMaxUserPerNetwork, parameter NetworkCode ={0}", networkCode));
			StringBuilder strBuffer = new StringBuilder();
			strBuffer.append(
					" select count(*) from UsersLoginInfo u where u.networkCode=:PNetworkCode and expiryTokenTime > :today  ");
			queryMax = entityManager.createQuery(strBuffer.toString());
			queryMax.setParameter("PNetworkCode", networkCode);
			Date now = new Date();
			queryMax.setParameter("today", now, TemporalType.TIMESTAMP);
			Object listcountObj = queryMax.getSingleResult();
			if (!CommonUtils.isNullorEmpty(listcountObj)) {
				return (Long) listcountObj;
			} else {
				return Long.valueOf("0");
			}
		} catch (PersistenceException pe) {
			LOGGER.error("Exception occurs at getMaxUserPerNetwork {}", pe);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

	}

	@Override
	public void deleteExpiredTokens(String userId) {
		Query queryDeleteExpirdTokens;
		try {
			LOGGER.debug(
					MessageFormat.format("deleteExpiredTokens, parameter userID ={0}", userId));
			StringBuilder strBuffer = new StringBuilder();
			strBuffer.append(
					" delete from UsersLoginInfo u where u.userID=:PUserId and expiryTokenTime < :today  ");
			queryDeleteExpirdTokens = entityManager.createQuery(strBuffer.toString());
			Date now = new Date();
			Date oneHoursLessTime = BTSLUtil.addHoursInUtilDate(now,-1);
			queryDeleteExpirdTokens.setParameter("PUserId", userId);
			queryDeleteExpirdTokens.setParameter("today", oneHoursLessTime, TemporalType.TIMESTAMP);
			int deleteCount = queryDeleteExpirdTokens.executeUpdate();
			LOGGER.info("Deleted expired tokens count"+deleteCount );

		} catch (PersistenceException pe) {
			LOGGER.error("Exception occurs at deleteExpiredTokens {}", pe);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

	}

	@Override
	public UsersLoginInfo getFirstUserPerNetwork(String networkCode) {
		UsersLoginInfo usersLoginInfo = null;
		try {
			LOGGER.debug(
					MessageFormat.format("getMaxUserPerNetwork, parameter NetworkCode ={0}", networkCode));
			StringBuilder strBuffer = new StringBuilder();
			strBuffer.append(
					" select u from UsersLoginInfo u where u.networkCode=:PNetworkCode and expiryTokenTime > :today order by  createdOn asc  ");
			TypedQuery<UsersLoginInfo> queryFirstlogin = entityManager.createQuery(strBuffer.toString(),
					UsersLoginInfo.class);
			queryFirstlogin.setParameter("PNetworkCode", networkCode);
			Date now = new Date();
			queryFirstlogin.setParameter("today", now, TemporalType.TIMESTAMP);
			List<UsersLoginInfo> listUserloginInfo = queryFirstlogin.getResultList();
			if (!CommonUtils.isNullorEmpty(listUserloginInfo)) {
				usersLoginInfo = listUserloginInfo.get(0);
			}

		} catch (PersistenceException pe) {
			LOGGER.error("Exception occurs at getMaxUserPerNetwork {}", pe);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return usersLoginInfo;
	}

	@Override
	public Long getMaxUserPerNetworkCategory(String networkCode, String categoryCode) {
		Query query;
		try {
			LOGGER.debug(
					MessageFormat.format(
							"getMaxUserPerNetwork, parameter NetworkCode ={0}, parameter CategoryCode :{1}",
							networkCode, categoryCode));
			StringBuilder strBuff = new StringBuilder();
			strBuff.append(
					" select count(*) from UsersLoginInfo u where u.networkCode=:PNetworkCode and  u.categoryCode=:PcategoryCode and expiryTokenTime > :today ");
			query = entityManager.createQuery(strBuff.toString());
			query.setParameter("PNetworkCode", networkCode);
			query.setParameter("PcategoryCode", categoryCode);
			Date now = new Date();
			query.setParameter("today", now, TemporalType.TIMESTAMP);
			Object listcountObj = query.getSingleResult();
			if (!CommonUtils.isNullorEmpty(listcountObj)) {

				return (Long) listcountObj;
			} else {
				return Long.valueOf("0");
			}

		} catch (PersistenceException pe) {
			LOGGER.error("Exception occurs at getMaxUserPerNetworkCategory {}", pe);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

	}

	@Override
	public Long getMaxUserPerUser(String loginId) {
		Query query;
		try {
			LOGGER.debug(
					MessageFormat.format(
							"getMaxUserPerUser, parameter loginId ={0}",
							loginId));
			StringBuilder strBuff = new StringBuilder();
			strBuff.append(
					" select count(*) from UsersLoginInfo u where u.loginID=:loginId and expiryTokenTime > :today ");
			query = entityManager.createQuery(strBuff.toString());
			query.setParameter("loginId", loginId);

			Date now = new Date();
			query.setParameter("today", now, TemporalType.TIMESTAMP);
			Object listcountObj = query.getSingleResult();
			if (!CommonUtils.isNullorEmpty(listcountObj)) {

				return (Long) listcountObj;
			} else {
				return Long.valueOf("0");
			}

		} catch (PersistenceException pe) {
			LOGGER.error("Exception occurs at getMaxUserPerNetworkCategory {}", pe);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

	}

	@Override
	public UsersLoginInfo getFirstUserPerNetworkCategory(String networkCode, String categoryCode) {
		UsersLoginInfo usersLoginInfo = null;

		try {

			LOGGER.debug(
					MessageFormat.format(
							"getFirstUserPerNetworkCategory, parameter NetworkCode ={0}, parameter CategoryCode :{1}",
							networkCode, categoryCode));

			StringBuilder strBuff = new StringBuilder();
			strBuff.append(
					" select u from UsersLoginInfo u where u.networkCode=:PNetworkCode and  u.categoryCode=:PcategoryCode and expiryTokenTime > :today order by  createdOn asc  ");
			TypedQuery<UsersLoginInfo> queryloginFirst = entityManager.createQuery(strBuff.toString(),
					UsersLoginInfo.class);
			queryloginFirst.setParameter("PNetworkCode", networkCode);
			queryloginFirst.setParameter("PcategoryCode", categoryCode);
			Date todayDate = new Date();
			queryloginFirst.setParameter("today", todayDate, TemporalType.TIMESTAMP);
			List<UsersLoginInfo> listUserlogInfo = queryloginFirst.getResultList();
			if (!CommonUtils.isNullorEmpty(listUserlogInfo)) {
				usersLoginInfo = listUserlogInfo.get(0);
			}

		} catch (PersistenceException pe) {
			LOGGER.error("Exception occurs at getFirstUserPerNetworkCategory {}", pe);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return usersLoginInfo;

	}

	@Override
	public void saveUsersLoginInfo(UsersLoginInfo usersLoginInfo) {
		try {
			usersLoginRepository.save(usersLoginInfo);
		} catch (PersistenceException pe) {
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue(), pe);
		}
	}

	@Override
	public void deleteUsersLoginInfo(UsersLoginInfo usersLoginInfo) {
		usersLoginRepository.delete(usersLoginInfo);

	}

	/**
	 * Method to load the user details for login info
	 *
	 * @param con
	 * @param loginID
	 * @param password
	 * @param localeLanguage
	 * @return
	 * @throws SQLExceptionR
	 * @throws Exception
	 */
	@Override
	public ChannelUserVO loadUserDetails(String loginID, String password, Locale locale) {
		final String methodName = "loadUserDetails";
		logRequestedUserDetails(loginID, password, locale, methodName);
		ChannelUserVO channelUserVO = new ChannelUserVO();
		Query query;
		try {
			StringBuilder sqlQuery = new StringBuilder(
					" SELECT u.userId, u.userName, u.networkCode,l.networkName,l.reportHeaderName, u.loginId, u.pword, "); // 7
			sqlQuery.append(
					" u.categoryCode, u.parentId, u.company, u.fax, u.ownerId, u.msisdn, u.allowedIp,  u.allowedDays, u.fromTime, u.toTime, u.firstname, u.lastname, "); // 19
			sqlQuery.append(
					" u.lastLoginOn, u.employeeCode, u.status AS userstatus, u.email, u.createdBy, u.createdOn, u.modifiedBy, "); // 27
			sqlQuery.append(
					" u.modifiedOn, u.pswdModifiedOn,  cusers.contactPerson, u.contactNo, u.designation, u.division, u.department, "); // 34
			sqlQuery.append(
					" u.msisdn, u.userType, cusers.inSuspend, cusers.outSuspend, u.address1, u.address2, u.city, u.state, u.country, "); // 43
			sqlQuery.append(
					" u.ssn, u.userNamePrefix, u.externalCode, u.userCode, u.shortName, u.referenceId, u.invalidPasswordCount, u.passwordCountUpdatedOn, u.paymentType, "); // 52
			sqlQuery.append(
					" l.status AS networkstatus, l.language1Message, l.language2Message, cat.categoryCode, cat.categoryName, "); // 57
			sqlQuery.append(
					" cat.domainCode, cat.sequenceNo, cat.multipleLoginAllowed, cat.maxLoginCount, cat.viewOnNetworkBlock, "); // 62
			sqlQuery.append(
					" cat.status AS catstatus, cat.maxTxnMsisdn, cat.uncntrlTransferAllowed, cat.scheduledTransferAllowed, cat.restrictedMsisdns, "); // 67
			sqlQuery.append(
					" cat.parentCategoryCode, cat.productTypesAllowed, cat.categoryType, cat.hierarchyAllowed, cat.transfertolistonly, "); // 72
			sqlQuery.append(
					" cat.grphDomainType, cat.multipleGrphDomains, cat.fixedRoles, cat.userIdPrefix, cat.webInterfaceAllowed,"); // 77
			sqlQuery.append(
					" cat.serviceAllowed, cat.domainAllowed, cat.fixedDomains, cat.outletsAllowed, cat.status AS categorystatus, cusers.commProfileSetId, cusers.transferProfileId, cusers.userGrade, gdt.sequenceNo AS grphSequenceNo, "); // 86
			sqlQuery.append(
					" gdt.grphDomainTypeName, dm.domainName, dm.status AS domainstatus, dm.domainTypeCode, up.smsPin, up.prefixId, up.pinRequired, up.invalidPinCount, dt.restrictedMsisdn AS restrictedMsisdnAllow, up.pinReset "); // 96
			sqlQuery.append(
					" , cusers.applicationId, cusers.mpayProfileId, cusers.userProfileId, cusers.mcommerceServiceAllow, up.accessType"); // 101
			sqlQuery.append(
					" , u.pswdReset, up.phoneProfile , u.rsaflag, cat.smsInterfaceAllowed, u.authenticationAllowed, cat.authenticationType, cusers.controlGroup "); // 108

			boolean sos_enable = isSOSEnabled(sqlQuery);
			boolean isTrfRuleUserLevelAllow = isTrfRuleUserLevelAllow(sqlQuery);
			boolean lmsAppl = isLmsAppl(sqlQuery);
			boolean optInOutAllow = isOptInOutAllowed(sqlQuery);
			boolean allowdUsrTypCreation = isUserTypeCreationAllowed(sqlQuery);
			sqlQuery.append(" FROM Users u LEFT JOIN ChannelUsers cusers ON cusers.userId=u.userId ");
			sqlQuery.append(" LEFT JOIN Networks l on l.networkCode=u.networkCode ");
			sqlQuery.append(" LEFT OUTER JOIN UserPhones up ON (up.userId=u.userId AND up.msisdn=u.msisdn), ");
			sqlQuery.append(" Categories cat, GeographicalDomainTypes gdt, Domains dm, DomainTypes dt ");
			sqlQuery.append(" WHERE UPPER(u.loginId)=:loginId AND gdt.grphDomainType = cat.grphDomainType ");
			sqlQuery.append(" AND u.status <>:statusDeleted AND u.status <>:statusCanceled ");
			sqlQuery.append(
					" AND cat.categoryCode=u.categoryCode AND cat.status <>:statusDel AND dm.domainCode = cat.domainCode ");
			sqlQuery.append(" AND dt.domainTypeCode = dm.domainTypeCode ");
			LOGGER.info("Query-->: " + sqlQuery.toString());
			query = entityManager.createQuery(sqlQuery.toString());
			query.setParameter("loginId", loginID.toUpperCase());
			query.setParameter("statusDeleted", PretupsI.USER_STATUS_DELETED);
			query.setParameter("statusCanceled", PretupsI.USER_STATUS_DELETED);
			query.setParameter("statusDel", PretupsI.STATUS_DELETE);
			List<Object[]> userlist = query.getResultList();
			if (!CommonUtils.isNullorEmpty(userlist)) {
				channelUserVO = parseUserDetailsResultSet(loginID, locale, channelUserVO, sos_enable,
						isTrfRuleUserLevelAllow, lmsAppl, optInOutAllow, allowdUsrTypCreation, userlist);
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return channelUserVO;

	}

	private void logRequestedUserDetails(String loginID, String password, Locale locale, final String methodName) {
		if (LOGGER.isDebugEnabled()) {
			StringBuilder msg = new StringBuilder("");
			msg.append("loadInterfaceTypeId():: Entered with p_loginID:");
			msg.append(loginID);
			msg.append(" p_password=");
			msg.append(password);
			msg.append(" locale=");
			msg.append(locale);
			String message = msg.toString();
			LOGGER.debug(methodName, message);
		}
	}

	private void parseUserDetailsResultSetLocaleCheck(Locale locale) {
		if ("en".equalsIgnoreCase(locale.getLanguage()) && !"US".equalsIgnoreCase(locale.getCountry())) {
			locale = new Locale(locale.getLanguage(), locale.getCountry());
		}
	}

	private void parseUserDetailsResultSetLocaleCheck1(Object[] objects, ChannelUserVO channelUserVO,
													   LocaleMasterModal localeVO) {
		if (localeVO != null && PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
			channelUserVO.setMessage((String) objects[NumberConstants.N52.getIntValue()]);
		} else {
			channelUserVO.setMessage((String) objects[NumberConstants.N53.getIntValue()]);
		}
	}

	private void isTrfRuleUserLevelAllow(boolean isTrfRuleUserLevelAllow, int num, ChannelUserVO channelUserVO,
										 Object[] objects) {
		if (isTrfRuleUserLevelAllow) {
			num = num + 1;
			channelUserVO.setTrannferRuleTypeId((String) objects[num]);
		}
	}

	private ChannelUserVO parseUserDetailsResultSet(String loginID, Locale locale, ChannelUserVO channelUserVO,
													boolean sos_enable, boolean isTrfRuleUserLevelAllow, boolean lmsAppl, boolean optInOutAllow,
													boolean allowdUsrTypCreation, List<Object[]> userlist) {
		for (Object[] objects : userlist) {
			channelUserVO = getChannelUserVoObj();
			Categories categoryVO = getCategoriesObj();
			//channelUserVO = new ChannelUserVO();
			//Categories categoryVO = new Categories();
			parseUserDetailsResultSetLocaleCheck(locale);
			LocaleMasterModal localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
			getChannelUserVO10(channelUserVO, objects, loginID);
			getChannelUserVO20(channelUserVO, objects);
			getChannelUserVO30(channelUserVO, objects);
			getChannelUserVO40(channelUserVO, objects);
			getChannelUserVO50(channelUserVO, objects);
			getGeographicalDomainTypes(channelUserVO, objects);
			getCategory10(categoryVO, channelUserVO, objects);
			int num = NumberConstants.N106.getIntValue();
			parseUserDetailsResultSetLocaleCheck1(objects, channelUserVO, localeVO);
			if (sos_enable) {
				num = num + NumberConstants.THREE.getIntValue();
				channelUserVO.setSosAllowed((String) objects[NumberConstants.N107.getIntValue()]);
				channelUserVO.setSosAllowedAmount(Long.valueOf((String) objects[NumberConstants.N108.getIntValue()]));
				channelUserVO.setSosThresholdLimit(Long.valueOf((String) objects[NumberConstants.N109.getIntValue()]));
			}

			isTrfRuleUserLevelAllow(isTrfRuleUserLevelAllow, num, channelUserVO, objects);

			if (lmsAppl) {
				num = num + 1;
				channelUserVO.setLmsProfile((String) objects[num]);
			}
			if (optInOutAllow) {
				num = num + 1;
				channelUserVO.setOptInOutStatus((String) objects[num]);
			}
			if (allowdUsrTypCreation) {
				num = num + 1;
				channelUserVO.setAllowdUsrTypCreation((String) objects[num]);
			}

			categoryVO.setParentCategoryCode((String) objects[NumberConstants.N66.getIntValue()]);
			categoryVO.setProductTypesAllowed((String) objects[NumberConstants.N67.getIntValue()]);
			categoryVO.setCategoryType((String) objects[NumberConstants.N68.getIntValue()]);
			categoryVO.setHierarchyAllowed((String) objects[NumberConstants.N69.getIntValue()]);
			categoryVO.setTransfertolistonly((String) objects[NumberConstants.N70.getIntValue()]);
			categoryVO.setGrphDomainType((String) objects[NumberConstants.N71.getIntValue()]);
			categoryVO.setMultipleGrphDomains((String) objects[NumberConstants.N72.getIntValue()]);
			categoryVO.setFixedRoles((String) objects[NumberConstants.N73.getIntValue()]);
			categoryVO.setUserIdPrefix((String) objects[NumberConstants.N74.getIntValue()]);
			categoryVO.setWebInterfaceAllowed((String) objects[NumberConstants.N75.getIntValue()]);
			categoryVO.setServiceAllowed((String) objects[NumberConstants.N76.getIntValue()]);
			categoryVO.setDomainAllowed((String) objects[NumberConstants.N77.getIntValue()]);
			categoryVO.setFixedDomains((String) objects[NumberConstants.N78.getIntValue()]);
			categoryVO.setOutletsAllowed((String) objects[NumberConstants.N79.getIntValue()]);
			categoryVO.setSmsInterfaceAllowed((String) objects[NumberConstants.N103.getIntValue()]);
			categoryVO.setAuthenticationType((String) objects[NumberConstants.N105.getIntValue()]);
			channelUserVO.setCategories(categoryVO);
		}
		return channelUserVO;
	}

	private boolean isUserTypeCreationAllowed(StringBuilder sqlQuery) {
		boolean allowdUsrTypCreation = Boolean.parseBoolean(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.OPT_IN_OUT_ALLOW.getType()));
		if (allowdUsrTypCreation) {
			sqlQuery.append(" , u.allowdUsrTypCreation ");
		}
		return allowdUsrTypCreation;
	}

	private boolean isOptInOutAllowed(StringBuilder sqlQuery) {
		boolean optInOutAllow = Boolean.parseBoolean(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.OPT_IN_OUT_ALLOW.getType()));
		if (optInOutAllow) {
			sqlQuery.append(" , cusers.optInOutStatus ");
		}
		return optInOutAllow;
	}

	private boolean isLmsAppl(StringBuilder sqlQuery) {
		boolean lmsAppl = Boolean.parseBoolean(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.LMS_APPL.getType()));
		if (lmsAppl) {
			sqlQuery.append(" , cusers.lmsProfile ");
		}
		return lmsAppl;
	}

	private boolean isTrfRuleUserLevelAllow(StringBuilder sqlQuery) {
		boolean isTrfRuleUserLevelAllow = Boolean.parseBoolean(vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.IS_TRF_RULE_USER_LEVEL_ALLOW.getType()));
		if (isTrfRuleUserLevelAllow) {
			sqlQuery.append(" , cusers.trfRuleType ");
		}
		return isTrfRuleUserLevelAllow;
	}

	private boolean isSOSEnabled(StringBuilder sqlQuery) {
		boolean sos_enable = Boolean.parseBoolean(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.CHANNEL_SOS_ENABLE.getType()));
		if (sos_enable) {
			sqlQuery.append(" , cusers.sosAllowed ,cusers.sosAllowedAmount, cusers.sosThresholdLimit "); // 3
		}
		return sos_enable;
	}

	private void getChannelUserVO10(ChannelUserVO channelUserVO, Object[] objects, String loginID) {
		String userID = (String) objects[NumberConstants.ZERO.getIntValue()];
		channelUserVO.setUserId(userID);
		channelUserVO.setUserName((String) objects[NumberConstants.ONE.getIntValue()]);
		channelUserVO.setNetworkId((String) objects[NumberConstants.TWO.getIntValue()]);
		channelUserVO.setNetworkName((String) objects[NumberConstants.THREE.getIntValue()]);
		channelUserVO.setReportHeaderName((String) objects[NumberConstants.FOUR.getIntValue()]);
		channelUserVO.setLoginId(loginID);
		channelUserVO.setPword((String) objects[NumberConstants.SIX.getIntValue()]);
		channelUserVO.setCategoryCode((String) objects[NumberConstants.SEVEN.getIntValue()]);
		channelUserVO.setParentId((String) objects[NumberConstants.EIGHT.getIntValue()]);
		channelUserVO.setCompany((String) objects[NumberConstants.NINE.getIntValue()]);
		channelUserVO.setFax((String) objects[NumberConstants.N10.getIntValue()]);
	}

	private void getChannelUserVO20(ChannelUserVO channelUserVO, Object[] objects) {
		channelUserVO.setOwnerId((String) objects[NumberConstants.N11.getIntValue()]);
		channelUserVO.setPaymentType((String) objects[NumberConstants.N50.getIntValue()]);
		getUserdata(channelUserVO);
		channelUserVO.setMsisdn((String) objects[NumberConstants.N12.getIntValue()]);
		channelUserVO.setValidRequestURLs((String) objects[NumberConstants.N13.getIntValue()]);
		channelUserVO.setAllowedDays((String) objects[NumberConstants.N14.getIntValue()]);
		channelUserVO.setFromTime((String) objects[NumberConstants.N15.getIntValue()]);
		channelUserVO.setToTime((String) objects[NumberConstants.N16.getIntValue()]);
		channelUserVO.setFirstname((String) objects[NumberConstants.N17.getIntValue()]);
		channelUserVO.setLastname((String) objects[NumberConstants.N18.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N19.getIntValue()])) {
			channelUserVO.setLastLoginOn(
					BTSLUtil.getTimestampFromUtilDate((Date) objects[NumberConstants.N19.getIntValue()]));
		}
		channelUserVO.setEmployeeCode((String) objects[NumberConstants.N20.getIntValue()]);
		channelUserVO.setStatus((String) objects[NumberConstants.N21.getIntValue()]);
		channelUserVO.setEmail((String) objects[NumberConstants.N22.getIntValue()]);
	}

	private void getChannelUserVO30(ChannelUserVO channelUserVO, Object[] objects) {
		String userID = (String) objects[NumberConstants.ZERO.getIntValue()];
		channelUserVO.setCreatedBy(userID);
		channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate((Date) objects[NumberConstants.N24.getIntValue()]));
		channelUserVO.setModifiedBy(userID);
		channelUserVO
				.setModifiedOn(BTSLUtil.getTimestampFromUtilDate((Date) objects[NumberConstants.N26.getIntValue()]));
		channelUserVO.setContactPerson((String) objects[NumberConstants.N28.getIntValue()]);
		channelUserVO.setContactNo((String) objects[NumberConstants.N20.getIntValue()]);
		channelUserVO.setDesignation((String) objects[NumberConstants.N30.getIntValue()]);
		channelUserVO.setDivision((String) objects[NumberConstants.N31.getIntValue()]);
		channelUserVO.setDepartment((String) objects[NumberConstants.N32.getIntValue()]);
		channelUserVO.setUserType((String) objects[NumberConstants.N34.getIntValue()]);
		channelUserVO.setInSuspend((String) objects[NumberConstants.N35.getIntValue()]);
		channelUserVO.setOutSuspened((String) objects[NumberConstants.N36.getIntValue()]);
		channelUserVO.setAddress1((String) objects[NumberConstants.N37.getIntValue()]);
		channelUserVO.setAddress2((String) objects[NumberConstants.N38.getIntValue()]);
		channelUserVO.setCity((String) objects[NumberConstants.N39.getIntValue()]);
	}

	private void getChannelUserVO40(ChannelUserVO channelUserVO, Object[] objects) {
		channelUserVO.setState((String) objects[NumberConstants.N40.getIntValue()]);
		channelUserVO.setCountry((String) objects[NumberConstants.N41.getIntValue()]);
		channelUserVO.setSsn((String) objects[NumberConstants.N42.getIntValue()]);
		channelUserVO.setUserNamePrefix((String) objects[NumberConstants.N43.getIntValue()]);
		channelUserVO.setExternalCode((String) objects[NumberConstants.N44.getIntValue()]);
		channelUserVO.setUserCode((String) objects[NumberConstants.N45.getIntValue()]);
		channelUserVO.setShortName((String) objects[NumberConstants.N46.getIntValue()]);
		channelUserVO.setReferenceId((String) objects[NumberConstants.N47.getIntValue()]);
		channelUserVO.setInvalidPasswordCount((Long) objects[NumberConstants.N48.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N49.getIntValue()])) {
			channelUserVO.setPasswordCountUpdatedOn(
					BTSLUtil.getTimestampFromUtilDate((Date) objects[NumberConstants.N49.getIntValue()]));
		}
		channelUserVO.setNetworkstatus((String) objects[NumberConstants.N51.getIntValue()]);
	}

	private void getChannelUserVO50(ChannelUserVO channelUserVO, Object[] objects) {
		channelUserVO.setDomainID((String) objects[NumberConstants.N56.getIntValue()]);
		channelUserVO.setCommissionProfileSetID((String) objects[NumberConstants.N81.getIntValue()]);
		channelUserVO.setTransferProfileID((String) objects[NumberConstants.N82.getIntValue()]);
		channelUserVO.setUserGrade((String) objects[NumberConstants.N83.getIntValue()]);
		channelUserVO.setDomainName((String) objects[NumberConstants.N86.getIntValue()]);
		channelUserVO.setDomainStatus((String) objects[NumberConstants.N87.getIntValue()]);
		channelUserVO.setDomainTypeCode((String) objects[NumberConstants.N88.getIntValue()]);
		channelUserVO.setSmsPin((String) objects[NumberConstants.N89.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N90.getIntValue()])) {
			channelUserVO.setPrefixId(Long.valueOf((String) objects[NumberConstants.N90.getIntValue()]));
		}
		channelUserVO.setPinRequired((String) objects[NumberConstants.N91.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N92.getIntValue()])) {
			channelUserVO.setInvalidPinCount((int) objects[NumberConstants.N92.getIntValue()]);
		}

		channelUserVO.setRestrictedMsisdnAllow((String) objects[NumberConstants.N93.getIntValue()]);
		channelUserVO.setPinReset((String) objects[NumberConstants.N94.getIntValue()]);
		channelUserVO.setApplicationID((String) objects[NumberConstants.N95.getIntValue()]);
	}

	private void getChannelUserVO60(ChannelUserVO channelUserVO, Object[] objects) {
		channelUserVO.setMpayProfileID((String) objects[NumberConstants.N96.getIntValue()]);
		channelUserVO.setUserProfileID((String) objects[NumberConstants.N97.getIntValue()]);
		channelUserVO.setMcommerceServiceAllow((String) objects[NumberConstants.N98.getIntValue()]);
		channelUserVO.setAccessType((String) objects[NumberConstants.N99.getIntValue()]);
		channelUserVO.setPswdReset((String) objects[NumberConstants.N100.getIntValue()]);
		channelUserVO.setPhoneProfile((String) objects[NumberConstants.N101.getIntValue()]);
		channelUserVO.setRsaflag((String) objects[NumberConstants.N102.getIntValue()]);
		channelUserVO.setAuthenticationAllowed((String) objects[NumberConstants.N104.getIntValue()]);
		channelUserVO.setControlGroup((String) objects[NumberConstants.N106.getIntValue()]);
	}

	private void getGeographicalDomainTypes(ChannelUserVO channelUserVO, Object[] objects) {
		GeographicalDomainTypes geographicalDomainTypes = new GeographicalDomainTypes();
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N84.getIntValue()])) {
			geographicalDomainTypes.setSequenceNo((Long) objects[NumberConstants.N84.getIntValue()]);
		}
		geographicalDomainTypes.setGrphDomainTypeName((String) objects[NumberConstants.N85.getIntValue()]);
		channelUserVO.setGeographicalDomainTypes(geographicalDomainTypes);
	}

	private void getCategory10(Categories categoryVO, ChannelUserVO channelUserVO, Object[] objects) {
		categoryVO.setCategoryCode((String) objects[NumberConstants.N54.getIntValue()]);
		categoryVO.setCategoryName((String) objects[NumberConstants.N55.getIntValue()]);
		categoryVO.setDomainCode((String) objects[NumberConstants.N56.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N57.getIntValue()])) {
			categoryVO.setSequenceNo((Integer) objects[NumberConstants.N57.getIntValue()]);
		}

		categoryVO.setMultipleLoginAllowed((String) objects[NumberConstants.N58.getIntValue()]);
		categoryVO.setMaxLoginCount((Long) objects[NumberConstants.N59.getIntValue()]);
		categoryVO.setViewOnNetworkBlock((String) objects[NumberConstants.N60.getIntValue()]);
		categoryVO.setStatus((String) objects[NumberConstants.N61.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N62.getIntValue()])) {
			categoryVO.setMaxTxnMsisdn((Long) objects[NumberConstants.N62.getIntValue()]);
		}
		categoryVO.setUncntrlTransferAllowed((String) objects[NumberConstants.N63.getIntValue()]);
		categoryVO.setScheduledTransferAllowed((String) objects[NumberConstants.N64.getIntValue()]);
		categoryVO.setRestrictedMsisdns((String) objects[NumberConstants.N65.getIntValue()]);
	}

	@Override
	public ChannelUserVO loadAllUserDetailsByLoginID(String ploginID) {
		final String METHOD_NAME = "loadUserDetails";
		StringBuilder loggerValue = new StringBuilder();
		if (LOGGER.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: ploginID=");
			loggerValue.append(ploginID);
			LOGGER.debug(METHOD_NAME, loggerValue);
		}
		boolean isTrfRuleUserLevelAllow = Boolean.parseBoolean(vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.IS_TRF_RULE_USER_LEVEL_ALLOW.getType()));
		boolean lmsAppl = Boolean.parseBoolean(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.LMS_APPL.getType()));
		boolean optInOutAllow = Boolean.parseBoolean(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.OPT_IN_OUT_ALLOW.getType()));
		ChannelUserVO channelUserVO = new ChannelUserVO();
		Query query;
		try {
			StringBuilder sqlQuery = new StringBuilder(
					" SELECT u.userId, u.userName, u.networkCode,l.networkName,l.reportHeaderName, u.loginId, u.pword, ");
			sqlQuery.append(
					"u.categoryCode, u.parentId, u.company, u.fax, u.ownerId, u.msisdn, u.allowedIp,  u.allowedDays, u.fromTime, u.toTime, u.firstname, u.lastname, ");
			sqlQuery.append(
					"u.lastLoginOn, u.employeeCode, u.status AS userstatus, u.email, u.createdBy, u.createdOn, u.modifiedBy, "); // 27
			sqlQuery.append(
					"u.modifiedOn, u.pswdModifiedOn,  cusers.contactPerson, u.contactNo, u.designation, u.division, u.department, "); // 34
			sqlQuery.append(
					"u.msisdn, u.userType, cusers.inSuspend, cusers.outSuspend, u.address1, u.address2, u.city, u.state, u.country, "); // 43
			sqlQuery.append(
					"u.ssn, u.userNamePrefix, u.externalCode, u.userCode, u.shortName, u.referenceId, u.invalidPasswordCount, u.passwordCountUpdatedOn, u.appointmentDate, "); // 52
			sqlQuery.append(
					"l.status AS networkstatus, l.language1Message, l.language2Message, cat.categoryCode, cat.categoryName, "); // 57
			sqlQuery.append(
					"cat.domainCode, cat.sequenceNo, cat.multipleLoginAllowed, cat.maxLoginCount, cat.viewOnNetworkBlock, "); // 62
			sqlQuery.append(
					"cat.status AS catstatus, cat.maxTxnMsisdn, cat.uncntrlTransferAllowed, cat.scheduledTransferAllowed, cat.restrictedMsisdns, "); // 67
			sqlQuery.append(
					"cat.parentCategoryCode, cat.productTypesAllowed, cat.categoryType, cat.hierarchyAllowed, cat.transfertolistonly, "); // 72
			sqlQuery.append(
					"cat.grphDomainType, cat.multipleGrphDomains, cat.fixedRoles, cat.userIdPrefix, cat.webInterfaceAllowed,"); // 77
			sqlQuery.append(
					"cat.serviceAllowed, cat.domainAllowed, cat.fixedDomains, cat.outletsAllowed, cat.status AS categorystatus, cusers.commProfileSetId, cusers.transferProfileId, cusers.userGrade, gdt.sequenceNo AS grphSequenceNo, "); // 86
			sqlQuery.append(
					"gdt.grphDomainTypeName, dm.domainName, dm.status AS domainstatus, dm.domainTypeCode, up.smsPin, up.prefixId, up.pinRequired, up.invalidPinCount, dt.restrictedMsisdn AS restrictedMsisdnAllow, up.pinReset"); // 96
			sqlQuery.append(
					", cusers.applicationId, cusers.mpayProfileId, cusers.userProfileId, cusers.mcommerceServiceAllow, up.accessType"); // 101
			sqlQuery.append(
					", u.pswdReset, up.phoneProfile , u.rsaflag, cat.smsInterfaceAllowed, u.authenticationAllowed, cat.authenticationType, cusers.controlGroup"); // 108

			if (isTrfRuleUserLevelAllow) {
				sqlQuery.append(", cusers.trfRuleType");
			}
			if (lmsAppl) {
				sqlQuery.append(", cusers.lmsProfile");
			}
			if (optInOutAllow) {
				sqlQuery.append(", cusers.optInOutStatus");
			}
			sqlQuery.append("FROM Users u LEFT JOIN ChannelUsers cusers ON cusers.userId=u.userId");
			sqlQuery.append("LEFT JOIN Networks l on l.networkCode=u.networkCode");
			sqlQuery.append("LEFT JOIN UserPhones up ON (up.userId=u.userId AND up.msisdn=u.msisdn),");
			sqlQuery.append("Categories cat, GeographicalDomainTypes gdt, Domains dm, DomainTypes dt");
			sqlQuery.append("WHERE  UPPER(u.loginId)=:loginId AND gdt.grphDomainType = cat.grph_domain_type");
			sqlQuery.append("AND u.status <>:statusDeleted AND u.status <>:statusCanceled");
			sqlQuery.append(
					"AND cat.categoryCode=U.categoryCode AND cat.status <>:statusDel AND dm.domainCode = cat.domainCode ");
			sqlQuery.append("AND dt.domainTypeCode = dm.domainTypeCode");
			query = entityManager.createQuery(sqlQuery.toString());
			query.setParameter("loginId", ploginID.toUpperCase());
			query.setParameter("statusDeleted", PretupsI.USER_STATUS_DELETED);
			query.setParameter("statusCanceled", PretupsI.USER_STATUS_DELETED);
			query.setParameter("statusDel", PretupsI.STATUS_DELETE);
			List<Object[]> userlist = query.getResultList();
			if (!CommonUtils.isNullorEmpty(userlist)) {
				for (Object[] objects : userlist) {
					channelUserVO = getChannelUserVoObj();
					//channelUserVO = new ChannelUserVO();
					Categories categoryVO = getCategoriesObj();
					//Categories categoryVO = new Categories();
					getChannelUserVO10(channelUserVO, objects, ploginID);
					getChannelUserVO20(channelUserVO, objects);
					getChannelUserVO30(channelUserVO, objects);
					getChannelUserVO40(channelUserVO, objects);
					getChannelUserVO50(channelUserVO, objects);
					getChannelUserVO60(channelUserVO, objects);
					getGeographicalDomainTypes(channelUserVO, objects);
					getCategory10(categoryVO, channelUserVO, objects);
				}
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

		return null;
	}

	private Categories getCategoriesObj() {
		Categories categoryVO = new Categories();
		return categoryVO;
	}

	private ChannelUserVO getChannelUserVoObj() {
		ChannelUserVO channelUserVO = new ChannelUserVO();
		return channelUserVO;
	}

	private void getUserdata(ChannelUserVO channelUserVO) {
		try {
			Query query1 = entityManager
					.createQuery("SELECT userId,userName FROM Users WHERE userId IN (:parentId,:owenerId)");
			query1.setParameter("parentId", channelUserVO.getParentId());
			query1.setParameter("owenerId", channelUserVO.getOwnerId());
			//List<Object[]> userlist1 = query1.getResultList();
			List<Object[]> userlist1 = getUserDataObjFromDB(query1);
			if (!CommonUtils.isNullorEmpty(userlist1)) {
				String uid;
				for (Object[] obj : userlist1) {
					uid = (String) obj[NumberConstants.ZERO.getIntValue()];
					if (PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentId())) {
						channelUserVO.setParentName(PretupsI.ROOT_PARENT_ID);
					} else if (PretupsI.SYSTEM.equals(channelUserVO.getParentId())) {
						channelUserVO.setParentName(PretupsI.SYSTEM);
					} else if (channelUserVO.getParentId().equals(uid)) {
						channelUserVO.setParentName((String) obj[NumberConstants.ONE.getIntValue()]);
					}
					if (channelUserVO.getOwnerId().equals(uid)) {
						channelUserVO.setOwnerName((String) obj[NumberConstants.ONE.getIntValue()]);
					}
				}
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getUserDataObjFromDB(Query query1) {
		List<Object[]> userlist1 = query1.getResultList();
		return userlist1;
	}

	/**
	 * Check for existance of fixed role
	 *
	 *
	 * @param pcategoryCode
	 * @param proleCode
	 * @param pdomainType
	 * @return boolean
	 */
	@Override
	public boolean isFixedRoleAndExist(String pcategoryCode, String proleCode, String pdomainType) {
		final String methodName = "isFixedRoleAndExist";
		StringBuilder loggerValue = new StringBuilder();
		if (LOGGER.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: pcategoryCode=");
			loggerValue.append(pcategoryCode);
			loggerValue.append("pdomainType=");
			loggerValue.append(pdomainType);
			LOGGER.debug(methodName, loggerValue);
		}
		boolean roleStatus = false;
		Query query;
		try {
			StringBuilder sqlQuery = new StringBuilder(
					"SELECT 1 FROM CategoryRoles CR, Roles R WHERE CR.categoryCode=:categoryCode AND CR.roleCode=:roleCode");
			sqlQuery.append(
					" AND R.domainType=:domainType AND CR.roleCode=R.roleCode AND (R.status IS NULL OR R.status='Y')");
			if (LOGGER.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("query");
				loggerValue.append(sqlQuery.toString());
				LOGGER.debug(methodName, loggerValue);
			}
			query = entityManager.createQuery(sqlQuery.toString());
			query.setParameter("categoryCode", pcategoryCode);
			query.setParameter("roleCode", proleCode);
			query.setParameter("domainType", pdomainType);
			List<Object[]> catRoles = query.getResultList();
			if (!CommonUtils.isNullorEmpty(catRoles)) {
				roleStatus = true;
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return roleStatus;
	}

	/**
	 * Check for existance of Assigned role
	 *
	 * @param con
	 * @param userId
	 * @param proleCode
	 * @param pdomainType
	 * @return boolean
	 */
	@Override
	public boolean isAssignedRoleAndExist(String puserID, String proleCode, String pdomainType) {
		final String methodName = "isAssignedRoleAndExist";
		StringBuilder loggerValue = new StringBuilder();
		if (LOGGER.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: puserID=");
			loggerValue.append(puserID);
			loggerValue.append("proleCode=");
			loggerValue.append(proleCode);
			loggerValue.append("pdomainType=");
			loggerValue.append(pdomainType);
			LOGGER.debug(methodName, loggerValue);
		}
		boolean roleStatus = false;
		Query query;
		Query query1;
		try {
			StringBuilder sqlQuery = new StringBuilder("SELECT 1 FROM UserRoles UR, Roles R");
			sqlQuery.append("WHERE UR.userId=:puserID AND UR.roleCode=R.roleCode AND R.domainType=:domainType ");
			sqlQuery.append("AND R.groupRole='Y'AND (R.status IS NULL OR R.status='Y')");
			query = entityManager.createQuery(sqlQuery.toString());
			query.setParameter("puserID", puserID);
			query.setParameter("domainType", pdomainType);
			Object catRoles = query.getSingleResult();
			if (!CommonUtils.isNullorEmpty(catRoles)) {
				StringBuilder sqlQuery1 = new StringBuilder(
						"SELECT 1 FROM UserRoles UR, Roles R, GroupRoles GR, Roles R1 WHERE UR.userId=:puserID AND R1.roleCode=:proleCode AND R.domainType=:domainType ");
				sqlQuery1.append(
						"AND UR.roleCode=R.roleCode AND R.groupRole='Y' AND (R.status IS NULL OR R.status='Y') AND R.roleCode=GR.groupRoleCode AND GR.roleCode=R1.roleCode AND R1.status='Y' AND R1.domainType=:domainType");
				query1 = entityManager.createQuery(sqlQuery1.toString());
			} else {
				StringBuilder sqlQuery2 = new StringBuilder(
						"SELECT 1 FROM Roles R, UserRoles UR WHERE UR.userId=:puserID AND UR.roleCode=:proleCode ");
				sqlQuery2.append(
						"AND R.domainType=:domainType AND R.roleCode=UR.roleCode AND (R.status IS NULL OR R.status='Y')");
				query1 = entityManager.createQuery(sqlQuery2.toString());
			}
			query1.setParameter("puserID", puserID);
			query1.setParameter("domainType", pdomainType);
			query1.setParameter("roleCode", proleCode);
			Object catRoles1 = query.getSingleResult();
			if (!CommonUtils.isNullorEmpty(catRoles1)) {
				roleStatus = true;
			}

		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

		return roleStatus;

	}

	@Transactional
	@Override
	public int updateUserLoginDetails(String userId) {
		Query query;
		int updateCount = 0;
		try {

			System.out.println("userId >>>>>>>>>>>>>>>>>>>>>>. "+userId);
			try {
				System.out.println("userId >>>>>>>>>>>>>>>>>>>>>>. "+CommonUtils.getTimestampFromUtilDate(new Date()));
			}catch(Exception e) {
				e.printStackTrace();
			}
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("UPDATE Users SET lastLoginOn=:lastLoginOn WHERE userId=:userId");
			query = entityManager.createQuery(sqlQuery.toString());
			query.setParameter("lastLoginOn", CommonUtils.getTimestampFromUtilDate(new Date()));
			query.setParameter("userId", userId);
			//updateCount = query.executeUpdate();
		} catch (PersistenceException e) {
			e.printStackTrace();
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return updateCount;
	}

	@Override
	public String isUserGroupRoleSupended(String userId) {
		final String methodName = "isUserGroupRoleSuspended";
		StringBuilder loggerValue = new StringBuilder();
		if (LOGGER.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: puserId=");
			loggerValue.append(userId);
			LOGGER.debug(methodName, loggerValue);
		}
		String roleStatus = Constants.NO.getStrValue(); // Assuming initially not suspended
		Query query;
		try {
			StringBuilder sqlQuery = new StringBuilder(
					"SELECT DISTINCT R.status FROM Roles R, GroupRoles GR, UserRoles UR  WHERE R.roleCode=GR.groupRoleCode AND R.roleCode = UR.roleCode ");
			sqlQuery.append("AND R.status ='S' AND UR.userId =:userId ");
			if (LOGGER.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("query");
				loggerValue.append(sqlQuery.toString());
				LOGGER.debug(methodName, loggerValue);
			}
			query = entityManager.createQuery(sqlQuery.toString());
			query.setParameter("userId", userId);
			List<String> roleStatusList = query.getResultList();
			if (!CommonUtils.isNullorEmpty(roleStatusList) && roleStatusList.contains(Constants.SUSPEND.getStrValue())) {
				roleStatus = Constants.YES.getStrValue();
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at" + methodName, e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return roleStatus;
	}


}
