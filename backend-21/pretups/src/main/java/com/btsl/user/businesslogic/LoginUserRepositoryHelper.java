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


/**
 * LoginUserRepositoryHelper class is used for helper.
 *
 * @author VENKATESAN.S
 */
public class LoginUserRepositoryHelper {
    /**
     * LoginUserRepositoryHelper construct
     */
    private LoginUserRepositoryHelper() {
        throw new IllegalStateException("Instance cannot be created.");
    }

    /**
     * populateChannelUserDetails
     * 
     * @param channelUser
     *            - input value
     * @param objects
     *            - input value
     */
    public static void populateChannelUserDetails(ChannelUserVO channelUser, Object[] objects) {
        String userID = (String) objects[NumberConstants.ZERO.getIntValue()];
        channelUser.setUserId(userID);
        channelUser.setUserName((String) objects[NumberConstants.ONE.getIntValue()]);
        channelUser.setNetworkCode((String) objects[NumberConstants.TWO.getIntValue()]);
        channelUser.setNetworkName((String) objects[NumberConstants.THREE.getIntValue()]);
        channelUser.setReportHeaderName((String) objects[NumberConstants.FOUR.getIntValue()]);
        channelUser.setLoginId((String) objects[NumberConstants.FIVE.getIntValue()]);
        channelUser.setPword((String) objects[NumberConstants.SIX.getIntValue()]);
        channelUser.setCategoryCode((String) objects[NumberConstants.SEVEN.getIntValue()]);
        channelUser.setParentId((String) objects[NumberConstants.EIGHT.getIntValue()]);
        channelUser.setCompany((String) objects[NumberConstants.NINE.getIntValue()]);
        channelUser.setFax((String) objects[NumberConstants.N10.getIntValue()]);
        channelUser.setOwnerId((String) objects[NumberConstants.N11.getIntValue()]);
        channelUser.setMsisdn((String) objects[NumberConstants.N12.getIntValue()]);
        channelUser.setAllowedIp((String) objects[NumberConstants.N13.getIntValue()]);
        channelUser.setAllowedDays((String) objects[NumberConstants.N14.getIntValue()]);
        channelUser.setFromTime((String) objects[NumberConstants.N15.getIntValue()]);
        channelUser.setToTime((String) objects[NumberConstants.N16.getIntValue()]);
        channelUser.setFirstname((String) objects[NumberConstants.N17.getIntValue()]);
        channelUser.setLastname((String) objects[NumberConstants.N18.getIntValue()]);
        if (!CommonUtils.isNullorEmpty(objects[NumberConstants.N19.getIntValue()])) {
            channelUser.setLastLoginOn(
                    CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N19.getIntValue()]));
        }
        channelUser.setEmployeeCode((String) objects[NumberConstants.N20.getIntValue()]);
        channelUser.setStatus((String) objects[NumberConstants.N21.getIntValue()]);
        channelUser.setEmail((String) objects[NumberConstants.N22.getIntValue()]);
        channelUser.setCreatedBy((String) objects[NumberConstants.N23.getIntValue()]);
        channelUser
                .setCreatedOn(CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N24.getIntValue()]));
        channelUser.setModifiedBy((String) objects[NumberConstants.N25.getIntValue()]);
        channelUser
                .setModifiedOn(CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N26.getIntValue()]));
        channelUser.setPswdModifiedOn(
                CommonUtils.getTimestampFromUtilDate((Date) objects[NumberConstants.N27.getIntValue()]));
        channelUser.setContactPerson((String) objects[NumberConstants.N28.getIntValue()]);
        channelUser.setContactNo((String) objects[NumberConstants.N29.getIntValue()]);
        channelUser.setDesignation((String) objects[NumberConstants.N30.getIntValue()]);
        channelUser.setDesignation((String) objects[NumberConstants.N31.getIntValue()]);
        channelUser.setDesignation((String) objects[NumberConstants.N32.getIntValue()]);
        channelUser.setMsisdn((String) objects[NumberConstants.N33.getIntValue()]);
        channelUser.setUserType((String) objects[NumberConstants.N34.getIntValue()]);
        channelUser.setInSuspend((String) objects[NumberConstants.N35.getIntValue()]);
        channelUser.setOutSuspened((String) objects[NumberConstants.N36.getIntValue()]);
        channelUser.setAddress1((String) objects[NumberConstants.N37.getIntValue()]);
        channelUser.setAddress2((String) objects[NumberConstants.N38.getIntValue()]);
        channelUser.setCity((String) objects[NumberConstants.N39.getIntValue()]);
        channelUser.setState((String) objects[NumberConstants.N40.getIntValue()]);
        channelUser.setCountry((String) objects[NumberConstants.N41.getIntValue()]);
        channelUser.setSsn((String) objects[NumberConstants.N42.getIntValue()]);
        channelUser.setUserNamePrefix((String) objects[NumberConstants.N43.getIntValue()]);
        channelUser.setExternalCode((String) objects[NumberConstants.N44.getIntValue()]);
        channelUser.setUserCode((String) objects[NumberConstants.N45.getIntValue()]);
        channelUser.setShortName((String) objects[NumberConstants.N46.getIntValue()]);
        channelUser.setReferenceId((String) objects[NumberConstants.N47.getIntValue()]);

    }

}
