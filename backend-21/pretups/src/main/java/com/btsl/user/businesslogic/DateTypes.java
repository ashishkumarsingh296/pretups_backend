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

/**
 * Date Format types
 * 
 * @author SubeshKCV
 * @date : 05-Oct-2019
 */
@Getter
public enum DateTypes {

    DDMMYYYY("ddMMyyyy"), 
    
    YYMMDD("yyMMdd"),
    
    HHMM("HHmm"),
    
    DD_MM_YYYY("dd-MM-yyyy"),
    
    YYYY_MM_DD("yyyy-MM-dd"),
    
    MMDDYYYYHHMMSS("MMddyyyyHHmmss"),
    
    DDMMYY( "dd'/'MM'/'yy"),
    
    DDSLMMSLYYYY("dd/MM/yyyy"),
    
    DATE_FORMAT_ONE("([0-9]{2})/([0-9]{2})/([0-9]{4})"),
    
    DATE_FORMAT_TWO("([0-9]{2})-([0-9]{2})-([0-9]{4})"),
    
    DATE_FORMAT_THREE("([0-9]{4})-([0-9]{2})-([0-9]{2})"),
    
    DATE_FORMAT_FOUR("([0-9]{4})/([0-9]{2})/([0-9]{2})"),
    
    DATE_FORMAT_FIVE("([0-9]{2})/([0-9]{2})/([0-9]{2})"),
    
    DATE_FORMAT_SIX("([0-9]{2})-([0-9]{2})-([0-9]{2})"),
    
    DATE_FORMAT_SEVEN("([0-9]{4})/([a-zA-Z]{3})/([0-9]{2})"),
    
    DATE_FORMAT_EIGHT("([0-9]{2})/([a-zA-Z]{3})/([0-9]{4})"),
    
    DATE_FORMAT_NINE("([0-9]{2})/([a-zA-Z]{3})/([0-9]{2})"),
    
    DATE_FORMAT_TEN("([0-9]{4})-([a-zA-Z]{3})-([0-9]{2})"),
    
    DATE_FORMAT_ELEVEN("([0-9]{2})-([a-zA-Z]{3})-([0-9]{4})"),
    
    DATE_FORMAT_TWELVE("([0-9]{2})-([a-zA-Z]{3})-([0-9]{2})"),
    
    MD5("MD5"),
    
    SHA_256("SHA-256"),
    
    
    
    
    
    ;

    // Type
    private String format;

    /**
     * Construct the DateTypes
     * 
     * @param format
     *            - format
     */
    DateTypes(String format) {
        this.format = format;
    }

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

    
}
