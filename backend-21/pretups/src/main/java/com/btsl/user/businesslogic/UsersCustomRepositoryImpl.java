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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.btsl.user.businesslogic.entity.Categories;
import com.btsl.user.businesslogic.entity.Users;


/**
 * Data base operations for Users.
 * 
 * @author VENKATESAN.S
 * @date : 20-DEC-2019
 */
@Component
public class UsersCustomRepositoryImpl implements UsersCustomRepository {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UsersCustomRepositoryImpl.class);

	/** The entity manager. */
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private VMSCacheRepository vmsCacheRepository;

	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String dbDialect;

	/**
	 * Date : DEC 28, 2019 Discription : Method : loadUserDetailsFormUserID
	 * 
	 * @param userID
	 * @return ChannelUser
	 * @author VENKATESANS.S
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChannelUserVO loadUserDetailsFormUserID(String userID) {
		StringBuilder loggerValue = new StringBuilder();
		if (LOGGER.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: userID=");
			loggerValue.append(userID);
			LOGGER.debug(MessageFormat.format("loadUserDetailsFormUserID {0}", loggerValue));
		}
		ChannelUserVO channelUser = new ChannelUserVO();
		Query query;
		try {
			String nativeSqlQuery = null;
			if (dbDialect.equals(Constants.postgresDialect.getStrValue())) {
				nativeSqlQuery = getPostgresLoadUserDetailsFormUserIDQuery();
			} else {
				nativeSqlQuery = getOracleLoadUserDetailsFormUserIDQuery();
			}
			LOGGER.info("Sqlquery= " + nativeSqlQuery);
			query = entityManager.createNativeQuery(nativeSqlQuery);
			query.setParameter("userID", userID.toUpperCase(Locale.ENGLISH));
			query.setParameter("lookupType", Constants.USER_STATUS_TYPE.getStrValue());
			query.setParameter("userStatus", Constants.ACTIVE_STATUS.getStrValue());
			List<Object[]> channelUserObject = query.getResultList();
			if (!CommonUtils.isNullorEmpty(channelUserObject)) {
				for (Object[] obj : channelUserObject) {
					channelUser = constructChannelUserService(obj);
				}
			}
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at loadUserDetailsFormUserID {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

		return channelUser;
	}

	private String getPostgresLoadUserDetailsFormUserIDQuery() {
		StringBuilder sqlQuery = new StringBuilder(
				"SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id,U.creation_type,"); // 0-6

		sqlQuery.append(
				" U.category_code, U.city, U.contact_no, U.contact_person, U.country, U.created_by, U.created_on, U.department, U.designation,"); // 7-
																																					// 15

		sqlQuery.append(
				" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,"); // 16
																																							// -23

		sqlQuery.append(
				" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,"); // 24
																																					// -
																																					// 30

		sqlQuery.append(
				"U.company,U.fax,U.firstname,U.lastname, U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status,"); // 31
																																					// -
																																					// 39

		sqlQuery.append(
				" U.pswd_modified_on, U.reference_id, U.remarks,U.short_name, U.ssn,U.rsaflag, U.state, U.status as userstatus, U.to_time,"); // 40
																																				// -
																																				// 48

		sqlQuery.append(
				" U.user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on, CU.comm_profile_set_id, CU.contact_person as contPerson,"); // 49
																																										// -
																																										// 56

		sqlQuery.append(
				" CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id, CU.user_grade, C.agent_allowed,"); // 57
																																				// -
																																				// 63

		sqlQuery.append(
				" C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles, C.grph_domain_type,"); // 64
																																			// -
																																			// 70

		sqlQuery.append(
				" C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, "); // 71
																																						// -
																																						// 76

		sqlQuery.append(
				" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no AS catseq,"); // 77
																																					// -
																																					// 81

		sqlQuery.append(
				" C.services_allowed, C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix,"); // 82
																																				// -
																																				// 87

		sqlQuery.append(
				" C.view_on_network_block, C.web_interface_allowed, UP.msisdn, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile,"); // 88
																																					// -
																																					// 94

		sqlQuery.append(" UP.phone_language, UP.country as coun , UP.invalid_pin_count, UP.last_transaction_status,"); // 95-
																														// 98

		sqlQuery.append(
				" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time,"); // 99
																																								// -
																																								// 104

		sqlQuery.append(
				" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow, UP.PREFIX_ID,l.lookup_name,"); // 105-110

		sqlQuery.append(" USR_CRBY.user_name as created_by_name, u.AUTHENTICATION_ALLOWED "); // 111- 112

		sqlQuery.append(
				" FROM users U left join user_phones UP on (UP.user_id=U.user_id and UP.primary_number=:userStatus ) ");

		sqlQuery.append(
				" left join (users PRNT_USR left join categories  PRNT_CAT on PRNT_CAT.category_code=PRNT_USR.category_code) on  PRNT_USR.user_id=U.parent_id ");

		sqlQuery.append(" left join users USR_CRBY on USR_CRBY.user_id = U.created_by ,");

		sqlQuery.append(" channel_users CU, categories C,  lookups l");

		sqlQuery.append(" WHERE CU.user_id=U.user_id AND U.category_code=C.category_code AND U.user_id=:userID ");

		sqlQuery.append(" AND U.status = l.lookup_code AND l.lookup_type=:lookupType");
		return sqlQuery.toString();
	}

	private String getOracleLoadUserDetailsFormUserIDQuery() {
		StringBuilder sqlQuery = new StringBuilder(
				"SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id, U.creation_type,"); // 0-6

		sqlQuery.append(
				" U.category_code, U.city, U.contact_no, U.contact_person, U.country, U.created_by, U.created_on, U.department, U.designation,"); // 7-15

		sqlQuery.append(
				" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,"); // 16-23

		sqlQuery.append(
				" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,"); // 24
																																					// -
																																					// 30

		sqlQuery.append(
				" U.company,U.fax,U.firstname,U.lastname, U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status,"); // 31-39

		sqlQuery.append(
				" U.pswd_modified_on, U.reference_id, U.remarks,U.short_name, U.ssn,U.rsaflag, U.state, U.status as userstatus, U.to_time,"); // 40-48

		sqlQuery.append(
				" U.user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on, CU.comm_profile_set_id, CU.contact_person as contPerson,"); // 49-56

		sqlQuery.append(
				" CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id, CU.user_grade, C.agent_allowed,"); // 57
																																				// -
																																				// 63

		sqlQuery.append(
				" C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles, C.grph_domain_type, "); // 64-70

		sqlQuery.append(
				" C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, "); // 71-76

		sqlQuery.append(
				" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no as catseq, "); // 77-81

		sqlQuery.append(
				" C.services_allowed, C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix, "); // 82-87

		sqlQuery.append(
				" C.view_on_network_block, C.web_interface_allowed,  UP.msisdn, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile, "); // 88
																																					// -94

		sqlQuery.append(" UP.phone_language, UP.country coun , UP.invalid_pin_count, UP.last_transaction_status,"); // 95-98

		sqlQuery.append(
				" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time,"); // 99-104

		sqlQuery.append(
				" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow, UP.PREFIX_ID,l.lookup_name, "); // 105-110

		sqlQuery.append(" USR_CRBY.user_name created_by_name,u.AUTHENTICATION_ALLOWED "); // 111-112

		sqlQuery.append(" FROM users U, channel_users CU, categories C, user_phones UP, lookups l, ");

		sqlQuery.append(" users PRNT_USR,categories  PRNT_CAT, users USR_CRBY ");

		sqlQuery.append(" WHERE CU.user_id=U.user_id AND U.category_code=C.category_code AND U.user_id=:userID");

		sqlQuery.append(
				" AND U.user_id=UP.user_id(+) AND UP.primary_number(+)=:userStatus AND U.status = l.lookup_code AND l.lookup_type=:lookupType ");

		sqlQuery.append(" AND U.parent_id=PRNT_USR.user_id(+) ");

		sqlQuery.append(" AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");

		sqlQuery.append(" AND USR_CRBY.user_id(+) = U.created_by ");
		return sqlQuery.toString();
	}

	/**
	 * Construct the channel user data
	 */
	private static ChannelUserVO constructChannelUserService(Object[] objects) {
		ChannelUserVO channelUser = new ChannelUserVO();
		channelUser.setAddress1((String) objects[NumberConstants.ZERO.getIntValue()]);
		channelUser.setAddress2((String) objects[NumberConstants.ONE.getIntValue()]);
		channelUser.setAllowedDays((String) objects[NumberConstants.TWO.getIntValue()]);
		channelUser.setAllowedIp((String) objects[NumberConstants.THREE.getIntValue()]);
		channelUser.setAppointmentDate(
				CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.FOUR.getIntValue()]));
		channelUser.setBatchId((String) objects[NumberConstants.FIVE.getIntValue()]);
		LOGGER.debug("Creation Type : {}", objects[NumberConstants.SIX.getIntValue()]);
		try{
			String abcd = objects[NumberConstants.SIX.getIntValue()].toString();
			channelUser.setCreationType(abcd);
		}catch(Exception e){
			LOGGER.error("Exception occurs at constructChannelUserService {}", e);
			channelUser.setCreationType(String.valueOf(objects[NumberConstants.SIX.getIntValue()]));
		}

		channelUser.setCategoryCode((String) objects[NumberConstants.SEVEN.getIntValue()]);
		channelUser.setCity((String) objects[NumberConstants.EIGHT.getIntValue()]);
		channelUser.setContactNo((String) objects[NumberConstants.NINE.getIntValue()]);
		channelUser.setContactPerson((String) objects[NumberConstants.N10.getIntValue()]);
		channelUser.setCountry((String) objects[NumberConstants.N11.getIntValue()]);
		channelUser.setCreatedBy((String) objects[NumberConstants.N12.getIntValue()]);
		channelUser
				.setCreatedOn(CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N13.getIntValue()]));
		channelUser.setDepartment((String) objects[NumberConstants.N14.getIntValue()]);
		channelUser.setDesignation((String) objects[NumberConstants.N15.getIntValue()]);
		channelUser.setDivision((String) objects[NumberConstants.N16.getIntValue()]);
		channelUser.setEmail((String) objects[NumberConstants.N17.getIntValue()]);
		channelUser.setEmployeeCode((String) objects[NumberConstants.N18.getIntValue()]);
		channelUser.setExternalCode((String) objects[NumberConstants.N19.getIntValue()]);
		channelUser.setFromTime((String) objects[NumberConstants.N20.getIntValue()]);
		if (CommonUtils.isNullorEmpty(objects[NumberConstants.N21.getIntValue()])) {
			channelUser.setInvalidPasswordCount(0L);
		} else {
			channelUser.setInvalidPasswordCount(Long.parseLong(objects[NumberConstants.N21.getIntValue()].toString()));
		}
		channelUser.setLastLoginOn(
				CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N22.getIntValue()]));
		channelUser.setLevel1ApprovedBy((String) objects[NumberConstants.N23.getIntValue()]);
		channelUser.setLevel1ApprovedOn(
				CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N24.getIntValue()]));
		channelUser.setLevel2ApprovedBy((String) objects[NumberConstants.N25.getIntValue()]);
		channelUser.setLevel2ApprovedOn(
				CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N26.getIntValue()]));
		channelUser.setLoginId((String) objects[NumberConstants.N27.getIntValue()]);
		channelUser.setModifiedBy((String) objects[NumberConstants.N28.getIntValue()]);
		channelUser.setMsisdn((String) objects[NumberConstants.N90.getIntValue()]);//priyank
		channelUser
				.setModifiedOn(CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N29.getIntValue()]));
		constructChannelUserService1(channelUser, objects);
		constructChannelUserService2(channelUser, objects);
		channelUser.setCategories(getCategoriesDetails(objects));
		constructPhoneUserService(channelUser, objects);
		return channelUser;

	}

	/**
	 * Construct the channel user data
	 */
	private static void constructChannelUserService1(ChannelUserVO channelUser, Object[] objects) {
		channelUser.setNetworkCode((String) objects[NumberConstants.N30.getIntValue()]);
		channelUser.setCompany((String) objects[NumberConstants.N31.getIntValue()]);
		channelUser.setFax((String) objects[NumberConstants.N32.getIntValue()]);
		channelUser.setFirstname((String) objects[NumberConstants.N33.getIntValue()]);
		channelUser.setLastname((String) objects[NumberConstants.N34.getIntValue()]);
		channelUser.setOwnerId((String) objects[NumberConstants.N35.getIntValue()]);
		channelUser.setParentId((String) objects[NumberConstants.N36.getIntValue()]);
		channelUser.setPword((String) objects[NumberConstants.N37.getIntValue()]);
		if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N38.getIntValue()])) {
			channelUser.setPasswordCountUpdatedOn(
					CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N38.getIntValue()]));
		}
		channelUser.setPreviousStatus((String) objects[NumberConstants.N39.getIntValue()]);
		channelUser.setPswdModifiedOn(
				CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N40.getIntValue()]));
		channelUser.setReferenceId((String) objects[NumberConstants.N41.getIntValue()]);
		channelUser.setRemarks((String) objects[NumberConstants.N42.getIntValue()]);
		channelUser.setShortName((String) objects[NumberConstants.N43.getIntValue()]);
		channelUser.setSsn((String) objects[NumberConstants.N44.getIntValue()]);
		channelUser.setRsaflag((String) objects[NumberConstants.N45.getIntValue()]);
		channelUser.setState((String) objects[NumberConstants.N46.getIntValue()]);
		channelUser.setStatus((String) objects[NumberConstants.N47.getIntValue()]);
		channelUser.setToTime((String) objects[NumberConstants.N48.getIntValue()]);
		channelUser.setUserCode((String) objects[NumberConstants.N49.getIntValue()]);
		channelUser.setUserId((String) objects[NumberConstants.N50.getIntValue()]);
		channelUser.setUserName((String) objects[NumberConstants.N51.getIntValue()]);
		channelUser.setUserNamePrefix((String) objects[NumberConstants.N52.getIntValue()]);
		channelUser.setUserType((String) objects[NumberConstants.N53.getIntValue()]);
	}

	/**
	 * Construct the channel user data
	 */
	private static void constructChannelUserService2(ChannelUserVO channelUser, Object[] objects) {
		channelUser.setActivatedOn(
				CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N54.getIntValue()]));
		channelUser.setCommissionProfileSetID((String) objects[NumberConstants.N55.getIntValue()]);
		channelUser.setContactPersonch((String) objects[NumberConstants.N56.getIntValue()]);
		channelUser.setInSuspend((String) objects[NumberConstants.N57.getIntValue()].toString());
		channelUser.setOutSuspened((String) objects[NumberConstants.N58.getIntValue()].toString());
		if(objects[NumberConstants.N59.getIntValue()] !=null){
			channelUser.setOutletCode((String) objects[NumberConstants.N59.getIntValue()].toString());
		}

		if(objects[NumberConstants.N60.getIntValue()] !=null) {
			channelUser.setSubOutletCode((String) objects[NumberConstants.N60.getIntValue()].toString());
		}
		if(objects[NumberConstants.N61.getIntValue()] !=null) {
			channelUser.setTransferProfileID((String) objects[NumberConstants.N61.getIntValue()].toString());
		}
		if(objects[NumberConstants.N62.getIntValue()] !=null) {
			channelUser.setUserGrade((String) objects[NumberConstants.N62.getIntValue()].toString());
		}
	}

	/**
	 * Construct the Category data
	 */
	private static Categories getCategoriesDetails(Object[] objects) {
		Categories categories = new Categories();
		if(objects[NumberConstants.N63.getIntValue()] != null) {
			categories.setAgentAllowed((String) objects[NumberConstants.N63.getIntValue()].toString());
		}

		if(objects[NumberConstants.N64.getIntValue()] != null) {
			categories.setCategoryName((String) objects[NumberConstants.N64.getIntValue()].toString());
		}

		if(objects[NumberConstants.N65.getIntValue()] != null) {
			categories.setCategoryType((String) objects[NumberConstants.N65.getIntValue()].toString());

		}

		if(objects[NumberConstants.N66.getIntValue()] != null) {
			categories.setDomainAllowed((String) objects[NumberConstants.N66.getIntValue()].toString());
		}

		if(objects[NumberConstants.N67.getIntValue()] != null) {
			categories.setDomainCode((String) objects[NumberConstants.N67.getIntValue()].toString());
		}

		if(objects[NumberConstants.N68.getIntValue()] != null) {
			categories.setFixedDomains((String) objects[NumberConstants.N68.getIntValue()].toString());
		}

		if(objects[NumberConstants.N69.getIntValue()] != null) {
			categories.setFixedRoles((String) objects[NumberConstants.N69.getIntValue()].toString());
		}

		if(objects[NumberConstants.N70.getIntValue()] != null) {
			categories.setGrphDomainType((String) objects[NumberConstants.N70.getIntValue()].toString());
		}

		if(objects[NumberConstants.N71.getIntValue()] != null) {
			categories.setHierarchyAllowed((String) objects[NumberConstants.N71.getIntValue()].toString());
		}

		if(objects[NumberConstants.N72.getIntValue()] != null) {
			categories.setMaxLoginCount(Long.parseLong(objects[NumberConstants.N72.getIntValue()].toString()));
		}

		if(objects[NumberConstants.N73.getIntValue()] != null) {
			categories.setMaxTxnMsisdn(Long.parseLong(objects[NumberConstants.N73.getIntValue()].toString()));
		}

		if(objects[NumberConstants.N74.getIntValue()] != null) {
			categories.setMultipleGrphDomains((String) objects[NumberConstants.N74.getIntValue()].toString());
		}

		if(objects[NumberConstants.N75.getIntValue()] != null) {
			categories.setMultipleLoginAllowed((String) objects[NumberConstants.N75.getIntValue()].toString());
		}

		if(objects[NumberConstants.N76.getIntValue()] != null) {
			categories.setOutletsAllowed((String) objects[NumberConstants.N76.getIntValue()].toString());
		}

		if(objects[NumberConstants.N77.getIntValue()] != null) {
			categories.setParentCategoryCode((String) objects[NumberConstants.N77.getIntValue()].toString());
		}

		if(objects[NumberConstants.N78.getIntValue()] != null) {
			categories.setProductTypesAllowed((String) objects[NumberConstants.N78.getIntValue()].toString());
		}

		if(objects[NumberConstants.N79.getIntValue()] != null) {
			categories.setRestrictedMsisdns((String) objects[NumberConstants.N79.getIntValue()].toString());
		}

		if(objects[NumberConstants.N80.getIntValue()] != null) {
			categories.setScheduledTransferAllowed((String) objects[NumberConstants.N80.getIntValue()].toString());
		}

		if(objects[NumberConstants.N81.getIntValue()] != null) {
			categories.setSequenceNo(Integer.parseInt(objects[NumberConstants.N81.getIntValue()].toString()));
		}

		if(objects[NumberConstants.N82.getIntValue()] != null) {
			categories.setServiceAllowed((String) objects[NumberConstants.N82.getIntValue()].toString());
		}

		if(objects[NumberConstants.N83.getIntValue()] != null) {
			categories.setSmsInterfaceAllowed((String) objects[NumberConstants.N83.getIntValue()].toString());
		}

		if(objects[NumberConstants.N84.getIntValue()] != null) {
			categories.setStatus((String) objects[NumberConstants.N84.getIntValue()].toString());
		}

		if(objects[NumberConstants.N85.getIntValue()] != null) {
			categories.setTransfertolistonly((String) objects[NumberConstants.N85.getIntValue()].toString());
		}

		if(objects[NumberConstants.N86.getIntValue()] != null) {
			categories.setUncntrlTransferAllowed((String) objects[NumberConstants.N86.getIntValue()].toString());
		}

		if(objects[NumberConstants.N87.getIntValue()] != null) {
			categories.setUserIdPrefix((String) objects[NumberConstants.N87.getIntValue()].toString());
		}

		if(objects[NumberConstants.N88.getIntValue()] != null) {
			categories.setViewOnNetworkBlock((String) objects[NumberConstants.N88.getIntValue()].toString());
		}
		return categories;
	}

	/**
	 * Construct the phone user data for channel user data
	 */
	private static void constructPhoneUserService(ChannelUserVO channelUser, Object[] objects) {



		if(objects[NumberConstants.N92.getIntValue()] != null) {

		channelUser.setSmsPin((String) objects[NumberConstants.N92.getIntValue()].toString());
		}

		if(objects[NumberConstants.N93.getIntValue()] != null) {
			channelUser.setPinRequired((String) objects[NumberConstants.N93.getIntValue()].toString());
		}

		if(objects[NumberConstants.N94.getIntValue()] != null) {
			channelUser.setPhoneProfile((String) objects[NumberConstants.N94.getIntValue()].toString());
		}

		if(objects[NumberConstants.N95.getIntValue()] != null) {
			channelUser.setLanguage((String) objects[NumberConstants.N95.getIntValue()].toString());
		}

		if(objects[NumberConstants.N96.getIntValue()] != null) {
			channelUser.setCountryCode((String) objects[NumberConstants.N96.getIntValue()].toString());
		}

		if(objects[NumberConstants.N97.getIntValue()] != null) {
			channelUser.setInvalidPinCount(Integer.parseInt(objects[NumberConstants.N97.getIntValue()].toString()));
		}

		if(objects[NumberConstants.N109.getIntValue()] != null) {
			try{
				channelUser.setPrefixId(Long.parseLong(objects[NumberConstants.N109.getIntValue()].toString()));
			}catch(Exception e){
				LOGGER.error("Exception occurs at constructPhoneUserService {}", e);
//				channelUser.setPrefixId(Long.parseLong(objects[NumberConstants.N109.getIntValue()].toString()));
			}
		}

		if(objects[NumberConstants.N105.getIntValue()] != null) {
			channelUser.setApplicationID((String) objects[NumberConstants.N105.getIntValue()].toString());
		}

		if(objects[NumberConstants.N106.getIntValue()] != null) {
			channelUser.setMpayProfileID((String) objects[NumberConstants.N106.getIntValue()].toString());
		}

		if(objects[NumberConstants.N107.getIntValue()] != null) {
			channelUser.setUserProfileID((String) objects[NumberConstants.N107.getIntValue()].toString());
		}

		if(objects[NumberConstants.N108.getIntValue()] != null) {
			channelUser.setMcommerceServiceAllow((String) objects[NumberConstants.N108.getIntValue()].toString());
		}

		if(objects[NumberConstants.N111.getIntValue()] != null) {
			channelUser.setCreatedByUserName((String) objects[NumberConstants.N111.getIntValue()].toString());
		}

		if(objects[NumberConstants.N112.getIntValue()] != null) {
			channelUser.setAuthenticationAllowed((String) objects[NumberConstants.N112.getIntValue()].toString());
		}
	}

	@Override
	public boolean checkUserAuthenticationForRole(String userId, String roleCode, String eventCode, Users user,
			Categories category) {		
		boolean returnValue = false;
		try {
			if (category.getFixedRoles().equals(Constants.YES.getStrValue())) {
				returnValue = checkFixedRole(userId, roleCode, eventCode, user, category);
			} else {
				returnValue = checkAssignedRole(userId, roleCode, eventCode, user, category);
			}

		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return returnValue;
	}

	private boolean checkFixedRole(String userId, String roleCode, String eventCode, Users user, Categories category) {
		StringBuilder sqlQuery = new StringBuilder();
		Query query1;
		try {
			sqlQuery.append(
					"select R.accessType ,RE.roleCode,RE.eventCode,RE.eventName,RE.eventLabelKey,RE.roleLabelKey  ");
			sqlQuery.append(" FROM CategoryRoles CR, Roles R, Pages P, Modules M , RoleEvents RE  ");
			sqlQuery.append(
					" WHERE CR.categoryCode=:categoryCode AND CR.roleCode=R.roleCode AND R.domainType=:domainType");
			sqlQuery.append(" AND R.roleCode=P.roleCode ");
			sqlQuery.append(" AND CR.roleCode=RE.roleCode ");
			sqlQuery.append(" and CR.categoryCode = RE.categoryCode");
			sqlQuery.append(" AND (R.status IS NULL OR R.status='Y') ");
			sqlQuery.append(" AND P.moduleCode=M.moduleCode ");
			sqlQuery.append(" AND P.applicationId='2' ");
			sqlQuery.append(" AND M.applicationId='2' ");
			sqlQuery.append(" AND R.applicationId='2' ");
			sqlQuery.append(" AND CR.applicationId='2' ");
			sqlQuery.append(" AND RE.status='Y' ");
			sqlQuery.append(" AND R.gatewayTypes = :gatewayTypes ");
			sqlQuery.append(" AND R.roleCode = :roleCode ");
			if (!CommonUtils.isNullorEmpty(eventCode)) {
				sqlQuery.append(" AND RE.eventCode = :eventCode ");
			}

			query1 = entityManager.createQuery(sqlQuery.toString());

			query1.setParameter("domainType", user.getUserType());
			query1.setParameter("gatewayTypes", "WEB");
			query1.setParameter("roleCode", roleCode);
			if (!CommonUtils.isNullorEmpty(eventCode)) {
				query1.setParameter("eventCode", eventCode);
			}
			query1.setParameter("categoryCode", category.getCategoryCode());
			@SuppressWarnings("unchecked")
			List<Object[]> listRoles = query1.getResultList();
			return !CommonUtils.isNullorEmpty(listRoles);
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkFixedRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

	}

	private boolean checkAssignedRole(String userId, String roleCode, String eventCode, Users user,
			Categories category) {
		StringBuilder sqlQuery = new StringBuilder();
		Query query;
		try {
			sqlQuery.append("SELECT P.pageCode, P.moduleCode, P.pageUrl, P.menuName,");
			sqlQuery.append(" P.menuItem, P.sequenceNo, M.moduleName, P.menuLevel,");
			sqlQuery.append(
					" (CASE WHEN R.fromHour IS NOT NULL THEN R.fromHour ELSE '0' END) AS FROMHOUR, (CASE WHEN R.toHour IS NOT NULL THEN R.toHour ELSE '24' END) AS TOHOUR, M.sequenceNo AS MSEQ, R.roleCode");
			sqlQuery.append(" ,R.accessType,URE.roleCode,URE.eventCode,RE.eventName,RE.eventLabelKey,RE.roleLabelKey ");
			sqlQuery.append(
					" FROM CategoryRoles CR, UserRoles UR, Roles R,Pages P, Modules M, RoleEvents RE,UserRoleEvents URE  ");
			sqlQuery.append(" WHERE UR.userId =:userId AND UR.roleCode=R.roleCode AND R.domainType =:domainType");
			sqlQuery.append(
					" AND CR.categoryCode =:categoryCode AND  CR.roleCode= R.roleCode and CR.roleCode=UR.roleCode ");
			sqlQuery.append(" AND R.roleCode=P.roleCode ");
			sqlQuery.append(" AND CR.categoryCode=RE.categoryCode ");
			sqlQuery.append(" AND CR.roleCode=RE.roleCode ");
			sqlQuery.append(" AND URE.roleCode=RE.roleCode ");
			sqlQuery.append(" AND URE.eventCode=RE.eventCode ");
			sqlQuery.append(" AND URE.userId=:userId ");
			sqlQuery.append(" AND R.roleCode = :roleCode ");
			sqlQuery.append(" AND (R.status IS NULL OR R.status='Y') ");
			sqlQuery.append(" AND P.moduleCode=M.moduleCode");
			sqlQuery.append(" AND P.applicationId='2' ");
			sqlQuery.append(" AND P.menuItem='Y' ");
			sqlQuery.append(" AND M.applicationId='2' ");
			sqlQuery.append(" AND R.applicationId='2' ");
			sqlQuery.append(" AND CR.applicationId='2' ");
			sqlQuery.append(" AND RE.status='Y' ");
			sqlQuery.append(" AND URE.status='Y' ");
			sqlQuery.append(
					" AND (R.roleType IS NULL OR R.roleType= (CASE 'ALL' WHEN 'ALL' THEN R.roleType ELSE 'ALL' END))");
			sqlQuery.append(" AND R.gatewayTypes = :gatewayTypes ");

			if (!CommonUtils.isNullorEmpty(eventCode)) {
				sqlQuery.append(" AND URE.eventCode = :eventCode ");
			}
			sqlQuery.append(" ORDER BY M.applicationId, M.sequenceNo, P.applicationId, P.sequenceNo");

			query = entityManager.createQuery(sqlQuery.toString());

			query.setParameter("userId", userId);
			query.setParameter("domainType", user.getUserType());
			query.setParameter("gatewayTypes", "WEB");
			query.setParameter("roleCode", roleCode);
			if (!CommonUtils.isNullorEmpty(eventCode)) {
				query.setParameter("eventCode", eventCode);
			}
			query.setParameter("categoryCode", category.getCategoryCode());
			@SuppressWarnings("unchecked")
			List<Object[]> listRoles = query.getResultList();
			return !CommonUtils.isNullorEmpty(listRoles);
		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkFixedRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

	}

	@Override
	public UsersVO getEmailIdOfApproversQry(String parentUserId, String rolceCode) {

		UsersVO usersVO = null;
		try {
			Query query = entityManager.createQuery("select u.email,u.msisdn Users u,UserRoles ur "
					+ "and u.userId = ur.userId and (u.userId = :puserId OR u.parentId = :pParentID) and ur.roleCode= :pRoleCode ");
			query.setParameter("puserId", parentUserId);
			query.setParameter("pParentID", parentUserId);
			query.setParameter("pRoleCode", rolceCode);

			@SuppressWarnings("unchecked")
			List<Object[]> userlist = query.getResultList();
			if (!CommonUtils.isNullorEmpty(userlist)) {
				for (Object[] obj : userlist) {
					usersVO = getUsersVO();
					usersVO.setEmail(CommonUtils.convertStringObject(obj[NumberConstants.ZERO.getIntValue()]));
					usersVO.setMsisdn(CommonUtils.convertStringObject(obj[NumberConstants.ONE.getIntValue()]));

				}
			}

			if (!CommonUtils.isNullorEmpty(usersVO)) {
				return usersVO;
			}

		} catch (PersistenceException e) {
			LOGGER.error("Exception occurs at checkUserAuthanticationForRole {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}

		return null;
	}
	
	
	private UsersVO getUsersVO() {
		UsersVO userVO = new UsersVO();
		return userVO;
	}

}
