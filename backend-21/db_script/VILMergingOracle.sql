ALTER TABLE IDS MODIFY LAST_NO NUMBER(20);

ALTER TABLE VOMS_BATCHES modify REMARKS varchar2(100);

ALTER TABLE VOMS_BATCHES modify FIRST_APPROVER_REMARKS varchar2(100);

ALTER TABLE VOMS_BATCHES modify SECOND_APPROVER_REMARKS varchar2(100);

ALTER TABLE VOMS_BATCHES modify THIRD_APPROVER_REMARKS varchar2(100);


SET DEFINE OFF;
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('WH', 'Ware House', 'RSTAT', 'Y', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('PE', 'Print Enable', 'RSTAT', 'Y', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('UP', 'Under Procsess', 'VSTAT', 'Y', TO_DATE('12/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('12/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;
 
SET DEFINE OFF;
Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
Values
   ('VSCH', 'C2S', 'PRE', 'TYPE FROM_SERIALNO TO_SERIALNO STATUS EXTNWCODE STATE_CHANGE_REASON', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherStatusChangeHandler', 
    'Voucher Status Change Service', 'VOMS Status Changes', 'Y', TO_DATE('01/01/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('01/01/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Voucher Status Change Service', 'Y', 'Y', 
    'Y', NULL, 'N', 'NA', 'N', 
    'com.btsl.pretups.gateway.parsers.IVRPlainStringParser.java', NULL, NULL, 'TYPE,FROM_SERIALNO,TO_SERIALNO,PRE_STATUS,REQ_STATUS,TXNSTATUS,EXTNWCODE,MESSAGE', 'TYPE,FROM_SERIALNO,TO_SERIALNO,STATUS,EXTNWCODE,STATE_CHANGE_REASON', 
    'Y');

Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
Values
   ('VOMSSTCHGREQ', 'EXTGW', '190', 'VSCH', 'VoucherStatusChange', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('09/28/2017 17:11:06', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('09/28/2017 17:11:06', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK4100232', 
    NULL, 'TYPE,FROM_SERIALNO,TO_SERIALNO,STATUS,EXTNWCODE,STATE_CHANGE_REASON');
COMMIT;

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR', 'Prefix Validation for Bar UnBar', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Prefix Validation for Bar UnBar', 'N', 
    'Y', 'P2P', 'Prefix Validation for Bar UnBar', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2005 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
COMMIT;


Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
SELECT ID_YEAR,'VMSSRLCNTR',NETWORK_CODE,0,SYSDATE,'NA','Voucher Generation Serial No Counter' from IDS where ID_TYPE='USERID';
COMMIT;
SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('VOMS_IS_MRPID_IN_SERIAL', 'IS MRP ID IN VOUCHER SERIAL NO', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'IS MRP ID IN VOUCHER SERIAL NO', 'Y', 
    'Y', 'C2S', 'IS MRP ID IN VOUCHER SERIAL NO', sysdate, 'ADMIN', 
    sysdate, 'SU0001', NULL, 'Y');
COMMIT;

CREATE OR REPLACE PACKAGE BODY "CHANGE_STATUS_PKG"
AS
PROCEDURE p_changeVoucherStatus
(p_batchNo IN VOMS_BATCHES.BATCH_NO%TYPE,
p_batchType IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_fromSerialNo IN VOMS_BATCHES.FROM_SERIAL_NO%TYPE,
p_toSerialNo IN VOMS_BATCHES.TO_SERIAL_NO%TYPE,
p_batchEnableStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchGenStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchOnHoldStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchStolenStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchSoldStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchDamageStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchReconcileStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchPrintStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_wareHouseStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_preActiveStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_suspendStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_createdOn IN VOMS_VOUCHERS.EXPIRY_DATE%TYPE,
p_maxErrorAllowed IN NUMBER,
p_modifiedBy IN VOMS_VOUCHER_AUDIT.MODIFIED_BY%TYPE,
p_noOfVouchers IN NUMBER,
p_successProcessStatus IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
p_errorProcessStatus IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
p_batchConStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_processScreen IN NUMBER,
p_modifiedTime IN VOMS_BATCHES.MODIFIED_ON%TYPE,
p_referenceNo IN VOMS_BATCHES.REFERENCE_NO%TYPE,
p_RCAdminMaxdateallowed IN NUMBER,
p_EnableProcess VOMS_BATCHES.PROCESS%TYPE,
p_ChangeProcess VOMS_BATCHES.PROCESS%TYPE,
p_ReconcileProcess VOMS_BATCHES.PROCESS%TYPE,
p_networkCode VOMS_BATCHES.NETWORK_CODE%TYPE,
p_returnMessage  OUT VARCHAR2,
p_returnLogMessage  OUT VARCHAR2,
p_sqlErrorMessage OUT VARCHAR2
)IS
BEGIN
     /*set parameters to global variables so that they can be
     used by other procedures as well */
     v_batchEnableStat:=p_batchEnableStat;
     v_batchGenStat:=p_batchGenStat;
     v_batchOnHoldStat:=p_batchOnHoldStat;
     v_batchStolenStat:=p_batchStolenStat;
     v_batchSoldStat:= p_batchSoldStat;
     v_batchDamageStat:=p_batchDamageStat;
     v_batchReconcileStat:=p_batchReconcileStat;
     v_batchPrintStat:=p_batchPrintStat;
     v_wareHouseStat:=p_wareHouseStat;
     v_preActiveStat:=p_preActiveStat;
     v_suspendStat:=p_suspendStat;
     v_batchConStat:=p_batchConStat;
     v_createdOn:=p_createdOn;
     v_modifiedBy:=p_modifiedBy;
     v_errorCount:=0;
     --v_successCount:=0;
     v_serialStart:=p_fromSerialNo;
     v_batchNo :=p_batchNo;
     v_batchType:=p_batchType;
     v_processScreen:=p_processScreen;
     v_modifiedTime:=p_modifiedTime;
     v_enableCount  :=0;
     v_DamageStolenCount :=0;
     v_DamageStolenAfterEnCount :=0;
     v_onHoldCount :=0;
     v_counsumedCount:=0;
     v_wareHouseCount:=0;
     v_preActiveCount:=0;
     v_suspendCount:=0;
     v_referenceNo:=p_referenceNo;
     v_RCAdminMaxdateallowed:=p_RCAdminMaxdateallowed;
     v_EnableProcess :=p_EnableProcess;
     v_ChangeProcess :=p_ChangeProcess;
     v_ReconcileProcess :=p_ReconcileProcess;
     v_networkCode:=p_networkCode;
     v_voucherNotFoundCount:=0;
     v_returnMessage:='';
     v_sqlErrorMessage:='';
     v_serialNoLength:=LENGTH(p_fromSerialNo);

     -- Start the Loop --
     WHILE(v_serialStart<=p_toSerialNo) LOOP
      BEGIN
       v_succFailFlag:='FAILED';
       v_maxErrorFlag:=FALSE;
       v_voucherNotFoundFlag:=FALSE;
       rcd_count:=0;
       v_message:='';
       v_returnLogMessage:='';
           /* Check that the total invalid vouchers are less
       than the max error entries allowed */
       IF(v_errorCount<= p_maxErrorAllowed) THEN
            --Block for checking which vouchers are valid for the incoming new voucher status
         BEGIN
           CHECK_CHANGE_VALID_PROC;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while checking if voucher is valid  ='||SQLERRM);
              v_returnMessage:='FAILED';
            RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while checking if voucher is valid  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
         END;
          DBMS_OUTPUT.PUT_LINE('Iv_succFailFlag  ='||v_succFailFlag);

          -- If vouchers are valid then perform these steps
          IF(v_succFailFlag='SUCCESS') THEN
          /* If vouchers are valid for change status and the new
          voucher status is of enable type then
          1. Update voucher Table */

          IF(p_batchType=p_batchEnableStat AND v_processScreen=1) THEN
          BEGIN
            UPDATE_VOUCHER_ENABLE;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
         END;
         ELSIF(p_batchType=p_batchEnableStat AND (v_processScreen=2)) THEN
          BEGIN
            UPDATE_VOUCHER_ENABLE_OTHER;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
         END;
         /*code changed by kamini .
         elsif(p_batchType=p_batchEnableStat AND (v_processScreen=2 OR v_processScreen=3)) then*/

         ELSIF(p_batchType=p_batchEnableStat AND v_processScreen=3) THEN
          BEGIN
            UPDATE_VOUCHER_ENABLE_OTHER;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
         END;

         /* If new voucher status is other than enable
         then perform these steps
         1. Update Vouchers. */
          ELSE
          BEGIN
                UPDATE_VOUCHERS;
          EXCEPTION
          WHEN EXITEXCEPTION THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
          END;
          END IF;  --   en d of if(p_batchType=p_batchEnableStat)
      END IF;  --end of if(SUCCESS)

       IF(v_succFailFlag='SUCCESS') THEN
             v_processStatus:=p_successProcessStatus;  --store SU in status of VA table in case of success
             v_message:='Success';
            ELSE
                v_processStatus:=p_errorProcessStatus; --store ER in status of VA table in case of error
       END IF;

       /* For all voucher status change log entry of each serial no
       in voucher udit table . Block for insertion in VA table */
        -- if condition added on 13/02/04 so that if voucher not found then
        -- that entry is not made in VA table
        IF(v_voucherNotFoundFlag=FALSE) THEN
        BEGIN
              INSERT_IN_AUDIT_PROC;
        EXCEPTION
          WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while inserting in VA table ='||SQLERRM);
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting in VA table  ='||SQLERRM);
              RAISE EXITEXCEPTION;
        END;  -- end of inserting record in voucher_audit table
        END IF; -- end of  if(v_voucherNotFoundFlag=false)

      ELSE  -- Else of exceeding the max error allowed
         v_succFailFlag:='FAILED';
         v_returnMessage:='FAILED';
        v_maxErrorFlag:=TRUE;
         v_message:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
         v_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
         RAISE EXITEXCEPTION;
         --ROLLBACK OR THROW EXECPTION
      END IF;
      v_serialStart:=v_serialStart+1; -- incrementing from serial no by 1
      v_serialStart:=LPAD(v_serialStart,v_serialNoLength,0);
      DBMS_OUTPUT.PUT_LINE('v_serialStart after incrementing ='||v_serialStart);

      /* catch the Exception of type EXITEXCEPTION thrown above */
      EXCEPTION
      WHEN     EXITEXCEPTION THEN
      v_returnMessage:='FAILED';
      RAISE EXITEXCEPTION;  --Throw same Exception to come out of the loop
      WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      RAISE EXITEXCEPTION; --Throw same Exception to come out of the loop
      END;
      END LOOP;  -- end of while loop
      DBMS_OUTPUT.PUT_LINE('v_serialStart after loop ='||v_serialStart);
      /*    Update the Voucher batch and voucher summary  Table */
     BEGIN
        INSERT_IN_SUMMARY_PROC;
        EXCEPTION
      WHEN EXITEXCEPTION THEN
            v_returnMessage:='FAILED';
          DBMS_OUTPUT.PUT_LINE('EXCEPTION while inserting in summary table  ='||SQLERRM);
          RAISE EXITEXCEPTION;
      WHEN OTHERS THEN
            v_returnMessage:='FAILED';
          DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting in summary table  ='||SQLERRM);
          RAISE EXITEXCEPTION;
     END;

     --COMMIT;  --final commit

      /* If all the entries are inavlid for status change then
      return FAILED else return SUCCESS . Also return the message that
      needs to be written in the log file. */
      IF(p_noOfVouchers=v_errorCount) THEN
        p_returnMessage:='FAILED';
    p_returnLogMessage:='Vouchers is ins '||v_vouchStat||' and cannot be made to '||v_batchType||'';
      ELSIF(v_maxErrorFlag=TRUE) THEN
     p_returnMessage:='FAILED';
        p_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
      ELSIF(v_returnMessage='FAILED') THEN
        p_returnMessage:='FAILED';
        p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
      ELSE
        p_returnMessage:='SUCCESS';
        IF(v_voucherNotFoundCount>0) THEN
        p_returnLogMessage:='Successfully changed status with '||v_voucherNotFoundCount||' vouchers not found';
    ELSIF(v_errorCount> 0) THEN
    p_returnLogMessage:='Not able to update the status to '||v_batchType||' of  '|| v_errorCount ||' vouchers';
    ELSE
        p_returnLogMessage:='Successfully changed status of '||p_noOfVouchers||' vouchers';
        END IF;
      END IF;
EXCEPTION
WHEN EXITEXCEPTION THEN
p_returnMessage:='FAILED';
p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_sqlErrorMessage:=v_sqlErrorMessage;
DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||SQLERRM);
--ROLLBACK;  --Rollback in case of Exception
WHEN OTHERS THEN
p_returnMessage:='FAILED';
p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_sqlErrorMessage:=v_sqlErrorMessage;
DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||SQLERRM);
--ROLLBACK;
END; --Rollback in case of Exception

/*Procedure that will check whether the voucher status to be changes is
of valid type */

PROCEDURE CHECK_CHANGE_VALID_PROC
IS
BEGIN
         /* Get the voucher status abd then check whether that voucher
         is valid for status change or not */
         SELECT STATUS,CURRENT_STATUS,EXPIRY_DATE,GENERATION_BATCH_NO, PRODUCTION_NETWORK_CODE,
         NVL(USER_NETWORK_CODE,''),PRODUCT_ID,PREVIOUS_STATUS,LAST_CONSUMED_ON,LAST_REQUEST_ATTEMPT_NO,LAST_ATTEMPT_VALUE  INTO v_vouchStat,v_voucCurrStat,v_expDate,v_generationBatchNo,
         v_prodNetworkCode,v_userNetworkCode,v_productID,v_PreviousStatus,v_LastConsumedOn,v_LastRequestAttemptNo,v_LastAttemptValue
         FROM VOMS_VOUCHERS WHERE SERIAL_NO=v_serialStart
         FOR UPDATE OF STATUS,CURRENT_STATUS;
         DBMS_OUTPUT.PUT_LINE('v_serialStart  ='||v_serialStart||'v_vouchStat'||v_vouchStat);

        /* Check whether that voucher has expired or not . If not then
        perform voucher valid for change status checking.*/
        BEGIN           -- Begin of batch type checking
         IF(v_expDate>=v_createdOn) THEN
         --BEGIN           -- Begin of batch type checking

         -- If user is coming from 1 screen ie Enable Screen
          DBMS_OUTPUT.PUT_LINE('v_processScreen='||v_processScreen);
         IF(v_processScreen=1) THEN
         /*Condition for Enable voucher status , The status should be GE*/
         IF(v_batchType=v_batchEnableStat) THEN
           IF(v_vouchStat=v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_enableCount:=v_enableCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_EnableProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Enable type checking


           /*Condition for stolen voucher status, The status should be GE,Enable,
         Sold, On Hold status*/
         ELSIF(v_batchType=v_batchStolenStat) THEN
           IF(v_vouchStat=v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
           ELSE
                    v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannotSS be made to '||v_batchType||' from '||v_EnableProcess||' Screen'; -- Message to be written in VA
           END IF; -- of stolen type checking

           /*Condition for damage voucher status, The status should be GE,Enable,
         Sold, On Hold status*/
         ELSIF(v_batchType=v_batchDamageStat) THEN
           IF(v_vouchStat=v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannotaa be made to '||v_batchType||' from '||v_EnableProcess||' Screen';-- Message to be written in VA
           END IF; -- of damage type checking
           END IF; -- END OF batch type checking for 1

          -- If user is coming from 2 screen ie Change Voucher Screen
         ELSIF(v_processScreen=2) THEN
         /*Condition for Enable voucher status , The status should be GE,
         On hold, Reconcile*/


         IF(v_batchType=v_batchEnableStat) THEN
           IF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';

           ELSIF(v_vouchStat=v_preActiveStat) THEN ---added for new preactive state---
              v_succFailFlag:='SUCCESS';
           ELSIF(v_vouchStat=v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Enable type checking


           /*Condition for On Hold voucher status, The status should be Enable,
         status*/
         ELSIF(v_batchType=v_batchOnHoldStat) THEN
           IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_onHoldCount:=v_onHoldCount+1;
        ELSIF(v_vouchStat=v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
              v_onHoldCount:=v_onHoldCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen'; -- Message to be written in VA
           END IF; -- of On Hold type checking

           /*Condition for stolen voucher status, The status should be GE,
         Enable status*/
         ELSIF(v_batchType=v_batchStolenStat) THEN
          IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
         ELSIF(v_vouchStat=v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSE
                    v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen'; -- Message to be written in VA
           END IF; -- of stolen type checking

           /*Condition for damage voucher status, The status should be GE,Enable,
         status*/
         ELSIF(v_batchType=v_batchDamageStat) THEN
           IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat=v_wareHouseStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
		   ELSIF(v_vouchStat=v_suspendStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of damage type checking


           /*Condition for warehouse voucher status, The status should be PE
         status*/

           ELSIF(v_batchType=v_wareHouseStat) THEN ---added for new state warehouse--
           IF(v_vouchStat=v_batchPrintStat) THEN
              v_succFailFlag:='SUCCESS';
              v_wareHouseCount:=v_wareHouseCount+1;
           ELSIF(v_vouchStat=v_suspendStat) THEN
              v_succFailFlag:='SUCCESS';
              v_wareHouseCount:=v_wareHouseCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of warehouse type checking

            /*Condition for warehouse voucher status, The status should be PE
         status*/

           ELSIF(v_batchType=v_suspendStat) THEN ---added for new state SUSPEND--
           IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat=v_wareHouseStat) THEN
            v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat=v_preActiveStat) THEN
            v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of SUSPEND type checking




          END IF; -- END OF batch type checking for 2

         -- Condition if user is coming from 3 Screen ie Change Reconcile Status
         ELSIF(v_processScreen=3) THEN

         /*Condition for Enable voucher status , The status should be Reconcile.If request is to enable the voucher
         two condition is chacked
         a)the current_status is 'RC'
         b) The days difference between last consumed on and current date should be greater less than or equal to RCAdmindaysdiffallowed.*/
         IF(v_batchType=v_batchEnableStat) THEN
           IF(v_vouchStat=v_batchReconcileStat) THEN ----Check1
           v_modifieddate:=(TRUNC(v_modifiedTime));
           v_lastModifieddate:=(TRUNC(v_LastConsumedOn));
           v_daysDifflastconCurrDate:=(v_modifieddate-v_lastModifieddate);
               IF v_daysDifflastconCurrDate<=v_RCAdminMaxdateallowed THEN
              v_succFailFlag:='SUCCESS';
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Time Checking
           END IF;----Check1

           /*Condition for consume voucher status, The status should be
         Reconcile only*/
         ELSIF(v_batchType=v_batchConStat)  THEN
           IF(v_vouchStat=v_batchReconcileStat) THEN
             v_succFailFlag:='SUCCESS';
              --v_counsumedCount:=v_counsumedCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen'; -- Message to be written in VA
           END IF; -- of consume type checking

          --Added By Gurjeet on 04/10/2004 so that voucher can be
         --made to Damage from RC stage
         --Uncomment if have to be implemented
        /* elsif(v_batchType=v_batchDamageStat) then
           if(v_vouchStat=v_batchReconcileStat) then
             v_succFailFlag:='SUCCESS';
           else
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen'; -- Message to be written in VA
           end if; -- of Damage type checking
         */
         END IF; -- END OF batch type checking for 2

         END IF; -- end of voucher valid type process wise

          ELSE  -- Else of if voucher has expired
              v_errorCount:=v_errorCount+1;
           v_succFailFlag:='FAILED';
           v_message:='Voucher Has Expired';
         END IF;      -- end of  if(v_expDat>=p_createdOn)

       END;  -- end of batch type checking

EXCEPTION
           WHEN NO_DATA_FOUND THEN
                  v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              --v_returnMessage:='FAILED';
              v_message:='NO Record found for voucher in vouchers table';
              --v_returnLogMessage:='NO Record found for voucher in vouchers table'||v_serialStart;

              --Added On 13/02/04 so that archiving process does not affect this --
              v_voucherNotFoundCount:=v_voucherNotFoundCount+1;
              v_voucherNotFoundFlag:=TRUE;
              DBMS_OUTPUT.PUT_LINE('NO Record found for voucher in vouchers table'||v_serialStart);
             -- RAISE EXITEXCEPTION;
         WHEN OTHERS THEN
               DBMS_OUTPUT.PUT_LINE('SQL Exception for updating records '||v_serialStart);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while checking for voucher status in vouchers table'||v_serialStart||SQLERRM;
              RAISE EXITEXCEPTION;
END;

/*Procedure that will update voucher table. This will be called only
if new voucher status is of Enable type and user is coming from Enable screen*/

PROCEDURE UPDATE_VOUCHER_ENABLE
IS
BEGIN

/* If previous voucher status is of Reconcile then update */
            IF(v_vouchStat=v_batchReconcileStat) THEN

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/

            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

            /* If previous voucher status other than Reconcile then update */
            ELSE

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/

            UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
      DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
      RAISE EXITEXCEPTION;

WHEN OTHERS THEN
     v_returnMessage:='FAILED';
       DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      RAISE EXITEXCEPTION;
END;

/*Procedure that will update voucher table. This will be called for all
voucher status other than Enable type */

PROCEDURE UPDATE_VOUCHERS
IS
BEGIN
              DBMS_OUTPUT.PUT_LINE('INSIDE SUCCESS  ='||v_batchType||v_batchNo);

            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;LAST_ATTEMPT_NO=LAST_ATTEMPT_NO+1
            **************************/
            IF(v_voucCurrStat=v_batchReconcileStat) THEN
                UPDATE VOMS_VOUCHERS SET STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
                MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,PREVIOUS_STATUS=v_vouchStat,
                LAST_Attempt_NO=v_LastRequestAttemptNo,ATTEMPT_USED=ATTEMPT_USED+1,
                TOTAL_VALUE_USED=(TOTAL_VALUE_USED+v_LastAttemptValue)
                WHERE serial_no=v_serialStart;

                IF SQL%NOTFOUND THEN
                  DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating voucher status  ='||SQLERRM);
                RAISE SQLException;
                END IF;

            ELSE
                UPDATE VOMS_VOUCHERS SET STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
                MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

                IF SQL%NOTFOUND THEN
                  DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating voucher status  ='||SQLERRM);
                RAISE SQLException;
                END IF;
            END IF;
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
      DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
      RAISE EXITEXCEPTION;

WHEN OTHERS THEN
     v_returnMessage:='FAILED';
       DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      RAISE EXITEXCEPTION;
END;

/*Procedure that will insert values in voucher summary table.
This will be called at the end to update details */

PROCEDURE INSERT_IN_SUMMARY_PROC
IS
BEGIN
            DBMS_OUTPUT.PUT_LINE('v_batchNo='||v_batchNo);
            UPDATE VOMS_VOUCHER_BATCH_SUMMARY SET
            TOTAL_ENABLED=TOTAL_ENABLED+v_enableCount,
            TOTAL_RECHARGED=TOTAL_RECHARGED+v_counsumedCount,
            TOTAL_STOLEN_DMG=TOTAL_STOLEN_DMG+(v_DamageStolenCount-v_DamageStolenAfterEnCount),
            TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount
            WHERE  BATCH_NO =v_referenceNo;
            --WHERE  BATCH_NO =v_generationBatchNo;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating BATCH_SUMMARY  ='||SQLERRM);
            v_returnLogMessage:='Exception while updating BATCH_SUMMARY table';
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

      BEGIN      --block for insertion/updation in voucher_summary
         BEGIN  --block checking if record exist in voucher_summary
         SELECT '1' INTO rcd_count FROM dual
         WHERE EXISTS (SELECT 1 FROM VOMS_VOUCHER_SUMMARY
                       WHERE SUMMARY_DATE=v_createdOn
                     AND PRODUCT_ID=v_productID
                     AND PRODUCTION_NETWORK_CODE=v_networkCode
                     AND USER_NETWORK_CODE=v_networkCode);

        DBMS_OUTPUT.PUT_LINE('rcd_count='||rcd_count);
        EXCEPTION
        WHEN NO_DATA_FOUND THEN  --when no row returned for the distributor
             DBMS_OUTPUT.PUT_LINE('No Record found in voucher summary table');
             rcd_count := 0;
       WHEN SQLException THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while checking for voucher_summary  ='||SQLERRM);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while checking is record exist in summary table ';
              RAISE  SQLException;
        WHEN OTHERS THEN
             DBMS_OUTPUT.PUT_LINE('Exception while checking is record exist');
             v_returnMessage:='FAILED';
             v_returnLogMessage:='Exception while checking is record exist in summary table ';
             RAISE SQLException;
       END;
      IF rcd_count = 0 THEN
        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,
        USER_NETWORK_CODE,TOTAL_ENABLED, TOTAL_STOLEN_DMG,TOTAL_STOLEN_DMG_AFTER_EN, TOTAL_ON_HOLD)
        VALUES(v_createdOn,v_productID,v_networkCode,v_networkCode,v_enableCount,
            (v_DamageStolenCount-v_DamageStolenAfterEnCount),v_DamageStolenAfterEnCount,v_onHoldCount);
      ELSE
           UPDATE VOMS_VOUCHER_SUMMARY
        SET TOTAL_ENABLED=TOTAL_ENABLED+v_enableCount,
        TOTAL_STOLEN_DMG=TOTAL_STOLEN_DMG+(v_DamageStolenCount-v_DamageStolenAfterEnCount),
        TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount,
        TOTAL_ON_HOLD=TOTAL_ON_HOLD+v_onHoldCount
                       WHERE SUMMARY_DATE=v_createdOn
                     AND PRODUCT_ID=v_productID
                     AND PRODUCTION_NETWORK_CODE=v_networkCode
                     AND USER_NETWORK_CODE=v_networkCode;
      END IF;
      EXCEPTION
        WHEN  SQLException THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('SQL/OTHERS EXCEPTION CAUGHT while Record exist in summary'||SQLERRM);
              RAISE SQLException;
        WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION CAUGHT while Record exist in summary='||SQLERRM);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while insertin/updating summary table ';
              RAISE NOTINSERTEXCEPTION;
       END;  --end of voucher_audit insertion block
EXCEPTION
WHEN SQLException THEN
v_returnMessage:='FAILED';
--DBMS_OUTPUT.PUT_LINE('SQL Exception for updating records '||p_fromSerialNo);
RAISE EXITEXCEPTION;
WHEN NOTINSERTEXCEPTION THEN
v_returnMessage:='FAILED';
  --DBMS_OUTPUT.PUT_LINE('Not able to insert record in voucher_audit '||p_fromSerialNo);
RAISE EXITEXCEPTION;
WHEN OTHERS THEN
v_returnMessage:='FAILED';
v_returnLogMessage:='Exception while inserting/updating summary table ';
RAISE EXITEXCEPTION;
END;

/*Procedure that will update voucher table. This will be called only
if new voucher status is of Enable type and process is 2 or 3 ie
change status or Reconcile */

PROCEDURE UPDATE_VOUCHER_ENABLE_OTHER
IS
BEGIN

            /* If previous voucher status and current status both is in Reconcile state then update current status and status both*/
            IF((v_vouchStat=v_batchReconcileStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN

            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/

            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

            /* If previous voucher status is consumed and current status is in Reconcile state then update only current status*/
            ELSIF ((v_vouchStat=v_batchConStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN

            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/

            ELSE --Added By Gurjeet on 11/10/2004 because this was missing
            UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
      DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
      RAISE EXITEXCEPTION;

WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      RAISE EXITEXCEPTION;
END;

/*Procedure that will log entries in the voucher audit table for
every serial no.*/

PROCEDURE INSERT_IN_AUDIT_PROC
IS
BEGIN
            BEGIN -- block for getting next row ID
              SELECT voucher_audit_id.NEXTVAL INTO v_row_id  FROM dual;
              v_insertRowId:=TO_CHAR(v_row_id);
               DBMS_OUTPUT.PUT_LINE('v_insertRowId  ='||v_insertRowId);

              EXCEPTION
              WHEN NO_DATA_FOUND THEN
              v_returnLogMessage:='Exception while getting next row no for VA '||v_serialStart;
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
              WHEN OTHERS THEN
              v_returnLogMessage:='Exception while getting next row no for VA '||v_serialStart;
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
            END;  -- end of getting next row id block

            BEGIN -- Block for inserting record in voucher_audit table
              INSERT INTO VOMS_VOUCHER_AUDIT(ROW_ID, SERIAL_NO, CURRENT_STATUS, PREVIOUS_STATUS,
              MODIFIED_BY, MODIFIED_ON, STATUS_CHANGE_SOURCE, STATUS_CHANGE_PARTNER_ID,
              BATCH_NO, MESSAGE, PROCESS_STATUS)
              VALUES(v_insertRowId,v_serialStart,v_batchType,v_vouchStat,v_modifiedBy,v_modifiedTime,
              'WEB','',v_batchNo,v_message,v_processStatus);
              EXCEPTION
              WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting next row no  ='||SQLERRM);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while inserting in VA table '||v_serialStart||SQLERRM;
              RAISE NOTINSERTEXCEPTION;

            END;  -- end of inserting record in voucher_audit table
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
     v_sqlErrorMessage:=SQLERRM;
      DBMS_OUTPUT.PUT_LINE('SQL Exception for inserting in VA table '||v_serialStart);
      RAISE EXITEXCEPTION;
WHEN NOTINSERTEXCEPTION THEN
     v_returnMessage:='FAILED';
      v_sqlErrorMessage:=SQLERRM;
      DBMS_OUTPUT.PUT_LINE('SQL Exception for inserting in VA table '||v_serialStart);
      RAISE EXITEXCEPTION;
WHEN EXITEXCEPTION THEN
     v_returnMessage:='FAILED';
      v_sqlErrorMessage:=SQLERRM;
      RAISE EXITEXCEPTION;
WHEN OTHERS THEN
v_returnMessage:='FAILED';
v_returnLogMessage:='Exception while inserting in VA table '||v_serialStart||SQLERRM;
v_sqlErrorMessage:=SQLERRM;
RAISE EXITEXCEPTION;
END;


PROCEDURE p_changeVoucherStatus_NEWV
(p_batchNo IN VOMS_BATCHES.BATCH_NO%TYPE,
p_batchType IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_fromSerialNo IN VOMS_BATCHES.FROM_SERIAL_NO%TYPE,
p_toSerialNo IN VOMS_BATCHES.TO_SERIAL_NO%TYPE,
p_batchEnableStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchGenStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchOnHoldStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchStolenStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchSoldStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchDamageStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchReconcileStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_batchPrintStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_wareHouseStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_preActiveStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_suspendStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_createdOn IN VOMS_VOUCHERS.EXPIRY_DATE%TYPE,
p_maxErrorAllowed IN NUMBER,
p_modifiedBy IN VOMS_VOUCHER_AUDIT.MODIFIED_BY%TYPE,
p_noOfVouchers IN NUMBER,
p_successProcessStatus IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
p_errorProcessStatus IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
p_batchConStat IN VOMS_BATCHES.BATCH_TYPE%TYPE,
p_processScreen IN NUMBER,
p_modifiedTime IN VOMS_BATCHES.MODIFIED_ON%TYPE,
p_referenceNo IN VOMS_BATCHES.REFERENCE_NO%TYPE,
p_RCAdminMaxdateallowed IN NUMBER,
p_EnableProcess VOMS_BATCHES.PROCESS%TYPE,
p_ChangeProcess VOMS_BATCHES.PROCESS%TYPE,
p_ReconcileProcess VOMS_BATCHES.PROCESS%TYPE,
p_networkCode VOMS_BATCHES.NETWORK_CODE%TYPE,
p_seqID IN voms_batches.SEQUENCE_ID%type,
p_returnMessage  OUT VARCHAR2,
p_returnLogMessage  OUT VARCHAR2,
p_sqlErrorMessage OUT VARCHAR2
)
IS
BEGIN
     /*set parameters to global variables so that they can be
     used by other procedures as well */
     v_batchEnableStat:=p_batchEnableStat;
     v_batchGenStat:=p_batchGenStat;
     v_batchOnHoldStat:=p_batchOnHoldStat;
     v_batchStolenStat:=p_batchStolenStat;
     v_batchSoldStat:= p_batchSoldStat;
     v_batchDamageStat:=p_batchDamageStat;
     v_batchReconcileStat:=p_batchReconcileStat;
     v_batchPrintStat:=p_batchPrintStat;
     v_wareHouseStat:=p_wareHouseStat;
     v_preActiveStat:=p_preActiveStat;
     v_suspendStat:=p_suspendStat;
     v_batchConStat:=p_batchConStat;
     v_createdOn:=p_createdOn;
     v_modifiedBy:=p_modifiedBy;
     v_errorCount:=0;
     --v_successCount:=0;
     v_serialStart:=p_fromSerialNo;
     v_batchNo :=p_batchNo;
     v_batchType:=p_batchType;
     v_processScreen:=p_processScreen;
     v_modifiedTime:=p_modifiedTime;
     v_enableCount  :=0;
     v_DamageStolenCount :=0;
     v_DamageStolenAfterEnCount :=0;
     v_onHoldCount :=0;
     v_counsumedCount:=0;
     v_wareHouseCount:=0;
     v_preActiveCount:=0;
     v_suspendCount:=0;
     v_referenceNo:=p_referenceNo;
     v_RCAdminMaxdateallowed:=p_RCAdminMaxdateallowed;
     v_EnableProcess :=p_EnableProcess;
     v_ChangeProcess :=p_ChangeProcess;
     v_ReconcileProcess :=p_ReconcileProcess;
     v_networkCode:=p_networkCode;
     v_voucherNotFoundCount:=0;
     v_seqId:=p_seqID;
     v_returnMessage:='';
     v_sqlErrorMessage:='';
     v_serialNoLength:=LENGTH(p_fromSerialNo);

     -- Start the Loop --
     WHILE(v_serialStart<=p_toSerialNo) LOOP
      BEGIN
       v_succFailFlag:='FAILED';
       v_maxErrorFlag:=FALSE;
       v_voucherNotFoundFlag:=FALSE;
       rcd_count:=0;
       v_message:='';
       v_returnLogMessage:='';
           /* Check that the total invalid vouchers are less
       than the max error entries allowed */
       IF(v_errorCount<= p_maxErrorAllowed) THEN
            --Block for checking which vouchers are valid for the incoming new voucher status
         BEGIN
           CHECK_CHANGE_VALID_PROC_NEWV;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while checking if voucher is valid  ='||SQLERRM);
              v_returnMessage:='FAILED';
            RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while checking if voucher is valid  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
         END;
          DBMS_OUTPUT.PUT_LINE('Iv_succFailFlag  ='||v_succFailFlag);

          -- If vouchers are valid then perform these steps
          IF(v_succFailFlag='SUCCESS') THEN
          /* If vouchers are valid for change status and the new
          voucher status is of enable type then
          1. Update voucher Table */

          IF(p_batchType=p_batchEnableStat AND v_processScreen=1) THEN
          BEGIN
            UPDATE_VOUCHER_ENABLE_NEWV;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
         END;
         ELSIF(p_batchType=p_batchEnableStat AND (v_processScreen=2)) THEN
          BEGIN
            UPDATE_VOUCHER_ENABLE_OTR_NEWV;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
         END;
         /*code changed by kamini .
         elsif(p_batchType=p_batchEnableStat AND (v_processScreen=2 OR v_processScreen=3)) then*/

         ELSIF(p_batchType=p_batchEnableStat AND v_processScreen=3) THEN
          BEGIN
            UPDATE_VOUCHER_ENABLE_OTR_NEWV;
         EXCEPTION
          WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
              RAISE EXITEXCEPTION;
         END;

         /* If new voucher status is other than enable
         then perform these steps
         1. Update Vouchers. */
          ELSE
          BEGIN
                UPDATE_VOUCHERS_NEWV;
          EXCEPTION
          WHEN EXITEXCEPTION THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers  ='||SQLERRM);
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
          END;
          END IF;  --   en d of if(p_batchType=p_batchEnableStat)
      END IF;  --end of if(SUCCESS)

       IF(v_succFailFlag='SUCCESS') THEN
             v_processStatus:=p_successProcessStatus;  --store SU in status of VA table in case of success
             v_message:='Success';
            ELSE
                v_processStatus:=p_errorProcessStatus; --store ER in status of VA table in case of error
       END IF;

       /* For all voucher status change log entry of each serial no
       in voucher udit table . Block for insertion in VA table */
        -- if condition added on 13/02/04 so that if voucher not found then
        -- that entry is not made in VA table
        IF(v_voucherNotFoundFlag=FALSE) THEN
        BEGIN
              INSERT_IN_AUDIT_PROC_NEWV;
        EXCEPTION
          WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while inserting in VA table ='||SQLERRM);
              RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
                v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting in VA table  ='||SQLERRM);
              RAISE EXITEXCEPTION;
        END;  -- end of inserting record in voucher_audit table
        END IF; -- end of  if(v_voucherNotFoundFlag=false)

      ELSE  -- Else of exceeding the max error allowed
         v_succFailFlag:='FAILED';
         v_returnMessage:='FAILED';
        v_maxErrorFlag:=TRUE;
         v_message:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
         v_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
         RAISE EXITEXCEPTION;
         --ROLLBACK OR THROW EXECPTION
      END IF;
      v_serialStart:=v_serialStart+1; -- incrementing from serial no by 1
      v_serialStart:=LPAD(v_serialStart,v_serialNoLength,0);
      DBMS_OUTPUT.PUT_LINE('v_serialStart after incrementing ='||v_serialStart);

      /* catch the Exception of type EXITEXCEPTION thrown above */
      EXCEPTION
      WHEN     EXITEXCEPTION THEN
      v_returnMessage:='FAILED';
      RAISE EXITEXCEPTION;  --Throw same Exception to come out of the loop
      WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      RAISE EXITEXCEPTION; --Throw same Exception to come out of the loop
      END;
      END LOOP;  -- end of while loop
      DBMS_OUTPUT.PUT_LINE('v_serialStart after loop ='||v_serialStart);
      /*    Update the Voucher batch and voucher summary  Table */
     BEGIN
        INSERT_IN_SUMMARY_PROC_NEWV;
        EXCEPTION
      WHEN EXITEXCEPTION THEN
            v_returnMessage:='FAILED';
          DBMS_OUTPUT.PUT_LINE('EXCEPTION while inserting in summary table  ='||SQLERRM);
          RAISE EXITEXCEPTION;
      WHEN OTHERS THEN
            v_returnMessage:='FAILED';
          DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting in summary table  ='||SQLERRM);
          RAISE EXITEXCEPTION;
     END;

     --COMMIT;  --final commit

      /* If all the entries are inavlid for status change then
      return FAILED else return SUCCESS . Also return the message that
      needs to be written in the log file. */
      IF(p_noOfVouchers=v_errorCount) THEN
        p_returnMessage:='FAILED';
    p_returnLogMessage:='Vouchers is ino '||v_vouchStat||' and cannot be made to '||v_batchType||'';
      ELSIF(v_maxErrorFlag=TRUE) THEN
     p_returnMessage:='FAILED';
        p_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
      ELSIF(v_returnMessage='FAILED') THEN
        p_returnMessage:='FAILED';
        p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
      ELSE
        p_returnMessage:='SUCCESS';
        IF(v_voucherNotFoundCount>0) THEN
        p_returnLogMessage:='Successfully changed status with '||v_voucherNotFoundCount||' vouchers not found';
    ELSIF(v_errorCount> 0) THEN
    p_returnLogMessage:='Not able to update the status to '||v_batchType||' of  '|| v_errorCount ||' vouchers';
    ELSE
        p_returnLogMessage:='Successfully changed status of '||p_noOfVouchers||' vouchers';
        END IF;
      END IF;
EXCEPTION
WHEN EXITEXCEPTION THEN
p_returnMessage:='FAILED';
p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_sqlErrorMessage:=v_sqlErrorMessage;
DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||SQLERRM);
--ROLLBACK;  --Rollback in case of Exception
WHEN OTHERS THEN
p_returnMessage:='FAILED';
p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_sqlErrorMessage:=v_sqlErrorMessage;
DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||SQLERRM);
--ROLLBACK;
END; --Rollback in case of Exception

/*Procedure that will check whether the voucher status to be changes is
of valid type */

PROCEDURE CHECK_CHANGE_VALID_PROC_NEWV
IS
BEGIN
         /* Get the voucher status abd then check whether that voucher
         is valid for status change or not */
         SELECT STATUS,CURRENT_STATUS,EXPIRY_DATE,GENERATION_BATCH_NO, PRODUCTION_NETWORK_CODE,
         NVL(USER_NETWORK_CODE,''),PRODUCT_ID,PREVIOUS_STATUS,LAST_CONSUMED_ON,LAST_REQUEST_ATTEMPT_NO,LAST_ATTEMPT_VALUE  INTO v_vouchStat,v_voucCurrStat,v_expDate,v_generationBatchNo,
         v_prodNetworkCode,v_userNetworkCode,v_productID,v_PreviousStatus,v_LastConsumedOn,v_LastRequestAttemptNo,v_LastAttemptValue
         FROM VOMS_VOUCHERS WHERE SERIAL_NO=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/
         FOR UPDATE OF STATUS,CURRENT_STATUS;
         DBMS_OUTPUT.PUT_LINE('v_serialStart  ='||v_serialStart||'v_vouchStat'||v_vouchStat);

        /* Check whether that voucher has expired or not . If not then
        perform voucher valid for change status checking.*/
        BEGIN           -- Begin of batch type checking
         IF(v_expDate>=v_createdOn) THEN
         --BEGIN           -- Begin of batch type checking

         -- If user is coming from 1 screen ie Enable Screen
          DBMS_OUTPUT.PUT_LINE('v_processScreen='||v_processScreen);
         IF(v_processScreen=1) THEN
         /*Condition for Enable voucher status , The status should be GE*/
         IF(v_batchType=v_batchEnableStat) THEN
           IF(v_vouchStat=v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_enableCount:=v_enableCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_EnableProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Enable type checking


           /*Condition for stolen voucher status, The status should be GE,Enable,
         Sold, On Hold status*/
         ELSIF(v_batchType=v_batchStolenStat) THEN
           IF(v_vouchStat=v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
           ELSE
                    v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_EnableProcess||' Screen'; -- Message to be written in VA
           END IF; -- of stolen type checking

           /*Condition for damage voucher status, The status should be GE,Enable,
         Sold, On Hold status*/
         ELSIF(v_batchType=v_batchDamageStat) THEN
           IF(v_vouchStat=v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannotsss be made to '||v_batchType||' from '||v_EnableProcess||' Screen';-- Message to be written in VA
           END IF; -- of damage type checking
           END IF; -- END OF batch type checking for 1

          -- If user is coming from 2 screen ie Change Voucher Screen
         ELSIF(v_processScreen=2) THEN
         /*Condition for Enable voucher status , The status should be GE,
         On hold, Reconcile*/


         IF(v_batchType=v_batchEnableStat) THEN
           IF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';

           ELSIF(v_vouchStat=v_preActiveStat) THEN ---added for new preactive state---
              v_succFailFlag:='SUCCESS';
           ELSIF(v_vouchStat=v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Enable type checking


           /*Condition for On Hold voucher status, The status should be Enable,
         status*/
         ELSIF(v_batchType=v_batchOnHoldStat) THEN
           IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_onHoldCount:=v_onHoldCount+1;
        ELSIF(v_vouchStat=v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
              v_onHoldCount:=v_onHoldCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen'; -- Message to be written in VA
           END IF; -- of On Hold type checking

           /*Condition for stolen voucher status, The status should be GE,
         Enable status*/
         ELSIF(v_batchType=v_batchStolenStat) THEN
          IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
         ELSIF(v_vouchStat=v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSE
                    v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen'; -- Message to be written in VA
           END IF; -- of stolen type checking

           /*Condition for damage voucher status, The status should be GE,Enable,
         status*/
         ELSIF(v_batchType=v_batchDamageStat) THEN
           IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
                v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat=v_wareHouseStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat=v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
			ELSIF(v_vouchStat=v_suspendStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
            ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of damage type checking


           /*Condition for warehouse voucher status, The status should be PE
         status*/

           ELSIF(v_batchType=v_wareHouseStat) THEN ---added for new state warehouse--
           IF(v_vouchStat=v_batchPrintStat) THEN
              v_succFailFlag:='SUCCESS';
              v_wareHouseCount:=v_wareHouseCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of warehouse type checking

            /*Condition for warehouse voucher status, The status should be PE
         status*/

           ELSIF(v_batchType=v_suspendStat) THEN ---added for new state SUSPEND--
           IF(v_vouchStat=v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat=v_wareHouseStat) THEN
            v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat=v_preActiveStat) THEN
            v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of SUSPEND type checking




          END IF; -- END OF batch type checking for 2

         -- Condition if user is coming from 3 Screen ie Change Reconcile Status
         ELSIF(v_processScreen=3) THEN

         /*Condition for Enable voucher status , The status should be Reconcile.If request is to enable the voucher
         two condition is chacked
         a)the current_status is 'RC'
         b) The days difference between last consumed on and current date should be greater less than or equal to RCAdmindaysdiffallowed.*/
         IF(v_batchType=v_batchEnableStat) THEN
           IF(v_vouchStat=v_batchReconcileStat) THEN ----Check1
           v_modifieddate:=(TRUNC(v_modifiedTime));
           v_lastModifieddate:=(TRUNC(v_LastConsumedOn));
           v_daysDifflastconCurrDate:=(v_modifieddate-v_lastModifieddate);
               IF v_daysDifflastconCurrDate<=v_RCAdminMaxdateallowed THEN
              v_succFailFlag:='SUCCESS';
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Time Checking
           END IF;----Check1

           /*Condition for consume voucher status, The status should be
         Reconcile only*/
         ELSIF(v_batchType=v_batchConStat)  THEN
           IF(v_vouchStat=v_batchReconcileStat) THEN
             v_succFailFlag:='SUCCESS';
              --v_counsumedCount:=v_counsumedCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen'; -- Message to be written in VA
           END IF; -- of consume type checking

          --Added By Gurjeet on 04/10/2004 so that voucher can be
         --made to Damage from RC stage
         --Uncomment if have to be implemented
        /* elsif(v_batchType=v_batchDamageStat) then
           if(v_vouchStat=v_batchReconcileStat) then
             v_succFailFlag:='SUCCESS';
           else
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen'; -- Message to be written in VA
           end if; -- of Damage type checking
         */
         END IF; -- END OF batch type checking for 2

         END IF; -- end of voucher valid type process wise

          ELSE  -- Else of if voucher has expired
              v_errorCount:=v_errorCount+1;
           v_succFailFlag:='FAILED';
           v_message:='Voucher Has Expired';
         END IF;      -- end of  if(v_expDat>=p_createdOn)

       END;  -- end of batch type checking

EXCEPTION
           WHEN NO_DATA_FOUND THEN
                  v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              --v_returnMessage:='FAILED';
              v_message:='NO Record found for voucher in vouchers table';
              --v_returnLogMessage:='NO Record found for voucher in vouchers table'||v_serialStart;

              --Added On 13/02/04 so that archiving process does not affect this --
              v_voucherNotFoundCount:=v_voucherNotFoundCount+1;
              v_voucherNotFoundFlag:=TRUE;
              DBMS_OUTPUT.PUT_LINE('NO Record found for voucher in vouchers table'||v_serialStart);
             -- RAISE EXITEXCEPTION;
         WHEN OTHERS THEN
               DBMS_OUTPUT.PUT_LINE('SQL Exception for updating records '||v_serialStart);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while checking for voucher status in vouchers table'||v_serialStart||SQLERRM;
              RAISE EXITEXCEPTION;
END;

/*Procedure that will update voucher table. This will be called only
if new voucher status is of Enable type and user is coming from Enable screen*/

PROCEDURE UPDATE_VOUCHER_ENABLE_NEWV
IS
BEGIN

/* If previous voucher status is of Reconcile then update */
            IF(v_vouchStat=v_batchReconcileStat) THEN

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/
            /************************/

            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

            /* If previous voucher status other than Reconcile then update */
            ELSE

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/

            /********************/

            UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
      DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
      RAISE EXITEXCEPTION;

WHEN OTHERS THEN
     v_returnMessage:='FAILED';
       DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      RAISE EXITEXCEPTION;
END;

/*Procedure that will update voucher table. This will be called for all
voucher status other than Enable type */

PROCEDURE UPDATE_VOUCHERS_NEWV
IS
BEGIN
              DBMS_OUTPUT.PUT_LINE('INSIDE SUCCESS  ='||v_batchType||v_batchNo);

            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  LAST_ATTEMPT_NO=LAST_ATTEMPT_NO+1;
            /**************************/
            IF(v_voucCurrStat=v_batchReconcileStat) THEN
                UPDATE VOMS_VOUCHERS SET STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
                MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,PREVIOUS_STATUS=v_vouchStat,
                LAST_Attempt_NO=v_LastRequestAttemptNo,ATTEMPT_USED=ATTEMPT_USED+1,
                TOTAL_VALUE_USED=(TOTAL_VALUE_USED+v_LastAttemptValue)
                WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/;

                IF SQL%NOTFOUND THEN
                  DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating voucher status  ='||SQLERRM);
                RAISE SQLException;
                END IF;

            ELSE
                UPDATE VOMS_VOUCHERS SET STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
                MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/;

                IF SQL%NOTFOUND THEN
                  DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating voucher status  ='||SQLERRM);
                RAISE SQLException;
                END IF;
            END IF;
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
      DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
      RAISE EXITEXCEPTION;

WHEN OTHERS THEN
     v_returnMessage:='FAILED';
       DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      RAISE EXITEXCEPTION;
END;

/*Procedure that will insert values in voucher summary table.
This will be called at the end to update details */

PROCEDURE INSERT_IN_SUMMARY_PROC_NEWV
IS
BEGIN
            DBMS_OUTPUT.PUT_LINE('v_batchNo='||v_batchNo);
            UPDATE VOMS_VOUCHER_BATCH_SUMMARY SET
            TOTAL_ENABLED=TOTAL_ENABLED+v_enableCount,
            TOTAL_RECHARGED=TOTAL_RECHARGED+v_counsumedCount,
            TOTAL_STOLEN_DMG=TOTAL_STOLEN_DMG+(v_DamageStolenCount-v_DamageStolenAfterEnCount),
            TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount
            WHERE  BATCH_NO =v_referenceNo;
            --WHERE  BATCH_NO =v_generationBatchNo;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating BATCH_SUMMARY  ='||SQLERRM);
            v_returnLogMessage:='Exception while updating BATCH_SUMMARY table';
            RAISE SQLException;
           END IF;  -- end of if SQL%NOTFOUND

      BEGIN      --block for insertion/updation in voucher_summary
         BEGIN  --block checking if record exist in voucher_summary
         SELECT '1' INTO rcd_count FROM dual
         WHERE EXISTS (SELECT 1 FROM VOMS_VOUCHER_SUMMARY
                       WHERE SUMMARY_DATE=v_createdOn
                     AND PRODUCT_ID=v_productID
                     AND PRODUCTION_NETWORK_CODE=v_networkCode
                     AND USER_NETWORK_CODE=v_networkCode);

        DBMS_OUTPUT.PUT_LINE('rcd_count='||rcd_count);
        EXCEPTION
        WHEN NO_DATA_FOUND THEN  --when no row returned for the distributor
             DBMS_OUTPUT.PUT_LINE('No Record found in voucher summary table');
             rcd_count := 0;
       WHEN SQLException THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while checking for voucher_summary  ='||SQLERRM);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while checking is record exist in summary table ';
              RAISE  SQLException;
        WHEN OTHERS THEN
             DBMS_OUTPUT.PUT_LINE('Exception while checking is record exist');
             v_returnMessage:='FAILED';
             v_returnLogMessage:='Exception while checking is record exist in summary table ';
             RAISE SQLException;
       END;
      IF rcd_count = 0 THEN
        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,
        USER_NETWORK_CODE,TOTAL_ENABLED, TOTAL_STOLEN_DMG,TOTAL_STOLEN_DMG_AFTER_EN, TOTAL_ON_HOLD)
        VALUES(v_createdOn,v_productID,v_networkCode,v_networkCode,v_enableCount,
            (v_DamageStolenCount-v_DamageStolenAfterEnCount),v_DamageStolenAfterEnCount,v_onHoldCount);
      ELSE
           UPDATE VOMS_VOUCHER_SUMMARY
        SET TOTAL_ENABLED=TOTAL_ENABLED+v_enableCount,
        TOTAL_STOLEN_DMG=TOTAL_STOLEN_DMG+(v_DamageStolenCount-v_DamageStolenAfterEnCount),
        TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount,
        TOTAL_ON_HOLD=TOTAL_ON_HOLD+v_onHoldCount
                       WHERE SUMMARY_DATE=v_createdOn
                     AND PRODUCT_ID=v_productID
                     AND PRODUCTION_NETWORK_CODE=v_networkCode
                     AND USER_NETWORK_CODE=v_networkCode;
      END IF;
      EXCEPTION
        WHEN  SQLException THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('SQL/OTHERS EXCEPTION CAUGHT while Record exist in summary'||SQLERRM);
              RAISE SQLException;
        WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION CAUGHT while Record exist in summary='||SQLERRM);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while insertin/updating summary table ';
              RAISE NOTINSERTEXCEPTION;
       END;  --end of voucher_audit insertion block
EXCEPTION
WHEN SQLException THEN
v_returnMessage:='FAILED';
--DBMS_OUTPUT.PUT_LINE('SQL Exception for updating records '||p_fromSerialNo);
RAISE EXITEXCEPTION;
WHEN NOTINSERTEXCEPTION THEN
v_returnMessage:='FAILED';
  --DBMS_OUTPUT.PUT_LINE('Not able to insert record in voucher_audit '||p_fromSerialNo);
RAISE EXITEXCEPTION;
WHEN OTHERS THEN
v_returnMessage:='FAILED';
v_returnLogMessage:='Exception while inserting/updating summary table ';
RAISE EXITEXCEPTION;
END;

/*Procedure that will update voucher table. This will be called only
if new voucher status is of Enable type and process is 2 or 3 ie
change status or Reconcile */

PROCEDURE UPDATE_VOUCHER_ENABLE_OTR_NEWV
IS
BEGIN

            /* If previous voucher status and current status both is in Reconcile state then update current status and status both*/
            IF((v_vouchStat=v_batchReconcileStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN

            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/
            /************************/

            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

            /* If previous voucher status is consumed and current status is in Reconcile state then update only current status*/
            ELSIF ((v_vouchStat=v_batchConStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN

            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/

            /********************/

            ELSE --Added By Gurjeet on 11/10/2004 because this was missing
            UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart and SEQUENCE_ID=v_seqId  /*ak*/;

            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
      DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
      RAISE EXITEXCEPTION;

WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      RAISE EXITEXCEPTION;
END;

/*Procedure that will log entries in the voucher audit table for
every serial no.*/

PROCEDURE INSERT_IN_AUDIT_PROC_NEWV
IS
BEGIN
            BEGIN -- block for getting next row ID
              SELECT voucher_audit_id.NEXTVAL INTO v_row_id  FROM dual;
              v_insertRowId:=TO_CHAR(v_row_id);
               DBMS_OUTPUT.PUT_LINE('v_insertRowId  ='||v_insertRowId);

              EXCEPTION
              WHEN NO_DATA_FOUND THEN
              v_returnLogMessage:='Exception while getting next row no for VA '||v_serialStart;
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
              WHEN OTHERS THEN
              v_returnLogMessage:='Exception while getting next row no for VA '||v_serialStart;
              v_returnMessage:='FAILED';
              RAISE EXITEXCEPTION;
            END;  -- end of getting next row id block

            BEGIN -- Block for inserting record in voucher_audit table
              INSERT INTO VOMS_VOUCHER_AUDIT(ROW_ID, SERIAL_NO, CURRENT_STATUS, PREVIOUS_STATUS,
              MODIFIED_BY, MODIFIED_ON, STATUS_CHANGE_SOURCE, STATUS_CHANGE_PARTNER_ID,
              BATCH_NO, MESSAGE, PROCESS_STATUS)
              VALUES(v_insertRowId,v_serialStart,v_batchType,v_vouchStat,v_modifiedBy,v_modifiedTime,
              'WEB','',v_batchNo,v_message,v_processStatus);
              EXCEPTION
              WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting next row no  ='||SQLERRM);
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while inserting in VA table '||v_serialStart||SQLERRM;
              RAISE NOTINSERTEXCEPTION;

            END;  -- end of inserting record in voucher_audit table
EXCEPTION
WHEN SQLException THEN
     v_returnMessage:='FAILED';
     v_sqlErrorMessage:=SQLERRM;
      DBMS_OUTPUT.PUT_LINE('SQL Exception for inserting in VA table '||v_serialStart);
      RAISE EXITEXCEPTION;
WHEN NOTINSERTEXCEPTION THEN
     v_returnMessage:='FAILED';
      v_sqlErrorMessage:=SQLERRM;
      DBMS_OUTPUT.PUT_LINE('SQL Exception for inserting in VA table '||v_serialStart);
      RAISE EXITEXCEPTION;
WHEN EXITEXCEPTION THEN
     v_returnMessage:='FAILED';
      v_sqlErrorMessage:=SQLERRM;
      RAISE EXITEXCEPTION;
WHEN OTHERS THEN
v_returnMessage:='FAILED';
v_returnLogMessage:='Exception while inserting in VA table '||v_serialStart||SQLERRM;
v_sqlErrorMessage:=SQLERRM;
RAISE EXITEXCEPTION;
END;

END CHANGE_STATUS_PKG;
/



