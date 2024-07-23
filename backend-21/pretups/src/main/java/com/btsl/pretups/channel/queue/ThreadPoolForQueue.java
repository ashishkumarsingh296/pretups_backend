package com.btsl.pretups.channel.queue;

/*
 * @(#)ThreadPoolForQueue.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Anu Garg 03/08/2013 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.QueueLogger;
import com.btsl.pretups.channel.receiver.ChannelReceiver;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ThreadPoolForQueue {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    public static ThreadPoolExecutor threadPoolExecutor = null;
    private LinkedBlockingQueue<Runnable> threadPoolQueue = null;
    private List<ServiceKeywordControllerI> serviceList = null;
    private long keepAliveTime = ThreadPoolConstants.DEFAULT_KEEP_ALIVE_TIME;
    private long queueTimeoutSec = 3000L;
    private int minWorkerThreads = 0;
    private int maxWorkerThreads = 0;
    private int poolQueueSize = 0;
    private RequestQueueTimeOutManager requestQueueTimeOutManager = null;
    private int thresholdPoolQueueSize = 0; // this variable is for storing the
    // threshold values ..
    private Object threadPoolMonitor = null;
    private static int count = 0;
    public RequestQueueTimeOutManager getRequestQueueTimeOutManager(){
    	return requestQueueTimeOutManager;
    }
    public ThreadPoolForQueue() {
    }

    public ThreadPoolForQueue(
                    String[] serviceNameArr)
                    throws Exception {
        if (null != serviceNameArr && serviceNameArr.length <= 0) {
            _log.error("Constructor", "Service Name cannot be null");
            return;
        }
        populateServiceList(serviceNameArr);
    }

    public ThreadPoolForQueue(
                    String serviceFileName)
                    throws Exception {

    }

    /**
     * This method is used to initialize thread pool executor and also overrides
     * the rejectedExecution() method
     * 
     */
    public void init(DataQueue dataQueue) {
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "init() ", "Entered: ");
        }
        final String METHOD_NAME = "init";
        poolQueueSize = ThreadPoolConstants.DEFAULT_QUEUE_SIZE;

        if (thresholdPoolQueueSize <= 0) {
            // thresholdPoolQueueSize = (int) (0.9 * ThreadPoolConstants.DEFAULT_QUEUE_SIZE);
            thresholdPoolQueueSize = BTSLUtil.parseDoubleToInt(0.9 * ThreadPoolConstants.DEFAULT_QUEUE_SIZE);
        }
        if (minWorkerThreads <= 0) {
            minWorkerThreads = ThreadPoolConstants.DEFAULT_MIN_NUMBER_WORKER_THREADS;
        }
        if (maxWorkerThreads <= 0) {
            maxWorkerThreads = ThreadPoolConstants.DEFAULT_MAX_NUMBER_WORKER_THREADS;
        }
        if (minWorkerThreads > maxWorkerThreads) {
            _log.error("init()", " Error in initialising threadpool,minWorkerThreads cann't be greater than maxWorkerThreads");
            return;
        }
        threadPoolQueue = new LinkedBlockingQueue<Runnable>(poolQueueSize);
        threadPoolExecutor = new ThreadPoolExecutor(minWorkerThreads, maxWorkerThreads, keepAliveTime, TimeUnit.SECONDS, threadPoolQueue);

        if (requestQueueTimeOutManager == null) {
            requestQueueTimeOutManager = new RequestQueueTimeOutManager(this, dataQueue);
        }
        // if time out value is 0 then set default value.
        try {
            if (Long.valueOf(Constants.getProperty("QUEUE_REQUEST_TIMEOUT")) > 0) {
                this.queueTimeoutSec = Long.valueOf(Constants.getProperty("QUEUE_REQUEST_TIMEOUT"));
            }
        } catch (Exception e) {
            this.queueTimeoutSec = 3000L;
            _log.errorTrace(METHOD_NAME, e);
        }
        /*
         * threadPoolExecutor.setRejectedExecutionHandler(new
         * RejectedExecutionHandler(){
         * public void rejectedExecution(Runnable r,ThreadPoolExecutor executor)
         * {
         * if(_log.isDebugEnabled())_log.debug("rejectedExecution",r,
         * "~~~~~~~~~~~~Entered");
         * ThreadPoolClient threadPoolClient = (ThreadPoolClient)r;
         * ChannelReceiver.listOfRequestsSendToPool.remove(threadPoolClient.
         * getRequestQueueVO
         * ().getSenderMsisdn()+"_"+((ThreadPoolClient)r).getRequestQueueVO
         * ().getReceiverMsisdn
         * ()+"_"+((ThreadPoolClient)r).getRequestQueueVO().getServiceType());
         * if(_log.isDebugEnabled())_log.debug("rejection execution",
         * "Request refused from thread pool executor");
         * try
         * {
         * LoadController.checkInstanceLoad(((ThreadPoolClient)r).getRequestQueueVO
         * ().getRequestIDMethod(),LoadControllerI.REFUSED_FROM_QUEUE);
         * if(_log.isDebugEnabled())_log.debug("rejectedExecution",r,
         * "~~~~~~~~~~~~throw exception");
         * threadPoolClient.getRequestQueueVO().getRequestVO().
         * setSenderReturnMessage
         * (BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO
         * ().getRequestVO
         * ().getSenderLocale(),PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE
         * ,null));
         * //throw new
         * BTSLBaseException("ThreadPoolForQueue","rejectedExecution"
         * ,PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE);
         * }
         * catch(BTSLBaseException be)
         * {
         * if(_log.isDebugEnabled())_log.debug("rejectedExecution",r,
         * "~~~~~~~~~~~~caught exception");
         * //threadPoolClient.getRequestQueueVO().getRequestVO().setSuccessTxn(false
         * );
         * if(_log.isDebugEnabled())_log.debug("ThreadPoolForQueue",
         * "BTSLBaseException be:"+be.getMessage());
         * if(be.isKey())
         * {
         * if(_log.isDebugEnabled())_log.debug("rejectedExecution",r,
         * "~~~~~~~~~~~~inside if exception");
         * threadPoolClient.getRequestQueueVO().getRequestVO().
         * setSenderReturnMessage(null);
         * threadPoolClient.getRequestQueueVO().getRequestVO().setMessageCode(be.
         * getMessageKey());
         * threadPoolClient.getRequestQueueVO().getRequestVO().setMessageArguments
         * (be.getArgs());
         * }
         * else
         * {
         * threadPoolClient.getRequestQueueVO().getRequestVO().setMessageCode(
         * PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
         * if(_log.isDebugEnabled())_log.debug("rejectedExecution",r,
         * "~~~~~~~~~~~~inside else exception");
         * }
         * 
         * }
         * finally
         * {
         * if(_log.isDebugEnabled())_log.debug("rejectedExecution",r,
         * "~~~~~~~~~~~~inside finally exception");
         * 
         * }
         * }
         * });
         */
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "init()", "Exited: ");
        }
    }

    /**
     * Process method
     * Used when order of Service Execution is defined dynamically by client,
     * this method
     * initiates the Timeout thread and calls the execute method of thread pool
     * executor
     * 
     * @param ConcurrentLinkedQueue
     *            concurrentLinkedQueue
     * 
     */
    public void process(ThreadPoolClient threadPoolClient, List<ServiceKeywordControllerI> serviceList) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "process ", "Entered:count " + ++count);
        }
        final String METHOD_NAME = "process";
        try {
            threadPoolClient.setServiceList(serviceList);
            final String[] messageArgArray = { threadPoolClient.getRequestQueueVO().getReceiverMsisdn(), String.valueOf(queueTimeoutSec * 0.001) };
            threadPoolClient.getRequestQueueVO().getRequestVO().setSenderReturnMessage(
                BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO().getRequestVO().getSenderLocale(), PretupsErrorCodesI.TXN_STATUS_IN_QUEUE, messageArgArray));
            QueueLogger.Outlog(threadPoolClient.getRequestQueueVO());

            threadPoolExecutor.execute(threadPoolClient);
        } catch (RejectedExecutionException rej) {
            if (_log.isDebugEnabled()) {
                _log.error("rejection execution", "Request refused from thread pool executor");
            }
            _log.errorTrace(METHOD_NAME, rej);
            ChannelReceiver.listOfRequestsSendToPool.remove(threadPoolClient.getRequestQueueVO().getSenderMsisdn() + "_" + threadPoolClient.getRequestQueueVO()
                .getReceiverMsisdn() + "_" + threadPoolClient.getRequestQueueVO().getServiceType());
            threadPoolClient.getRequestQueueVO().getRequestVO().setSenderReturnMessage(
                BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO().getRequestVO().getSenderLocale(), PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE, null));
            QueueLogger.log(BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO().getRequestVO().getSenderLocale(), PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE, null));
        }

        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "process ", "Exited: ");
        }
    }

    // used only when order of Service Execution is predefined
    public void process(ThreadPoolClient threadPoolClient) throws RejectedExecutionException {
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "process", "Entered: ");
        }
        // threadPoolClient.setServiceList(serviceList);
        final String METHOD_NAME = "process";
        try {
            QueueLogger.Outlog(threadPoolClient.getRequestQueueVO());
            threadPoolExecutor.execute(threadPoolClient);
        } catch (RejectedExecutionException rej) {
            if (_log.isDebugEnabled()) {
                _log.error("rejection execution", "Request refused from thread pool executor");
            }
            _log.errorTrace(METHOD_NAME, rej);
            ChannelReceiver.listOfRequestsSendToPool.remove(threadPoolClient.getRequestQueueVO().getSenderMsisdn() + "_" + threadPoolClient.getRequestQueueVO()
                .getReceiverMsisdn() + "_" + threadPoolClient.getRequestQueueVO().getServiceType());
            threadPoolClient.getRequestQueueVO().getRequestVO().setSenderReturnMessage(
                BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO().getRequestVO().getSenderLocale(), PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE, null));
            QueueLogger.log(BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO().getRequestVO().getSenderLocale(), PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE, null));
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                ;
            }
            _log.error("rejection execution", "Request refused from thread pool executor");
            _log.errorTrace(METHOD_NAME, e);
            ChannelReceiver.listOfRequestsSendToPool.remove(threadPoolClient.getRequestQueueVO().getSenderMsisdn() + "_" + threadPoolClient.getRequestQueueVO()
                .getReceiverMsisdn() + "_" + threadPoolClient.getRequestQueueVO().getServiceType());
            QueueLogger.log(BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO().getRequestVO().getSenderLocale(), PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE, null));
        }
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "process", "Exiting: ");
        }
    }

    // used only when process method of Poolable class has overridden to have
    // control over Service Execution
    public void processCustomService(ThreadPoolClient threadPoolClient) throws RejectedExecutionException {
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "processCustomService", "Entered: ");
        }
        try {
            threadPoolExecutor.execute(threadPoolClient);
        } catch (RejectedExecutionException rex) {
            // logger.error("Exception in put() RejectedExecutionException ",
            // rex);
            throw rex;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "processCustomService", "Exited: ");
        }
    }

    /**
     * checkTimeOut() method
     * this method removes and returns the list of all tasks(requests) for which
     * timeout has reached
     * 
     * @return ArrayList<ThreadPoolClient>
     */
    public ArrayList<ThreadPoolClient> checkTimeOut(DataQueue dataQueue) {
        Long timeOut = 0L;
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "checkTimeOut", "Entered: ");
        }
        // Iterator<ThreadPoolClient> iterator =
        // ChannelReceiver.listOfRequestsSendToPool.iterator();
        final ArrayList<ThreadPoolClient> listOfRequestsRemoved = new ArrayList<ThreadPoolClient>();
        QueueLogger.log("entered in CheckTimeOut() method");

        if (!dataQueue.isEmpty()) {
            final Iterator<ThreadPoolClient> iterator = dataQueue.getIterator();
            if (Long.valueOf(Constants.getProperty("QUEUE_TIMEOUT_LOOP")) > 0) {
                timeOut = Long.valueOf(Constants.getProperty("QUEUE_TIMEOUT_LOOP"));
            }
            while (iterator.hasNext()) {
                timeOut--;
                final ThreadPoolClient threadPoolClient = (ThreadPoolClient) iterator.next();
                if (System.currentTimeMillis() > threadPoolClient.getRequestQueueVO().getQueueAdditionTime() + threadPoolClient.getRequestQueueVO().getRequestTimeout()) {
                    final boolean removed = dataQueue.remove(threadPoolClient);
                    if (removed) {
                        listOfRequestsRemoved.add(threadPoolClient);
                        final String listValue = threadPoolClient.getRequestQueueVO().getSenderMsisdn() + "_" + threadPoolClient.getRequestQueueVO().getReceiverMsisdn() + "_" + threadPoolClient
                            .getRequestQueueVO().getServiceType();
                        if (ChannelReceiver.listOfRequestsSendToPool.contains(listValue)) {
                            ChannelReceiver.listOfRequestsSendToPool.remove(listValue);
                        }
                    }
                }
                if (timeOut == 0) {
                    break;
                }
            }
        } else {
            requestQueueTimeOutManager.shutdown();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("ThreadPoolForQueue", "checkTimeOut", "Exited list size is: " + listOfRequestsRemoved.size());
        }
        return listOfRequestsRemoved;
    }

    public void resetMaxWorkerThreads(int maxNumOfWorkerThreads) {
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setMaximumPoolSize(maxNumOfWorkerThreads);
        }
    }

    public void resetMinWorkerThreads(int minNumOfWorkerThreads) {
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setCorePoolSize(minNumOfWorkerThreads);
        }
    }

    public void shutdown() {
        if (null != threadPoolExecutor) {
            threadPoolExecutor.shutdown();
        }
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getMinWorkerThreads() {
        return minWorkerThreads;
    }

    public void setMinWorkerThreads(int minWorkerThreads) {
        this.minWorkerThreads = minWorkerThreads;
    }

    public int getMaxWorkerThreads() {
        return maxWorkerThreads;
    }

    public void setMaxWorkerThreads(int maxWorkerThreads) {
        this.maxWorkerThreads = maxWorkerThreads;
    }

    public int getPoolQueueSize() {
        return poolQueueSize;
    }

    public void setPoolQueueSize(int poolQueueSize) {
        this.poolQueueSize = poolQueueSize;
    }

    public Object getThreadPoolMonitor() {
        return threadPoolMonitor;
    }

    public void setThreadPoolMonitor(Object threadPoolMonitor) {
        this.threadPoolMonitor = threadPoolMonitor;
    }

    public int getThresholdPoolQueueSize() {
        return thresholdPoolQueueSize;
    }

    public void setThresholdPoolQueueSize(int thresholdPoolQueueSize) {
        this.thresholdPoolQueueSize = thresholdPoolQueueSize;
    }

    public int getPoolSize() {
        return threadPoolQueue.size();
    }

    /**
     * populateServiceList() method
     * this method populate the serviceList with the submitted array of service
     * controllers
     * 
     * @param String
     *            [] serviceNameArr
     */
    private void populateServiceList(String[] serviceNameArr) throws Exception {
        serviceList = new ArrayList<ServiceKeywordControllerI>();
        final Class[] classArr = {};
        final Object[] objArr = {};
        for (int i = 0; i < serviceNameArr.length; i++) {
            try {
                final Class classObj = Class.forName(serviceNameArr[i]);
                final Constructor constObj = classObj.getConstructor(classArr);
                final ServiceKeywordControllerI serviceObject = (ServiceKeywordControllerI) constObj.newInstance(objArr);
                serviceList.add(serviceObject);
            } catch (ClassNotFoundException cnfe) {
                _log.error("Exception in getServiceNameList ClassNotFoundException", cnfe);
                throw cnfe;
            } catch (NoSuchMethodException nex) {
                _log.error("Exception from ServiceHandler NoSuchMethodException : ", nex);
                throw nex;
            } catch (IllegalAccessException iaex) {
                _log.error("Exception from ServiceHandler IllegalAccessException : ", iaex);
                throw iaex;
            } catch (InvocationTargetException ite) {
                _log.error("Exception from ServiceHandler InvocationTargetException : ", ite);
                throw ite;
            } catch (InstantiationException iex) {
                _log.error("Exception from ServiceHandler InstantiationException : ", iex);
                throw iex;
            }
        }// end of for loop
    }

    public void destroy() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }
        if (requestQueueTimeOutManager != null) {
            requestQueueTimeOutManager.shutdown();
        }
        if (ChannelReceiver.listOfRequestsSendToPool != null) {
            ChannelReceiver.listOfRequestsSendToPool = null;
        }
    }
}
