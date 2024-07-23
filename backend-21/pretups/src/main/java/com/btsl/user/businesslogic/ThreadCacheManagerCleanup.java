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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Title: Caching Description: Copyright: Copyright (c) 2001 Company: Comviva
 * Filename: ThreadCacheManagerCleanup.java
 * 
 * @author Subesh KCV
 * @version 1.0
 */
public class ThreadCacheManagerCleanup implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadCacheManagerCleanup.class);

    /**
     * milliSecondSleepTime
     */
    private final int milliSecondSleepTime = 500_000_000;

    private volatile boolean exit;

    /**
     * run
     */
    @Override
    public void run() {
        try {
            while (exit) {
                LOGGER.debug("ThreadCleanerUpper Scanning For Expired Objects...");
                CacheManager.getCacheHashMap().forEach((key, value) -> {
                    Cacheable cacheableObj = (Cacheable) value;
                    if (cacheableObj.isExpired()) {
                        LOGGER.debug("ThreadCleanerUpper Running. Found an Expired Object in the Cache.");
                    }

                });
                Thread.sleep(this.milliSecondSleepTime);
            }
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue(), e);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue(), e);
        }

    }

    /**
     * stop
     */
    public void stop() {
        exit = true;
    }

}
