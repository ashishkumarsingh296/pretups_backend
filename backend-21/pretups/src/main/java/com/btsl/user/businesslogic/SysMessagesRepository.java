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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.btsl.user.businesslogic.entity.SysMessages;
import com.btsl.user.businesslogic.entity.SysMessagesId;


/**
 * Data base operations for SystemPreferences
 * 
 * @author subesh KCV
 * @date : 19-Dec-2019
 */
@Repository
public interface SysMessagesRepository extends JpaRepository<SysMessages, SysMessagesId> {

    /**
     * Load System Messages
     * 
     * @param messageCode
     *            the bearer code
     * @param languageCode
     *            the language code
     * @return List<SysMessages>
     */
    @Query("SELECT SM FROM SysMessages SM WHERE  SM.id.messageCode=:messageCode and SM.id.languagecode=:languageCode")
    List<SysMessages> loadsysMessages(@Param("messageCode") String messageCode,
            @Param("languageCode") Integer languageCode);
    
    
    /**
     * Load All SysMessages 
     * 
     * 
     * @return List<SysMessages>
     */
    @Query("SELECT s FROM  SysMessages s")
    List<SysMessages> loadAllSysMessages();


}