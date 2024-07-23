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



/**
 * Configuration class for SessionHolder.
 *
 * @author sudharshans
 */
public final class VMSSessionHolder {

    /** The thread local. */
    private static ThreadLocal<ConfigParams> threadLocal = new ThreadLocal<>();

    /**
     * Instantiates a new session holder.
     */
    private VMSSessionHolder() {
    }

    /**
     * Add configuration parameter to session.
     *
     * @param configParams
     *            - configuration parameters
     */
    public static void put(ConfigParams configParams) {
        threadLocal.set(configParams);
    }

    /**
     * Get the configParams from session.
     *
     * @return ConfigParams - return configuration parameters
     */
    public static ConfigParams get() {
        return threadLocal.get();
    }

}
