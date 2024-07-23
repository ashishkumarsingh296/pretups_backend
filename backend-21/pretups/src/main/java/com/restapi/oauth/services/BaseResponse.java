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
package com.restapi.oauth.services;

import java.util.List;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response Model of base.
 *
 * @author SubeshKCV
 */
@Setter
@Getter
@ToString
public class BaseResponse {

    /** The reference id. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Reference Number Genarated By MDS Application", required = true/* , position = 1 */, example = "MDS01.001")
    protected String referenceId;

    /** The external ref id. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "External Reference Id : Reference Id from Request originator", required = false/* , position = 2 */, example = "WEB01.001")
    protected String externalRefId;

    /** The status. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Response Status", required = true/* , position = 3 */, example = "200")
    protected String status;

    /** The message code. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Message Code", required = true/* , position = 4 */, example = "1035")
    protected String messageCode;

    /** The message. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Response Message", required = true/* , position = 5 */, example = "Success")
    protected String message;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getExternalRefId() {
		return externalRefId;
	}

	public void setExternalRefId(String externalRefId) {
		this.externalRefId = externalRefId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
   
    

}
