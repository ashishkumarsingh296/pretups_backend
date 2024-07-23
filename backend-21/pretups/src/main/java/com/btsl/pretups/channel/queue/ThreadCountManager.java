package com.btsl.pretups.channel.queue;

import java.util.concurrent.ThreadPoolExecutor;

import com.btsl.common.BTSLBaseException;
import com.btsl.loadcontroller.LoadController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

/*
 * @(#)ThreadCountManager.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gaurav Pandey 01/01/2014 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2014 Mahindra Comviva Ltd.
 */
public class ThreadCountManager extends Thread {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private long avilableCounter = 0l;
    private int minthreads = 0;
    private int corepoolSize = 0;
    private ThreadPoolForQueue threadPoolForQueue = null;
    private ThreadPoolExecutor threadPoolExecutor = null;
    private DataQueue dataQueue = null;
    private boolean stop = false;
    private RequestQueueTimeOutManager requestQueueTimeOutManager = null;
    private Long sleeptime = 0L;

    /*
     * thread count manager constructor
     */
    public ThreadCountManager(
                    ThreadPoolForQueue threadPoolForQueue, DataQueue dataQueue) {
        final String METHOD_NAME = "ThreadCountManager";
        this.threadPoolForQueue = threadPoolForQueue;
        this.threadPoolExecutor = this.threadPoolForQueue.threadPoolExecutor;
        this.dataQueue = dataQueue;
        try {
            if (Long.valueOf(Constants.getProperty("THREAD_MANAGER_SLEEP_TIME")) == 0) {
                this.sleeptime = 50L;
            } else {
                this.sleeptime = Long.valueOf(Constants.getProperty("THREAD_MANAGER_SLEEP_TIME"));
            }
        } catch (Exception e) {
            this.sleeptime = 50L;
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
        final String METHOD_NAME = "run";
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadCountManager", "run() ", "Entered: ");
        }
        try {
            // to start time out manager
            if (!dataQueue.isEmpty() && "NEW".equals(threadPoolForQueue.getRequestQueueTimeOutManager().getState().toString())) {
                threadPoolForQueue.getRequestQueueTimeOutManager().start();
            } else if (!dataQueue.isEmpty() && ("TERMINATED".equals(threadPoolForQueue.getRequestQueueTimeOutManager().getState().toString()))) {

                requestQueueTimeOutManager = new RequestQueueTimeOutManager(threadPoolForQueue, dataQueue);
                requestQueueTimeOutManager.start();
            }

            // end

            avilableCounter = LoadController.getCurrentInstanceCounter();
            corepoolSize = threadPoolExecutor.getCorePoolSize();
            ThreadPoolClient threadPoolClient = null;

            if (avilableCounter > 0) {
                // minthreads = Math.min((int) avilableCounter, corepoolSize);
            	 minthreads = Math.min(Integer.valueOf(String.valueOf(avilableCounter)), corepoolSize);
                while (minthreads > 0) {
                    if (dataQueue.isEmpty()) {
                        stop = true;
                        break;
                    }
                    threadPoolClient = (ThreadPoolClient) dataQueue.dequeue();
                    threadPoolForQueue.process(threadPoolClient);
                    minthreads--;
                }
            }

            this.sleep(sleeptime);
            if (!stop) {
                this.run();
            }
        } catch (BTSLBaseException be) {
            if (_log.isDebugEnabled()) {
                _log.error("rejection execution", "Request refused from thread pool executor");
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                ;
            }
            _log.error("rejection execution", "Request refused from thread pool executor");
            _log.errorTrace(METHOD_NAME, e);
        }

    }

}
