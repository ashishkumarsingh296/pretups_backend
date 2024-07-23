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

import java.util.Locale;

import org.springframework.stereotype.Repository;

import com.btsl.user.businesslogic.entity.UsersLoginInfo;


/**
 * Repository of UsersLoginQueryRep interface.
 *
 * @author VENKATESAN.S
 */

public interface UsersLoginQueryRep {

    /**
     * getMaxUserPerNetwork
     * 
     * @param networkCode
     *            is astring value
     * 
     * @return Long
     */
    Long getMaxUserPerNetwork(String networkCode);

    /**
     * getMaxUserPerNetworkCategory
     * 
     * @param networkCode
     *            is a string value
     * @param categoryCode
     *            is a string value
     * @return Long
     */
    Long getMaxUserPerNetworkCategory(String networkCode, String categoryCode);

    Long getMaxUserPerUser(String loginId);
    
    
    /**
     * saveUsersLoginInfo
     * 
     * @param usersLoginInfo
     *            is a string value
     */
    void saveUsersLoginInfo(UsersLoginInfo usersLoginInfo);

    /**
     * deleteUsersLoginInfo
     * 
     * @param usersLoginInfo
     *            is a Object value
     */
    void deleteUsersLoginInfo(UsersLoginInfo usersLoginInfo);

    /**
     * getFirstUserPerNetwork
     * 
     * @param networkCode
     *            is a string value
     * @return UsersLoginInfo
     */
    UsersLoginInfo getFirstUserPerNetwork(String networkCode);

    /**
     * getFirstUserPerNetworkCategory
     * 
     * @param networkCode
     *            is a string value
     * @param categoryCode
     *            is a string value
     * @return UsersLoginInfo
     */
    UsersLoginInfo getFirstUserPerNetworkCategory(String networkCode, String categoryCode);

    ChannelUserVO loadUserDetails(String loginID, String password, Locale locale);

    ChannelUserVO loadAllUserDetailsByLoginID(String ploginID);

    boolean isAssignedRoleAndExist(String puserID, String proleCode, String pdomainType);

    boolean isFixedRoleAndExist(String pcategoryCode, String proleCode, String pdomainType);
    
   int updateUserLoginDetails(String userId);
   public void  deleteExpiredTokens(String userId);
    String isUserGroupRoleSupended(String userId);
}
