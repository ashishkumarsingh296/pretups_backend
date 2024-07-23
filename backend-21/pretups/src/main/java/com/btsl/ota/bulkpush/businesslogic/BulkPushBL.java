package com.btsl.ota.bulkpush.businesslogic;

import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.util.OtaMessage;
import com.btsl.ota.util.SimUtil;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;

/**
 * @(#)BulkPushBL
 *                Copyright(c) 2003, Bharti Telesoft Ltd.
 *                All Rights Reserved
 *                Business logic class for Bulk Pushing of the messages
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Author Date History
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Gurjeet 18/12/2003 Initial Creation
 *                --------------------------------------------------------------
 *                -----------------------------------
 */
public class BulkPushBL {
	private static final Log _logger = LogFactory.getLog(BulkPushBL.class.getName());

    /**
     * This method creates an ArrayList that needs to be inserted in the job
     * database
     * 
     * @return ArrayList
     * @param p_bulkPushVO
     *            BulkPushVO
     * @param p_mobileList
     *            ArrayList
     * @param p_operationType
     *            String
     */

    public ArrayList populateJobList(BulkPushVO p_bulkPushVO, ArrayList p_mobileList, String p_operationType) throws BaseException, Exception {
        final String METHOD_NAME = "populateJobList";
        ArrayList finalValueList = new ArrayList();
        BulkPushVO bulkPushVO = null;
        // int jobSize=p_bulkPushVO.getJobSize();
        int batchSize = p_bulkPushVO.getBatchSize();
        // String jSize=""+jobSize;
        String bsize = "" + batchSize;
        String totalMobileinJob = "" + p_mobileList.size();
        String name = p_bulkPushVO.getJobName();
        String locationCode = p_bulkPushVO.getLocationCode().trim();
        String userType = p_bulkPushVO.getUserType().trim();
        String profile = p_bulkPushVO.getProfile().trim();
        String serviceSetId = BTSLUtil.NullToString(p_bulkPushVO.getServiceSetID());
        String serviceId = BTSLUtil.NullToString(p_bulkPushVO.getServiceID());
        String majVersion = BTSLUtil.NullToString(p_bulkPushVO.getMajorVersion());
        String minVersion = BTSLUtil.NullToString(p_bulkPushVO.getMinorVersion());
        String message = BTSLUtil.NullToString(p_bulkPushVO.getByteCode());
        String status = BTSLUtil.NullToString(p_bulkPushVO.getStatus());
        String createdBy = BTSLUtil.NullToString(p_bulkPushVO.getCreatedBy());
        String byteCode = p_bulkPushVO.getByteCode();
        String label1 = p_bulkPushVO.getLabel1();
        String label2 = p_bulkPushVO.getLabel2();
        int position = p_bulkPushVO.getPosition();
        long offset = p_bulkPushVO.getOffset();
        long length = p_bulkPushVO.getLength();

        String transactionID = null;
        String jobId = p_bulkPushVO.getJobId();
        if (_logger.isDebugEnabled()) {
            _logger.debug("", "jobId:" + jobId);
        }
        SimProfileVO simProfileVO = p_bulkPushVO.getSimProfileVO();

        String operationHexCode = p_bulkPushVO.getOperationsHexCode();
        Calendar rightNow = BTSLDateUtil.getInstance();
        String day = SimUtil.lengthConverter(rightNow.get(Calendar.DAY_OF_MONTH));
        String hour = SimUtil.lengthConverter(rightNow.get(Calendar.HOUR_OF_DAY));
        String min = SimUtil.lengthConverter(rightNow.get(Calendar.MINUTE));
        String sec = SimUtil.lengthConverter(rightNow.get(Calendar.SECOND));
        transactionID = day + hour + min + sec;

        if (_logger.isDebugEnabled()) {
            _logger.debug("", "Transaction id generated in BL:" + transactionID);
        }
        String newBatchId = "0";
        String newJobId = "0";
        int mobileCount = 0;
        int batchId = 1;
        String msisdn = null;
        // for(int i=1;i<jobSize;i++)
        // {
        ArrayList jobList = new ArrayList();

        ListValueVO listVal = new ListValueVO(jobId + " " + locationCode + "|" + name, totalMobileinJob + "|" + "0" + " " + createdBy);
        jobList.add(listVal);
        p_bulkPushVO.setJobList(jobList);
        ArrayList finalList = new ArrayList();
        ArrayList batchList = new ArrayList();
        ListValueVO listValVO = null;
        String batchName = null;
        ArrayList compareList = null;
        int totalBatchMobile = 0;
        ServicesVO oldServiceVO = null;
        String msisdnStr = null;
        String serviceHex = null;
        // String messageForJob=null;
        /*
         * This is used only in case of add/modify service where no. of services
         * is 1 only
         * This checks whether the Major version , minor version needs to be
         * send
         */
        if ((p_bulkPushVO.getCompareStringList().size() == 1) && p_bulkPushVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.ADD)) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("", "In list size=1, case ADD/MODIFY Service");
            }
            oldServiceVO = (ServicesVO) p_bulkPushVO.getCompareStringList().get(0);
            String oldHexCode = oldServiceVO.getCompareHexString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("", "Compare string List has only one entry operation" + p_bulkPushVO.getOperation());
            }
             int p_mobileListSizes=p_mobileList.size();
            for (int i = 0; i < p_mobileListSizes; i++) {
                msisdnStr = (String) p_mobileList.get(i);
                compareList = new ArrayList();
                bulkPushVO = new BulkPushVO();
                msisdn = msisdnStr.substring(0, msisdnStr.indexOf("|"));
                try {
                    serviceHex = BTSLUtil.NullToString(msisdnStr.substring(msisdnStr.indexOf("|") + 1));
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                    serviceHex = "";
                }
                if (oldHexCode.equalsIgnoreCase(serviceHex)) {
                    // there
                    continue;
                } else if (!oldHexCode.substring(0, 4).equalsIgnoreCase(serviceHex.substring(0, 4))) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("", "Major version change ");
                    }
                    ServicesVO serviceVO = new ServicesVO();
                    serviceVO.setServiceID(serviceId);
                    serviceVO.setMajorVersion(majVersion);
                    serviceVO.setMinorVersion(minVersion);
                    serviceVO.setPosition(position);
                    serviceVO.setOperation(ByteCodeGeneratorI.ADD);
                    serviceVO.setOffSet(offset);
                    serviceVO.setLength(length);
                    serviceVO.setLabel1(label1);
                    serviceVO.setLabel2(label2);
                    serviceVO.setByteCode(byteCode);
                    serviceVO.setStatus(status);
                    compareList.add(serviceVO);
                } else if (!oldHexCode.substring(0, 6).equalsIgnoreCase(serviceHex.substring(0, 6))) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("", "Minor version change ");
                    }
                    ServicesVO serviceVO = new ServicesVO();
                    serviceVO.setServiceID(serviceId);
                    serviceVO.setMajorVersion(majVersion);
                    serviceVO.setMinorVersion(minVersion);
                    serviceVO.setPosition(position);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("", "position::::::=" + position);
                    }
                    serviceVO.setOperation(ByteCodeGeneratorI.CHANGE_TITLE);
                    serviceVO.setOffSet(offset);
                    serviceVO.setLabel1(label1);
                    serviceVO.setLabel2(label2);
                    serviceVO.setByteCode(byteCode);
                    serviceVO.setLength(length);
                    serviceVO.setStatus(status);
                    compareList.add(serviceVO);
                } else {
                    continue;
                }
                try {
                    operationHexCode = new SimUtil().returnOperationByteCode(compareList, simProfileVO);
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("csms.error.internalServer");
                }
                if (operationHexCode == null) {
                    // throw new BaseException("csms.error.internalServer",new
                    // Exception());
                    throw new BTSLBaseException("csms.error.internalServer");
                }

                if (_logger.isDebugEnabled()) {
                    _logger.debug("", "In operation hex code  case ADD/MODIFY Service" + operationHexCode);
                }

                try {
                    message = new OtaMessage().generateByteCode(compareList, false, simProfileVO);
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("csms.error.internalServer");
                }
                if (message == null) {
                    // throw new
                    // BTSLBaseException("csms.error.internalServer",new
                    // Exception());
                    throw new BTSLBaseException("csms.error.internalServer");
                }

                if (mobileCount == batchSize) {
                    totalBatchMobile = totalBatchMobile + mobileCount;
                    mobileCount = 0;
                    finalList.add(finalValueList);
                    finalValueList = new ArrayList();
                    batchName = ByteCodeGeneratorI.BATCH + batchId;
                    listValVO = new ListValueVO(batchId + " " + batchName + "|" + bsize, bsize + " " + jobId + "|" + createdBy);
                    batchList.add(listValVO);
                    batchId = batchId + 1;
                }
                bulkPushVO.setJobId(jobId);
                bulkPushVO.setBatchId("" + batchId);
                bulkPushVO.setLocationCode(locationCode);
                bulkPushVO.setUserType(userType);
                bulkPushVO.setProfile(profile);
                bulkPushVO.setMsisdn(msisdn);
                bulkPushVO.setTransactionId(transactionID);
                bulkPushVO.setServiceSetID(serviceSetId);
                bulkPushVO.setServiceID(serviceId);
                bulkPushVO.setMajorVersion(majVersion);
                bulkPushVO.setMinorVersion(minVersion);
                bulkPushVO.setByteCode(message);
                bulkPushVO.setOperationsHexCode(operationHexCode);
                bulkPushVO.setOperationType(p_operationType);
                bulkPushVO.setStatus(ByteCodeGeneratorI.NEWSTATUS);
                bulkPushVO.setCreatedBy(createdBy);
                bulkPushVO.setNewBatchId(newBatchId);
                bulkPushVO.setNewJobId(newJobId);
                mobileCount = mobileCount + 1;
                finalValueList.add(bulkPushVO);
            }
            int leftMobileNos = p_mobileList.size() - totalBatchMobile;
            String leftMobile = "" + leftMobileNos;
            batchName = ByteCodeGeneratorI.BATCH + batchId;
            listValVO = new ListValueVO(batchId + " " + batchName + "|" + bsize, leftMobile + " " + jobId + "|" + createdBy);
            batchList.add(listValVO);
            finalList.add(finalValueList);
        } else {
            for (int i = 0; i < p_mobileList.size(); i++) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("", "CAse other than ADD/MODIFY");
                }
                msisdnStr = (String) p_mobileList.get(i);
                msisdn = msisdnStr.substring(0, msisdnStr.indexOf("|"));
                bulkPushVO = new BulkPushVO();
                if (mobileCount == batchSize) {
                    totalBatchMobile = totalBatchMobile + mobileCount;
                    mobileCount = 0;
                    if (finalValueList != null && !finalValueList.isEmpty()) {
                        finalList.add(finalValueList);
                    }
                    finalValueList = new ArrayList();
                    batchName = ByteCodeGeneratorI.BATCH + batchId;
                    listValVO = new ListValueVO(batchId + " " + batchName + "|" + bsize, bsize + " " + jobId + "|" + createdBy);
                    batchList.add(listValVO);
                    batchId = batchId + 1;
                }
                bulkPushVO.setJobId(jobId);
                bulkPushVO.setBatchId("" + batchId);
                bulkPushVO.setLocationCode(locationCode);
                bulkPushVO.setUserType(userType);
                bulkPushVO.setProfile(profile);
                bulkPushVO.setMsisdn(msisdn);
                bulkPushVO.setTransactionId(transactionID);
                bulkPushVO.setServiceSetID(serviceSetId);
                bulkPushVO.setServiceID(serviceId);
                bulkPushVO.setMajorVersion(majVersion);
                bulkPushVO.setMinorVersion(minVersion);
                bulkPushVO.setByteCode(message);
                bulkPushVO.setOperationsHexCode(operationHexCode);
                bulkPushVO.setOperationType(p_operationType);
                bulkPushVO.setStatus(ByteCodeGeneratorI.NEWSTATUS);
                bulkPushVO.setCreatedBy(createdBy);
                bulkPushVO.setNewBatchId(newBatchId);
                bulkPushVO.setNewJobId(newJobId);
                mobileCount = mobileCount + 1;
                finalValueList.add(bulkPushVO);

            }
            int leftMobileNos = p_mobileList.size() - totalBatchMobile;
            String leftMobile = "" + leftMobileNos;
            batchName = ByteCodeGeneratorI.BATCH + batchId;
            listValVO = new ListValueVO(batchId + " " + batchName + "|" + bsize, leftMobile + " " + jobId + "|" + createdBy);
            batchList.add(listValVO);
            if (finalValueList != null && !finalValueList.isEmpty()){
                finalList.add(finalValueList);
            }
        }
        // p_bulkPushVO.setJobList(jobList);
        p_bulkPushVO.setBatchList(batchList);
        return finalList;
    }

    /**
     * This method creates an ArrayList that needs to be inserted in the job
     * database
     * and also updates the previous mobile list in new baych and new job fields
     * , used in Retry option only
     * 
     * @return BulkPushVO
     * @param p_bulkPushVO
     *            BulkPushVO
     * @param p_mobileList
     *            ArrayList
     */

    public BulkPushVO populateRetryJobList(BulkPushVO p_bulkPushVO, ArrayList p_mobileList) throws BaseException, Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug("populateRetryJobList ", "entered with p_OldmobileList=" + p_mobileList.size());
        }
        ArrayList finalValueList = new ArrayList();
        BulkPushVO bulkPushVO = null;

        int batchSize = p_bulkPushVO.getBatchSize();

        String bsize = "" + batchSize;
        String totalMobileinJob = "" + p_mobileList.size();
        String name = p_bulkPushVO.getJobName();
        String locationCode = p_bulkPushVO.getLocationCode().trim();
        String createdBy = p_bulkPushVO.getCreatedBy();

        String transactionID = null;
        String jobId = p_bulkPushVO.getJobId();

        Calendar rightNow = BTSLDateUtil.getInstance();
        String day = SimUtil.lengthConverter(rightNow.get(Calendar.DAY_OF_MONTH));
        String hour = SimUtil.lengthConverter(rightNow.get(Calendar.HOUR_OF_DAY));
        String min = SimUtil.lengthConverter(rightNow.get(Calendar.MINUTE));
        String sec = SimUtil.lengthConverter(rightNow.get(Calendar.SECOND));
        transactionID = day + hour + min + sec;

        if (_logger.isDebugEnabled()) {
            _logger.debug("", "Transaction id generated in BL:" + transactionID);
        }
        String newBatchId = "0";
        String newJobId = "0";
        int mobileCount = 0;
        int batchId = 1;

        ArrayList jobList = new ArrayList();

        ListValueVO listVal = new ListValueVO(jobId + " " + locationCode + "|" + name, totalMobileinJob + "|" + "0" + " " + createdBy);
        jobList.add(listVal);
        p_bulkPushVO.setJobList(jobList);

        ArrayList finalList = new ArrayList();
        ArrayList batchList = new ArrayList();

        ListValueVO listValVO = null;
        String batchName = null;

        int totalBatchMobile = 0;
        /*
         * This updates the oldjoblist with the new batch and new job id fields
         * as well as
         * create a new arrayList with new entries
         */

        BulkPushVO innerBulkPushVO = null;
        int mobileListSizes=p_mobileList.size();
        for (int i = 0; i <mobileListSizes ; i++) {
            innerBulkPushVO = (BulkPushVO) p_mobileList.get(i);

            bulkPushVO = new BulkPushVO();
            if (mobileCount == batchSize) {
                totalBatchMobile = totalBatchMobile + mobileCount;
                mobileCount = 0;
                if (finalValueList != null && !finalValueList.isEmpty())  {
                    finalList.add(finalValueList);
                }
                finalValueList = new ArrayList();
                batchName = ByteCodeGeneratorI.BATCH + batchId;
                listValVO = new ListValueVO(batchId + " " + batchName + "|" + bsize, bsize + " " + jobId + "|" + createdBy);
                batchList.add(listValVO);
                batchId = batchId + 1;
            }
            bulkPushVO.setJobId(jobId);
            bulkPushVO.setBatchId("" + batchId);
            bulkPushVO.setLocationCode(innerBulkPushVO.getLocationCode());
            bulkPushVO.setUserType(innerBulkPushVO.getUserType());
            bulkPushVO.setProfile(innerBulkPushVO.getProfile());
            bulkPushVO.setMsisdn(innerBulkPushVO.getMsisdn());
            bulkPushVO.setTransactionId(transactionID);
            bulkPushVO.setServiceSetID(innerBulkPushVO.getServiceSetID());
            bulkPushVO.setServiceID(innerBulkPushVO.getServiceID());
            bulkPushVO.setMajorVersion(innerBulkPushVO.getMajorVersion());
            bulkPushVO.setMinorVersion(innerBulkPushVO.getMinorVersion());
            bulkPushVO.setByteCode(innerBulkPushVO.getByteCode());
            bulkPushVO.setOperationsHexCode(innerBulkPushVO.getOperationsHexCode());
            bulkPushVO.setOperationType(innerBulkPushVO.getOperationType());
            bulkPushVO.setStatus(ByteCodeGeneratorI.NEWSTATUS);
            bulkPushVO.setCreatedBy(createdBy);
            bulkPushVO.setNewBatchId(newBatchId);
            bulkPushVO.setNewJobId(newJobId);
            innerBulkPushVO.setNewBatchId("" + batchId);
            innerBulkPushVO.setNewJobId(jobId);
            innerBulkPushVO.setStatus(ByteCodeGeneratorI.NEWSTATUS);
            mobileCount = mobileCount + 1;
            finalValueList.add(bulkPushVO);

        }
        int leftMobileNos = p_mobileList.size() - totalBatchMobile;
        String leftMobile = "" + leftMobileNos;
        batchName = ByteCodeGeneratorI.BATCH + batchId;
        listValVO = new ListValueVO(batchId + " " + batchName + "|" + bsize, leftMobile + " " + jobId + "|" + createdBy);
        batchList.add(listValVO);
        if (finalValueList != null && !finalValueList.isEmpty())  {
            finalList.add(finalValueList);
        }
        // p_bulkPushVO.setJobList(jobList);
        p_bulkPushVO.setBatchList(batchList);
        if (_logger.isDebugEnabled()) {
            _logger.debug("populateRetryJobList", " exit with finalList=" + finalList.size());
        }
        p_bulkPushVO.setNewOtaJobList(finalList);
        return p_bulkPushVO;

    }

}
