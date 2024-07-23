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



import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Login Response model
 */
@Setter
@Getter
public class LoginResponse extends BaseResponse {

    /**
     * Login Response
     * 
     * @return String
     */

   // private TokenData jwtaccessToken;
    private ChannelUserVO channelUser;
    private ChannelUserInfo channelUserInfo;
    private String uniqueSessionIDforlogin;
    private String clientDateFormat;
    private String clientDateTimeFormat;
    private String calendarType;
    private boolean changePassword;
    private String genToken;
    private String refreshToken;
	private Date serverTime;
	public ChannelUserVO getChannelUser() {
		return channelUser;
	}
	public void setChannelUser(ChannelUserVO channelUser) {
		this.channelUser = channelUser;
	}
	public ChannelUserInfo getChannelUserInfo() {
		return channelUserInfo;
	}
	public void setChannelUserInfo(ChannelUserInfo channelUserInfo) {
		this.channelUserInfo = channelUserInfo;
	}
	public String getUniqueSessionIDforlogin() {
		return uniqueSessionIDforlogin;
	}
	public void setUniqueSessionIDforlogin(String uniqueSessionIDforlogin) {
		this.uniqueSessionIDforlogin = uniqueSessionIDforlogin;
	}
	public String getClientDateFormat() {
		return clientDateFormat;
	}
	public void setClientDateFormat(String clientDateFormat) {
		this.clientDateFormat = clientDateFormat;
	}
	public String getClientDateTimeFormat() {
		return clientDateTimeFormat;
	}
	public void setClientDateTimeFormat(String clientDateTimeFormat) {
		this.clientDateTimeFormat = clientDateTimeFormat;
	}
	public String getCalendarType() {
		return calendarType;
	}
	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}
	public boolean isChangePassword() {
		return changePassword;
	}
	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}
	public String getGenToken() {
		return genToken;
	}
	public void setGenToken(String genToken) {
		this.genToken = genToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

    /*@Override
    public String toString() {
        return "LoginResponse [referenceId=" + referenceId + ", externalRefId=" + externalRefId + ", status=" + status
                + ", message=" + message + ", channelUser=" + channelUser + ", jwtaccessToken=" + jwtaccessToken + ", genToken=" + genToken +", refreshToken=" + refreshToken +"]";
    }*/
    
    
    
    
}
