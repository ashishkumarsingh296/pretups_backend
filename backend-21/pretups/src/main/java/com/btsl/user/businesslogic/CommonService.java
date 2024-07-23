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

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.btsl.user.businesslogic.entity.Categories;
import com.btsl.user.businesslogic.entity.Users;

/*import com.comviva.vms.common.config.ApplicationException;
import com.comviva.vms.common.config.CacheManager;
import com.comviva.vms.common.config.LocaleTranslator;
import com.comviva.vms.common.config.LoggedInUserInfo;
import com.comviva.vms.common.config.VMSSessionHolder;
import com.comviva.vms.common.config.ValidationException;
import com.comviva.vms.common.enums.Constants;
import com.comviva.vms.common.enums.MessageCodes;
import com.comviva.vms.common.enums.ServiceRoleCodes;
import com.comviva.vms.common.utils.CommonUtils;
import com.comviva.vms.entity.Categories;
import com.comviva.vms.entity.Users;
import com.comviva.vms.model.BaseRequest;
import com.comviva.vms.model.ConfigParams;
import com.comviva.vms.model.login.LogoutRequest;
import com.comviva.vms.repository.CategoriesRepository;
import com.comviva.vms.repository.UsersRepository;
import com.comviva.vms.repository.VMSCacheRepository;*/

/**
 * This service used for CommonService.
 *
 * @author sudharshans
 */
public abstract class CommonService implements CommonServiceInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);

	/** The Constant NEW_OBJECT. */
	protected static final String NEW_OBJECT = "newObject";

	/** The Constant NEW_OBJECT_ONE. */
	protected static final String NEW_OBJECT_ONE = "newObjectOne";

	/** The Constant OLD_OBJECT. */
	protected static final String OLD_OBJECT = "oldObject";

	/** The response status. */
	protected String responseStatus;

	/** The response message. */
	protected String responseMessage;

	/** The response language. */
	protected Integer responseLanguage;

	/** The service role code. */
	protected String serviceRoleCode;

	/** The reference id. */
	protected String referenceId;

	/** The external ref id. */
	protected String externalRefId;

	protected String roleCode;
	
	protected String eventCode;

	protected String userId;

	/** The response message code. */
	protected String responseMessageCode;

	@Value("${appilication.default.language}")
	protected Integer defaultLanguage;

	@Autowired
	private VMSCacheRepository vmsCacheRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private LocaleTranslator localeTranslator;
	
	@Autowired
	private CategoriesRepository categoryRepository;

	/**
	 * Execution of CommonService with request parameters.
	 *
	 * @param request          - BaseRequest
	 * @param roleCode         - String
	 * @param loggedinUserInfo - loggedinUserInfo
	 */
	public void initiate(BaseRequest request, String roleCode,String eventCode, LoggedInUserInfo loggedinUserInfo) {
		responseMessageCode = "";

		this.externalRefId = request.getExternalRefId();
		this.userId = loggedinUserInfo.getUserID();
		responseLanguage = CacheManager.getLanguageCode(loggedinUserInfo.getLanguage());
		ConfigParams configParams = new ConfigParams();
		configParams.setUniqueSessionId(loggedinUserInfo.getLoginIdentifier());
		configParams.setLanguageSelected(loggedinUserInfo.getLanguage());
		VMSSessionHolder.put(configParams);

		if (roleCode != null && !roleCode.equalsIgnoreCase(ServiceRoleCodes.NO_ROLE.getStrValue())) {
			this.roleCode = roleCode;
			this.eventCode=eventCode;
			isAuthorisedServiceRole();
		}

	}

	/**
	 * Validate the service role.
	 */
	private void isAuthorisedServiceRole() {
		Users user =usersRepository.getUserDataById(userId);
		Categories categories =categoryRepository.getCategoryByCode(user.getCategoryCode());
		 
		
		boolean isAuthorisedUser = usersRepository.checkUserAuthenticationForRole(userId, roleCode, eventCode,user,categories);
		if (!isAuthorisedUser) {
			throw new ValidationException(Constants.USERID.getStrValue(),
					MessageCodes.USER_NOT_AUTHORIZED.getStrValue());
		}
	}
	
	
	

	/**
	 * Construct for CommonService
	 * 
	 * @param params - List<String>
	 */
	public void generateReponseMessage(List<String> params) {
		String message = vmsCacheRepository.loadMessageByMessageCodeAndLangCode(responseMessageCode, responseLanguage);

		if (CommonUtils.isNullorEmpty(message)) {
			Locale locale = CacheManager.getLanguageLocale(responseLanguage);
			message = localeTranslator.toLocale(responseMessageCode, locale);
		}

		if (message != null) {
			if (params != null) {
				responseMessage = new MessageFormat(message).format(params.toArray());
			}
			if (responseStatus.equals(Constants.SUCCESS_RESPONSE.getStrValue())) {
				responseMessageCode = "";
			}
		}
	}

	/**
	 * logoutprocess
	 * 
	 * @param logoutRequest -logoutRequest
	 */
/*	public void logoutprocess(LogoutRequest logoutRequest) {

	}*/

	protected void copyObjects(Object destination, Object source) {
		try {
			copyProperties(destination, source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error("IllegalAccessException | InvocationTargetException", "copyObjects", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
	}
}
