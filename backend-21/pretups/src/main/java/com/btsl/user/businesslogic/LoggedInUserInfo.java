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

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * For LoggedInUserInfo.
 *
 * @author Subesh KCV
 * @date : @date : 20-DEC-2019
 */

@Getter
@Setter
public class LoggedInUserInfo {
    private String userID;
    private String loginIdentifier;
    private String language;
    private String jwttoken;
    private String refreshToken;
    private String networkCode;
    private String categoryCode;
    private String userName;

    
   public  LoggedInUserInfo(){
        
    }
    
    public LoggedInUserInfo(String userID, String loginIdentifier, String language, String jwttoken,
            String refreshToken, String networkCode, String categoryCode) {
        super();
        this.userID = userID;
        this.loginIdentifier = loginIdentifier;
        this.language = language;
        this.jwttoken = jwttoken;
        this.refreshToken = refreshToken;
        this.networkCode = networkCode;
        this.categoryCode = categoryCode;

    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getLoginIdentifier());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoggedInUserInfo other = (LoggedInUserInfo) obj;
        return Objects.equals(this.getLoginIdentifier(), other.getLoginIdentifier());
    }

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getLoginIdentifier() {
		return loginIdentifier;
	}

	public void setLoginIdentifier(String loginIdentifier) {
		this.loginIdentifier = loginIdentifier;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getJwttoken() {
		return jwttoken;
	}

	public void setJwttoken(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}



}
