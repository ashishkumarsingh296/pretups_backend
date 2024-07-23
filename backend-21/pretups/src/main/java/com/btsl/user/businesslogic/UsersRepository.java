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

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.btsl.user.businesslogic.entity.Users;


/**
 * Data base operations for Users
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String>, UsersCustomRepository {
    /**
     * getParentsUserDetails
     * 
     * @param userIds
     *            is a list of string value
     * @return List<Users>
     */
    @Query("SELECT u FROM Users u WHERE u.userId IN (:userIds)")
    List<Users> getParentsUserDetails(@Param("userIds") List<String> userIds);

    /**
     * getUserDataById
     * 
     * @param userId
     *            is a string value
     * @return Users
     */
    @Query("SELECT u FROM Users u WHERE u.userId=:userId")
    Users getUserDataById(@Param("userId") String userId);
    
    @Transactional
    @Modifying
    @Query("UPDATE Users u set u.lastLoginOn = :date WHERE u.loginId=:loginId")
    void updateuserLastlogin(@Param("date") Date date, @Param("loginId") String loginId);

}
