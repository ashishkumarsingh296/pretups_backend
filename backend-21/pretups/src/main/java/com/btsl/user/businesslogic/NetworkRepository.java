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

import org.springframework.stereotype.Repository;


/**
 * Data base operations for LoginUsersRespository.
 *
 * @author Subesh KCV
 * @date : @date : 20-DEC-2019
 */
@Repository
public interface NetworkRepository {
    /**
     * loadNetworkListForSuperOperatorUsers
     * 
     * @param userId
     *            is a string value
     * @param status
     *            is a string value
     * @return List<NetworksVO>
     */
    List<NetworksVO> loadNetworkListForSuperOperatorUsers(String userId, String status);

    /**
     * loadNetworkListForSuperChannelAdm
     * 
     * @param userId
     *            is a string value
     * @param status
     *            is a string value
     * @return List<NetworksVO>
     */
    List<NetworksVO> loadNetworkListForSuperChannelAdm(String userId, String status);

    /**
     * loadNetworkList
     * 
     * @param status
     *            is a string value
     * @return List<NetworksVO>
     */
    List<NetworksVO> loadNetworkList(String status);

    /**
     * loadUserGeographyList
     * 
     * @param userId
     *            is a string value
     * @param networkCode
     *            is a string value
     * @return List<NetworksVO>
     */
    List<UserGeographiesVO> loadUserGeographyList(String userId, String networkCode);

}