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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.btsl.user.businesslogic.entity.Categories;
import com.btsl.user.businesslogic.entity.Users;
import com.btsl.util.BTSLUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.text.MessageFormat;
import java.util.*;

/**
 * Data base operations for ChannelUsers.
 * 
 * @author VENKATESAN.S
 * @date : 20-DEC-2019
 */
@Component
public class LoginUsersRespositoryImpl implements LoginUsersRespository {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginUsersRespositoryImpl.class);

	/** The entity manager. */
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private VMSCacheRepository vmsCacheRepository;

	private boolean channelSosEnable;
	private boolean isTrfRuleUserLevelAllow;
	private boolean lmsAppl;
	private boolean optInOutAllow;
	private String allowedUsrTypCreation;

	/**
	 * Get the ChannelUser based on userLoginID and userPassword.
	 *
	 * @param userLoginID  - request
	 * @param userPassword - request
	 * 
	 * @return ChannelUser
	 */

	@SuppressWarnings("unchecked")
	@Override
	public ChannelUserVO getloadUserDetails(String identifierType, String userLoginID) {
		ChannelUserVO channelUser = new ChannelUserVO();
		Query query;
		try {

			channelSosEnable = Boolean.parseBoolean(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.CHANNEL_SOS_ENABLE.getType()));
			isTrfRuleUserLevelAllow = Boolean.parseBoolean(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.IS_TRF_RULE_USER_LEVEL_ALLOW.getType()));
			lmsAppl = Boolean.parseBoolean(
					vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.LMS_APPL.getType()));
			optInOutAllow = Boolean.parseBoolean(
					vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.OPT_IN_OUT_ALLOW.getType()));
			allowedUsrTypCreation = vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.ALLOWD_USR_TYP_CREATION.getType());
			StringBuilder sqlQuery = new StringBuilder(
					"SELECT u.userId, u.userName, u.networkCode, n.networkName, n.reportHeaderName, u.loginId, u.pword, ");
			sqlQuery.append(
					"u.categoryCode, u.parentId, u.company, u.fax, u.ownerId, u.msisdn, u.allowedIp, u.allowedDays, u.fromTime, u.toTime, u.firstname, u.lastname, ");
			sqlQuery.append(
					"u.lastLoginOn, u.employeeCode, u.status AS userstatus, u.email, u.createdBy, u.createdOn, u.modifiedBy, u.modifiedOn, u.pswdModifiedOn, ");
			sqlQuery.append(
					"cu.contactPerson, u.contactNo, u.designation, u.division, u.department, u.msisdn, u.userType, cu.inSuspend, cu.outSuspend, u.address1, ");
			sqlQuery.append(
					"u.address2, u.city, u.state, u.country, u.ssn, u.userNamePrefix, u.externalCode, u.userCode, u.shortName, u.referenceId, u.invalidPasswordCount, ");
			sqlQuery.append(
					"u.passwordCountUpdatedOn, u.paymentType, n.status AS networkstatus, n.language1Message, n.language2Message, cat.categoryCode, cat.categoryName, ");
			sqlQuery.append(
					"cat.domainCode, cat.sequenceNo, cat.multipleLoginAllowed, cat.maxLoginCount, cat.viewOnNetworkBlock, cat.status AS catstatus, cat.maxTxnMsisdn, ");
			sqlQuery.append(
					"cat.uncntrlTransferAllowed, cat.scheduledTransferAllowed, cat.restrictedMsisdns, cat.parentCategoryCode, cat.productTypesAllowed, ");
			sqlQuery.append(
					"cat.categoryType, cat.hierarchyAllowed, cat.transfertolistonly, cat.grphDomainType, cat.multipleGrphDomains, cat.fixedRoles, ");
			sqlQuery.append(
					"cat.userIdPrefix, cat.webInterfaceAllowed, cat.serviceAllowed, cat.domainAllowed, cat.fixedDomains, cat.outletsAllowed, ");
			sqlQuery.append(
					"cat.status AS categorystatus, cu.commProfileSetId, cu.transferProfileId, cu.userGrade, gdt.sequenceNo AS grphSequenceNo, gdt.grphDomainTypeName, ");
			sqlQuery.append(
					"dm.domainName, dm.status AS domainstatus, dm.domainTypeCode, up.smsPin, up.prefixId, up.pinRequired, up.invalidPinCount, dt.restrictedMsisdn AS restrictedMsisdnAllow, ");
			sqlQuery.append(
					"up.pinReset, cu.applicationId, cu.mpayProfileId, cu.userProfileId, cu.mcommerceServiceAllow, up.accessType, ");
			sqlQuery.append(
					"u.pswdReset, up.phoneProfile, u.rsaflag, cat.smsInterfaceAllowed, u.authenticationAllowed, cat.authenticationType, cu.controlGroup ");
			if (isTrfRuleUserLevelAllow) {
				sqlQuery.append(", cu.trfRuleType ");
			}
			if (lmsAppl) {
				sqlQuery.append(", cu.lmsProfile ");
			}
			if (optInOutAllow) {
				sqlQuery.append(", cu.optInOutStatus ");
			}
			if (allowedUsrTypCreation.equals(Constants.YES.getStrValue())) {
				sqlQuery.append(", u.allowdUsrTypCreation ");
			}

			if (channelSosEnable) {
				sqlQuery.append(", cu.sosAllowed, cu.sosAllowedAmount, cu.sosThresholdLimit ");
			}
			sqlQuery.append("FROM Users u LEFT JOIN Networks n ON n.networkCode=u.networkCode ");
			sqlQuery.append(" LEFT JOIN ChannelUsers cu ON cu.userId=u.userId ");

			//sqlQuery.append(" LEFT OUTER JOIN UserPhones up ON up.userId=u.userId ");

			sqlQuery.append(
					" LEFT OUTER JOIN UserPhones up ON up.msisdn=u.msisdn, Categories cat, GeographicalDomainTypes gdt, Domains dm, DomainTypes dt ");


			sqlQuery.append(" WHERE ");
			appendIdentifierTypeInQuery(identifierType, sqlQuery);
			sqlQuery.append(" AND gdt.grphDomainType=cat.grphDomainType AND u.status <>:userStatus1 "
					+ " AND u.status <>:userStatus2 ");
			sqlQuery.append(" AND cat.categoryCode=u.categoryCode " + " AND cat.status <>:catStatus ");
			sqlQuery.append(" AND dm.domainCode=cat.domainCode " + " AND dt.domainTypeCode=dm.domainTypeCode");
			query = entityManager.createQuery(sqlQuery.toString());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(MessageFormat.format("getloadUserDetails, QUERY query={0}", query));
			}
			query.setParameter("userLoginID", userLoginID.toUpperCase(Locale.ENGLISH));
			query.setParameter("userStatus1", Constants.USER_STATUS_DELETED.getStrValue());
			query.setParameter("userStatus2", Constants.USER_STATUS_CANCELED.getStrValue());
			query.setParameter("catStatus", Constants.INACTIVE_STATUS.getStrValue());
			List<Object[]> channelUserobj = query.getResultList();
			if (!CommonUtils.isNullorEmpty(channelUserobj)) {
				for (Object[] obj : channelUserobj) {
					channelUser = constructChannelUserVOService(obj);
				}
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at getloadUserDetails {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return channelUser;

	}

	private void appendIdentifierTypeInQuery(String identifierType, StringBuilder sqlQuery) {
		if (identifierType.equalsIgnoreCase(Constants.IDENTIFIER_TYPE_LOGINID.getStrValue())) {
			sqlQuery.append(" UPPER(u.loginId)=:userLoginID ");
		} else if (identifierType.equalsIgnoreCase(Constants.IDENTIFIER_TYPE_MSISDN.getStrValue())) {
			sqlQuery.append(" u.msisdn=:userLoginID ");
		}else if(identifierType.equalsIgnoreCase(Constants.IDENTIFIER_TYPE_EXTGW.getStrValue())) {
			sqlQuery.append(" u.externalCode=:userLoginID ");
		}
	}

	/**
	 * Construct the channel user data
	 */
	private ChannelUserVO constructChannelUserVOService(Object[] objects) {
		ChannelUserVO channelUser = new ChannelUserVO();

		LoginUserRepositoryHelper.populateChannelUserDetails(channelUser, objects);

		if (CommonUtils.isNullorEmpty(objects[NumberConstants.N48.getIntValue()])) {
			channelUser.setInvalidPasswordCount(0L);
		} else {
			channelUser.setInvalidPasswordCount((Long) objects[NumberConstants.N48.getIntValue()]);
		}

		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N49.getIntValue()])) {
			channelUser.setPasswordCountUpdatedOn(
					CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N49.getIntValue()]));
		}
		channelUser.setPaymentType((String) objects[NumberConstants.N50.getIntValue()]);
		channelUser.setNetworkstatus((String) objects[NumberConstants.N51.getIntValue()]);
		channelUser.setDomainID((String) objects[NumberConstants.N56.getIntValue()]);
		channelUser.setCommissionProfileSetID((String) objects[NumberConstants.N81.getIntValue()]);
		channelUser.setTransferProfileID((String) objects[NumberConstants.N82.getIntValue()]);
		channelUser.setUserGrade((String) objects[NumberConstants.N83.getIntValue()]);
		channelUser.setDomainName((String) objects[NumberConstants.N86.getIntValue()]);
		channelUser.setDomainStatus((String) objects[NumberConstants.N87.getIntValue()]);
		channelUser.setDomainTypeCode((String) objects[NumberConstants.N88.getIntValue()]);
		channelUser.setSmsPin((String) objects[NumberConstants.N89.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N90.getIntValue()])) {
			channelUser.setPrefixId((Long) objects[NumberConstants.N90.getIntValue()]);
		}
		channelUser.setPinRequired((String) objects[NumberConstants.N91.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N92.getIntValue()])) {
			channelUser.setInvalidPinCount(((Long) objects[NumberConstants.N92.getIntValue()]).intValue());
		}
		channelUser.setRestrictedMsisdnAllow((String) objects[NumberConstants.N93.getIntValue()]);
		channelUser.setPinReset((String) objects[NumberConstants.N94.getIntValue()]);
		channelUser.setApplicationID((String) objects[NumberConstants.N95.getIntValue()]);
		channelUser.setMpayProfileID((String) objects[NumberConstants.N96.getIntValue()]);
		channelUser.setUserProfileID((String) objects[NumberConstants.N97.getIntValue()]);
		channelUser.setMcommerceServiceAllow((String) objects[NumberConstants.N98.getIntValue()]);
		channelUser.setAccessType((String) objects[NumberConstants.N99.getIntValue()]);
		channelUser.setPswdReset((String) objects[NumberConstants.N100.getIntValue()]);
		channelUser.setPhoneProfile((String) objects[NumberConstants.N101.getIntValue()]);
		channelUser.setRsaflag((String) objects[NumberConstants.N102.getIntValue()]);
		channelUser.setAuthenticationAllowed((String) objects[NumberConstants.N104.getIntValue()]);
		channelUser.setControlGroup((String) objects[NumberConstants.N106.getIntValue()]);
		channelUser.setNetworkId((String) objects[NumberConstants.TWO.getIntValue()]);
		channelUser.setCategories(getCategoriesDetails(objects));
		int i = NumberConstants.N107.getIntValue();
		if (isTrfRuleUserLevelAllow) {
			channelUser.setTrannferRuleTypeId((String) objects[i]);
		}

		if (lmsAppl) {
			if (isTrfRuleUserLevelAllow) {
				channelUser.setLmsProfile((String) objects[i + 1]);
			} else {
				channelUser.setLmsProfile((String) objects[i]);
			}
		}
		checkoptInOutAllow(channelUser, objects, i);
		constructParentUserData(channelUser);
		return channelUser;
	}

	private void checkoptInOutAllow(ChannelUserVO channelUser, Object[] objects, int i) {

		if (optInOutAllow) {
			if (isTrfRuleUserLevelAllow && !lmsAppl) {
				channelUser.setOptInOutStatus((String) objects[i + 1]);
			} else if (!isTrfRuleUserLevelAllow && lmsAppl) {
				channelUser.setOptInOutStatus((String) objects[i + 1]);
			} else if (isTrfRuleUserLevelAllow) {
				channelUser.setOptInOutStatus((String) objects[i + NumberConstants.TWO.getIntValue()]);
			} else {
				channelUser.setOptInOutStatus((String) objects[i]);
			}
		}

		if (allowedUsrTypCreation.equals(Constants.YES.getStrValue())) {
			allowUserTypeCreationYes(channelUser, isTrfRuleUserLevelAllow, lmsAppl, optInOutAllow, objects, i);
		}
		if (channelSosEnable) {
			channelSosEnable(channelUser, objects, i);
		}

	}

	private static void allowUserTypeCreationYes(ChannelUserVO channelUser, Boolean isTrfRuleUserLevelAllow,
			Boolean lmsAppl, Boolean optInOutAllow, Object[] objects, int i) {
		if (isTrfRuleUserLevelAllow && !lmsAppl && !optInOutAllow) {
			channelUser.setAllowdUsrTypCreation((String) objects[i + 1]);
		} else if (isTrfRuleUserLevelAllow && lmsAppl && !optInOutAllow) {
			channelUser.setAllowdUsrTypCreation((String) objects[i + NumberConstants.TWO.getIntValue()]);
		} else if (isTrfRuleUserLevelAllow) {
			channelUser.setAllowdUsrTypCreation((String) objects[i + NumberConstants.THREE.getIntValue()]);
		} else {
			channelUser.setAllowdUsrTypCreation((String) objects[i]);
		}

	}

	private void channelSosEnable(ChannelUserVO channelUser, Object[] objects, int i) {
		channelsSOSEnableOne(channelUser, objects, i);
		channelsSOSEnableTwo(channelUser, objects, i);
		if (isTrfRuleUserLevelAllow && lmsAppl && optInOutAllow
				&& allowedUsrTypCreation.equals(Constants.YES.getStrValue())) {
			channelUser.setSosAllowed((String) objects[i + NumberConstants.FOUR.getIntValue()]);
			channelUser.setSosAllowedAmount((Long) objects[i + NumberConstants.FIVE.getIntValue()]);
			channelUser.setSosThresholdLimit((Long) objects[i + NumberConstants.SIX.getIntValue()]);
		} else {
			int indexToSkip = 0;
			if(isTrfRuleUserLevelAllow)indexToSkip++;
			if(lmsAppl)indexToSkip++;
			if(optInOutAllow)indexToSkip++;
			if(allowedUsrTypCreation.equals(Constants.YES.getStrValue()))indexToSkip++;
			channelUser.setSosAllowed((String) objects[i + indexToSkip]);
			//channelUser.setSosAllowedAmount((Long) objects[i + 1]);
			channelUser.setSosThresholdLimit((Long) objects[i + indexToSkip + NumberConstants.TWO.getIntValue()]);
		}
	}

	private void channelsSOSEnableOne(ChannelUserVO channelUser, Object[] objects, int i) {

		if (isTrfRuleUserLevelAllow && !lmsAppl && !optInOutAllow
				&& !(allowedUsrTypCreation.equals(Constants.YES.getStrValue()))) {
			channelUser.setSosAllowed((String) objects[i + 1]);
			channelUser.setSosAllowedAmount((Long) objects[i + NumberConstants.TWO.getIntValue()]);
			channelUser.setSosThresholdLimit((Long) objects[i + NumberConstants.THREE.getIntValue()]);
		} else if (isTrfRuleUserLevelAllow && lmsAppl && !optInOutAllow
				&& !(allowedUsrTypCreation.equals(Constants.YES.getStrValue()))) {
			channelUser.setSosAllowed((String) objects[i + NumberConstants.TWO.getIntValue()]);
			channelUser.setSosAllowedAmount((Long) objects[i + NumberConstants.THREE.getIntValue()]);
			channelUser.setSosThresholdLimit((Long) objects[i + NumberConstants.FOUR.getIntValue()]);
		}

	}

	private void channelsSOSEnableTwo(ChannelUserVO channelUser, Object[] objects, int i) {
		if (isTrfRuleUserLevelAllow && lmsAppl && optInOutAllow
				&& !(allowedUsrTypCreation.equals(Constants.YES.getStrValue()))) {
			channelUser.setSosAllowed((String) objects[i + NumberConstants.THREE.getIntValue()]);
			channelUser.setSosAllowedAmount((Long) objects[i + NumberConstants.FOUR.getIntValue()]);
			channelUser.setSosThresholdLimit((Long) objects[i + NumberConstants.FIVE.getIntValue()]);
		}

	}

	/**
	 * Construct the Category data
	 */
	private static Categories getCategoriesDetails(Object[] objects) {
		Categories categories = new Categories();
		categories.setCategoryCode((String) objects[NumberConstants.N54.getIntValue()]);
		categories.setCategoryName((String) objects[NumberConstants.N55.getIntValue()]);
		categories.setDomainCode((String) objects[NumberConstants.N56.getIntValue()]);
		categories.setSequenceNo((Integer) objects[NumberConstants.N57.getIntValue()]);
		categories.setMultipleLoginAllowed((String) objects[NumberConstants.N58.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N59.getIntValue()])) {
			categories.setMaxLoginCount((Long) objects[NumberConstants.N59.getIntValue()]);
		}
		categories.setViewOnNetworkBlock((String) objects[NumberConstants.N60.getIntValue()]);
		categories.setStatus((String) objects[NumberConstants.N61.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N62.getIntValue()])) {
			categories.setMaxTxnMsisdn((Long) objects[NumberConstants.N62.getIntValue()]);
		}
		categories.setUncntrlTransferAllowed((String) objects[NumberConstants.N63.getIntValue()]);
		categories.setScheduledTransferAllowed((String) objects[NumberConstants.N64.getIntValue()]);
		categories.setRestrictedMsisdns((String) objects[NumberConstants.N65.getIntValue()]);
		categories.setParentCategoryCode((String) objects[NumberConstants.N66.getIntValue()]);
		categories.setProductTypesAllowed((String) objects[NumberConstants.N67.getIntValue()]);
		categories.setCategoryType((String) objects[NumberConstants.N68.getIntValue()]);
		categories.setHierarchyAllowed((String) objects[NumberConstants.N69.getIntValue()]);
		categories.setTransfertolistonly((String) objects[NumberConstants.N70.getIntValue()]);
		categories.setGrphDomainType((String) objects[NumberConstants.N71.getIntValue()]);
		categories.setMultipleGrphDomains((String) objects[NumberConstants.N72.getIntValue()]);
		categories.setFixedRoles((String) objects[NumberConstants.N73.getIntValue()]);
		categories.setUserIdPrefix((String) objects[NumberConstants.N74.getIntValue()]);
		categories.setWebInterfaceAllowed((String) objects[NumberConstants.N75.getIntValue()]);
		categories.setServiceAllowed((String) objects[NumberConstants.N76.getIntValue()]);
		categories.setDomainAllowed((String) objects[NumberConstants.N77.getIntValue()]);
		categories.setFixedDomains((String) objects[NumberConstants.N78.getIntValue()]);
		categories.setOutletsAllowed((String) objects[NumberConstants.N79.getIntValue()]);
		categories.setSmsInterfaceAllowed((String) objects[NumberConstants.N103.getIntValue()]);
		categories.setAuthenticationType((String) objects[NumberConstants.N105.getIntValue()]);
		return categories;
	}

	/**
	 * Construct the construct Parent User data
	 */
	private void constructParentUserData(ChannelUserVO channelUser) {
		List<String> userIds = Arrays.asList(channelUser.getParentId(), channelUser.getOwnerId());
		List<Users> list = usersRepository.getParentsUserDetails(userIds);
		for (Users user : list) {
			if (Constants.ROOT_PARENT_ID.getStrValue().equals(channelUser.getParentId())) {
				channelUser.setParentName(Constants.ROOT_PARENT_ID.getStrValue());
			} else if (Constants.SYSTEM.getStrValue().equals(channelUser.getParentId())) {
				channelUser.setParentName(Constants.SYSTEM.getStrValue());
			} else if (channelUser.getOwnerId().equals(user.getUserId())) {
				channelUser.setOwnerName(user.getUserName());
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList loadUserServicesList(String userId) {
		StringBuilder loggerValue = new StringBuilder();
		ArrayList list = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			loggerValue.append("Entered:UserId =");
			loggerValue.append(userId);
			LOGGER.debug(MessageFormat.format("loadUserServicesList {0}", loggerValue));
		}
		try {
			StringBuilder sqlQuery = new StringBuilder(
					"SELECT US.serviceType, US.status FROM UserServices US, Users U, ");
			sqlQuery.append(
					"CategoryServiceType CST WHERE US.userId =:userId AND US.status <>:status AND U.userId=US.userId ");
			sqlQuery.append(
					"AND U.categoryCode=CST.categoryCode AND CST.serviceType=US.serviceType AND CST.networkCode=U.networkCode");
			Query query = entityManager.createQuery(sqlQuery.toString());
			query.setParameter("userId", userId);
			query.setParameter("status", Constants.INACTIVE_STATUS.getStrValue());
			List<Object[]> userServiceList = query.getResultList();
			if (!CommonUtils.isNullorEmpty(userServiceList)) {
				String userSer;
				for (Object[] obj : userServiceList) {
					userSer = (String) obj[NumberConstants.ONE.getIntValue()] + "|"
							+ obj[NumberConstants.ZERO.getIntValue()];
					list.add(userSer);
				}
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at loadUserServicesList {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

		return list;

	}

	/**
	 * Method updatePasswordCounter.
	 * 
	 * @param channelUser channelUser
	 * @return int
	 */
	@Override
	@Transactional
	public int updatePasswordCounter(ChannelUserVO channelUser) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(MessageFormat.format("updatePasswordCounter, Entered p_userVO :{0}", channelUser));
		int updateCount = 0;
		try {			
			StringBuilder sqlQuery = new StringBuilder(
					"UPDATE users SET invalid_password_count =:invalid, password_count_updated_on =:updatedOn, modified_by =:modifiedUser, modified_on =:modifiedOn");
			if (channelUser.getPswdReset() != null) {
				//sqlQuery.append(", pswdReset =:reset ");
				sqlQuery.append(", pswd_reset =:reset ");
			}
			sqlQuery.append("WHERE user_id =:userId");
			Query query = entityManager.createNativeQuery(sqlQuery.toString());
			query.setParameter("invalid", channelUser.getInvalidPasswordCount());
			if (channelUser.getPasswordCountUpdatedOn() != null) {
				query.setParameter("updatedOn",
						CommonUtils.getTimestampFromUtilDate(channelUser.getPasswordCountUpdatedOn()));
			} else {
				query.setParameter("updatedOn", null);
			}

			query.setParameter("modifiedOn", CommonUtils.getTimestampFromUtilDate(channelUser.getModifiedOn()));
			if (channelUser.getPswdReset() != null) {
				query.setParameter("reset", channelUser.getPswdReset());
			}

			if (channelUser.isStaffUser()) {
				if (!CommonUtils.isNullorEmpty(channelUser.getActiveUserID())) {
					query.setParameter("userId", channelUser.getActiveUserID());
					query.setParameter("modifiedUser", channelUser.getActiveUserID());
				} else {
					query.setParameter("userId", channelUser.getUserId());
					query.setParameter("modifiedUser", channelUser.getModifiedBy());
				}
			} else {
				query.setParameter("userId", channelUser.getUserId());
				query.setParameter("modifiedUser", channelUser.getModifiedBy());
			} 
			updateCount = query.executeUpdate();
			
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at updatePasswordCounter {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return updateCount;
	}


	@Override
	public ArrayList<UserBalanceVO> getUserBalance(String userID) {
		ArrayList<UserBalanceVO> list = new ArrayList<UserBalanceVO>();
		Query query;
		try {
			final StringBuilder selectQuery = new StringBuilder();
			selectQuery.append("SELECT ub.balance,ub.prevBalance,p.productShortCode,p.productName,u.msisdn,u.userName,u.address1,p.productCode ");
			selectQuery.append("FROM Users u,UserBalances ub,Products p WHERE ub.productCode=p.productCode ");
			selectQuery.append("AND u.userId=ub.userId AND ub.userId=:userID AND u.status<>'N' AND u.status<>'C' ");
			query = entityManager.createQuery(selectQuery.toString());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(MessageFormat.format("getUserBalance, QUERY query={0}", query));
			}
			query.setParameter("userID", userID.toUpperCase(Locale.ENGLISH));
			List<Object[]> userBalObj = query.getResultList();
			if (!CommonUtils.isNullorEmpty(userBalObj)) {
				for (Object[] objects : userBalObj) {
					
					UserBalanceVO userBalanceVO = new UserBalanceVO();
					userBalanceVO.setBalance(BTSLUtil.getDisplayAmount((long)objects[NumberConstants.ZERO.getIntValue()])+"");
					userBalanceVO.setProductShortCode((Long)objects[NumberConstants.TWO.getIntValue()]);
					userBalanceVO.setProductName((String)objects[NumberConstants.THREE.getIntValue()]);
					userBalanceVO.setProductCode((String)objects[NumberConstants.SEVEN.getIntValue()]);
					list.add(userBalanceVO);
				}
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at getUserBalance {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return list;

	}


}
