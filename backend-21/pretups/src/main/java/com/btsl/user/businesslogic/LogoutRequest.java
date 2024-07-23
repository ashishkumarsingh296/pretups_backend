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

/**
 * LogoutRequest Model
 * 
 * @author VENKATESAN.S
 *
 */
@Setter
@Getter
public class LogoutRequest extends BaseRequest {

    @io.swagger.v3.oas.annotations.media.Schema(description = "First Login JTI", required = true/* , position = 1 */, example = "SU0001-09d149f2-c0f1-418d-9d52-0ed6f3fce97b")
    private String uniqueSessionId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "User Language", required = true/* , position = 2 */, example = "EN")
    private String userLang;

	private String remarks;

    /**
     * LoginRequest details
     */
    @Override
    public String toString() {
        return "LogoutRequest [userLang=" + userLang + ",firstLoginJTI=" + uniqueSessionId + "]";
    }

	public String getUniqueSessionId() {
		return uniqueSessionId;
	}

	public void setUniqueSessionId(String uniqueSessionId) {
		this.uniqueSessionId = uniqueSessionId;
	}

	public String getUserLang() {
		return userLang;
	}

	public void setUserLang(String userLang) {
		this.userLang = userLang;
	}

    
}
