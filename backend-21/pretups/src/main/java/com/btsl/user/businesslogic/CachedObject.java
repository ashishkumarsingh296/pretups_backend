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

import java.text.MessageFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * *
 * 
 * @author Subesh KCV
 * @version 1.0
 */
public class CachedObject implements Cacheable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedObject.class);
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /*
     * This variable will be used to determine if the object is expired.
     */
    private java.util.Date dateofExpiration;
    private Object identifier;
    /*
     * This contains the real "value". This is the object which needs to be
     * shared.
     */
    private Object objectVal;

    /**
     * Constructor...
     * 
     * @param obj
     *            -input
     * 
     * @param id
     *            -input
     * 
     * @param minutesToLive
     *            -input
     * 
     */
    public CachedObject(Object obj, Object id, int minutesToLive) {
        this.objectVal = obj;
        this.identifier = id;
        // minutesToLive of 0 means it lives on indefinitely.
        if (minutesToLive != 0) {
            dateofExpiration = new java.util.Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateofExpiration);
            cal.add(Calendar.MINUTE, minutesToLive);
            dateofExpiration = cal.getTime();
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public boolean isExpired() {
        // Remember if the minutes to live is zero then it lives forever!
        Boolean returnVal = false;
        if (dateofExpiration != null) {
            // date of expiration is compared.
            if (dateofExpiration.before(new java.util.Date())) {
                LOGGER.debug(MessageFormat.format(
                        "CachedResultSet.isExpired:  Expired from Cache! EXPIRE TIME: {0} CURRENT TIME: {1}",
                        dateofExpiration, dateofExpiration));
                returnVal = true;
            } else {
                LOGGER.info("CachedResultSet.isExpired:  Expired not from Cache!");
                returnVal = false;
            }
        } else {
            returnVal = false;
        }
        return returnVal;
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public Object getIdentifier() {
        return identifier;
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public Object getObjectVal() {
        return objectVal;
    }

    /**
     * Constructor...
     * 
     * @param objectVal
     *            -input
     * 
     */
    public void setObjectVal(Object objectVal) {
        this.objectVal = objectVal;
    }

}
