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


import org.springframework.stereotype.Repository;


import java.util.ArrayList;

/**
 * Data base operations for LoginUsersRespository.
 *
 * @author VENKATESAN.S
 * @date : @date : 20-DEC-2019
 */
@Repository
public interface LoginUsersRespository {
    /**
     * getloadUserDetails
     *
     * @param userLoginID is a string
     * @return ChannelUserVO
     */
    ChannelUserVO getloadUserDetails(String identifierType, String userLoginID);

    /**
     * loadUserServicesList
     *
     * @param userId is a string
     * @return ArrayList
     */
    @SuppressWarnings("rawtypes")
    ArrayList loadUserServicesList(String userId);

    /**
     * updatePasswordCounter
     *
     * @param channelUser is a object
     * @return integer
     */
    int updatePasswordCounter(ChannelUserVO channelUser);

    /**
     * getUserBalance
     *
     * @param userID
     * @return
     */
    ArrayList<UserBalanceVO> getUserBalance(String userID);
    
}