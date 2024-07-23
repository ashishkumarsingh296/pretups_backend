DROP PROCEDURE USER_DAILY_CLOSING_BALANCE;

CREATE OR REPLACE PROCEDURE user_daily_closing_balance (
                                   rtn_message           OUT      VARCHAR2,
                            rtn_messageforlog     OUT      VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT      VARCHAR2
                                   )
IS
/*       ############## TEMP table FOR USER daily  closing balance#####################
CREATE TABLE USER_DAILY_BAL_TEMP
(
  START_TIME DATE,
  END_TIME DATE,
  PROCESS VARCHAR2(20),
  STATUS_LOG VARCHAR2(100)
);              */

p_user_id  USER_BALANCES.user_id%TYPE;
p_product_code USER_BALANCES.product_code%TYPE;
p_network_code USER_BALANCES.network_code%TYPE;
p_network_code_for USER_BALANCES.network_code_for%TYPE;

q_user_id USER_DAILY_BALANCES.user_id%TYPE ;
q_network_code USER_DAILY_BALANCES.network_code%TYPE;
q_network_code_for USER_DAILY_BALANCES.network_code_for%TYPE;
q_product_code USER_DAILY_BALANCES.product_code%TYPE;
q_balance USER_DAILY_BALANCES.balance%TYPE;
q_prev_balance USER_DAILY_BALANCES.prev_balance%TYPE;
q_last_transfer_type USER_DAILY_BALANCES.last_transfer_type%TYPE;
q_last_transfer_no USER_DAILY_BALANCES.last_transfer_no%TYPE;
q_last_transfer_on USER_DAILY_BALANCES.last_transfer_on%TYPE;


q_daily_balance_updated_on DATE;
q_created_on DATE;
dayDifference NUMBER (5):= 0;
startCount NUMBER(3);
dateCounter DATE;




sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 CURSOR user_list_cur IS
        SELECT ub.user_id,ub.product_code,ub.network_code,ub.network_code_for
        FROM USER_BALANCES ub, USERS u
        WHERE ub.USER_ID=u.USER_ID
        AND TRUNC(daily_balance_updated_on) <>TRUNC(SYSDATE)
        AND TRUNC (u.modified_on) >= CASE WHEN (u.status='N') THEN (SYSDATE-366) WHEN (u.status='C') THEN (SYSDATE-366) ELSE TRUNC (u.modified_on) END;

--BEGIN

        --INSERT INTO USER_DAILY_BAL_TEMP(PROCESS,START_TIME) VALUES ('USER_DAILY_CLOSING_BALANCE_PROCESS',SYSDATE);


BEGIN
             FOR user_records IN user_list_cur
             LOOP
                     p_user_id:=user_records.user_id;
                    p_product_code:=user_records.product_code;
                    p_network_code:=user_records.network_code;
                    p_network_code_for:=user_records.network_code_for;
             BEGIN
                        SELECT user_id,network_code,network_code_for,product_code,balance,prev_balance,last_transfer_type,
                    last_transfer_no,last_transfer_on,TRUNC(daily_balance_updated_on) daily_balance_updated_on
                    INTO q_user_id,q_network_code,q_network_code_for,q_product_code,q_balance,q_prev_balance,
                    q_last_transfer_type,q_last_transfer_no,q_last_transfer_on,q_daily_balance_updated_on
                    FROM USER_BALANCES
                    WHERE user_id=p_user_id
                    AND network_code=p_network_code
                    AND network_code_for=p_network_code_for
                    FOR UPDATE;

                    IF SQL%NOTFOUND
                    THEN
                          DBMS_OUTPUT.PUT_LINE ('Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM );
                         rtn_messageforlog:='Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
                         rtn_sqlerrmsgforlog:=SQLERRM;
                         RAISE sqlexception;
                    END IF;

                    EXCEPTION
                       WHEN NO_DATA_FOUND
                       THEN
                          DBMS_OUTPUT.PUT_LINE ('Exception NO_DATA_FOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM );

                       WHEN OTHERS
                       THEN
                          DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id || SQLERRM );
                          rtn_messageforlog := 'OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
                          rtn_sqlerrmsgforlog := SQLERRM;
                          RAISE sqlexception;

             END;

             BEGIN

                     q_created_on  :=SYSDATE;
                  startCount := 1;
                  dateCounter:= q_daily_balance_updated_on;
                  dayDifference:= TRUNC(q_created_on) - q_daily_balance_updated_on;

                  DBMS_OUTPUT.PUT_LINE(' No Of dayDifference::'||dayDifference);


                FOR xyz IN startCount .. dayDifference
                LOOP


                     BEGIN


                       INSERT INTO USER_DAILY_BALANCES
                                  (balance_date,user_id,network_code,network_code_for,
                                    product_code,balance,prev_balance,last_transfer_type,
                                  last_transfer_no,last_transfer_on,created_on
                                  )
                              VALUES(dateCounter,q_user_id,q_network_code,
                                   q_network_code_for,q_product_code,q_balance,q_prev_balance,
                                   q_last_transfer_type,q_last_transfer_no,q_last_transfer_on,
                                   q_created_on
                                  );
                      EXCEPTION
                        WHEN OTHERS
                        THEN
                           DBMS_OUTPUT.PUT_LINE ('Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id || SQLERRM );
                           rtn_messageforlog := 'Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id ;
                           rtn_sqlerrmsgforlog := SQLERRM;
                           RAISE sqlexception;



                    END;-- End of insert SQL

                    BEGIN

                        UPDATE USER_BALANCES SET
                               daily_balance_updated_on=q_created_on
                        WHERE user_id=p_user_id
                        AND product_code=p_product_code
                        AND network_code=p_network_code
                        AND network_code_for=p_network_code_for;

                        EXCEPTION
                           WHEN OTHERS
                           THEN
                              DBMS_OUTPUT.PUT_LINE ('Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id || SQLERRM );
                              rtn_messageforlog := 'Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id ;
                                 rtn_sqlerrmsgforlog := SQLERRM;
                              RAISE sqlexception;

                    END;-- End of update SQL

                 startCount:= startCount+1;
                dateCounter:= dateCounter+1;



           END LOOP;--End of daydiffrence loop

           COMMIT;
                DBMS_OUTPUT.PUT_LINE ('RECORDS COMMITED::'||p_user_id);

        END;--End oF Outer begin

            COMMIT;


     END LOOP;--End of outer for loop

                 rtn_message:='SUCCESS';
                 rtn_messageForLog :='PreTUPS USER_DAILY_CLOSING_BALANCE MIS successfully executed, Date Time:'||SYSDATE;
                 rtn_sqlerrMsgForLog :=' ';

                 --UPDATE USER_DAILY_BAL_TEMP SET END_TIME=SYSDATE,STATUS_LOG=rtn_message WHERE PROCESS='USER_DAILY_CLOSING_BALANCE_PROCESS' AND trunc(START_TIME)=trunc(sysdate);
                 COMMIT;

         EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;

         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in USER_DAILY_CLOSING_BALANCE procedure:='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;



END;

--END;
/


DROP PROCEDURE UPDATE_OPNING_CLOSING_BALANCE;

CREATE OR REPLACE PROCEDURE UPDATE_OPNING_CLOSING_BALANCE
    (p_fromdate IN USER_DAILY_BALANCES.BALANCE_DATE%TYPE,
    p_todate IN USER_DAILY_BALANCES.BALANCE_DATE%TYPE,
    v_messageforlog OUT varchar2
    )IS
    gv_userid USER_DAILY_BALANCES.USER_ID%TYPE;
    gv_balance USER_DAILY_BALANCES.BALANCE%TYPE;
    gv_networkcode USER_DAILY_BALANCES.NETWORK_CODE%TYPE;
    gv_networkcodefor USER_DAILY_BALANCES.NETWORK_CODE_FOR%TYPE;
    gv_productcode USER_DAILY_BALANCES.PRODUCT_CODE%TYPE;
    --p_fromdate USER_DAILY_BALANCES.BALANCE_DATE%TYPE;
    --p_todate USER_DAILY_BALANCES.BALANCE_DATE%TYPE;
    p_date USER_DAILY_BALANCES.BALANCE_DATE%TYPE;
    gd_transaction_date USER_DAILY_BALANCES.BALANCE_DATE%TYPE;
    gv_pre_balance USER_DAILY_BALANCES.BALANCE%TYPE;
    v_modify_count    NUMBER (20);
    --v_messageforlog varchar2(200);
    V_SQLERRMSGFORLOG varchar2(200);
    SQLException EXCEPTION;
    NOTINSERTEXCEPTION EXCEPTION;
    EXITEXCEPTION EXCEPTION;

CURSOR USER_BALANCE_CUR (p_fromdate DATE,p_todate DATE) IS
         SELECT USER_ID, BALANCE, NETWORK_CODE, NETWORK_CODE_FOR, PRODUCT_CODE,BALANCE_DATE FROM USER_DAILY_BALANCES WHERE  TRUNC(BALANCE_DATE) >= p_fromdate and TRUNC(BALANCE_DATE) <= p_todate order by BALANCE_DATE;

BEGIN
    gv_userid := '';
    gv_balance := '';
    gv_networkcode := '';
    gv_networkcodefor := '';
    gv_productcode := '';
    p_date := '';
    v_modify_count := 0;

      FOR USER_BAL_CUR IN USER_BALANCE_CUR (p_fromdate, p_todate)
     LOOP
        gv_userid := USER_BAL_CUR.user_id;
        gv_balance := USER_BAL_CUR.balance;
        gv_networkcode := USER_BAL_CUR.network_code;
        gv_networkcodefor := USER_BAL_CUR.network_code_for;
        gv_productcode := USER_BAL_CUR.product_code;
        p_date := USER_BAL_CUR.BALANCE_DATE;
        gd_transaction_date := USER_BAL_CUR.BALANCE_DATE-1;
        gv_pre_balance := 0;
        BEGIN
            SELECT balance INTO gv_pre_balance
            FROM USER_DAILY_BALANCES
            WHERE user_id = gv_userid
            AND network_code = gv_networkcode
            AND network_code_for = gv_networkcodefor
            AND product_code = gv_productcode
            AND balance_date = gd_transaction_date;

               IF SQL%NOTFOUND
               THEN
              v_messageforlog :='SQL Exception in , User '|| gv_userid || ' Date:' || gd_transaction_date;
              v_sqlerrmsgforlog := SQLERRM;
              RAISE sqlexception;
               END IF;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                       
              --DBMS_OUTPUT.PUT_LINE('No Record found for user_id,date,'||gv_userid||gd_transaction_date);
              gv_pre_balance := 0;
               WHEN OTHERS
               THEN
              v_messageforlog := 'SQL Exception in , User ' || gv_userid || ' Date:' || gd_transaction_date;
              v_sqlerrmsgforlog := SQLERRM;
              RAISE sqlexception;
            END;

           UPDATE DAILY_CHNL_TRANS_MAIN  SET OPENING_BALANCE = gv_pre_balance,  CLOSING_BALANCE= gv_balance  WHERE TRANS_DATE = p_date and USER_ID=gv_userid and
        NETWORK_CODE=gv_networkcode and NETWORK_CODE_FOR=gv_networkcodefor and PRODUCT_CODE=gv_productcode;
        v_modify_count := v_modify_count  + SQL%ROWCOUNT;
        IF  MOD( v_modify_count  ,  1000   ) = 0 THEN
            DBMS_OUTPUT.PUT_LINE('Committed On ' || v_modify_count || ',p_date=' ||p_date);
            commit;
        END IF;
        --DBMS_OUTPUT.PUT_LINE('gv_userid:='||gv_userid||',gv_opn_balance:='||gv_pre_balance||',gv_cls_balance:='||gv_balance||',p_date:='||p_date);
        --DBMS_OUTPUT.PUT_LINE(gv_userid||','||gv_pre_balance||','||gv_balance||','||p_date);
    END LOOP;
    commit;
END;
/


DROP PROCEDURE UPDATE_CREATED_DATE;

CREATE OR REPLACE PROCEDURE UPDATE_CREATED_DATE
AS

V_SERIAL_NO VOMS_VOUCHERS.SERIAL_NO%TYPE;
V_CREATED_DATE VOMS_VOUCHERS.CREATED_DATE%TYPE;
V_MODIFY_COUNT  NUMBER (20);

CURSOR VOUCHER_LIST_CUR IS SELECT  SERIAL_NO,  TRUNC(CREATED_ON) FROM VOMS_VOUCHERS WHERE CREATED_DATE IS NULL  ;

BEGIN
          OPEN VOUCHER_LIST_CUR;
         LOOP
         FETCH VOUCHER_LIST_CUR INTO V_SERIAL_NO, V_CREATED_DATE;
         EXIT WHEN VOUCHER_LIST_CUR%NOTFOUND;
         UPDATE VOMS_VOUCHERS  SET CREATED_DATE =  V_CREATED_DATE  WHERE SERIAL_NO = V_SERIAL_NO;
         V_MODIFY_COUNT :=     V_MODIFY_COUNT  + SQL%ROWCOUNT;
         IF  MOD( V_MODIFY_COUNT  ,  1000   ) = 0 THEN
         COMMIT;
         DBMS_OUTPUT.PUT_LINE('Committed On ' || V_MODIFY_COUNT );
         END IF;

         END LOOP;
         CLOSE VOUCHER_LIST_CUR;
        COMMIT;
END;
/


DROP PROCEDURE UPDATE_ACCPNT_DLY_C2S_LMS_SMRY;

CREATE OR REPLACE PROCEDURE update_accpnt_dly_c2s_lms_smry (
                            aiv_Date               IN      VARCHAR2,
                                   rtn_message           OUT   VARCHAR2,
                            rtn_messageforlog     OUT   VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT   VARCHAR2
                                   )
IS
p_trans_date            DAILY_C2S_LMS_SUMMARY.trans_date%type;
p_user_id                DAILY_C2S_LMS_SUMMARY.user_id%type;
p_product_code            DAILY_C2S_LMS_SUMMARY.product_code%type;
p_lms_profile            DAILY_C2S_LMS_SUMMARY.lms_profile%type;
p_accumulated_points    DAILY_C2S_LMS_SUMMARY.accumulated_points%type;
p_count NUMBER;
sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist
 CURSOR update_cur is
        SELECT USER_ID_OR_MSISDN, PRODUCT_CODE, PROFILE_ID, ACCUMULATED_POINTS,POINTS_DATE from  BONUS where PROFILE_TYPE='LMS' and POINTS_DATE=to_date(aiv_Date,'dd/mm/yy');
    BEGIN
      p_count:=0;
      FOR user_records IN update_cur
             LOOP
                     p_user_id:=user_records.USER_ID_OR_MSISDN;
                    p_product_code:=user_records.product_code;
                    p_lms_profile:=user_records.PROFILE_ID;
                    p_accumulated_points:=user_records.accumulated_points;
                 BEGIN
                    p_count:=p_count+1;
                    UPDATE DAILY_C2S_LMS_SUMMARY SET  accumulated_points=p_accumulated_points
                    WHERE user_id=p_user_id
                    AND product_code=p_product_code
                    AND trans_date=to_date(aiv_Date,'dd/mm/yy')
                    AND lms_profile=p_lms_profile;
                    EXCEPTION
                        WHEN OTHERS       THEN
                                  DBMS_OUTPUT.PUT_LINE ('Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id ||' DATE:'||p_trans_date||' Profile:'||p_lms_profile|| SQLERRM );
                                  rtn_messageforlog := 'Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id||' DATE:'||p_trans_date||' Profile:'||p_lms_profile;
                                  rtn_sqlerrmsgforlog := SQLERRM;
                                  RAISE sqlexception;
                 END;
             END LOOP;
                 rtn_message:='SUCCESS';
                rtn_messageForLog :='PreTUPS update_acc_pnt_daily_c2s_lms_summary successfully executed, Excuted Date Time:'||SYSDATE||' For date:'||p_trans_date||' Number updates:'||p_count;
                rtn_sqlerrMsgForLog :=' ';
        EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;
         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in update_acc_pnt_daily_c2s_lms_summary procedure:='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;
    END;
/


DROP PROCEDURE UPDATETABLE;

CREATE OR REPLACE PROCEDURE updateTable (
      p_date                  IN   DATE,
      p_networkCode           IN   VARCHAR2,
      p_messageOut            OUT     VARCHAR2
   )
IS
BEGIN
 
 update c2s_transfers set transfer_status='200' where transfer_status='250' and transfer_date=p_date and network_code=p_networkCode;
  CURSOR c2s_data (p_date DATE) IS
    SELECT (TRANSFER_ID||';'||TRANSFER_STATUS||';'||SERVICE_TYPE||';'||'C2S'||';'||SENDER_ID||';'||SENDER_MSISDN
                ||';'||SENDER_CATEGORY||';'||'NA'||';'||RECEIVER_MSISDN||';'||'NA'
                ||';'||'NA'||';'||TO_CHAR(TRANSFER_DATE_TIME,'DD/MM/YYYY HH24:MI:SS')||';'||TRANSFER_VALUE
                ||';'||ERROR_CODE||';'||(TO_NUMBER(END_TIME)-TO_NUMBER(START_TIME))
                ||';'||bonus_details) DATA
                FROM C2S_TRANSFERS 
                 WHERE transfer_date=p_date and network_code=p_networkCode;
 
 trans_data_file  UTL_FILE.FILE_TYPE;
BEGIN
  v_date := to_char(p_date,'ddmmyy_hh24');
  v_date1 := to_char(p_date+p_before_interval/1440,'_hh24');
  
  trans_data_file := UTL_FILE.FOPEN(location     => 'C2SDWH_DIR',
                           filename     => 'RP2PHRTrans_'||v_date||v_date1||'.csv',
                           open_mode    => 'w',
                           max_linesize => 32767);   --max_linesize => 32767
  UTL_FILE.PUT_LINE(trans_data_file,'TRANSACTION ID;TRANSACTION STATUS;SERVICE TYPE;TRANSACTION TYPE;FROM_USER_ID;SENDER MSISDN;SENDER NAME;SENDER CATEGORY;TO_USER_ID;RECEIVER MSISDN;RECEIVER NAME;RECEIVER CATEGORY;TRANSACTION DATE TIME;TRANSACTION AMOUNT;SENDER PRE BALANCE;SENDER POST BALANCE;ERROR CODE;ERROR DESCRIPTION;TRANSACTION DURATION;BONUS DETAILS');
  FOR c2s_cur_rec IN c2s_data (p_date) LOOP
    UTL_FILE.PUT_LINE(trans_data_file, c2s_cur_rec.DATA);
  END LOOP;
  UTL_FILE.FCLOSE(trans_data_file);
  v_filename := v_filename ||'RP2PHRTrans_'||v_date||v_date1||'.csv'||',';
EXCEPTION
  WHEN OTHERS THEN
                UTL_FILE.FCLOSE(trans_data_file);
    RAISE;
 
 
  INSERT INTO  c2s_transfers
  SELECT * FROM  c2s_transfers
  WHERE transfer_date=p_date and network_code=p_networkCode;
 
 
  COMMIT;
 
 
END;
/


DROP PROCEDURE TRN_BKP_PROCEDURE;

CREATE OR REPLACE PROCEDURE TRN_BKP_PROCEDURE(
p_date      IN    DATE,
p_interval  IN  NUMBER,
v_message   OUT VARCHAR2
)
IS
the_date    DATE;
the_interval NUMBER(4);
sqlexception EXCEPTION;
    BEGIN
    the_date:=p_date;
    the_interval:=p_interval;
        INSERT INTO PRETUPS_VMS.ADJUSTMENTS SELECT * FROM ADJUSTMENTS WHERE ADJUSTMENT_DATE BETWEEN the_date AND the_date+the_interval/1440;
        INSERT INTO PRETUPS_VMS.C2S_TRANSFERS SELECT * FROM C2S_TRANSFERS WHERE TRANSFER_DATE_TIME BETWEEN the_date AND the_date+the_interval/1440;
        INSERT INTO PRETUPS_VMS.CHANNEL_TRANSFERS SELECT * FROM CHANNEL_TRANSFERS WHERE CLOSE_DATE BETWEEN the_date AND the_date+the_interval/1440;
        INSERT INTO PRETUPS_VMS.CHANNEL_TRANSFERS_ITEMS SELECT * FROM CHANNEL_TRANSFERS_ITEMS WHERE TRANSFER_DATE BETWEEN the_date AND the_date+the_interval/1440;
        EXCEPTION
        WHEN sqlexception
        THEN
       -- DBMS_OUTPUT.PUT_LINE ('procexception in TRANSACTIONS_HISTORY 1:' || SQLERRM);
        v_message:='FAILED';
        WHEN OTHERS
        THEN
        --DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in TRANSACTIONS_HISTORY 1:' || SQLERRM);
        v_message:='FAILED';
        COMMIT;
    
           
END TRN_BKP_PROCEDURE;
/


DROP PROCEDURE TRANSFERDATA;

CREATE OR REPLACE PROCEDURE transferData(
      p_date                  IN   DATE,
      p_networkCode           IN   VARCHAR2,
      p_message            OUT     VARCHAR2
   )
IS
gv_date DATE;

      
      CURSOR tom (p_date DATE,p_network_code VARCHAR2) IS 
        SELECT * FROM pretups630.C2S_TRANSFERS WHERE NETWORK_CODE = p_network_code and transfer_date=p_date;
        
    file UTL_FILE.FILE_TYPE;
  BEGIN  
    gv_date:=TO_DATE(p_date,'dd/mm/yy');
    
     
     DBMS_OUTPUT.PUT_LINE ('Start 1');
               p_message := 'FAILED';
               FOR C IN tom(gv_date, p_networkCode) 
                 LOOP
                    
                        --updating transfer status
                    
                    IF (C.TRANSFER_STATUS = '205') THEN
                        C.TRANSFER_STATUS := '200';
                     END IF;   
                        --INSERTING INTO ANOTHER TABLE HERE

                       INSERT INTO PRETUPS6113.C2S_TRANSFERS VALUES C;
                        
                    
                 END LOOP;
                 
                 delete from  pretups630.c2s_transfers WHERE NETWORK_CODE = p_networkCode and transfer_date=p_date;
                 COMMIT;
                 p_message := 'SUCCESS';
                 DBMS_OUTPUT.PUT_LINE ('END...');
  END;
/


DROP PROCEDURE TRANSACTIONS_HISTORY;

CREATE OR REPLACE PROCEDURE TRANSACTIONS_HISTORY(
in_date            IN    VARCHAR2,
in_inter        IN    NUMBER,
aov_message            OUT VARCHAR2,
aov_messageForLog    OUT VARCHAR2,
aov_sqlerrMsgForLog     OUT VARCHAR2
)
IS
ld_date                 DATE;
ld_inter                NUMBER(4);
n_date_for_pro          DATE;
the_date                DATE;
v_message               VARCHAR2 (2000);
pro_already_executed    NUMBER(1);
sqlexception EXCEPTION;
alreadydoneexception EXCEPTION;
retmainexception EXCEPTION;
mainexception EXCEPTION;

   BEGIN
        pro_already_executed:=0;
    IF in_date IS NULL
    THEN
        the_date:=sysdate;
    ELSE
    the_date:=TO_DATE(in_date,'dd/mm/yy');
    END IF;

        SELECT EXECUTED_UPTO,BEFORE_INTERVAL into ld_date,ld_inter FROM PROCESS_STATUS WHERE PROCESS_ID='TRNBKP';
        n_date_for_pro:=ld_date;

    IF in_inter IS NOT NULL
    THEN
        ld_inter:=in_inter;
    END IF;

        WHILE (the_date-n_date_for_pro)>(ld_inter/1440)
        LOOP
          --  DBMS_OUTPUT.PUT_LINE('EXECUTING FOR ::::::::'||the_date);
            BEGIN
                 SELECT 1,executed_upto INTO pro_already_executed,ld_date
                 FROM PROCESS_STATUS
                WHERE PROCESS_ID='TRNBKP' AND EXECUTED_UPTO > the_date-ld_inter/1440;
                --DBMS_OUTPUT.PUT_LINE('Process already Executed, Date:' || ld_date );
                aov_message :='FAILED';
                 aov_messageForLog:='Process already Executed, Date:' || ld_date;
                 aov_sqlerrMsgForLog:=' ';
                 RAISE alreadyDoneException;
                 EXCEPTION
                    WHEN NO_DATA_FOUND THEN
            
                    BEGIN
                             TRN_BKP_PROCEDURE (n_date_for_pro, ld_inter, v_message);
                            EXCEPTION
                              WHEN retmainexception THEN
                            --  DBMS_OUTPUT.PUT_LINE('retmainexception in TRANSACTIONS_HISTORY:'|| SQLERRM);
                              aov_messageForLog:=v_message;
                              aov_sqlerrMsgForLog:='';
                              RAISE mainexception;
                              WHEN OTHERS THEN
                             -- DBMS_OUTPUT.PUT_LINE('OTHERS Exception in TRANSACTIONS_HISTORY:'|| SQLERRM);
                              aov_messageForLog:='OTHERS Exception in TRANSACTIONS_HISTORY';
                              aov_sqlerrMsgForLog:=SQLERRM;
                              RAISE mainexception;
                    END;    
                    n_date_for_pro:=n_date_for_pro+ld_inter/1440;    
                   UPDATE PROCESS_STATUS SET executed_upto=n_date_for_pro, executed_on=SYSDATE WHERE PROCESS_ID='TRNBKP';
                COMMIT;

                aov_message :='SUCCESS';
                aov_messageForLog :='Transaction Procedure successfully executed till '|| n_date_for_pro ||' , Date Time:'||SYSDATE;
                aov_sqlerrMsgForLog :=' ';

              WHEN alreadyDoneException THEN--exception handled in case HOURLY C2S DWH already executed
                  aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainException;

              WHEN OTHERS THEN
                 -- DBMS_OUTPUT.PUT_LINE('OTHERS Error when checking if Transaction Procedure has already been executed'||SQLERRM);
                  aov_messageForLog:='OTHERS Error when checking if Transaction Procedure has already been executed, Date:'|| n_date_for_pro;
                  aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainException;

        END;

    END LOOP;

EXCEPTION --Exception Handling of main procedure
  WHEN mainException THEN
  ROLLBACK;
  DBMS_OUTPUT.PUT_LINE('mainException Caught='||SQLERRM);
  aov_message :='FAILED';

  WHEN OTHERS THEN
  ROLLBACK;
  --DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in Main procedure:='||SQLERRM);
  aov_message :='FAILED';

 END TRANSACTIONS_HISTORY;
/


DROP PROCEDURE SVC_SETOR_INTFC_MAPPING_INSERT;

CREATE OR REPLACE PROCEDURE SVC_SETOR_INTFC_MAPPING_INSERT
    IS
    v_service_type            SVC_SETOR_INTFC_MAPPING.SERVICE_TYPE%TYPE;                        
    v_selector_code            SVC_SETOR_INTFC_MAPPING.SELECTOR_CODE%TYPE;                       
    v_network_code            SVC_SETOR_INTFC_MAPPING.NETWORK_CODE%TYPE;                        
    v_interface_id            SVC_SETOR_INTFC_MAPPING.INTERFACE_ID%TYPE;                        
    v_prefix_id                SVC_SETOR_INTFC_MAPPING.PREFIX_ID%TYPE;                           
    v_action                SVC_SETOR_INTFC_MAPPING.ACTION%TYPE;                              
    v_method_type            SVC_SETOR_INTFC_MAPPING.METHOD_TYPE%TYPE;                         
    v_count                    NUMBER(10);     

    SQLException EXCEPTION;
    EXITEXCEPTION EXCEPTION;

    CURSOR   serise_cur IS select st.service_type,stm.selector_code, ns.sender_network, ss.interface_id,ss.prefix_id,ss.action,st.type  
            from service_type st, network_services ns, service_type_selector_mapping stm, intf_ntwrk_prfx_mapping ss
            where st.external_interface = 'Y'
            and st.status = 'Y'
            and ns.status='Y'
            and ns.service_type=st.service_type
            and st.type in('PRE','POST')
            and stm.service_type=st.service_type
            and stm.status='Y'
            and ss.METHOD_TYPE=st.TYPE
            and ss.NETWORK_CODE=ns.SENDER_NETWORK
            order by st.module,st.service_type,stm.selector_code, ss.prefix_id,ss.action;

    BEGIN
                DBMS_OUTPUT.PUT_LINE('Start SVC_SETOR_INTFC_MAPPING_INSERT');
                v_count:=1;

               OPEN serise_cur;
               LOOP
                            FETCH serise_cur INTO v_service_type, v_selector_code, v_network_code, v_interface_id, v_prefix_id, v_action, v_method_type;
                            EXIT WHEN serise_cur%NOTFOUND;
                            
                            INSERT INTO SVC_SETOR_INTFC_MAPPING(SERVICE_TYPE, SELECTOR_CODE, NETWORK_CODE, INTERFACE_ID, PREFIX_ID, ACTION, METHOD_TYPE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SRV_SELECTOR_INTERFACE_ID)
                            VALUES (v_service_type,v_selector_code,v_network_code,v_interface_id, v_prefix_id, v_action, v_method_type, sysdate,'SYSTEM',sysdate,'SYSTEM',v_count);                         
                             v_count:=v_count+1;

                            END LOOP;
                            CLOSE serise_cur;
                            
                            IF SQL%NOTFOUND THEN
                                DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while inserting into SVC_SETOR_INTFC_MAPPING, Error is '||SQLERRM);
                                RAISE SQLException;
                            END IF;

            COMMIT;
            DBMS_OUTPUT.PUT_LINE('End SVC_SETOR_INTFC_MAPPING_INSERT  v_count='||v_count);
    
END;
/


DROP PROCEDURE SP_UPDATE_MONTHLY_DATA3;

CREATE OR REPLACE PROCEDURE Sp_Update_Monthly_Data3 (aiv_date DATE)
   IS
   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
   --Cursor Declaration

   CURSOR monthly_c2s_details (aiv_date DATE)
   IS
      SELECT   user_id, trans_date,
               receiver_network_code, SERVICE_TYPE, Sub_Service,
               sender_category_code,receiver_service_class_id,
               total_tax1,total_tax2,total_tax3,
               sender_transfer_amount,receiver_credit_amount,
               receiver_access_fee,
               differential_adjustment_tax1,
               differential_adjustment_tax2,
               differential_adjustment_tax3,
               receiver_bonus,
               transaction_amount,
               transaction_count,
               differential_amount,
               receiver_validity,
               receiver_bonus_validity,
			   SENDER_REVERSE_AMOUNT ,
			   RECEIVER_DEBIT_AMOUNT ,
			   REVERSE_AMOUNT ,
			   REVERSE_COUNT ,
			   REVESRE_DIFF_AMOUNT, 
			   REVESRE_DIFF_COUNT 
          FROM DAILY_C2S_TRANS_DETAILS
         WHERE trans_date=aiv_date;
BEGIN
   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_c2s_details (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_C2S_TRANS_DETAILS
               SET total_tax1 = total_tax1 + t_r.total_tax1,
                   total_tax2 = total_tax2 + t_r.total_tax2,
                   total_tax3 = total_tax3 + t_r.total_tax3,
                   sender_transfer_amount = sender_transfer_amount + t_r.sender_transfer_amount,
                   receiver_credit_amount = receiver_credit_amount + t_r.receiver_credit_amount,
                   receiver_access_fee = receiver_access_fee + t_r.receiver_access_fee,
                   differential_adjustment_tax1 = differential_adjustment_tax1 + t_r.differential_adjustment_tax1,
                   differential_adjustment_tax2 = differential_adjustment_tax2 + t_r.differential_adjustment_tax2,
                   differential_adjustment_tax3 = differential_adjustment_tax3 + t_r.differential_adjustment_tax3,
                   receiver_bonus = receiver_bonus + t_r.receiver_bonus,
                   receiver_validity = receiver_validity + t_r.receiver_validity,
                   receiver_bonus_validity = receiver_bonus_validity + t_r.receiver_bonus_validity,
                   transaction_count=transaction_count+t_r.transaction_count,
                   transaction_amount=transaction_amount+t_r.transaction_amount,
                   differential_amount=differential_amount+t_r.differential_amount,
				    SENDER_REVERSE_AMOUNT = SENDER_REVERSE_AMOUNT + t_r.SENDER_REVERSE_AMOUNT,
					RECEIVER_DEBIT_AMOUNT = RECEIVER_DEBIT_AMOUNT + t_r.RECEIVER_DEBIT_AMOUNT,
					REVERSE_AMOUNT = REVERSE_AMOUNT + t_r.REVERSE_AMOUNT,
					REVERSE_COUNT = REVERSE_COUNT + t_r.REVERSE_COUNT,
					REVESRE_DIFF_AMOUNT = REVESRE_DIFF_AMOUNT + t_r.REVESRE_DIFF_AMOUNT,
					REVESRE_DIFF_COUNT = REVESRE_DIFF_COUNT + t_r.REVESRE_DIFF_COUNT
             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND receiver_network_code = t_r.receiver_network_code
               AND sender_category_code = t_r.sender_category_code
               AND receiver_service_class_id = t_r.receiver_service_class_id
               AND SERVICE_TYPE = t_r.SERVICE_TYPE 
           AND SUB_SERVICE = t_r.SUB_SERVICE;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO MONTHLY_C2S_TRANS_DETAILS
                              (user_id,
                            trans_date,
                            sender_category_code,
                            receiver_service_class_id,
                            receiver_network_code, SERVICE_TYPE,sub_service,
                            total_tax1, total_tax2, total_tax3,
                            sender_transfer_amount,
                            receiver_credit_amount,
                            receiver_access_fee,
                            differential_adjustment_tax1,
                            differential_adjustment_tax2,
                            differential_adjustment_tax3,
                            receiver_bonus, created_on,
                            receiver_validity,
                            receiver_bonus_validity,
                            transaction_amount,
                            transaction_count,
                            differential_amount,
                            SENDER_REVERSE_AMOUNT ,
                            RECEIVER_DEBIT_AMOUNT ,
                            REVERSE_AMOUNT ,
                            REVERSE_COUNT ,
                            REVESRE_DIFF_AMOUNT, 
                            REVESRE_DIFF_COUNT 
                            )
                    VALUES (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date, 'mm-yy'),'dd-mm-yy'),
                            t_r.sender_category_code,
                            t_r.receiver_service_class_id,
                            t_r.receiver_network_code, t_r.SERVICE_TYPE,t_r.sub_service,
                            t_r.total_tax1, t_r.total_tax2, t_r.total_tax3,
                            t_r.sender_transfer_amount,
                            t_r.receiver_credit_amount,
                            t_r.receiver_access_fee,
                            t_r.differential_adjustment_tax1,
                            t_r.differential_adjustment_tax2,
                            t_r.differential_adjustment_tax3,
                            t_r.receiver_bonus, SYSDATE,
                            t_r.receiver_validity,t_r.receiver_bonus_validity,
                            t_r.transaction_amount,t_r.transaction_count,
                            t_r.differential_amount,
                            t_r.SENDER_REVERSE_AMOUNT ,
                            t_r.RECEIVER_DEBIT_AMOUNT ,
                            t_r.REVERSE_AMOUNT ,
                            t_r.REVERSE_COUNT ,
                            t_r.REVESRE_DIFF_AMOUNT, 
                            t_r.REVESRE_DIFF_COUNT 
                            );
            END IF;

            EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Monthly_Data3 5, User:'|| t_r.user_id || SQLERRM);
               v_messageforlog := 'OTHERS Error in Sp_Update_Monthly_Data3 5, User:'|| t_r.user_id;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
          END;
        END LOOP;
   EXCEPTION
       WHEN procexception
     THEN
        DBMS_OUTPUT.PUT_LINE ('procexception in Sp_Update_Monthly_Data3 6');
        RAISE mainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION:' || SQLERRM);
     RAISE mainexception;
   END;
 EXCEPTION
      WHEN mainexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('mainexception in Sp_Update_Monthly_Data3 7');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data3 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END;
/


DROP PROCEDURE SP_UPDATE_MONTHLY_DATA1;

CREATE OR REPLACE PROCEDURE Sp_Update_Monthly_Data1 (aiv_date DATE)
   IS
   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;


   --Cursor Declaration
   CURSOR monthly_chnl_main  (aiv_date DATE)
   IS
      SELECT   user_id, trans_date,
               product_code, category_code, network_code, network_code_for,
               sender_domain_code, grph_domain_code,
               roam_c2s_transfer_out_amount,
               c2s_transfer_out_count,c2s_transfer_out_amount,
               o2c_transfer_in_count,o2c_transfer_in_amount,
               o2c_return_out_count,o2c_return_out_amount,
               o2c_withdraw_out_count,o2c_withdraw_out_amount,
               c2c_transfer_in_count,c2c_transfer_in_amount,
               c2c_transfer_out_count,c2c_transfer_out_amount,
               c2c_return_in_count,c2c_return_in_amount,
               c2c_return_out_count,c2c_return_out_amount,
               c2c_withdraw_in_count,c2c_withdraw_in_amount,
               c2c_withdraw_out_count,c2c_withdraw_out_amount,
               differential,adjustment_in,adjustment_out,
               c2c_reverse_in_count,c2c_reverse_in_amount,
               c2c_reverse_out_count,c2c_reverse_out_amount,
               o2c_reverse_in_count,o2c_reverse_in_amount,
               o2c_reverse_out_count,o2c_reverse_out_amount,
			   C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT ,REV_DIFFERENRIAL 
          FROM DAILY_CHNL_TRANS_MAIN
          WHERE trans_date = aiv_date;

BEGIN
   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_chnl_main (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_CHNL_TRANS_MAIN
               SET roam_c2s_transfer_out_amount = roam_c2s_transfer_out_amount + t_r.roam_c2s_transfer_out_amount,
                   c2s_transfer_out_count = c2s_transfer_out_count + t_r.c2s_transfer_out_count,
                   c2s_transfer_out_amount = c2s_transfer_out_amount + t_r.c2s_transfer_out_amount,
                   o2c_transfer_in_count = o2c_transfer_in_count + t_r.o2c_transfer_in_count,
                   o2c_transfer_in_amount = o2c_transfer_in_amount + t_r.o2c_transfer_in_amount,
                   o2c_return_out_count = o2c_return_out_count + t_r.o2c_return_out_count,
                   o2c_return_out_amount = o2c_return_out_amount + t_r.o2c_return_out_amount,
                   o2c_withdraw_out_count = o2c_withdraw_out_count + t_r.o2c_withdraw_out_count,
                   o2c_withdraw_out_amount = o2c_withdraw_out_amount + t_r.o2c_withdraw_out_amount,
                   c2c_transfer_in_count = c2c_transfer_in_count + t_r.c2c_transfer_in_count,
                   c2c_transfer_in_amount = c2c_transfer_in_amount + t_r.c2c_transfer_in_amount,
                   c2c_transfer_out_count = c2c_transfer_out_count + t_r.c2c_transfer_out_count,
                   c2c_transfer_out_amount = c2c_transfer_out_amount + t_r.c2c_transfer_out_amount,
                   c2c_return_in_count = c2c_return_in_count + t_r.c2c_return_in_count,
                   c2c_return_in_amount = c2c_return_in_amount + t_r.c2c_return_in_amount,
                   c2c_return_out_count = c2c_return_out_count + t_r.c2c_return_out_count,
                   c2c_return_out_amount = c2c_return_out_amount + t_r.c2c_return_out_amount,
                   c2c_withdraw_in_count = c2c_withdraw_in_count + t_r.c2c_withdraw_in_count,
                   c2c_withdraw_in_amount = c2c_withdraw_in_amount + t_r.c2c_withdraw_in_amount,
                   c2c_withdraw_out_count = c2c_withdraw_out_count + t_r.c2c_withdraw_out_count,
                   c2c_withdraw_out_amount = c2c_withdraw_out_amount + t_r.c2c_withdraw_out_amount,
                   differential = differential + t_r.differential,
                   adjustment_in = adjustment_in + t_r.adjustment_in,
                   adjustment_out = adjustment_out + t_r.adjustment_out,
                   c2c_reverse_in_count = c2c_reverse_in_count + t_r.c2c_reverse_in_count,
                   c2c_reverse_in_amount = c2c_reverse_in_amount + t_r.c2c_reverse_in_amount,
                   c2c_reverse_out_count = c2c_reverse_out_count + t_r.c2c_reverse_out_count,
                   c2c_reverse_out_amount = c2c_reverse_out_amount + t_r.c2c_reverse_out_amount,
                   o2c_reverse_in_count = o2c_reverse_in_count + t_r.o2c_reverse_in_count,
                   o2c_reverse_in_amount = o2c_reverse_in_amount + t_r.o2c_reverse_in_amount,
                   o2c_reverse_out_count = o2c_reverse_out_count + t_r.o2c_reverse_out_count,
                   o2c_reverse_out_amount = o2c_reverse_out_amount + t_r.o2c_reverse_out_amount,
				   C2S_TRANSFER_IN_AMOUNT  = C2S_TRANSFER_IN_AMOUNT + t_r.C2S_TRANSFER_IN_AMOUNT,
				   C2S_TRANSFER_IN_COUNT = C2S_TRANSFER_IN_COUNT + t_r.C2S_TRANSFER_IN_COUNT,
				   REV_DIFFERENRIAL  = REV_DIFFERENRIAL +  t_r.REV_DIFFERENRIAL

             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND product_code = t_r.product_code
               AND category_code = t_r.category_code
               AND network_code = t_r.network_code
               AND network_code_for = t_r.network_code_for
               AND sender_domain_code = t_r.sender_domain_code
               AND grph_domain_code = t_r.grph_domain_code;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO MONTHLY_CHNL_TRANS_MAIN
                                (user_id,trans_date,
                            product_code, category_code,
                            network_code, network_code_for,
                            sender_domain_code,
                            roam_c2s_transfer_out_amount,
                            c2s_transfer_out_count,
                            c2s_transfer_out_amount,
                            o2c_transfer_in_count,
                            o2c_transfer_in_amount,
                            o2c_return_out_count,
                            o2c_return_out_amount,
                            o2c_withdraw_out_count,
                            o2c_withdraw_out_amount,
                            c2c_transfer_in_count,
                            c2c_transfer_in_amount,
                            c2c_transfer_out_count,
                            c2c_transfer_out_amount,
                            c2c_return_in_count,
                            c2c_return_in_amount,
                            c2c_return_out_count,
                            c2c_return_out_amount,
                            c2c_withdraw_in_count,
                            c2c_withdraw_in_amount,
                            c2c_withdraw_out_count,
                            c2c_withdraw_out_amount, differential,
                            adjustment_in, adjustment_out, created_on,
                            grph_domain_code,
							c2c_reverse_in_count,
							c2c_reverse_in_amount,
                            c2c_reverse_out_count,
							c2c_reverse_out_amount,
							o2c_reverse_in_count,
							o2c_reverse_in_amount,
                            o2c_reverse_out_count,
							o2c_reverse_out_amount,
							C2S_TRANSFER_IN_AMOUNT ,
							C2S_TRANSFER_IN_COUNT ,
							REV_DIFFERENRIAL 
            )
                    VALUES (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date,'mm-yy'),'dd-mm-yy'),
                            t_r.product_code, t_r.category_code,
                            t_r.network_code, t_r.network_code_for,
                            t_r.sender_domain_code,
                            t_r.roam_c2s_transfer_out_amount,
                            t_r.c2s_transfer_out_count,
                            t_r.c2s_transfer_out_amount,
                            t_r.o2c_transfer_in_count,
                            t_r.o2c_transfer_in_amount,
                            t_r.o2c_return_out_count,
                            t_r.o2c_return_out_amount,
                            t_r.o2c_withdraw_out_count,
                            t_r.o2c_withdraw_out_amount,
                            t_r.c2c_transfer_in_count,
                            t_r.c2c_transfer_in_amount,
                            t_r.c2c_transfer_out_count,
                            t_r.c2c_transfer_out_amount,
                            t_r.c2c_return_in_count,
                            t_r.c2c_return_in_amount,
                            t_r.c2c_return_out_count,
                            t_r.c2c_return_out_amount,
                            t_r.c2c_withdraw_in_count,
                            t_r.c2c_withdraw_in_amount,
                            t_r.c2c_withdraw_out_count,
                            t_r.c2c_withdraw_out_amount, t_r.differential,
                            t_r.adjustment_in, t_r.adjustment_out, SYSDATE,
                            t_r.grph_domain_code,
							t_r.c2c_reverse_in_count,
							t_r.c2c_reverse_in_amount,
                            t_r.c2c_reverse_out_count,
							t_r.c2c_reverse_out_amount,
							t_r.o2c_reverse_in_count,
							t_r.o2c_reverse_in_amount,
                            t_r.o2c_reverse_out_count,
							t_r.o2c_reverse_out_amount,
							t_r.C2S_TRANSFER_IN_AMOUNT ,
							t_r.C2S_TRANSFER_IN_COUNT ,
							t_r.REV_DIFFERENRIAL 
                );
            END IF;
          EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Monthly_Data1 1, User:'  || t_r.user_id || SQLERRM);
               v_messageforlog := 'OTHERS EXCEPTION in Sp_Update_Monthly_Data1 1, User:' || t_r.user_id || ', Date:' || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
          END;
      END LOOP;
   EXCEPTION
      WHEN procexception
     THEN
        DBMS_OUTPUT.PUT_LINE ('procexception EXCEPTION in Sp_Update_Monthly_Data1 2');
        RAISE mainexception;

      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION:' || SQLERRM);
         RAISE mainexception;
   END;
 EXCEPTION
      WHEN mainexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('mainexception in Sp_Update_Monthly_Data1 7');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data1 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END;
/


DROP PROCEDURE SPLIT_STRING;

CREATE OR REPLACE PROCEDURE split_string (v_string) 
is lv_length Number(999)  := length(v_string); 
lv_string Varchar2(1000):='gaureav'; 
lv_appendstring Varchar2(1000);
 lv_resultstring Varchar2(100); 
 lv_count Number(2); begin lv_appendstring := lv_string||';'; 
 for i in 1..lv_length 
 loop 
 lv_resultstring:=substr(lv_appendstring,1,(instr(lv_appendstring,';')-1)); 
 lv_count:=instr(lv_appendstring,';')+1; 
 lv_appendstring:=substr(lv_appendstring,lv_count,length(lv_appendstring)); 
 dbms_output.put_line(' '||Nvl(lv_resultstring,'Null')); 
 exit when (lv_count=0); 
 end loop;
  end;
/


DROP PROCEDURE SELECT_UPDATE_INSERT_DELETE;

CREATE OR REPLACE PROCEDURE SELECT_UPDATE_INSERT_DELETE
(P_RETURNMESSAGE OUT VARCHAR2)
IS

V_TRANSFERSDATA               VARCHAR(255);
V_RETURNMESSAGE               VARCHAR2(255);
INSERTEXCEPTION EXCEPTION;



CURSOR C2STRANSFERS_CUR IS
                              select  * from C2S_TRANSFERS where NETWORK_CODE='NG';
                              


BEGIN
                OPEN  C2STRANSFERS_CUR;
                                LOOP
                                FETCH C2STRANSFERS_CUR INTO V_TRANSFERSDATA ;
                                EXIT WHEN C2STRANSFERS_CUR%NOTFOUND;
                                                BEGIN
                                                    DBMS_OUTPUT.PUT_LINE('test1');
                                                    update c2_transfers set network_code='SO';
                                                EXCEPTION
                                                                WHEN OTHERS THEN
                                                                DBMS_OUTPUT.PUT_LINE('EXCEPTION IN updating NEW RECORD FOR '||V_TRANSFERSDATA);
                                                                V_RETURNMESSAGE:='EXCEPTION IN INSERTING NEW RECORD FOR '||V_TRANSFERSDATA;
                                                               -- RAISE INSERTEXCEPTION;
                                                END;
                      END LOOP;
    COMMIT;
                      P_RETURNMESSAGE:=V_RETURNMESSAGE;
END ;
/


DROP PROCEDURE SELECT_UPDATE_INSERT;

CREATE OR REPLACE PROCEDURE SELECT_UPDATE_INSERT
 (
      p_date                  IN   DATE,
      p_networkCode           IN   VARCHAR2,
      p_messageOut            OUT     VARCHAR2
   )
IS
 gv_status C2S_TRANSFERS.TRANSFER_STATUS%TYPE;
CURSOR C2S_TRANSFER_LIST_CUR(p_date DATE, p_networkCode VARCHAR2) 
IS 
SELECT  * FROM C2S_TRANSFERS WHERE TRANSFER_DATE=p_date AND NETWORK_CODE=p_networkCode;

 BEGIN
    update 
   FOR test 
   IN C2S_TRANSFER_LIST_CUR
   LOOP
        gv_status:=test.transfer_status;
        IF gv_status:= '205';
                gv_status:= '200';
        END IF;
      DBMS_OUTPUT.put_line (test.transfer_status);
      update c2s_transfers set transfer_status='200' where transfer_status='250';
      
   END LOOP;
/


DROP PROCEDURE RP2PDWHTEMPPRC;

CREATE OR REPLACE PROCEDURE RP2PDWHTEMPPRC
(
           P_DATE                 IN DATE,
           P_MASTERCNT            OUT NUMBER,
           P_CHTRANSCNT           OUT    NUMBER,
           P_C2STRANSCNT          OUT    NUMBER,
           P_MESSAGE              OUT VARCHAR2
)
IS

SQLEXCEPTION EXCEPTION;
EXITEXCEPTION EXCEPTION;

BEGIN
        DBMS_OUTPUT.PUT_LINE('START RP2P DWH PROC');

        EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_RP2P_DWH_MASTER';
        EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_RP2P_DWH_CHTRANS';
        EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_RP2P_DWH_C2STRANS';


    INSERT INTO TEMP_RP2P_DWH_MASTER ( SRNO, DATA )
    SELECT ROWNUM,(U.USER_ID||','||PARENT_ID||','||OWNER_ID||','||USER_TYPE||','||EXTERNAL_CODE||','||MSISDN
    ||','||REPLACE(L.LOOKUP_NAME,',',' ')||','||REPLACE(LOGIN_ID,',',' ')||','||U.CATEGORY_CODE||','||CAT.CATEGORY_NAME||','||
    UG.GRPH_DOMAIN_CODE||','||REPLACE(GD.GRPH_DOMAIN_NAME,',',' ')||','||
    REPLACE(USER_NAME,',',' ')||','||REPLACE(CITY,',',' ')||','||REPLACE(STATE,',',' ')||','||REPLACE(COUNTRY,',',' ')||','||REPLACE(DESIGNATION,',',' ')||','||REPLACE(EMPLOYEE_CODE,',',' ')||',') DATA
    FROM USERS U, CATEGORIES CAT,USER_GEOGRAPHIES UG,GEOGRAPHICAL_DOMAINS GD,LOOKUPS L, LOOKUP_TYPES LT
    WHERE U.USER_ID=UG.USER_ID AND U.CATEGORY_CODE=CAT.CATEGORY_CODE AND U.STATUS<>'C'
    AND UG.GRPH_DOMAIN_CODE=GD.GRPH_DOMAIN_CODE AND L.LOOKUP_CODE=U.STATUS
    AND LT.LOOKUP_TYPE='URTYP' AND LT.LOOKUP_TYPE=L.LOOKUP_TYPE AND TRUNC(U.CREATED_ON)<=P_DATE
    AND USER_TYPE='CHANNEL';
    COMMIT;
    SELECT MAX(SRNO) INTO P_MASTERCNT FROM TEMP_RP2P_DWH_MASTER;


    INSERT INTO TEMP_RP2P_DWH_CHTRANS ( SRNO, DATA )
    SELECT ROWNUM,DATA FROM (SELECT (CT.TRANSFER_ID||','||REQUEST_GATEWAY_TYPE||','||TO_CHAR(CT.TRANSFER_DATE,'DD/MM/YYYY')
    ||','||TO_CHAR(CT.CREATED_ON,'DD/MM/YYYY HH12:MI:SS PM')||','||CT.NETWORK_CODE
    ||','||CT.TRANSFER_TYPE||','||CT.TRANSFER_SUB_TYPE||','||CT.TRANSFER_CATEGORY||','||CT.TYPE||','||CT.FROM_USER_ID||','||CT.TO_USER_ID||','||CT.MSISDN||','||CT.TO_MSISDN||','||CT.SENDER_CATEGORY_CODE||','||CT.RECEIVER_CATEGORY_CODE||','||CTI.SENDER_DEBIT_QUANTITY||','||CTI.RECEIVER_CREDIT_QUANTITY||','||CTI.REQUIRED_QUANTITY
    ||','||CTI.MRP||','||CTI.PAYABLE_AMOUNT||','||CTI.NET_PAYABLE_AMOUNT||','||0
    ||','||CTI.TAX1_VALUE||','||CTI.TAX2_VALUE||','||CTI.TAX3_VALUE||','||CTI.COMMISSION_VALUE
    ||','||','||','||CT.EXT_TXN_NO||','||TO_CHAR(CT.EXT_TXN_DATE,'DD/MM/YYYY')||','||','||CTI.PRODUCT_CODE||','||','
    || DECODE(CT.STATUS ,'CLOSE','200','240') ||','||','||','||','||','||','||','||','||','||','|| CT.CELL_ID||','||ABS(CASE CT.TRANSFER_SUB_TYPE WHEN 'T' THEN (CTI.SENDER_PREVIOUS_STOCK - CTI.APPROVED_QUANTITY) WHEN 'R' THEN (CTI.SENDER_PREVIOUS_STOCK - CTI.APPROVED_QUANTITY) ELSE (CTI.sender_previous_stock + CTI.APPROVED_QUANTITY) END)||','||CTI.SENDER_PREVIOUS_STOCK||','||ABS(CASE CT.TRANSFER_SUB_TYPE WHEN 'T' THEN (CTI.RECEIVER_PREVIOUS_STOCK + CTI.APPROVED_QUANTITY) WHEN 'R' THEN (CTI.RECEIVER_PREVIOUS_STOCK + CTI.APPROVED_QUANTITY) ELSE (CTI.RECEIVER_PREVIOUS_STOCK - CTI.APPROVED_QUANTITY) END)||','||CTI.RECEIVER_PREVIOUS_STOCK||',') DATA
    FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI
    WHERE CT.TRANSFER_ID=CTI.TRANSFER_ID(+)
    AND CT.STATUS IN('CLOSE','CNCL') AND TRUNC(CT.CLOSE_DATE)=P_DATE
    ORDER BY CT.MODIFIED_ON,CT.TYPE);
    COMMIT;
    SELECT MAX(SRNO) INTO P_CHTRANSCNT FROM TEMP_RP2P_DWH_CHTRANS;




   INSERT INTO TEMP_RP2P_DWH_C2STRANS ( SRNO, DATA,TRANSFER_STATUS)
    SELECT ROWNUM,DATA,TRANSFER_STATUS FROM (SELECT (CT.TRANSFER_ID||','||REQUEST_GATEWAY_TYPE||','||TO_CHAR(CT.TRANSFER_DATE,'DD/MM/YYYY')
    ||','||TO_CHAR(CT.TRANSFER_DATE_TIME,'DD/MM/YYYY HH12:MI:SS PM')||','||CT.NETWORK_CODE||','||CT.SERVICE_TYPE||','||','||
    'SALE'||','||'C2S'||','||CT.SENDER_ID||','||','||CT.SENDER_MSISDN||','||CT.RECEIVER_MSISDN||','||
    CT.SENDER_CATEGORY||','||','||CT.SENDER_TRANSFER_VALUE||','||CT.RECEIVER_TRANSFER_VALUE||','||
    CT.TRANSFER_VALUE||','||CT.QUANTITY||','||','||','|| CT.RECEIVER_ACCESS_FEE||','||
    CT.RECEIVER_TAX1_VALUE||','||CT.RECEIVER_TAX2_VALUE||','||0||','||','||CT.DIFFERENTIAL_APPLICABLE||','||
    CT.DIFFERENTIAL_GIVEN||','||','||','||','||CT.PRODUCT_CODE||','||CT.CREDIT_BACK_STATUS||','||CT.TRANSFER_STATUS
    ||','||CT.RECEIVER_BONUS_VALUE||','||CT.RECEIVER_VALIDITY||','||CT.RECEIVER_BONUS_VALIDITY||','
    ||CT.SERVICE_CLASS_CODE||','||CT.INTERFACE_ID||','||CT.CARD_GROUP_CODE||','||REPLACE(KV.VALUE,',',' ')||','||CT.SERIAL_NUMBER||','||CT.INTERFACE_REFERENCE_ID||','||CT.CELL_ID||','||CT.SENDER_POST_BALANCE||','||CT.SENDER_PREVIOUS_BALANCE||','||CT.RECEIVER_POST_BALANCE||','||CT.RECEIVER_PREVIOUS_BALANCE||','||CT.REVERSAL_ID||','||bbm.BUNDLE_NAME||','||STSM.SELECTOR_CODE||',') DATA,CT.TRANSFER_STATUS TRANSFER_STATUS
    FROM C2S_TRANSFERS CT, KEY_VALUES KV,Service_Type_Selector_Mapping STSM,BONUS_BUNDLE_MASTER bbm WHERE CT.TRANSFER_DATE=P_DATE AND stsm.SELECTOR_CODE=CT.SUB_SERVICE AND stsm.SERVICE_TYPE=CT.SERVICE_TYPE
    AND stsm.SELECTOR_CODE = bbm.BUNDLE_CODE
    AND KV.KEY(+)=CT.ERROR_CODE AND KV.TYPE(+)='C2S_ERR_CD' ORDER BY CT.TRANSFER_DATE_TIME);
    COMMIT;

    SELECT MAX(SRNO) INTO P_C2STRANSCNT FROM TEMP_RP2P_DWH_C2STRANS;


    DBMS_OUTPUT.PUT_LINE('RP2P DWH PROC COMPLETED');
    P_MESSAGE:='SUCCESS';

    EXCEPTION
                 WHEN SQLEXCEPTION THEN
            P_MESSAGE:='NOT ABLE TO MIGRATE DATA, SQL EXCEPTION OCCOURED';
            RAISE EXITEXCEPTION;
                 WHEN OTHERS THEN
                        P_MESSAGE:='NOT ABLE TO MIGRATE DATA, EXCEPTION OCCOURED';
                        RAISE  EXITEXCEPTION;

END;
/


DROP PROCEDURE RET_REVERSE_DATA_PROC;

CREATE OR REPLACE PROCEDURE ret_reverse_data_proc (p_date DATE)
   IS
      /* Variables for refill amount values */
      lv_servicetype            	DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_sub_service            	DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;
      
      ln_c2s_rev_ct                   DAILY_C2S_TRANS_DETAILS.REVERSE_COUNT%TYPE:= 0;
      ln_c2s_rev_amt                  DAILY_C2S_TRANS_DETAILS.REVERSE_AMOUNT%TYPE:= 0;

      ln_c2s_sender_rev_amt           DAILY_C2S_TRANS_DETAILS.SENDER_REVERSE_AMOUNT%TYPE:= 0;
      ln_c2s_rec_debit_amt             DAILY_C2S_TRANS_DETAILS.RECEIVER_DEBIT_AMOUNT%TYPE:= 0;
    
      ln_c2s_rev_diff_amt           DAILY_C2S_TRANS_DETAILS.REVESRE_DIFF_AMOUNT%TYPE:= 0;
      ln_c2s_rev_diff_count         DAILY_C2S_TRANS_DETAILS.REVESRE_DIFF_AMOUNT%TYPE:= 0;
      
      lv_sendercategory             DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE :=0;
      lv_receiverserviceclassid     DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE :=0;
      /* Cursor Declaration */
        CURSOR refill_data (p_date DATE)
        IS
        SELECT  Mast.sender_id, Mast.network_code,Mast.service_class_id,
                Mast.receiver_network_code, Mast.product_code,
                Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                COUNT (Mast.transfer_id) c2s_count,
                SUM(Mast.transfer_value) c2s_amount,
                NVL(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                NVL(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                NVL(SUM(adj.transfer_value),0) diff_amount,
                SUM(CASE  WHEN NVL(adj.transfer_value,0) <> 0 THEN 1 ELSE 0 END) diff_count
                FROM (SELECT 
                        c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
                        c2strans.receiver_network_code, p.product_code,
                        p.unit_value,ug.grph_domain_code, cat.category_code,
                        cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
                        c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
                        c2strans.sender_transfer_value
                        FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P, CATEGORIES cat,USER_GEOGRAPHIES ug
                        WHERE c2strans.transfer_date = p_date
                        AND   c2strans.transfer_status = '200'
                        AND   TRANSFER_TYPE = 'REV'
                        AND   p.product_code=c2strans.product_code
                        AND   cat.category_code=c2strans.sender_category
                        AND   ug.user_id=c2strans.sender_id
                      ) Mast, ADJUSTMENTS_MISTMP adj
                WHERE  Mast.transfer_id = adj.reference_id (+)
                AND    Mast.sender_id   = adj.user_id(+)
                GROUP BY Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
                Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;
                          

   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      lv_servicetype := '';
      lv_sub_service := '';
      lv_sendercategory := '';
      lv_receiverserviceclassid := '';
      gv_productmrp := '';
      
      /* Iterate thru. the Refill cursor */
      FOR ret_data_cur IN refill_data (n_date_for_mis)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         lv_sendercategory := ret_data_cur.category_code;
         lv_receiverserviceclassid := ret_data_cur.service_class_id;
         gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
         lv_sub_service:= ret_data_cur.sub_service;
         gd_transaction_date := p_date;
         gv_productmrp := ret_data_cur.unit_value;
         ln_c2s_rev_ct := ret_data_cur.c2s_count;
         ln_c2s_rev_amt := ret_data_cur.c2s_amount;
         ln_c2s_sender_rev_amt := ret_data_cur.c2s_sender_value;
         ln_c2s_rec_debit_amt := ret_data_cur.c2s_rectrans;
         ln_c2s_rev_diff_amt := ret_data_cur.diff_amount;
         ln_c2s_rev_diff_count:= ret_data_cur.diff_count;
         
         /* insert into DAILY_C2S_TRANS_DETAILS table */
         
         
         BEGIN
               SELECT 1 INTO user_rcd_count
               FROM DAILY_C2S_TRANS_DETAILS
               WHERE user_id = gv_userid 
               AND receiver_network_code = gv_networkcodefor
               AND sender_category_code = lv_sendercategory
               AND receiver_service_class_id = lv_receiverserviceclassid
               AND SERVICE_TYPE = lv_servicetype
               AND sub_service = lv_sub_service
               AND trans_date = gd_transaction_date;
              
               EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                        --when no row returned for the user
                 user_rcd_count := 0;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid);
                  v_messageforlog := 'OTHERS SQL Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
         END;
         
         IF user_rcd_count = 0
            THEN
                INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
                         sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, sub_service,SENDER_REVERSE_AMOUNT ,
                        RECEIVER_DEBIT_AMOUNT ,REVERSE_AMOUNT ,REVERSE_COUNT,
                        REVESRE_DIFF_AMOUNT ,REVESRE_DIFF_COUNT
                        )
                 VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
                          lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype, lv_sub_service,ln_c2s_sender_rev_amt,
                         ln_c2s_rec_debit_amt,ln_c2s_rev_amt,ln_c2s_rev_ct,
                         ln_c2s_rev_diff_amt,ln_c2s_rev_diff_count
                        );
         ELSE 
                UPDATE DAILY_C2S_TRANS_DETAILS
                SET SENDER_REVERSE_AMOUNT =  SENDER_REVERSE_AMOUNT + ln_c2s_sender_rev_amt,
                    RECEIVER_DEBIT_AMOUNT =  RECEIVER_DEBIT_AMOUNT + ln_c2s_rec_debit_amt,
                    REVERSE_AMOUNT        = REVERSE_AMOUNT + ln_c2s_rev_amt,
                    REVERSE_COUNT         = REVERSE_COUNT + ln_c2s_rev_ct,
                    REVESRE_DIFF_AMOUNT   = REVESRE_DIFF_AMOUNT + ln_c2s_rev_diff_amt,
                    REVESRE_DIFF_COUNT    = REVESRE_DIFF_COUNT + ln_c2s_rev_diff_count
                WHERE user_id = gv_userid
                AND receiver_network_code = gv_networkcodefor
                AND sender_category_code = lv_sendercategory
                AND receiver_service_class_id = lv_receiverserviceclassid
                AND SERVICE_TYPE = lv_servicetype
                AND sub_service = lv_sub_service
                AND trans_date = gd_transaction_date;
         END IF;
         
          /* insert into TEMP_DAILY_CHNL_TRANS_MAIN table */
          
         BEGIN
            BEGIN
                SELECT 1 INTO user_rcd_count
                FROM TEMP_DAILY_CHNL_TRANS_MAIN
                WHERE user_id = gv_userid
                AND network_code = gv_networkcode
                AND network_code_for = gv_networkcodefor
                AND product_code = gv_productcode
                AND grph_domain_code = gv_grphdomaincode
                AND trans_date = gd_transaction_date;

                IF SQL%NOTFOUND
                THEN
                  v_messageforlog := 'sqlexception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
                END IF;
                EXCEPTION
                WHEN NO_DATA_FOUND
                THEN                        --when no row returned for the user
                 user_rcd_count := 0;
                WHEN OTHERS
                THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid);
                  v_messageforlog := 'OTHERS SQL Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;

            IF user_rcd_count = 0 THEN
                INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                           (user_id, trans_date, product_code,
                            category_code, network_code,
                            network_code_for, sender_domain_code,
                            created_on, product_mrp,grph_domain_code,
                            C2S_TRANSFER_IN_COUNT , C2S_TRANSFER_IN_AMOUNT,
                            REV_DIFFERENTIAL
                           )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                            gv_categorycode, gv_networkcode,
                            gv_networkcodefor, gv_domaincode,
                            gd_createdon, gv_productmrp,gv_grphdomaincode,
                            ln_c2s_rev_ct, ln_c2s_rev_amt,ln_c2s_rev_diff_amt
                           );
            ELSE
                UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
                  SET C2S_TRANSFER_IN_COUNT =
                                      C2S_TRANSFER_IN_COUNT + ln_c2s_rev_ct,
                      C2S_TRANSFER_IN_AMOUNT =
                                    C2S_TRANSFER_IN_AMOUNT + ln_c2s_rev_amt,
                      REV_DIFFERENTIAL = REV_DIFFERENTIAL + ln_c2s_rev_diff_amt
                  WHERE user_id = gv_userid
                  AND trans_date = gd_transaction_date
                  AND product_code = gv_productcode
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor;
            END IF;

            EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE (   ' sqlexception SQL/OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'sqlexception Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' date=' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid  || SQLERRM );
               v_messageforlog :='OTHERS Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                             --end of distributor insertion block
      END LOOP;
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
   END;
/


DROP PROCEDURE RET_REFILLS_DATA_PROC;

CREATE OR REPLACE PROCEDURE ret_refills_data_proc (p_date DATE)
   IS
      /* Variables for refill amount values */
      lv_servicetype            	DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_sub_service            	DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;
      ln_c2s_trans_ct           	DAILY_C2S_TRANS_DETAILS.transaction_count%TYPE:= 0;
      ln_c2s_trans_amt          	DAILY_C2S_TRANS_DETAILS.transaction_amount%TYPE:= 0;
      ln_c2s_trans_tax1         	DAILY_C2S_TRANS_DETAILS.total_tax1%TYPE  := 0;
      ln_c2s_trans_tax2         	DAILY_C2S_TRANS_DETAILS.total_tax2%TYPE  := 0;
      ln_c2s_trans_tax3         	DAILY_C2S_TRANS_DETAILS.total_tax3%TYPE  := 0;
      ln_c2s_sender_trans_amt   	DAILY_C2S_TRANS_DETAILS.sender_transfer_amount%TYPE:= 0;
      ln_c2s_rec_credit_amt     	DAILY_C2S_TRANS_DETAILS.receiver_credit_amount%TYPE:= 0;
      ln_c2s_rec_access_fee     	DAILY_C2S_TRANS_DETAILS.receiver_access_fee%TYPE:= 0;
      ln_c2s_diff_tax1          	DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax1%TYPE:= 0;
      ln_c2s_diff_tax2          	DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax2%TYPE:= 0;
      ln_c2s_diff_tax3          	DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax3%TYPE:= 0;
      ln_c2s_receiver_bonus     	DAILY_C2S_TRANS_DETAILS.receiver_bonus%TYPE:= 0;
      ln_c2s_diff_amt           	DAILY_C2S_TRANS_DETAILS.differential_amount%TYPE:= 0;
      ln_c2s_diff_count         	DAILY_C2S_TRANS_DETAILS.differential_count%TYPE:= 0;
      ln_roam_c2s_amount        	TEMP_DAILY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE:= 0;
      lv_sendercategory 			DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE :=0;
      lv_receiverserviceclassid 	DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE :=0;
      ln_receiver_validity      	DAILY_C2S_TRANS_DETAILS.receiver_validity%TYPE:=0;
      ln_receiver_bonus_validity    DAILY_C2S_TRANS_DETAILS.receiver_bonus_validity%TYPE:=0;


      /* Cursor Declaration */
		CURSOR refill_data (p_date DATE)
		IS
		SELECT  Mast.sender_id, Mast.network_code,Mast.service_class_id,
                Mast.receiver_network_code, Mast.product_code,
                Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                COUNT (Mast.transfer_id) c2s_count,
                SUM(Mast.transfer_value) c2s_amount,
                NVL(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                NVL(SUM(Mast.receiver_access_fee),0) c2s_recfee,
                NVL(SUM(Mast.receiver_tax1_value),0) c2s_rectax1,
                NVL(SUM(Mast.receiver_tax2_value),0) c2s_rectax2,
                NVL(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                NVL(SUM(Mast.receiver_bonus_value),0) c2s_rec_bonus,
                NVL(SUM(adj.tax1_value),0) diff_tax1,
                NVL(SUM(adj.tax2_value),0) diff_tax2,
                NVL(SUM(adj.tax3_value),0) diff_tax3,
                NVL(SUM(adj.transfer_value),0) diff_amount,
                SUM(CASE  WHEN NVL(adj.transfer_value,0) <> 0 THEN 1 ELSE 0 END) diff_count,
				NVL(SUM(Mast.receiver_validity),0) validity,
                NVL(SUM(Mast.receiver_bonus_validity),0) bonus_validity
				FROM (SELECT 
						c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
						c2strans.receiver_network_code, p.product_code,
						p.unit_value,ug.grph_domain_code, cat.category_code,
						cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
						c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
						c2strans.receiver_access_fee,c2strans.receiver_tax1_value,c2strans.receiver_tax2_value,
						c2strans.sender_transfer_value,c2strans.receiver_bonus_value,c2strans.receiver_validity,
						c2strans.receiver_bonus_validity
						FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P, CATEGORIES cat,USER_GEOGRAPHIES ug
						WHERE c2strans.transfer_date = p_date
						AND  c2strans.transfer_status = '200'
						AND   TRANSFER_TYPE = 'TXN'
						AND   p.product_code=c2strans.product_code
						AND   cat.category_code=c2strans.sender_category
						AND ug.user_id=c2strans.sender_id
					  ) Mast, ADJUSTMENTS_MISTMP adj
				WHERE  Mast.transfer_id = adj.reference_id (+)
				AND    Mast.sender_id   = adj.user_id(+)
				GROUP BY Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
				Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;
                          

   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      lv_servicetype := '';
      lv_sub_service := '';
      lv_sendercategory := '';
      lv_receiverserviceclassid := '';
      gv_productmrp := '';
      
      /* Iterate thru. the Refill cursor */
      FOR ret_data_cur IN refill_data (n_date_for_mis)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         lv_sendercategory := ret_data_cur.category_code;
         lv_receiverserviceclassid := ret_data_cur.service_class_id;
         gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
		 lv_sub_service:= ret_data_cur.sub_service;
         gd_transaction_date := p_date;
         ln_c2s_trans_ct := ret_data_cur.c2s_count;
         ln_c2s_trans_amt := ret_data_cur.c2s_amount;
         ln_c2s_trans_tax1 := ret_data_cur.c2s_rectax1;
         ln_c2s_trans_tax2 := ret_data_cur.c2s_rectax2;
         ln_c2s_trans_tax3 := 0;
         ln_c2s_sender_trans_amt := ret_data_cur.c2s_sender_value;
         ln_c2s_rec_credit_amt := ret_data_cur.c2s_rectrans;
         ln_c2s_rec_access_fee := ret_data_cur.c2s_recfee;
         ln_c2s_diff_tax1 := ret_data_cur.diff_tax1;
         ln_c2s_diff_tax2 := ret_data_cur.diff_tax2;
         ln_c2s_diff_tax3 := ret_data_cur.diff_tax3;
         ln_c2s_receiver_bonus := ret_data_cur.c2s_rec_bonus;
         ln_c2s_diff_amt := ret_data_cur.diff_amount;
         ln_receiver_validity := ret_data_cur.validity;
         ln_receiver_bonus_validity := ret_data_cur.bonus_validity;
         gv_productmrp := ret_data_cur.unit_value;
         ln_c2s_diff_count:= ret_data_cur.diff_count;
         
         /* insert into temp_retailer_mis table */
         BEGIN
            INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
                         sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, sub_service, total_tax1,
                         total_tax2, total_tax3,
                         sender_transfer_amount, receiver_credit_amount,
                         receiver_access_fee, differential_adjustment_tax1,
                         differential_adjustment_tax2,
                         differential_adjustment_tax3, receiver_bonus,
                         created_on, transaction_amount, transaction_count,
                         differential_amount,receiver_validity,receiver_bonus_validity,differential_count
                        )
                 VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
                          lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype, lv_sub_service,ln_c2s_trans_tax1,
                         ln_c2s_trans_tax2, ln_c2s_trans_tax3,
                         ln_c2s_sender_trans_amt, ln_c2s_rec_credit_amt,
                         ln_c2s_rec_access_fee, ln_c2s_diff_tax1,
                         ln_c2s_diff_tax2,
                         ln_c2s_diff_tax3, ln_c2s_receiver_bonus,
                         gd_createdon, ln_c2s_trans_amt, ln_c2s_trans_ct,
                         ln_c2s_diff_amt,ln_receiver_validity,
                         ln_receiver_bonus_validity,ln_c2s_diff_count
                        );
         EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE(   'sqlexception in RET_REFILLS_DATA_PROC 1, User:' || gv_userid || SQLERRM );
               v_messageforlog :='sqlexception in RET_REFILLS_DATA_procedure 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 1, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'OTHERS EXCEPTION in RET_REFILLS_DATA_procedure 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                                --end of retailer insertion block

         BEGIN
            BEGIN
               SELECT 1 INTO user_rcd_count
               FROM TEMP_DAILY_CHNL_TRANS_MAIN
               WHERE user_id = gv_userid
               AND network_code = gv_networkcode
               AND network_code_for = gv_networkcodefor
               AND product_code = gv_productcode
               AND grph_domain_code = gv_grphdomaincode
               AND trans_date = gd_transaction_date;

               IF SQL%NOTFOUND
               THEN
                  v_messageforlog := 'sqlexception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
               END IF;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                        --when no row returned for the user
                 user_rcd_count := 0;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid);
                  v_messageforlog := 'OTHERS SQL Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;

            IF user_rcd_count = 0
            THEN
               INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                           (user_id, trans_date, product_code,
                            category_code, network_code,
                            network_code_for, sender_domain_code,
                            created_on, product_mrp,grph_domain_code,
                            c2s_transfer_out_count, c2s_transfer_out_amount,
                            differential
                           )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                            gv_categorycode, gv_networkcode,
                            gv_networkcodefor, gv_domaincode,
                            gd_createdon, gv_productmrp,gv_grphdomaincode,
                            ln_c2s_trans_ct, ln_c2s_trans_amt,
                            ln_c2s_diff_amt
                           );
            ELSE
               UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
                  SET c2s_transfer_out_count =
                                      c2s_transfer_out_count + ln_c2s_trans_ct,
                      c2s_transfer_out_amount =
                                    c2s_transfer_out_amount + ln_c2s_trans_amt,
                      differential = differential + ln_c2s_diff_amt
                WHERE user_id = gv_userid
                  AND trans_date = gd_transaction_date
                  AND product_code = gv_productcode
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor;
            END IF;

            --Update Roam Counts
            IF (gv_networkcode <> gv_networkcodefor)
            THEN
               sp_update_roam_c2s_trans (gv_userid,
                                         gd_transaction_date,
                                         gv_productcode,
                                         gv_categorycode,
                                         gv_networkcode,
                                         gv_networkcodefor,
                                         gv_domaincode,
                                         gv_productmrp,
                                         gv_grphdomaincode,
                                         ln_c2s_trans_amt
                                        );
            END IF;
         EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE (   ' sqlexception SQL/OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'sqlexception Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' date=' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid  || SQLERRM );
               v_messageforlog :='OTHERS Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                             --end of distributor insertion block
      END LOOP;
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
   END;
/


DROP PROCEDURE P_YEARENDPROCESS;

CREATE OR REPLACE PROCEDURE p_yearEndProcess
(p_returnMessage OUT varchar2)
IS
/* The p_yearEndProcess make entries in the IDS table for the new year
 and can be executed only on the first or last day of the financial year.
 It finds records for the last year and updates for the new year.
 The procedure cannot be executed two times for one financial year.
*/
v_prevYear ids.id_year%TYPE;
v_year number;
v_maxYear number;
v_newYear number;
v_idType ids.id_type%TYPE;
v_idsNetworkCode ids.network_code%type;
insertException EXCEPTION;
finalException EXCEPTION;
currentDay varchar2(2);
currentMonth varchar2(2);
currentYear varchar2(4);
v_currentDay number;
v_currentMonth number;
v_currentYear number;
v_returnMessage varchar2(255);
CURSOR c_idTypes IS
        SELECT id_type, network_code,frequency FROM ids
            WHERE id_year=(SELECT max(id_year) FROM ids WHERE id_year!='ALL');

BEGIN
       SELECT to_char(sysdate,'dd') INTO  currentDay FROM dual;
       SELECT to_char(sysdate,'mm') INTO  currentMonth FROM dual;
       SELECT to_char(sysdate,'yyyy') INTO  currentYear FROM dual;
       v_currentDay :=TO_NUMBER(currentDay);
       v_currentMonth :=TO_NUMBER(currentMonth);
       v_currentYear:=TO_NUMBER(currentYear);
       DBMS_OUTPUT.PUT_LINE('v_currentYear='||v_currentYear||' v_currentMonth='||v_currentMonth||' v_currentDay='||v_currentDay);
       SELECT max(id_year) INTO v_prevYear FROM ids WHERE id_year<>'ALL';
       DBMS_OUTPUT.PUT_LINE('STARTED'||TO_NUMBER(v_prevYear));
       v_year :=TO_NUMBER(v_prevYear);

       v_newYear:=v_year+1;
       DBMS_OUTPUT.PUT_LINE('STARTED v_newYear= '||v_newYear);
       --update queries for ids table


       IF ((v_currentDay=31 AND v_currentMonth=3) OR (v_currentday=1 AND v_currentMonth=4)) THEN
--       IF (v_currentDay=1 AND v_currentMonth=4 AND v_currentYear=v_newYear) THEN

       BEGIN
           Begin
                SELECT max(id_year) INTO v_maxYear FROM ids WHERE id_year<>'ALL';
                IF (v_currentDay=31 AND v_currentMonth=3 AND v_maxYear<=v_currentYear) THEN
                    FOR ids_type_cur IN c_idTypes
                        LOOP
                          BEGIN
                            v_idType :=ids_type_cur.id_type;
                            v_idsNetworkCode :=ids_type_cur.network_code;
                            DBMS_OUTPUT.PUT_LINE('ID_TYPE='||v_idType||'   NETWORK_CODE='||v_idsNetworkCode||'   FREQUENCY='||ids_type_cur.frequency);
                            INSERT INTO ids(id_year, id_type, network_code, last_no,frequency)
                            VALUES (v_newYear,v_idType,v_idsNetworkCode,0,ids_type_cur.frequency);
                          EXCEPTION
                               WHEN others THEN
                               DBMS_OUTPUT.PUT_LINE('EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType);
                             v_returnMessage:='EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType;
                             RAISE insertException;
                          END;
                        END LOOP;
                ELSIF(v_currentDay=1 AND v_currentMonth=4 AND v_maxYear<v_currentYear) THEN
                    FOR ids_type_cur IN c_idTypes
                        LOOP
                          BEGIN
                            v_idType :=ids_type_cur.id_type;
                            v_idsNetworkCode :=ids_type_cur.network_code;
                            DBMS_OUTPUT.PUT_LINE('ID_TYPE='||v_idType||'   NETWORK_CODE='||v_idsNetworkCode||'   FREQUENCY='||ids_type_cur.frequency);
                            INSERT INTO ids(id_year, id_type, network_code, last_no,frequency)
                            VALUES (v_newYear,v_idType,v_idsNetworkCode,0,ids_type_cur.frequency);
                          EXCEPTION
                               WHEN others THEN
                               DBMS_OUTPUT.PUT_LINE('EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType);
                             v_returnMessage:='EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType;
                             RAISE insertException;
                          END;
                        END LOOP;
                END IF;
           EXCEPTION
             WHEN insertException THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
              Raise finalException;
             WHEN others THEN
              DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
               v_returnMessage:='Not able to update the id series entries';
              Raise finalException;
            END;



        v_returnMessage:='Records successfully updated for new financial year';
        COMMIT;

       EXCEPTION
         WHEN others THEN
          DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
          Raise finalException;
      END;

      ELSE
            v_returnMessage:='Program can run only on the First day or the Last day of the financial year';
--            v_returnMessage:='Program can run only on the First day of the financial year';

      END IF;

    p_returnMessage:=v_returnMessage;

    EXCEPTION
    WHEN finalException THEN
         ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
        p_returnMessage:=v_returnMessage;
    WHEN others THEN
           ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
        p_returnMessage:=v_returnMessage;
END p_yearEndProcess;
/


DROP PROCEDURE P_WHITELISTDATAMGT;

CREATE OR REPLACE PROCEDURE p_whiteListDataMgt
(p_errorCode OUT varchar2,
p_returnMessage OUT varchar2)
IS
v_returnMessage varchar2(255);
v_errorCode varchar2(255);
finalException EXCEPTION;
BEGIN
    BEGIN
        EXECUTE IMMEDIATE 'RENAME white_list TO white_list_old';
    EXCEPTION
    WHEN others THEN
           ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
        p_errorCode :=sqlerrm;
        p_returnMessage:='Not able to Rename table to back up, Getting Exception='||sqlerrm;
        RAISE finalException;
    END;
        
    BEGIN
        EXECUTE IMMEDIATE 'RENAME white_list_bak TO white_list';
    EXCEPTION
    WHEN others THEN
           ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
        p_errorCode :=sqlerrm;
        p_returnMessage:='Not able to rename table white_list_bak to original one white_list , Getting Exception='||sqlerrm;
        BEGIN
            EXECUTE IMMEDIATE 'RENAME white_list_old TO white_list';
        EXCEPTION
        WHEN others THEN
               ROLLBACK;
            DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
                p_errorCode :=sqlerrm;
                p_returnMessage:='Not able to rename back up table to original one, Getting Exception='||sqlerrm;
            RAISE finalException;
        END;        
        RAISE finalException;
    END;

    BEGIN
        EXECUTE IMMEDIATE 'CREATE TABLE WHITE_LIST_BAK (NETWORK_CODE             VARCHAR2(2 BYTE)     NOT NULL,'||
          'MSISDN                   VARCHAR2(15 BYTE)    NOT NULL PRIMARY KEY,'||
          'ACCOUNT_ID               VARCHAR2(20 BYTE)    NOT NULL,'||
          'ENTRY_DATE               DATE                 NOT NULL,'||
          'ACCOUNT_STATUS           VARCHAR2(20 BYTE)    NOT NULL,'||
          'SERVICE_CLASS            VARCHAR2(10 BYTE)    NOT NULL,'||
          'CREDIT_LIMIT             NUMBER(20)           NOT NULL,'||
          'INTERFACE_ID             VARCHAR2(15 BYTE)    NOT NULL,'||
          'EXTERNAL_INTERFACE_CODE  VARCHAR2(15 BYTE)    NOT NULL,'||
          'CREATED_ON               DATE                 NOT NULL,'||
          'CREATED_BY               VARCHAR2(20 BYTE)    NOT NULL,'||
          'MODIFIED_ON              DATE                 NOT NULL,'||
          'MODIFIED_BY              VARCHAR2(20 BYTE)    NOT NULL,'||
          ' STATUS                   VARCHAR2(2 BYTE)     DEFAULT ''Y''                   NOT NULL,'||
          'ACTIVATED_ON             DATE                 NOT NULL,'||
          'ACTIVATED_BY             VARCHAR2(20 BYTE)    NOT NULL,'||
          'MOVEMENT_CODE            VARCHAR2(20 BYTE)    NOT NULL,'||
          'LANGUAGE                 VARCHAR2(2 BYTE)     NOT NULL,'||
          'COUNTRY                  VARCHAR2(2 BYTE)     NOT NULL,'||
          'IMSI                     VARCHAR2(20 BYTE)) TABLESPACE SYSTEM';
    EXCEPTION
    WHEN others THEN
           ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
        p_errorCode :=sqlerrm;
        p_returnMessage:='Not able to create table WHITE_LIST_BAK, Getting Exception='||sqlerrm;
        BEGIN
                EXECUTE IMMEDIATE 'DROP TABLE white_list_old';
        EXCEPTION
            WHEN others THEN
                   ROLLBACK;
                DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
                p_errorCode :=sqlerrm;
                p_returnMessage:='Not able to drop backup table, Getting Exception='||sqlerrm;
                RAISE finalException;
        END;        
        RAISE finalException;
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP TABLE white_list_old';
    EXCEPTION
    WHEN others THEN
           ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
        p_errorCode :=sqlerrm;
        p_returnMessage:='Not able to drop backup table, Getting Exception='||sqlerrm;
        RAISE finalException;
    END;
    p_errorCode :=v_errorCode;
    p_returnMessage:=v_returnMessage;

    EXCEPTION
    WHEN others THEN
           ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('EXCEPTION  '||sqlerrm);
        p_errorCode :=v_errorCode;
        p_returnMessage:=v_returnMessage;
END p_whiteListDataMgt;
/


DROP PROCEDURE P_UPDATESUMMARYINFO;

CREATE OR REPLACE PROCEDURE p_updateSummaryInfo
    (p_batchConStat IN voms_batches.BATCH_TYPE%type,
    p_batchDaStat IN voms_batches.BATCH_TYPE%type,
    p_batchReconcileStat IN voms_batches.BATCH_TYPE%type,
    p_modifiedDate IN voms_vouchers.MODIFIED_ON%type,
    p_returnMessage OUT varchar2,
    p_message OUT varchar2,
    p_messageToSend OUT varchar2
    )IS
    v_createdOn voms_vouchers.EXPIRY_DATE%type;
    v_generationBatchNo voms_vouchers.GENERATION_BATCH_NO%type;
    v_userNetworkCode voms_vouchers.USER_NETWORK_CODE%type;
    v_productionNetworkCode voms_vouchers.PRODUCTION_NETWORK_CODE%type;
    v_productID voms_vouchers.PRODUCT_ID%type;
    v_voucherStatus voms_vouchers.CURRENT_STATUS%type;
    v_vouchPreviousStatus voms_vouchers.PREVIOUS_STATUS%type;
    rcd_count NUMBER;
    v_recharge_count NUMBER;

    v_ErrMessage varchar2(200);
    v_voucherUpdateCount NUMBER;

    v_tempCount NUMBER:=0;

    SQLException EXCEPTION;
    NOTINSERTEXCEPTION EXCEPTION;
    EXITEXCEPTION EXCEPTION;

    cursor consumeVouch IS SELECT current_status,previous_status,trunc(v.MODIFIED_ON) createdOn,v.PRODUCT_ID PRODID,count(v.STATUS) cot,
    v.GENERATION_BATCH_NO GENNO,v.USER_NETWORK_CODE ULCODE ,v.PRODUCTION_NETWORK_CODE PRODLOCCODE
    From voms_vouchers v where (current_status=p_batchConStat OR current_status=p_batchDaStat OR current_status=p_batchReconcileStat)
    AND v.CON_SUMMARY_UPDATE='N' AND FIRST_CONSUMED_BY IS NOT NULL
    group by  trunc(v.MODIFIED_ON),v.PRODUCT_ID,v.GENERATION_BATCH_NO,v.USER_NETWORK_CODE,v.PRODUCTION_NETWORK_CODE,current_status,previous_status
    order by trunc(v.MODIFIED_ON),v.PRODUCT_ID,v.PRODUCTION_NETWORK_CODE,current_status,previous_status;
    --v.GENERATION_BATCH_NO;

    BEGIN
        v_voucherUpdateCount:=0;
        FOR VOUCH_CUR IN consumeVouch
         LOOP
         BEGIN
         exit when consumeVouch%NOTFOUND;
          v_tempCount:=0;
          v_productID:=VOUCH_CUR.PRODID;
          v_recharge_count:=VOUCH_CUR.cot;
          v_generationBatchNo:=VOUCH_CUR.GENNO;
          v_userNetworkCode:=VOUCH_CUR.ULCODE;
          v_productionNetworkCode:=VOUCH_CUR.PRODLOCCODE;
          v_createdOn:=VOUCH_CUR.createdOn;
          v_voucherStatus:=VOUCH_CUR.current_status;
          v_vouchPreviousStatus:=VOUCH_CUR.previous_status;

          DBMS_OUTPUT.PUT_LINE('v_productID:='||v_productID);
          DBMS_OUTPUT.PUT_LINE('v_recharge_count:='||v_recharge_count);
          DBMS_OUTPUT.PUT_LINE('v_createdOn:='||v_createdOn);
          DBMS_OUTPUT.PUT_LINE('v_voucherStatus:='||v_voucherStatus);
          DBMS_OUTPUT.PUT_LINE('v_vouchPreviousStatus:='||v_vouchPreviousStatus);
    --      v_tempCount:=v_tempCount+v_recharge_count;
          --DBMS_OUTPUT.PUT_LINE('values   =v_productID'||v_productID||'v_recharge_count'||v_recharge_count);
         -- DBMS_OUTPUT.PUT_LINE('v_generationBatchNo'||v_generationBatchNo||'v_userNetworkCode'||v_userNetworkCode);
    --     if(v_recharge_count>0) then
          BEGIN

                if(v_voucherStatus=p_batchConStat) THEN
                UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
                MODIFIED_ON=p_modifiedDate
                WHERE  PRODUCT_ID=v_productID
                 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                 and USER_NETWORK_CODE=v_userNetworkCode AND FIRST_CONSUMED_BY IS NOT NULL
                 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
                 and current_status=p_batchConStat and trunc(MODIFIED_ON)=v_createdOn;

                if SQL%NOTFOUND then
            DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating  voucher status  ='||sqlerrm);
                raise SQLException;
                end if;  -- end of if SQL%NOTFOUND

                ELSIF(v_voucherStatus=p_batchReconcileStat) THEN
                UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
                MODIFIED_ON=p_modifiedDate
                WHERE  PRODUCT_ID=v_productID
                 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                 and USER_NETWORK_CODE=v_userNetworkCode AND FIRST_CONSUMED_BY IS NOT NULL
                 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
                 and current_status=p_batchReconcileStat and trunc(MODIFIED_ON)=v_createdOn;

                if SQL%NOTFOUND then
            DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating  voucher status  ='||sqlerrm);
                raise SQLException;
                end if;  -- end of if SQL%NOTFOUND

                ELSE
                UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
                MODIFIED_ON=p_modifiedDate
                WHERE  PRODUCT_ID=v_productID
                 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                 and USER_NETWORK_CODE=v_userNetworkCode AND FIRST_CONSUMED_BY IS NOT NULL
                 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
                 and current_status=p_batchDaStat and trunc(MODIFIED_ON)=v_createdOn;

                if SQL%NOTFOUND then
            DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating  voucher status  ='||sqlerrm);
                raise SQLException;
                end if;  -- end of if SQL%NOTFOUND

                END IF;
                v_tempCount:=SQL%ROWCOUNT;
                DBMS_OUTPUT.PUT_LINE('rows updated='||v_tempCount);
                v_voucherUpdateCount:=v_voucherUpdateCount+v_tempCount;
                DBMS_OUTPUT.PUT_LINE('v_tempCount form vouchers '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);

            EXCEPTION
            when SQLException then
             v_ErrMessage:='Not able to update vouchers for PROD. LOC:='||v_productionNetworkCode||' USER_NETWORK_CODE='||v_userNetworkCode||'PRODUCT_ID='||v_productID||'v_voucherStatus='||v_voucherStatus;
            p_returnMessage:='FAILED';
             raise EXITEXCEPTION;
            when others then
             v_ErrMessage:='Not able to update vouchers for PROD. LOC:='||v_productionNetworkCode||' USER_NETWORK_CODE='||v_userNetworkCode||'PRODUCT_ID='||v_productID||'v_voucherStatus='||v_voucherStatus;
            p_returnMessage:='FAILED';
             raise  EXITEXCEPTION;
         END;

          BEGIN

                if(v_voucherStatus=p_batchConStat) THEN
                --UPDATE VOUCHER_BATCH_SUMMARY set TOTAL_RECHARGED=TOTAL_RECHARGED+v_recharge_count
                UPDATE VOMS_VOUCHER_BATCH_SUMMARY set TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
                WHERE  BATCH_NO =v_generationBatchNo;
                DBMS_OUTPUT.PUT_LINE('v_tempCount for voucherBatchsummary '||v_productionNetworkCode ||' v_userNetworkCode '||v_userNetworkCode||'is '||v_tempCount||'v_voucherStatus='||v_voucherStatus);

                ELSIF(v_voucherStatus=p_batchDaStat) THEN
                UPDATE VOMS_VOUCHER_BATCH_SUMMARY set TOTAL_STOLEN_DMG_AFTER_EN =TOTAL_STOLEN_DMG_AFTER_EN+v_tempCount
                WHERE  BATCH_NO =v_generationBatchNo;
                DBMS_OUTPUT.PUT_LINE('v_tempCount for voucherBatchsummary '||v_productionNetworkCode ||' v_userNetworkCode '||v_userNetworkCode||'is '||v_tempCount||'v_voucherStatus='||v_voucherStatus);
                END IF;

                if SQL%NOTFOUND then
            DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while checking updating batch summary  ='||sqlerrm);
                raise SQLException;
                end if;  -- end of if SQL%NOTFOUND
            EXCEPTION
            when SQLException then
             v_ErrMessage:='Not able to update batch for Batch No:='||v_generationBatchNo||'v_voucherStatus='||v_voucherStatus;
             raise SQLException;
            when others then
             v_ErrMessage:='Not able to update batch for Batch No:='||v_generationBatchNo||'v_voucherStatus='||v_voucherStatus;
             raise  NOTINSERTEXCEPTION;
            END;


          BEGIN      --block for insertion/updation in voucher_summary
             begin  --block checking if record exist in voucher_summary
             select '1' INTO rcd_count from dual
             where exists (select 1 from voms_voucher_summary
                         where SUMMARY_DATE=v_createdOn
                         and PRODUCT_ID=v_productID
                         and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                         and USER_NETWORK_CODE=v_userNetworkCode);

            EXCEPTION
            WHEN NO_DATA_FOUND THEN  --when no row returned for the distributor
                 --DBMS_OUTPUT.PUT_LINE('No Record found ');
                 rcd_count := 0;
            WHEN OTHERS THEN
                 DBMS_OUTPUT.PUT_LINE('Exception in Record check ');
                 raise SQLException;
           end;
          if(v_voucherStatus=p_batchConStat) THEN
                if rcd_count = 0 then
                   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
                        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
                        TOTAL_RECHARGED,TOTAL_RECONCILED_CHANGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount,v_tempCount);
                        --TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_recharge_count);
                        DBMS_OUTPUT.PUT_LINE('Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    ELSE
                        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
                        TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
                        --TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_recharge_count);
                        DBMS_OUTPUT.PUT_LINE('Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    END IF;
           else
                   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
                UPDATE VOMS_VOUCHER_SUMMARY
                        SET TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount,
                        TOTAL_RECONCILED_CHANGED=TOTAL_RECONCILED_CHANGED+v_tempCount
                        where SUMMARY_DATE=v_createdOn
                        and PRODUCT_ID=v_productID
                        and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                        and USER_NETWORK_CODE=v_userNetworkCode;
                        DBMS_OUTPUT.PUT_LINE('update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    ELSE
                UPDATE VOMS_VOUCHER_SUMMARY
                        SET TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
                        where SUMMARY_DATE=v_createdOn
                        and PRODUCT_ID=v_productID
                        and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                        and USER_NETWORK_CODE=v_userNetworkCode;
                        DBMS_OUTPUT.PUT_LINE('update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    END IF;
                end if;

            ELSIF(v_voucherStatus=p_batchReconcileStat) THEN
                if rcd_count = 0 then
                        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
                        TOTAL_RECONCILED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
                        DBMS_OUTPUT.PUT_LINE('Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
           else
                UPDATE VOMS_VOUCHER_SUMMARY
                        SET TOTAL_RECONCILED=TOTAL_RECONCILED+v_tempCount
                        where SUMMARY_DATE=v_createdOn
                        and PRODUCT_ID=v_productID
                        and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                        and USER_NETWORK_CODE=v_userNetworkCode;
                        DBMS_OUTPUT.PUT_LINE('update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                end if;

            ELSE
                if rcd_count = 0 then
                   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
                        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
                        TOTAL_STOLEN_DMG_AFTER_EN,TOTAL_RECONCILED_CHANGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount,v_tempCount);
                        DBMS_OUTPUT.PUT_LINE('Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    ELSE
                        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
                        TOTAL_STOLEN_DMG_AFTER_EN) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
                        DBMS_OUTPUT.PUT_LINE('Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    END IF;
           else
                   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
                UPDATE VOMS_VOUCHER_SUMMARY
                        SET TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_tempCount,
                        TOTAL_RECONCILED_CHANGED=TOTAL_RECONCILED_CHANGED+v_tempCount
                        where SUMMARY_DATE=v_createdOn
                        and PRODUCT_ID=v_productID
                        and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                        and USER_NETWORK_CODE=v_userNetworkCode;
                        DBMS_OUTPUT.PUT_LINE('update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    ELSE
                UPDATE VOMS_VOUCHER_SUMMARY
                        SET TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_tempCount
                        where SUMMARY_DATE=v_createdOn
                        and PRODUCT_ID=v_productID
                        and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                        and USER_NETWORK_CODE=v_userNetworkCode;
                        DBMS_OUTPUT.PUT_LINE('update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
                    END IF;
                end if;

          end if;
          exception
            WHEN  SQLException THEN
                  DBMS_OUTPUT.PUT_LINE('SQL/OTHERS EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY'||sqlerrm);
                  Raise SQLException;
            when others then
              DBMS_OUTPUT.PUT_LINE('EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY='||sqlerrm);
                   Raise NOTINSERTEXCEPTION;
          END;

          DBMS_OUTPUT.PUT_LINE('v_voucherUpdateCount===='||v_voucherUpdateCount);

          EXCEPTION
          WHEN SQLException then
          DBMS_OUTPUT.PUT_LINE('SQL Exception for update records '||sqlerrm);
          RAISE EXITEXCEPTION;
          WHEN NOTINSERTEXCEPTION then
          DBMS_OUTPUT.PUT_LINE('Not able to update records '||sqlerrm);
          RAISE EXITEXCEPTION;
          WHEN OTHERS then
          DBMS_OUTPUT.PUT_LINE('Not able to update records '||sqlerrm);
          RAISE EXITEXCEPTION;

          END;
          end loop;
          DBMS_OUTPUT.PUT_LINE('Voucher Count='||v_voucherUpdateCount);
          p_returnMessage:='SUCCESS';
          p_message :='Successfully Completed updation of '||v_voucherUpdateCount||' vouchers';
          p_messageToSend :='';
        --COMMIT;  --final commit

    EXCEPTION
    WHEN EXITEXCEPTION THEN
    p_message :='Not able to update Vouchers';
    p_messageToSend :=v_ErrMessage;
    if consumeVouch%ISOPEN then
          close consumeVouch;
    end if;
    p_returnMessage:='FAILED';
    DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||sqlerrm);
    ROLLBACK;
    WHEN OTHERS THEN
    p_message :='Not able to update Vouchers';
    p_messageToSend :=v_ErrMessage;
    if consumeVouch%ISOPEN then
          close consumeVouch;
    end if;
    DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||sqlerrm);
    p_returnMessage:='FAILED';
    ROLLBACK;
    END;
/


DROP PROCEDURE P_UPDATERECHARGEINFO;

CREATE OR REPLACE PROCEDURE p_updateRechargeInfo
    (p_batchConStat IN voms_batches.BATCH_TYPE%type,
    p_modifiedDate IN voms_vouchers.MODIFIED_ON%type,
    p_returnMessage OUT varchar2,
    p_message OUT varchar2,
    p_messageToSend OUT varchar2
    )IS
    v_createdOn voms_vouchers.EXPIRY_DATE%type;
    v_generationBatchNo voms_vouchers.GENERATION_BATCH_NO%type;
    v_userNetworkCode voms_vouchers.USER_NETWORK_CODE%type;
    v_productionNetworkCode voms_vouchers.PRODUCTION_NETWORK_CODE%type;
    v_productID voms_vouchers.PRODUCT_ID%type;
    rcd_count NUMBER;
    v_recharge_count NUMBER;

    v_ErrMessage varchar2(200);
    v_voucherUpdateCount NUMBER;

    v_tempCount NUMBER:=0;

    SQLException EXCEPTION;
    NOTINSERTEXCEPTION EXCEPTION;
    EXITEXCEPTION EXCEPTION;

    cursor consumeVouch IS select trunc(v.FIRST_CONSUMED_ON) createdOn,v.PRODUCT_ID PRODID,count(v.STATUS) cot,
    v.GENERATION_BATCH_NO GENNO,v.USER_NETWORK_CODE ULCODE ,v.PRODUCTION_NETWORK_CODE PRODLOCCODE
    From voms_vouchers v where status=p_batchConStat
    AND v.CON_SUMMARY_UPDATE='N'
    --AND to_date(v.CONSUMED_ON,'dd/mm/yy')=to_date(p_createdOn,'dd/mm/yy')
    group by  trunc(v.FIRST_CONSUMED_ON),v.PRODUCT_ID,v.GENERATION_BATCH_NO,v.USER_NETWORK_CODE,v.PRODUCTION_NETWORK_CODE
    order by trunc(v.FIRST_CONSUMED_ON),v.PRODUCT_ID,v.PRODUCTION_NETWORK_CODE;
    --v.GENERATION_BATCH_NO;

    BEGIN
        v_voucherUpdateCount:=0;
        FOR VOUCH_CUR IN consumeVouch
         LOOP
         BEGIN
         exit when consumeVouch%NOTFOUND;
          v_tempCount:=0;
          v_productID:=VOUCH_CUR.PRODID;
          v_recharge_count:=VOUCH_CUR.cot;
          v_generationBatchNo:=VOUCH_CUR.GENNO;
          v_userNetworkCode:=VOUCH_CUR.ULCODE;
          v_productionNetworkCode:=VOUCH_CUR.PRODLOCCODE;
          v_createdOn:=VOUCH_CUR.createdOn;
          DBMS_OUTPUT.PUT_LINE('v_productID:='||v_productID);
          DBMS_OUTPUT.PUT_LINE('v_recharge_count:='||v_recharge_count);
          DBMS_OUTPUT.PUT_LINE('v_createdOn:='||v_createdOn);
    --      v_tempCount:=v_tempCount+v_recharge_count;
          --DBMS_OUTPUT.PUT_LINE('values   =v_productID'||v_productID||'v_recharge_count'||v_recharge_count);
         -- DBMS_OUTPUT.PUT_LINE('v_generationBatchNo'||v_generationBatchNo||'v_userNetworkCode'||v_userNetworkCode);
    --     if(v_recharge_count>0) then
          BEGIN
                UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
                MODIFIED_ON=p_modifiedDate
                WHERE  PRODUCT_ID=v_productID
                 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                 and USER_NETWORK_CODE=v_userNetworkCode
                 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
                 and status=p_batchConStat and trunc(FIRST_CONSUMED_ON)=v_createdOn;

                if SQL%NOTFOUND then
            DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating  voucher status  ='||sqlerrm);
                raise SQLException;
                end if;  -- end of if SQL%NOTFOUND

                v_tempCount:=SQL%ROWCOUNT;
                DBMS_OUTPUT.PUT_LINE('rows updated='||v_tempCount);
                v_voucherUpdateCount:=v_voucherUpdateCount+v_tempCount;
                DBMS_OUTPUT.PUT_LINE('v_tempCount form vouchers '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);

            EXCEPTION
            when SQLException then
             v_ErrMessage:='Not able to update vouchers for PROD. LOC:='||v_productionNetworkCode||' USER_NETWORK_CODE='||v_userNetworkCode||'PRODUCT_ID='||v_productID;
            p_returnMessage:='FAILED';
             raise EXITEXCEPTION;
            when others then
             v_ErrMessage:='Not able to update vouchers for PROD. LOC:='||v_productionNetworkCode||' USER_NETWORK_CODE='||v_userNetworkCode||'PRODUCT_ID='||v_productID;
            p_returnMessage:='FAILED';
             raise  EXITEXCEPTION;
         END;

          BEGIN

                --UPDATE VOUCHER_BATCH_SUMMARY set TOTAL_RECHARGED=TOTAL_RECHARGED+v_recharge_count
                UPDATE VOMS_VOUCHER_BATCH_SUMMARY set TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
                WHERE  BATCH_NO =v_generationBatchNo;
                DBMS_OUTPUT.PUT_LINE('v_tempCount for voucherBatchsummary '||v_productionNetworkCode ||' v_userNetworkCode '||v_userNetworkCode||'is '||v_tempCount);

                if SQL%NOTFOUND then
            DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while checking updating batch summary  ='||sqlerrm);
                raise SQLException;
                end if;  -- end of if SQL%NOTFOUND
            EXCEPTION
            when SQLException then
             v_ErrMessage:='Not able to update batch for Batch No:='||v_generationBatchNo;
             raise SQLException;
            when others then
             v_ErrMessage:='Not able to update batch for Batch No:='||v_generationBatchNo;
             raise  NOTINSERTEXCEPTION;
            END;


          BEGIN      --block for insertion/updation in voucher_summary
             begin  --block checking if record exist in voucher_summary
             select '1' INTO rcd_count from dual
             where exists (select 1 from voms_voucher_summary
                         where SUMMARY_DATE=v_createdOn
                         and PRODUCT_ID=v_productID
                         and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                         and USER_NETWORK_CODE=v_userNetworkCode);

            EXCEPTION
            WHEN NO_DATA_FOUND THEN  --when no row returned for the distributor
                 --DBMS_OUTPUT.PUT_LINE('No Record found ');
                 rcd_count := 0;
            WHEN OTHERS THEN
                 DBMS_OUTPUT.PUT_LINE('Exception in Record check ');
                 raise SQLException;
           end;
          if rcd_count = 0 then
            INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
                    TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
                    --TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_recharge_count);
                    DBMS_OUTPUT.PUT_LINE('Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
          else
            UPDATE VOMS_VOUCHER_SUMMARY
            SET TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
                         where SUMMARY_DATE=v_createdOn
                         and PRODUCT_ID=v_productID
                         and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
                         and USER_NETWORK_CODE=v_userNetworkCode;
                         DBMS_OUTPUT.PUT_LINE('update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount);
          end if;
          exception
            WHEN  SQLException THEN
                  DBMS_OUTPUT.PUT_LINE('SQL/OTHERS EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY'||sqlerrm);
                  Raise SQLException;
            when others then
              DBMS_OUTPUT.PUT_LINE('EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY='||sqlerrm);
                   Raise NOTINSERTEXCEPTION;
          END;

          DBMS_OUTPUT.PUT_LINE('v_voucherUpdateCount===='||v_voucherUpdateCount);

          EXCEPTION
          WHEN SQLException then
          DBMS_OUTPUT.PUT_LINE('SQL Exception for update records '||sqlerrm);
          RAISE EXITEXCEPTION;
          WHEN NOTINSERTEXCEPTION then
          DBMS_OUTPUT.PUT_LINE('Not able to update records '||sqlerrm);
          RAISE EXITEXCEPTION;
          WHEN OTHERS then
          DBMS_OUTPUT.PUT_LINE('Not able to update records '||sqlerrm);
          RAISE EXITEXCEPTION;

          END;
          end loop;
          DBMS_OUTPUT.PUT_LINE('Voucher Count='||v_voucherUpdateCount);
          p_returnMessage:='SUCCESS';
          p_message :='Successfully Completed updation of '||v_voucherUpdateCount||' vouchers';
          p_messageToSend :='';
        COMMIT;  --final commit

    EXCEPTION
    WHEN EXITEXCEPTION THEN
    p_message :='Not able to update Vouchers';
    p_messageToSend :=v_ErrMessage;
    if consumeVouch%ISOPEN then
          close consumeVouch;
    end if;
    p_returnMessage:='FAILED';
    DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||sqlerrm);
    ROLLBACK;
    WHEN OTHERS THEN
    p_message :='Not able to update Vouchers';
    p_messageToSend :=v_ErrMessage;
    if consumeVouch%ISOPEN then
          close consumeVouch;
    end if;
    DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||sqlerrm);
    p_returnMessage:='FAILED';
    ROLLBACK;
    END;
/


DROP PROCEDURE P_CHNLSERVICEPROC;

CREATE OR REPLACE PROCEDURE P_CHNLSERVICEPROC
(P_RETURNMESSAGE OUT VARCHAR2)
IS

V_USERID               USER_SERVICES.USER_ID%TYPE;
V_RETURNMESSAGE               VARCHAR2(255);
INSERTEXCEPTION EXCEPTION;


CURSOR USER_ID_CUR IS
                                SELECT DISTINCT  USER_ID FROM USER_SERVICES;

BEGIN
                OPEN  USER_ID_CUR;
                                LOOP
                                FETCH USER_ID_CUR INTO V_USERID ;
                                EXIT WHEN USER_ID_CUR%NOTFOUND;
                                                BEGIN

                                                                INSERT INTO USER_SERVICES (USER_ID,SERVICE_TYPE,STATUS)
                                                                VALUES (V_USERID,'IR', 'Y');
                                                EXCEPTION
                                                                WHEN OTHERS THEN
                                                                DBMS_OUTPUT.PUT_LINE('EXCEPTION IN INSERTING NEW RECORD FOR

'||V_USERID);
                                                                V_RETURNMESSAGE:='EXCEPTION IN INSERTING NEW RECORD FOR

'||V_USERID;
                                                               -- RAISE INSERTEXCEPTION;
                                                END;
                      END LOOP;
    COMMIT;
                      P_RETURNMESSAGE:=V_RETURNMESSAGE;
END ;
/


DROP PROCEDURE PURGEPROC;

CREATE OR REPLACE PROCEDURE PURGEPROC
(
    P_QuerySet         IN STRING_ARRAY ,
    P_Result           OUT VARCHAR2,
    P_COUNTER            OUT NUMBER
)
IS
 vOutHandle utl_file.file_type;
 LINE_BUFF VARCHAR2(2000);
 pattern VARCHAR2(1);
 tblName VARCHAR2(50);
 tblShrtName VARCHAR2(10);
 prevTblname VARCHAR2(50);
 tblLen NUMBER;
 selectClause VARCHAR2(2000);
 whereClause VARCHAR2(2000);
 selectQuery VARCHAR2(4000);
 deleteQuery VARCHAR2(4000);
 SeekFlag BOOLEAN := TRUE;
 rec VARCHAR2(5000);
 TYPE curtype IS REF CURSOR;
 src_cur  curtype;
 fileName VARCHAR2(100);

BEGIN

  pattern := ':';
  P_COUNTER:=0;

     FOR i in 1..P_QuerySet.COUNT
     LOOP
        LINE_BUFF:=P_QuerySet(i);

        dbms_output.put_line(LINE_BUFF);
        tblLen:=INSTR(LINE_BUFF, pattern,-1);
        IF to_number(tblLen)>0 THEN

            tblName:=SUBSTR(LINE_BUFF,1,tblLen-1);
            tblName:=TRIM(tblName);
            whereClause:=SUBSTR(LINE_BUFF,tblLen+1);
            tblLen:=INSTR(tblName,' ',-1);
            dbms_output.put_line('tblLen-------->'||tblLen);
            IF to_number(tblLen)>0 THEN
                tblShrtName:=SUBSTR(tblName,tblLen);
                tblShrtName:=TRIM(tblShrtName);
                tblname:=SUBSTR(tblName,1,tblLen-1);
                tblname:=TRIM(tblname);
            ELSE
                tblShrtName:=' ';
            END IF;
            selectClause:='';

            dbms_output.put_line(tblName);
            dbms_output.put_line(tblShrtName);
            dbms_output.put_line(prevTblname);

            IF (prevTblname <> tblName OR prevTblname is null) THEN
                IF utl_file.is_open(vOutHandle) THEN
                   utl_file.FCLOSE(vOutHandle);
                END IF;
            fileName:=tblName||'_'||to_char(sysdate)||'.csv';
            dbms_output.put_line(fileName);
            vOutHandle := utl_file.fopen('OUTDIR', fileName, 'W',32767);
            P_COUNTER:=P_COUNTER+1;
            dbms_output.put_line(fileName);
            END IF;

            dbms_output.put_line(whereClause);

            FOR focrec IN ( SELECT decode(column_id,1,'','||'',''||')||column_name as col_name
                            from    user_tab_columns
                            where    table_name = upper(tblName)
                            order by column_id
                            )
            LOOP
                selectClause:=selectClause ||to_char(focrec.col_name);
            END LOOP;

            selectQuery:='select '||selectClause || ' ' || 'from' || ' '  || tblName || ' ' || tblShrtName || ' ' || whereClause;
            dbms_output.put_line(selectQuery);

            selectClause:=REPLACE(selectClause,'||'',''||',',');
            dbms_output.put_line(selectClause);

            IF (prevTblname <> tblName OR prevTblname is null) THEN
                utl_file.put_line(vOutHandle, selectClause);
            END IF;

            OPEN src_cur FOR selectQuery;

            LOOP
               FETCH src_cur INTO rec;
                EXIT WHEN src_cur%NOTFOUND;
                dbms_output.put_line(rec);
                utl_file.put_line(vOutHandle, rec);
                utl_file.fflush(vOutHandle);
            END LOOP;

            CLOSE src_cur;

            deleteQuery:='delete from ' || tblName || ' ' || tblShrtName || ' ' ||whereClause;
            dbms_output.put_line(deleteQuery);

            EXECUTE IMMEDIATE deleteQuery;

            prevTblname := tblName;
            COMMIT;
        END IF;

    END LOOP;

  utl_file.fclose_all;
  P_Result:='Y';

  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS
      THEN
        P_Result:='N';
        DBMS_OUTPUT.PUT_LINE('Exception while deleting ');

END PURGEPROC;
/


DROP PROCEDURE PRC_FILE_MULT_COLUMN_GENERATE;

CREATE OR REPLACE PROCEDURE prc_file_mult_column_generate(
p_file_dir VARCHAR2, -- mandatory (Oracle directory name)
p_file_name     VARCHAR2, -- mandatory
p_sql_query        VARCHAR2, -- Multiple column SQL SELECT statement that needs to be executed and processed
p_delimiter     CHAR      -- column delimiter
)
AS
l_cursor_handle  INTEGER;
l_dummy            NUMBER;
l_col_cnt          INTEGER;
l_rec_tab            DBMS_SQL.DESC_TAB;
l_current_col      NUMBER(16);
l_current_line   VARCHAR2(2047);
l_column_value   VARCHAR2(300);
l_file_handle      UTL_FILE.FILE_TYPE;
l_print_text       VARCHAR2(100);
l_record_count   NUMBER(16) := 0;
BEGIN
    /* Open file for append*/
    l_file_handle := UTL_FILE.FOPEN(p_file_dir, p_file_name, 'a', 2047); --Append Mode, 2047 chars per line max, possibly increasable
    l_cursor_handle := DBMS_SQL.OPEN_CURSOR;
    DBMS_SQL.PARSE(l_cursor_handle, p_sql_query, DBMS_SQL.native);
    l_dummy := DBMS_SQL.EXECUTE(l_cursor_handle);
    /* Output column names and define them for latter retrieval of data */
    DBMS_SQL.DESCRIBE_COLUMNS(l_cursor_handle, l_col_cnt, l_rec_tab); -- get column names
    /* Append to file column headers */
    l_current_col := l_rec_tab.FIRST;
    IF (l_current_col IS NOT NULL) THEN
        LOOP
            DBMS_SQL.DEFINE_COLUMN(l_cursor_handle, l_current_col, l_column_value, 300);
            l_print_text := l_rec_tab(l_current_col).col_name || p_delimiter;
            UTL_FILE.PUT (l_file_handle, l_print_text);
            l_current_col := l_rec_tab.NEXT(l_current_col);
            EXIT WHEN (l_current_col IS NULL);
        END LOOP;
    END IF;
    --UTL_FILE.PUT (l_file_handle, 'shishupal');
    UTL_FILE.PUT_LINE (l_file_handle,' ');
    /* Append data for each row */
    LOOP
        EXIT WHEN
        DBMS_SQL.FETCH_ROWS(l_cursor_handle) = 0; -- no more rows to be fetched
        l_current_line := '';       /* Append data for each column */
            FOR l_current_col IN 1..l_col_cnt LOOP
                DBMS_SQL.COLUMN_VALUE (l_cursor_handle, l_current_col, l_column_value);
                l_print_text := l_column_value || p_delimiter;
                l_current_line := l_current_line || l_column_value || p_delimiter;
            END LOOP;
        l_record_count := l_record_count + 1;
        UTL_FILE.PUT_LINE (l_file_handle, l_current_line);
    END LOOP;
    UTL_FILE.FCLOSE (l_file_handle);
    DBMS_SQL.CLOSE_CURSOR(l_cursor_handle);
EXCEPTION
WHEN OTHERS THEN     -- Release resources
    IF DBMS_SQL.IS_OPEN(l_cursor_handle) THEN
        DBMS_SQL.CLOSE_CURSOR(l_cursor_handle);
    END IF;
    IF UTL_FILE.IS_OPEN (l_file_handle) THEN
        UTL_FILE.FCLOSE (l_file_handle);
    END IF;     --RAISE ;
    DBMS_OUTPUT.PUT_LINE(DBMS_UTILITY.format_error_stack);
END;
/


DROP PROCEDURE P2PDWHTEMPPRC;

CREATE OR REPLACE PROCEDURE P2pdwhtempprc
(
        p_date IN DATE,
       p_masterCnt OUT NUMBER,
       p_transCnt        OUT      NUMBER,
       p_message OUT VARCHAR2
)
IS

v_srno                                   NUMBER;
v_data                               VARCHAR (1000);

SQLException EXCEPTION;
EXITEXCEPTION                      EXCEPTION;

CURSOR   P2P_MASTER  IS
        SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||PS.LAST_TRANSFER_ON
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        PS.REGISTERED_ON||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
        FROM P2P_SUBSCRIBERS PS, LOOKUPS LK, KEY_VALUES KV WHERE TRUNC(PS.ACTIVATED_ON) < p_date AND LK.LOOKUP_CODE = PS.STATUS
        AND LK.LOOKUP_TYPE = 'SSTAT'  AND KV.KEY(+) = PS.LAST_TRANSFER_STATUS AND KV.TYPE(+) = 'P2P_STATUS'  AND PS.STATUS IN('Y', 'S');

CURSOR P2P_TRANS IS
        SELECT STR.transfer_id||','||STR.transfer_date||','||STR.transfer_date_time||','||TRI1.msisdn||','||TRI2.msisdn||','
        ||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
        ||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
        ||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
        ||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
        ||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
        ||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
        FROM TRANSFER_ITEMS TRI1, TRANSFER_ITEMS TRI2, SUBSCRIBER_TRANSFERS STR, KEY_VALUES KV1, KEY_VALUES KV2, KEY_VALUES KV
        WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
        AND TRI2.sno = 2 AND STR.transfer_date = p_date
        AND KV1.KEY(+) = TRI1.transfer_status AND KV2.KEY(+) = TRI2.transfer_status AND KV1.TYPE(+) = 'P2P_STATUS'
        AND KV2.TYPE(+) = 'P2P_STATUS' AND KV.KEY(+) = STR.transfer_status AND KV.TYPE(+) = 'P2P_STATUS'  ;

BEGIN
               DBMS_OUTPUT.PUT_LINE('Start P2P DWH PROC');
            v_srno := 0;
            v_data    := NULL;

            DELETE   TEMP_P2P_DWH_MASTER;
            DELETE    TEMP_P2P_DWH_TRANS;
            COMMIT;

           OPEN P2P_MASTER;
           LOOP
                        FETCH P2P_MASTER INTO v_data;
                        EXIT WHEN P2P_MASTER%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_MASTER;

            p_masterCnt := v_srno;
            DBMS_OUTPUT.PUT_LINE('p_masterCnt = '||p_masterCnt);
            v_srno := 0;
            v_data    := NULL;

           OPEN P2P_TRANS;
           LOOP
                        FETCH P2P_TRANS INTO v_data;
                        EXIT WHEN P2P_TRANS%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_TRANS;

            p_transCnt :=v_srno;
            DBMS_OUTPUT.PUT_LINE('p_transCnt = '||p_transCnt);

        COMMIT;
        DBMS_OUTPUT.PUT_LINE('P2P DWH PROC Completed');
        p_message:='SUCCESS';

        EXCEPTION
                 WHEN SQLException THEN
                                   p_message:='Not able to migrate data, SQL Exception occoured';
                                 RAISE EXITEXCEPTION;
                  WHEN OTHERS THEN
                                   p_message:='Not able to migrate data, Exception occoured';
                              RAISE  EXITEXCEPTION;


END;
/


DROP PROCEDURE NETWORK_DAILY_CLOSING_STOCK;

CREATE OR REPLACE PROCEDURE network_daily_closing_stock (
                                   rtn_message           OUT      VARCHAR2,
                            rtn_messageforlog     OUT      VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT      VARCHAR2
                                   )
IS
/* ############## new procedure for network daily closing stock#####################
CREATE TABLE NETWORK_STOCK_TEMP
(
  PROCESS     VARCHAR2(40 BYTE),
  START_TIME  DATE,
  END_TIME    DATE,
  STATUS_LOG  VARCHAR2(100 BYTE)
);   */
p_network_code NETWORK_STOCKS.network_code%TYPE;
p_network_code_for NETWORK_STOCKS.network_code_for%TYPE;
p_product_code NETWORK_STOCKS.product_code%TYPE;
p_stock_created NETWORK_STOCKS.stock_created%TYPE;
p_stock_returned NETWORK_STOCKS.stock_returned%TYPE;
p_stock NETWORK_STOCKS.stock%TYPE;
p_stock_sold NETWORK_STOCKS.stock_sold%TYPE;
p_last_txn_no NETWORK_STOCKS.last_txn_no%TYPE;
p_last_txn_type NETWORK_STOCKS.last_txn_type%TYPE;
p_last_txn_stock NETWORK_STOCKS.last_txn_stock%TYPE;
p_previous_stock NETWORK_STOCKS.previous_stock%TYPE;
p_foc_stock_created NETWORK_STOCKS.foc_stock_created%TYPE;
p_foc_stock_returned NETWORK_STOCKS.foc_stock_returned%TYPE;
p_foc_stock NETWORK_STOCKS.foc_stock%TYPE;
p_foc_stock_sold NETWORK_STOCKS.foc_stock_sold%TYPE;
p_foc_last_txn_no NETWORK_STOCKS.foc_last_txn_no%TYPE;
p_foc_last_txn_type NETWORK_STOCKS.foc_last_txn_type%TYPE;
p_foc_last_txn_stock NETWORK_STOCKS.foc_last_txn_stock%TYPE;
p_foc_previous_stock NETWORK_STOCKS.foc_previous_stock%TYPE;
p_inc_stock_created NETWORK_STOCKS.inc_stock_created%TYPE;
p_inc_stock_returned NETWORK_STOCKS.inc_stock_returned%TYPE;
p_inc_stock NETWORK_STOCKS.inc_stock%TYPE;
p_inc_stock_sold NETWORK_STOCKS.inc_stock_sold%TYPE;
p_inc_last_txn_no NETWORK_STOCKS.inc_last_txn_no%TYPE;
p_inc_last_txn_type NETWORK_STOCKS.inc_last_txn_type%TYPE;
p_inc_last_txn_stock NETWORK_STOCKS.inc_last_txn_stock%TYPE;
p_inc_previous_stock NETWORK_STOCKS.inc_previous_stock%TYPE;
p_modified_by NETWORK_STOCKS.modified_by%TYPE;
p_modified_on NETWORK_STOCKS.modified_on%TYPE;
p_created_on NETWORK_STOCKS.created_on%TYPE;
p_created_by NETWORK_STOCKS.created_by%TYPE;
p_daily_stock_updated_on DATE;


q_created_on DATE;
dayDifference NUMBER (5):= 0;
startCount NUMBER(3);
dateCounter DATE;




sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 CURSOR network_stock_list_cur IS
        SELECT network_code,network_code_for,product_code,stock_created,
        stock_returned,stock,stock_sold,last_txn_no,last_txn_type,last_txn_stock,previous_stock,
        foc_stock_created, foc_stock_returned, foc_stock, foc_stock_sold,
        foc_last_txn_no, foc_last_txn_type, foc_last_txn_stock, foc_previous_stock,
        inc_stock_created, inc_stock_returned, inc_stock, inc_stock_sold,
        inc_last_txn_no, inc_last_txn_type, inc_last_txn_stock, inc_previous_stock,
        modified_by,modified_on,created_on,created_by,daily_stock_updated_on
        FROM NETWORK_STOCKS
        WHERE TRUNC(daily_stock_updated_on)<>TRUNC(SYSDATE) FOR UPDATE;

--BEGIN

        --INSERT INTO NETWORK_STOCK_TEMP(PROCESS,START_TIME) VALUES ('NETWORK_DAILY_CLOSING_STOCK_PROCESS',SYSDATE);


BEGIN
             FOR network_stock_records IN network_stock_list_cur
             LOOP
                    p_network_code:=network_stock_records.network_code;
                    p_network_code_for:=network_stock_records.network_code_for;
                    p_product_code:=network_stock_records.product_code;
                    p_stock_created:=network_stock_records.stock_created;
                    p_stock_returned:=network_stock_records.stock_returned;
                    p_stock:=network_stock_records.stock;
                    p_stock_sold:=network_stock_records.stock_sold;
                    p_last_txn_no:=network_stock_records.last_txn_no;
                    p_last_txn_type:=network_stock_records.last_txn_type;
                    p_last_txn_stock:=network_stock_records.last_txn_stock;
                    p_previous_stock:=network_stock_records.previous_stock;
                    p_foc_stock_created:=network_stock_records.foc_stock_created;
                    p_foc_stock_returned:=network_stock_records.foc_stock_returned;
                    p_foc_stock:=network_stock_records.foc_stock;
                    p_foc_stock_sold:=network_stock_records.foc_stock_sold;
                    p_foc_last_txn_no:=network_stock_records.foc_last_txn_no;
                    p_foc_last_txn_type:=network_stock_records.foc_last_txn_type;
                    p_foc_last_txn_stock:=network_stock_records.foc_last_txn_stock;
                    p_foc_previous_stock:=network_stock_records.foc_previous_stock;
                    p_inc_stock_created:=network_stock_records.inc_stock_created;
                    p_inc_stock_returned:=network_stock_records.inc_stock_returned;
                    p_inc_stock:=network_stock_records.inc_stock;
                    p_inc_stock_sold:=network_stock_records.inc_stock_sold;
                    p_inc_last_txn_no:=network_stock_records.inc_last_txn_no;
                    p_inc_last_txn_type:=network_stock_records.inc_last_txn_type;
                    p_inc_last_txn_stock:=network_stock_records.inc_last_txn_stock;
                    p_inc_previous_stock:=network_stock_records.inc_previous_stock;
                    p_modified_by:=network_stock_records.modified_by;
                    p_modified_on:=network_stock_records.modified_on;
                    p_created_on:=network_stock_records.created_on;
                    p_created_by:=network_stock_records.created_by;
                    p_daily_stock_updated_on:=network_stock_records.daily_stock_updated_on;

             BEGIN

                     q_created_on  :=SYSDATE;
                  startCount := 1;
                  dateCounter:= p_daily_stock_updated_on;
                  dayDifference:= TRUNC(q_created_on) - p_daily_stock_updated_on;

                  DBMS_OUTPUT.PUT_LINE(' No Of dayDifference::'||dayDifference);


                FOR xyz IN startCount .. dayDifference
                LOOP

                     BEGIN

                       INSERT INTO NETWORK_DAILY_STOCKS
                        (stock_date,product_code,network_code,network_code_for,stock_created,stock_returned,stock,
                        stock_sold,last_txn_no,last_txn_type,last_txn_stock,previous_stock,foc_stock_created, foc_stock_returned, foc_stock, foc_stock_sold, foc_last_txn_no, foc_last_txn_type, foc_last_txn_stock, foc_previous_stock,inc_stock_created,inc_stock_returned, inc_stock, inc_stock_sold,inc_last_txn_no, inc_last_txn_type,inc_last_txn_stock, inc_previous_stock,created_on
                        )
                      VALUES(dateCounter,p_product_code,p_network_code,p_network_code_for,p_stock_created,
                      p_stock_returned,p_stock,p_stock_sold,p_last_txn_no,p_last_txn_type,p_last_txn_stock,
                      p_previous_stock,p_foc_stock_created,p_foc_stock_returned,p_foc_stock,p_foc_stock_sold,
                      p_foc_last_txn_no,p_foc_last_txn_type,p_foc_last_txn_stock ,p_foc_previous_stock, p_inc_stock_created,p_inc_stock_returned,p_inc_stock,p_inc_stock_sold,p_inc_last_txn_no,
                      p_inc_last_txn_type,p_inc_last_txn_stock,p_inc_previous_stock,q_created_on);
                      EXCEPTION
                        WHEN OTHERS
                        THEN
                           DBMS_OUTPUT.PUT_LINE ('Exception OTHERS in NETWORK_DAILY_CLOSING_STOCK Insert SQL, Product:' || p_product_code || SQLERRM );
                           rtn_messageforlog := 'Exception OTHERS in NETWORK_DAILY_CLOSING_STOCK Insert SQL, Product:' || p_product_code ;
                           rtn_sqlerrmsgforlog := SQLERRM;
                           RAISE sqlexception;

                    END;-- End of insert SQL

                    BEGIN

                        UPDATE NETWORK_STOCKS SET
                                daily_stock_updated_on=q_created_on
                        WHERE product_code=p_product_code
                        AND network_code=p_network_code
                        AND network_code_for=p_network_code_for;

                        EXCEPTION
                           WHEN OTHERS
                           THEN
                              DBMS_OUTPUT.PUT_LINE ('Exception in NETWORK_DAILY_CLOSING_STOCK Update SQL, Product:' || p_product_code || SQLERRM );
                              rtn_messageforlog := 'Exception in NETWORK_DAILY_CLOSING_STOCK Update SQL, Product:' || p_product_code ;
                                 rtn_sqlerrmsgforlog := SQLERRM;
                              RAISE sqlexception;

                    END;-- End of update SQL

                startCount:= startCount+1;
                dateCounter:= dateCounter+1;

                END LOOP;--End of daydiffrence loop


             END;--End oF Outer begin


             END LOOP;--End of outer for loop

                 rtn_message:='SUCCESS';
                 rtn_messageForLog :='PreTUPS NETWORK_DAILY_CLOSING_STOCK MIS successfully executed, Date Time:'||SYSDATE;
                 rtn_sqlerrMsgForLog :=' ';

                -- UPDATE NETWORK_STOCK_TEMP SET END_TIME=SYSDATE,STATUS_LOG=rtn_message WHERE PROCESS='NETWORK_DAILY_CLOSING_STOCK_PROCESS' AND trunc(START_TIME)=trunc(sysdate);
         COMMIT;

         EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;

         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in NETWORK_DAILY_CLOSING_STOCK procedure:='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;



END;

--END;
/


DROP PROCEDURE MOVE_TO_FINAL_DATA;

CREATE OR REPLACE PROCEDURE move_to_final_data (p_date DATE)
   IS
      /* variables to store order related amounts */
      ln_roam_c2s_out           TEMP_DAILY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE;
      ln_c2s_trans_out_ct       TEMP_DAILY_CHNL_TRANS_MAIN.c2s_transfer_out_count%TYPE;
      ln_c2s_trans_out_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2s_transfer_out_amount%TYPE;
      ln_o2c_trans_in_ct        TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_count%TYPE;
      ln_o2c_trans_in_amt       TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_amount%TYPE;
      ln_o2c_return_out_ct      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_count%TYPE;
      ln_o2c_return_out_amt     TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_amount%TYPE;
      ln_o2c_withdraw_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_count%TYPE;
      ln_o2c_withdraw_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_amount%TYPE;
      ln_c2c_transfer_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_count%TYPE;
      ln_c2c_transfer_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_amount%TYPE;
      ln_c2c_transfer_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_count%TYPE;
      ln_c2c_transfer_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_amount%TYPE;
      ln_c2c_return_in_ct       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_count%TYPE;
      ln_c2c_return_in_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_amount%TYPE;
      ln_c2c_return_out_ct      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_count%TYPE;
      ln_c2c_return_out_amt     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_amount%TYPE;
      ln_c2c_withdraw_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_count%TYPE;
      ln_c2c_withdraw_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_amount%TYPE;
      ln_c2c_withdraw_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_count%TYPE;
      ln_c2c_withdraw_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_amount%TYPE;
      ln_differential           TEMP_DAILY_CHNL_TRANS_MAIN.differential%TYPE;
      ln_adjustment_in          TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_in%TYPE;
      ln_adjustment_out         TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_out%TYPE;
      ln_opening_stock          TEMP_DAILY_CHNL_TRANS_MAIN.opening_balance%TYPE;
      ln_closing_stock          DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      ln_stock_updated          DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      ln_productmrp             TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
      
      ln_c2c_reverse_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_in_count%TYPE;
      ln_c2c_reverse_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_in_amount%TYPE;
      ln_c2c_reverse_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_out_count%TYPE;
      ln_c2c_reverse_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_out_amount%TYPE;
      ln_o2c_reverse_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_in_count%TYPE;
      ln_o2c_reverse_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_in_amount%TYPE;
      ln_o2c_reverse_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_out_count%TYPE;
      ln_o2c_reverse_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_out_amount%TYPE;


      /* Cursor Declaration */
      CURSOR user_data (p_date DATE)
      IS
         SELECT user_id, trans_date, product_code, category_code,
                network_code, network_code_for, sender_domain_code,
                roam_c2s_transfer_out_amount, c2s_transfer_out_count,
                c2s_transfer_out_amount, o2c_transfer_in_count,
                o2c_transfer_in_amount, o2c_return_out_count,
                o2c_return_out_amount, o2c_withdraw_out_count,
                o2c_withdraw_out_amount, c2c_transfer_in_count,
                c2c_transfer_in_amount, c2c_transfer_out_count,
                c2c_transfer_out_amount, c2c_return_in_count,
                c2c_return_in_amount, c2c_return_out_count,
                c2c_return_out_amount, c2c_withdraw_in_count,
                c2c_withdraw_in_amount, c2c_withdraw_out_count,
                c2c_withdraw_out_amount, differential, adjustment_in,
                adjustment_out, created_on, opening_balance,product_mrp,
                grph_domain_code,c2c_reverse_in_count,c2c_reverse_in_amount, 
        c2c_reverse_out_count,c2c_reverse_out_amount,o2c_reverse_in_count,  
        o2c_reverse_in_amount,o2c_reverse_out_count,o2c_reverse_out_amount ,
		C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT,REV_DIFFERENRIAL
       FROM TEMP_DAILY_CHNL_TRANS_MAIN
          WHERE trans_date = p_date;
   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      ln_closing_stock := 0;
      ln_stock_updated := 0;
      gv_productmrp := '';

      /* Iterate RETAILER_DATA cursor */
      FOR user_data_cur IN user_data (n_date_for_mis)
      LOOP
         --  DBMS_OUTPUT.PUT_LINE('IN RETAILER CURSOR ');
         gv_userid := user_data_cur.user_id;
         gd_transaction_date := p_date;
         gv_productcode := user_data_cur.product_code;
         gv_categorycode := user_data_cur.category_code;
         gv_networkcode := user_data_cur.network_code;
         gv_networkcodefor := user_data_cur.network_code_for;
         gv_domaincode := user_data_cur.sender_domain_code;
         ln_roam_c2s_out := user_data_cur.roam_c2s_transfer_out_amount;
         ln_c2s_trans_out_ct := user_data_cur.c2s_transfer_out_count;
         ln_c2s_trans_out_amt := user_data_cur.c2s_transfer_out_amount;
         ln_o2c_trans_in_ct := user_data_cur.o2c_transfer_in_count;
         ln_o2c_trans_in_amt := user_data_cur.o2c_transfer_in_amount;
         ln_o2c_return_out_ct := user_data_cur.o2c_return_out_count;
         ln_o2c_return_out_amt := user_data_cur.o2c_return_out_amount;
         ln_o2c_withdraw_out_ct := user_data_cur.o2c_withdraw_out_count;
         ln_o2c_withdraw_out_amt := user_data_cur.o2c_withdraw_out_amount;
         ln_c2c_transfer_in_ct := user_data_cur.c2c_transfer_in_count;
         ln_c2c_transfer_in_amt := user_data_cur.c2c_transfer_in_amount;
         ln_c2c_transfer_out_ct := user_data_cur.c2c_transfer_out_count;
         ln_c2c_transfer_out_amt := user_data_cur.c2c_transfer_out_amount;
         ln_c2c_return_in_ct := user_data_cur.c2c_return_in_count;
         ln_c2c_return_in_amt := user_data_cur.c2c_return_in_amount;
         ln_c2c_return_out_ct := user_data_cur.c2c_return_out_count;
         ln_c2c_return_out_amt := user_data_cur.c2c_return_out_amount;
         ln_c2c_withdraw_in_ct := user_data_cur.c2c_withdraw_in_count;
         ln_c2c_withdraw_in_amt := user_data_cur.c2c_withdraw_in_amount;
         ln_c2c_withdraw_out_ct := user_data_cur.c2c_withdraw_out_count;
         ln_c2c_withdraw_out_amt := user_data_cur.c2c_withdraw_out_amount;
         ln_differential := user_data_cur.differential;
         ln_adjustment_in := user_data_cur.adjustment_in;
         ln_adjustment_out := user_data_cur.adjustment_out;
         ln_opening_stock := user_data_cur.opening_balance;
         gv_grphdomaincode := user_data_cur.grph_domain_code;
         ln_productmrp := user_data_cur.product_mrp;
         ln_c2c_reverse_in_ct := user_data_cur.c2c_reverse_in_count;
         ln_c2c_reverse_in_amt := user_data_cur.c2c_reverse_in_amount;
         ln_c2c_reverse_out_ct := user_data_cur.c2c_reverse_out_count;
         ln_c2c_reverse_out_amt := user_data_cur.c2c_reverse_out_amount;
     ln_o2c_reverse_in_ct := user_data_cur.o2c_reverse_in_count;
         ln_o2c_reverse_in_amt := user_data_cur.o2c_reverse_in_amount;
         ln_o2c_reverse_out_ct := user_data_cur.o2c_reverse_out_count;
         ln_o2c_reverse_out_amt := user_data_cur.o2c_reverse_out_amount;
		 
		 ---added on 12/09
		 ln_C2S_TRANSFER_IN_AMOUNT := user_data_cur.TRANSFER_IN_AMOUNT;
		 ln_C2S_TRANSFER_IN_COUNT  := user_data_cur.C2S_TRANSFER_IN_COUNT;
		 ln_REV_DIFFERENRIAL  :=  user_data_cur.REV_DIFFERENRIAL;
         -- TO DO CHANGE THE FORMULAE
         ln_stock_updated :=
              ln_o2c_trans_in_amt
            + ln_c2c_transfer_in_amt
            + ln_c2c_return_in_amt
            + ln_c2c_withdraw_in_amt
            + ln_differential
            + ln_adjustment_in
            - ln_c2s_trans_out_amt
            - ln_o2c_return_out_amt
            - ln_o2c_withdraw_out_amt
            - ln_c2c_transfer_out_amt
            - ln_c2c_return_out_amt
            - ln_c2c_withdraw_out_amt
            - ln_adjustment_out
        + ln_c2c_reverse_in_amt --added for C2C reversal
        - ln_c2c_reverse_out_amt --added for C2C reversal
        + ln_o2c_reverse_in_amt --added for O2C reversal
        - ln_o2c_reverse_out_amt; --added for O2C reversal

     --ln_stock_updated := ln_stock_updated / ln_productmrp; TBD

         BEGIN
            IF (ln_stock_updated > 0)
            THEN
               ln_closing_stock := ln_opening_stock + ln_stock_updated;
            ELSE
               ln_closing_stock := ln_opening_stock + ln_stock_updated;
            END IF;

            INSERT INTO DAILY_CHNL_TRANS_MAIN
                        (user_id, trans_date, product_code,
                         category_code, network_code, network_code_for,
                         sender_domain_code, roam_c2s_transfer_out_amount,opening_balance,
						 closing_balance,c2s_transfer_out_count, c2s_transfer_out_amount,
                         o2c_transfer_in_count, o2c_transfer_in_amount,o2c_return_out_count, 
						 o2c_return_out_amount,o2c_withdraw_out_count, o2c_withdraw_out_amount,
                         c2c_transfer_in_count, c2c_transfer_in_amount,c2c_transfer_out_count, 
						 c2c_transfer_out_amount,c2c_return_in_count, c2c_return_in_amount,
                         c2c_return_out_count, c2c_return_out_amount,c2c_withdraw_in_count, 
						 c2c_withdraw_in_amount,c2c_withdraw_out_count, c2c_withdraw_out_amount,
                         differential, adjustment_in, adjustment_out, 
						 created_on, grph_domain_code, c2c_reverse_in_count, 
						 c2c_reverse_in_amount,c2c_reverse_out_count, c2c_reverse_out_amount,
						 o2c_reverse_in_count, o2c_reverse_in_amount,o2c_reverse_out_count, 
						 o2c_reverse_out_amount,C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT,REV_DIFFERENRIAL
                        )
                 VALUES (gv_userid, gd_transaction_date, gv_productcode,
                         gv_categorycode, gv_networkcode, gv_networkcodefor,
                         gv_domaincode, ln_roam_c2s_out,
                         ln_opening_stock, ln_closing_stock,
                         ln_c2s_trans_out_ct, ln_c2s_trans_out_amt,
                         ln_o2c_trans_in_ct, ln_o2c_trans_in_amt,
                         ln_o2c_return_out_ct, ln_o2c_return_out_amt,
                         ln_o2c_withdraw_out_ct, ln_o2c_withdraw_out_amt,
                         ln_c2c_transfer_in_ct, ln_c2c_transfer_in_amt,
                         ln_c2c_transfer_out_ct, ln_c2c_transfer_out_amt,
                         ln_c2c_return_in_ct, ln_c2c_return_in_amt,
                         ln_c2c_return_out_ct, ln_c2c_return_out_amt,
                         ln_c2c_withdraw_in_ct, ln_c2c_withdraw_in_amt,
                         ln_c2c_withdraw_out_ct, ln_c2c_withdraw_out_amt,
                         ln_differential, ln_adjustment_in,
                         ln_adjustment_out, gd_createdon, gv_grphdomaincode,
                         ln_c2c_reverse_in_ct, ln_c2c_reverse_in_amt,
                         ln_c2c_reverse_out_ct, ln_c2c_reverse_out_amt,
                         ln_o2c_reverse_in_ct, ln_o2c_reverse_in_amt,
                         ln_o2c_reverse_out_ct, ln_o2c_reverse_out_amt,
						 ln_C2S_TRANSFER_IN_AMOUNT ,ln_C2S_TRANSFER_IN_COUNT, ln_REV_DIFFERENRIAL  
                        );
         EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION CAUGHT in MOVE_TO_FINAL_DATA 1, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'Exception in MOVE_TO_FINAL_DATA 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                                    --end of user insertion block
      END LOOP;                                                       --end of
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception CAUGHT in MOVE_TO_FINAL_DATA 2');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in MOVE_TO_FINAL_DATA 2');
         RAISE retmainexception;
   END;
/


DROP PROCEDURE MOVENETWORKSTOCKSDATA;

CREATE OR REPLACE PROCEDURE moveNetworkStocksData
IS
CURSOR stockData_cur IS SELECT * FROM network_stocks;
 oldTable_row network_stocks%ROWTYPE;    
 newTable_row network_stocks_new%ROWTYPE;

BEGIN 
       
  FOR oldTable_row in stockData_cur
    LOOP
       newTable_row.network_code := oldTable_row.network_code;
       newTable_row.network_code_for := oldTable_row.network_code_for;
       newTable_row.product_code := oldTable_row.product_code;
       newTable_row.daily_stock_updated_on := oldTable_row.daily_stock_updated_on;
       newTable_row.created_on := oldTable_row.created_on;
       newTable_row.created_by := oldTable_row.created_by;
       newTable_row.modified_on := oldTable_row.modified_on;
       newTable_row.modified_by := oldTable_row.modified_by;       
       newTable_row.wallet_type := 'SAL';
       newTable_row.wallet_created := oldTable_row.stock_created;
       newTable_row.wallet_returned := oldTable_row.stock_returned;
       newTable_row.wallet_sold := oldTable_row.stock_sold;
       newTable_row.wallet_balance := oldTable_row.stock;
       newTable_row.last_txn_no := oldTable_row.last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.last_txn_stock;
       newTable_row.last_txn_type := oldTable_row.last_txn_type;
       newTable_row.previous_balance := oldTable_row.previous_stock;
         BEGIN
            insert into network_stocks_new values(newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_type,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.modified_by,newTable_row.modified_on,newTable_row.created_on,newTable_row.created_by,newTable_row.daily_stock_updated_on);         
         END;         
       newTable_row.wallet_type := 'FOC';
       newTable_row.wallet_created := oldTable_row.foc_stock_created;
       newTable_row.wallet_returned := oldTable_row.foc_stock_returned;
       newTable_row.wallet_sold := oldTable_row.foc_stock_sold;
       newTable_row.wallet_balance := oldTable_row.foc_stock;
       newTable_row.last_txn_no := oldTable_row.foc_last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.foc_last_txn_stock;
       newTable_row.last_txn_type := oldTable_row.foc_last_txn_type;
       newTable_row.previous_balance := oldTable_row.foc_previous_stock;
          BEGIN
            insert into network_stocks_new values(newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_type,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.modified_by,newTable_row.modified_on,newTable_row.created_on,newTable_row.created_by,newTable_row.daily_stock_updated_on);         
         END;
        newTable_row.wallet_type := 'INC';
       newTable_row.wallet_created := oldTable_row.inc_stock_created;
       newTable_row.wallet_returned := oldTable_row.inc_stock_returned;
       newTable_row.wallet_sold := oldTable_row.inc_stock_sold;
       newTable_row.wallet_balance := oldTable_row.inc_stock;
       newTable_row.last_txn_no := oldTable_row.inc_last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.inc_last_txn_stock;
       newTable_row.last_txn_type := oldTable_row.inc_last_txn_type;
       newTable_row.previous_balance := oldTable_row.inc_previous_stock;
          BEGIN
            insert into network_stocks_new values(newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_type,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.modified_by,newTable_row.modified_on,newTable_row.created_on,newTable_row.created_by,newTable_row.daily_stock_updated_on);         
         END;
       
    END LOOP;
  commit;
END;
/


DROP PROCEDURE MOVENETWORKDAILYSTOCKSDATA;

CREATE OR REPLACE PROCEDURE moveNetworkDailyStocksData
IS
CURSOR stockData_cur IS SELECT * FROM network_daily_stocks;
 oldTable_row network_daily_stocks%ROWTYPE;    
 newTable_row network_daily_stocks_new%ROWTYPE;

BEGIN 
       
  FOR oldTable_row in stockData_cur
    LOOP
       newTable_row.network_code := oldTable_row.network_code;
	   newTable_row.network_code_for := oldTable_row.network_code_for;
       newTable_row.product_code := oldTable_row.product_code;
	   newTable_row.wallet_date := oldTable_row.stock_date;
	   newTable_row.created_on := oldTable_row.created_on;
	   newTable_row.creation_type := oldTable_row.creation_type;
	   newTable_row.wallet_type := 'SAL';
       newTable_row.wallet_created := oldTable_row.stock_created;
       newTable_row.wallet_returned := oldTable_row.stock_returned;
       newTable_row.wallet_sold := oldTable_row.stock_sold;
       newTable_row.wallet_balance := oldTable_row.stock;
       newTable_row.last_txn_no := oldTable_row.last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.last_txn_stock;
       newTable_row.last_txn_type := oldTable_row.last_txn_type;
       newTable_row.previous_balance := oldTable_row.previous_stock;
         BEGIN
            insert into network_daily_stocks_new values(newTable_row.wallet_date,newTable_row.wallet_type,newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.created_on,newTable_row.creation_type);         
         END;         
       newTable_row.network_code := oldTable_row.network_code;
       newTable_row.network_code_for := oldTable_row.network_code_for;
       newTable_row.product_code := oldTable_row.product_code;
       newTable_row.wallet_date := oldTable_row.stock_date;
       newTable_row.created_on := oldTable_row.created_on;
       newTable_row.creation_type := oldTable_row.creation_type;
       newTable_row.wallet_type := 'FOC';
       newTable_row.wallet_created := oldTable_row.stock_created;
       newTable_row.wallet_returned := oldTable_row.stock_returned;
       newTable_row.wallet_sold := oldTable_row.stock_sold;
       newTable_row.wallet_balance := oldTable_row.stock;
       newTable_row.last_txn_no := oldTable_row.last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.last_txn_stock;
       newTable_row.last_txn_type := oldTable_row.last_txn_type;
       newTable_row.previous_balance := oldTable_row.previous_stock;
         BEGIN
            insert into network_daily_stocks_new values(newTable_row.wallet_date,newTable_row.wallet_type,newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.created_on,newTable_row.creation_type);         
         END;
       newTable_row.network_code := oldTable_row.network_code;
       newTable_row.network_code_for := oldTable_row.network_code_for;
       newTable_row.product_code := oldTable_row.product_code;
       newTable_row.wallet_date := oldTable_row.stock_date;
       newTable_row.created_on := oldTable_row.created_on;
       newTable_row.creation_type := oldTable_row.creation_type;
       newTable_row.wallet_type := 'INC';
       newTable_row.wallet_created := oldTable_row.stock_created;
       newTable_row.wallet_returned := oldTable_row.stock_returned;
       newTable_row.wallet_sold := oldTable_row.stock_sold;
       newTable_row.wallet_balance := oldTable_row.stock;
       newTable_row.last_txn_no := oldTable_row.last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.last_txn_stock;
       newTable_row.last_txn_type := oldTable_row.last_txn_type;
       newTable_row.previous_balance := oldTable_row.previous_stock;
         BEGIN
            insert into network_daily_stocks_new values(newTable_row.wallet_date,newTable_row.wallet_type,newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.created_on,newTable_row.creation_type);         
         END;
       
    END LOOP;
  commit;
END;
/


DROP PROCEDURE INSERT_DLY_NO_C2S_LMS_SMRY;

CREATE OR REPLACE PROCEDURE insert_dly_no_c2s_lms_smry (
                            aiv_Date               IN      VARCHAR2,
                                   rtn_message           OUT   VARCHAR2,
                            rtn_messageforlog     OUT   VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT   VARCHAR2
                                   )
IS
p_trans_date            DAILY_C2S_LMS_SUMMARY.trans_date%type;
p_user_id                DAILY_C2S_LMS_SUMMARY.user_id%type;
p_product_code            DAILY_C2S_LMS_SUMMARY.product_code%type;
p_lms_profile            DAILY_C2S_LMS_SUMMARY.lms_profile%type;
p_txn_amount            DAILY_C2S_LMS_SUMMARY.TRANSACTION_AMOUNT%type;
p_txn_count                DAILY_C2S_LMS_SUMMARY.TRANSACTION_COUNT%type;
p_accumulatepoint        DAILY_C2S_LMS_SUMMARY.ACCUMULATED_POINTS%type;
p_count NUMBER;
sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist
CURSOR insert_cur is
        select distinct cu.user_id,cu.LMS_PROFILE, ps.PRODUCT_CODE  from channel_users cu,  users U, PROFILE_SET_VERSION ps
        where cu.LMS_PROFILE is not null and u.USER_ID=cu.USER_ID and u.status not in ('N','C') and cu.LMS_PROFILE=ps.SET_ID
        minus 
        select ds.user_id,ds.LMS_PROFILE, ds.PRODUCT_CODE from DAILY_C2S_LMS_SUMMARY ds where ds.TRANS_DATE=to_date(aiv_Date,'dd/mm/yy');
    BEGIN
      p_count:=0;
      p_trans_date:=to_date(aiv_Date,'dd/mm/yy');    
      p_txn_amount:=0;
      p_txn_count:=0;
      p_accumulatepoint:=0;      
      FOR user_records IN insert_cur
             LOOP
                     p_user_id:=user_records.USER_ID;
                    p_product_code:=user_records.product_code;
                    p_lms_profile:=user_records.LMS_PROFILE;
                BEGIN
                    p_count:=p_count+1;
                    insert into DAILY_C2S_LMS_SUMMARY(trans_date,user_id,product_code,lms_profile,TRANSACTION_AMOUNT,TRANSACTION_COUNT,ACCUMULATED_POINTS) VALUES  (p_trans_date,p_user_id,p_product_code,p_lms_profile,p_txn_amount,p_txn_count,p_accumulatepoint);
                    EXCEPTION
                        WHEN OTHERS       THEN
                                  DBMS_OUTPUT.PUT_LINE ('Exception in insert_dly_no_c2s_lms_smry Update SQL, User:' || p_user_id ||' DATE:'||p_trans_date||' Profile:'||p_lms_profile|| SQLERRM );
                                  rtn_messageforlog := 'Exception in insert_dly_no_c2s_lms_smry Update SQL, User:' || p_user_id||' DATE:'||p_trans_date||' Profile:'||p_lms_profile;
                                  rtn_sqlerrmsgforlog := SQLERRM;
                                  RAISE sqlexception;
                 END;
             END LOOP;
                 rtn_message:='SUCCESS';
                rtn_messageForLog :='PreTUPS insert_dly_no_c2s_lms_smry successfully executed, Excuted Date Time:'||SYSDATE||' For date:'||p_trans_date||' Number updates:'||p_count;
                rtn_sqlerrMsgForLog :=' ';
        EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_messageForLog :='insert_dly_no_c2s_lms_smry sqlException Caught='||SQLERRM;
              rtn_message :='FAILED';
              RAISE sqlexception;
         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in insert_dly_no_c2s_lms_smry procedure:='||SQLERRM);
              rtn_messageForLog :='OTHERS ERROR in insert_dly_no_c2s_lms_smry procedure'||SQLERRM;
              rtn_message :='FAILED';
              RAISE sqlexception;
    END;
/


DROP PROCEDURE INSERT_CAT_ROLES;

CREATE OR REPLACE PROCEDURE INSERT_CAT_ROLES
(P_RETURNMESSAGE OUT VARCHAR2)
IS

V_CATEGORY_CODE               CATEGORY_ROLES.CATEGORY_CODE%TYPE;
V_RETURNMESSAGE               VARCHAR2(255);
INSERTEXCEPTION EXCEPTION;


CURSOR CATEGORY_CUR IS
                              select  CATEGORY_CODE from CATEGORY_ROLES where CATEGORY_CODE not in ('SUADM','BTNADM','CHADM','SSADM');

BEGIN
                OPEN  CATEGORY_CUR;
                                LOOP
                                FETCH CATEGORY_CUR INTO V_CATEGORY_CODE ;
                                EXIT WHEN CATEGORY_CUR%NOTFOUND;
                                                BEGIN

                                                                INSERT INTO CATEGORY_ROLES (CATEGORY_CODE,ROLE_CODE)
                                                                VALUES (V_CATEGORY_CODE,'BC2CINITIATE');
                                 INSERT INTO CATEGORY_ROLES (CATEGORY_CODE,ROLE_CODE)
                                                                VALUES (V_CATEGORY_CODE,'BC2CAPPROVE');
                                                EXCEPTION
                                                                WHEN OTHERS THEN
                                                                DBMS_OUTPUT.PUT_LINE('EXCEPTION IN INSERTING NEW RECORD FOR '||V_CATEGORY_CODE);
                                                                V_RETURNMESSAGE:='EXCEPTION IN INSERTING NEW RECORD FOR '||V_CATEGORY_CODE;
                                                               -- RAISE INSERTEXCEPTION;
                                                END;
                      END LOOP;
    COMMIT;
                      P_RETURNMESSAGE:=V_RETURNMESSAGE;
END ;
/


DROP PROCEDURE IATDWHTEMPPRC;

CREATE OR REPLACE PROCEDURE IATDWHTEMPPRC
(
        P_DATE                 IN DATE,
    P_IATTRANSCNT          OUT    NUMBER,
        P_MESSAGE              OUT VARCHAR2
)
IS

SQLEXCEPTION EXCEPTION;
EXITEXCEPTION EXCEPTION;

BEGIN
        DBMS_OUTPUT.PUT_LINE('START IAT DWH PROC');

        EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_IAT_DWH_IATTRANS';


    INSERT INTO TEMP_IAT_DWH_IATTRANS ( SRNO, DATA,TRANSFER_STATUS)
    SELECT ROWNUM,DATA,TRANSFER_STATUS FROM(SELECT (CT.transfer_id||','
    ||ITI.iat_txn_id||','||CT.request_gateway_type||','
    ||TO_CHAR(CT.transfer_date,'dd/mm/yyyy')||','
    ||TO_CHAR(CT.transfer_date_time,'dd/mm/yyyy hh12:mi:ss PM')||','
    ||CT.network_code||','||ITI.rec_nw_code||','||ITI.rec_country_code||','
    ||CT.SERVICE_TYPE||','||'C2S'||','||CT.sender_id||','||CT.sender_msisdn
    ||','||CT.receiver_msisdn||','||ITI.notify_msisdn||','
    ||CT.sender_category||','||CT.sender_transfer_value||','
    ||CT.receiver_transfer_value||','||CT.transfer_value||','||CT.quantity
    ||','||CT.differential_applicable||','||CT.differential_given||','||','
    ||CT.product_code||','||CT.credit_back_status||','||CT.transfer_status
    ||','||CT.card_group_code||','||ITI.prov_ratio||','||ITI.exchange_rate
    ||','||REPLACE(KV.VALUE,',',' ') ) DATA,CT.TRANSFER_STATUS TRANSFER_STATUS
    FROM C2S_TRANSFERS CT,  KEY_VALUES KV,
    C2S_IAT_TRANSFER_ITEMS ITI WHERE CT.transfer_date=P_DATE
    AND KV.KEY(+)=CT.error_code
    AND KV.TYPE(+)='C2S_ERR_CD'
    AND ITI.transfer_id=CT.transfer_id
    AND CT.ext_credit_intfce_type='IAT'
    ORDER BY CT.transfer_date_time);
    COMMIT;

    SELECT MAX(SRNO) INTO P_IATTRANSCNT FROM TEMP_IAT_DWH_IATTRANS;


    DBMS_OUTPUT.PUT_LINE('IAT DWH PROC COMPLETED');
    P_MESSAGE:='SUCCESS';

    EXCEPTION
            WHEN SQLEXCEPTION THEN
        P_MESSAGE:='NOT ABLE TO MIGRATE DATA, SQL EXCEPTION OCCOURED';
        RAISE EXITEXCEPTION;
            WHEN OTHERS THEN
                P_MESSAGE:='NOT ABLE TO MIGRATE DATA, EXCEPTION OCCOURED';
                RAISE  EXITEXCEPTION;

END;
/


DROP PROCEDURE GETACCOUNTINFORMATION;

CREATE OR REPLACE PROCEDURE getAccountInformation
    (p_msisdn varchar2,
    p_transactionNumber IN varchar2,
    p_status OUT varchar2,
    p_transactionNumberOut OUT varchar2,
    p_serviceClass OUT varchar2,
    p_accountId OUT varchar2,
    p_accountStatus OUT varchar2,
    p_creditLimit OUT varchar2,
    p_languageId OUT varchar2,
    p_Imsi OUT varchar2,
    p_balance OUT varchar2
    )IS
--    oldtime varchar2(5);
--    newtime varchar2(5);
    BEGIN
--    select ((to_char(sysdate,'mi')*60)+ to_char(sysdate,'ss')) into OldTime from dual;
    p_status :='0';
    p_transactionNumberOut:=p_transactionNumber;
    p_serviceClass :='123';
    p_accountId :='AA12345';
    p_accountStatus:= 'ACTIVE';
    p_creditLimit:='10000';
    p_languageId :='1';
    p_Imsi :='123456789123456';
    p_balance :='500000';
 --dbms_lock.sleep(60);
 --dbms_output.put_line('aa');
--loop
--  select ((to_char(sysdate,'mi')*60)+ to_char(sysdate,'ss')) into NewTime from dual;
--   if (newtime-oldtime>60) then
--         dbms_output.put_line('aaaa');
--   end if;
--end loop;
    END;
/


DROP PROCEDURE EXPDATAPURGING1;

CREATE OR REPLACE PROCEDURE EXPDATAPURGING1
(
 P_TXNFLAG IN VARCHAR2,
 P_TXNHISTORY IN NUMBER,
 P_CONFFLAG IN VARCHAR2,
 P_CONFHISTORY IN NUMBER,
 P_USERS IN VARCHAR2,
 P_USERHISTORY IN NUMBER,
 P_MISPURGFLAG IN VARCHAR2,
 P_MISPURGHISTORY IN NUMBER,
 P_finalResult OUT VARCHAR2,
 P_totTblPrgd1 OUT VARCHAR2
)
IS
QuerySet STRING_ARRAY;
P_RESULT VARCHAR2(1);
p_counter NUMBER;
--P_totTblPrgd1 VARCHAR2(10);
P_totTblPrgd NUMBER;
i NUMBER;
BEGIN

    P_RESULT:=null;
    P_totTblPrgd:=0;

    IF(P_TXNFLAG='Y') THEN

        QuerySet := STRING_ARRAY
                (
                  'ADJUSTMENTS: WHERE ADJUSTMENT_DATE<sysdate-? ',
                  'C2S_TRANSFERS : WHERE TRANSFER_DATE<sysdate-? OR RECONCILIATION_DATE<sysdate-? ',
                  'C2S_TRANSFER_ITEMS : WHERE TRANSFER_DATE<sysdate-? OR ENTRY_DATE<sysdate-? ',
                  'CHANNEL_TRANSFERS : WHERE TRANSFER_DATE<sysdate-? OR CANCELLED_ON<sysdate-?',
                  'CHANNEL_TRANSFERS_ITEMS : WHERE TRANSFER_DATE<sysdate-?',
                  'SUBSCRIBER_TRANSFERS : WHERE TRANSFER_DATE<sysdate-? OR RECONCILIATION_DATE<sysdate-?',
                  'TRANSFER_ITEMS : WHERE TRANSFER_DATE<sysdate-? OR ENTRY_DATE<sysdate-?',
                  'USER_DAILY_BALANCES : WHERE BALANCE_DATE<sysdate-?',
                  'USER_BALANCES_HISTORY : WHERE ENTRY_DATE<sysdate-? OR DAILY_BALANCE_UPDATED_ON<sysdate-? OR LAST_TRANSFER_ON<sysdate-?'
                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_TXNHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_TXNHISTORY));
             END IF;
            dbms_output.put_line(QuerySet(i));
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for transaction tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;


    IF(P_CONFFLAG='Y') THEN

        QuerySet := STRING_ARRAY
                (
                  'commission_profile_details : WHERE comm_profile_products_id in (SELECT comm_profile_products_id FROM commission_profile_products cpd1, commission_profile_set cps WHERE cps.comm_profile_set_id =cpd1.comm_profile_set_id AND cps.status=''N'' AND cps.MODIFIED_ON<sysdate-? ) ',
                    'commission_profile_details : WHERE comm_profile_products_id in (SELECT cpp.comm_profile_products_id FROM commission_profile_products cpp WHERE cpp.comm_profile_set_version NOT IN (SELECT max(to_number(cpsv1.comm_profile_set_version)) FROM commission_profile_set_version cpsv1 WHERE cpsv1.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv1.applicable_from<sysdate GROUP BY cpsv1.comm_profile_set_id UNION SELECT to_number(cpsv2.comm_profile_set_version) FROM commission_profile_set_version cpsv2 WHERE cpsv2.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv2.applicable_from>sysdate UNION SELECT to_number(cpsv3.comm_profile_set_version) FROM commission_profile_set_version cpsv3 WHERE cpsv3.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv3.created_on>sysdate-? ))',
                    'commission_profile_products : WHERE comm_profile_set_id in (SELECT cps.comm_profile_set_id FROM commission_profile_set cps WHERE cps.status=''N'' AND cps.MODIFIED_ON<sysdate-? )',
                    'commission_profile_products cpp : WHERE cpp.comm_profile_set_version NOT IN (SELECT max(to_number(cpsv1.comm_profile_set_version)) FROM commission_profile_set_version cpsv1 WHERE cpsv1.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv1.applicable_from<sysdate GROUP BY cpsv1.comm_profile_set_id UNION SELECT to_number(cpsv2.comm_profile_set_version) FROM commission_profile_set_version cpsv2 WHERE cpsv2.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv2.applicable_from>sysdate UNION SELECT to_number(cpsv3.comm_profile_set_version) FROM commission_profile_set_version cpsv3 WHERE cpsv3.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv3.created_on>sysdate-? )',
                    'commission_profile_set_version : WHERE comm_profile_set_id in (SELECT cps.comm_profile_set_id FROM commission_profile_set cps WHERE cps.status=''N'' AND cps.MODIFIED_ON<sysdate-? )',
                    'commission_profile_set_version cpsv: WHERE cpsv.comm_profile_set_version NOT IN (SELECT max(to_number(cpsv1.comm_profile_set_version)) FROM commission_profile_set_version cpsv1 WHERE cpsv1.comm_profile_set_id=cpsv.comm_profile_set_id AND cpsv1.applicable_from<sysdate GROUP BY cpsv1.comm_profile_set_id UNION SELECT to_number(cpsv2.comm_profile_set_version) FROM commission_profile_set_version cpsv2 WHERE cpsv2.comm_profile_set_id=cpsv.comm_profile_set_id AND cpsv2.applicable_from>sysdate UNION SELECT to_number(cpsv3.comm_profile_set_version) FROM commission_profile_set_version cpsv3 WHERE cpsv3.comm_profile_set_id=cpsv.comm_profile_set_id AND cpsv3.created_on>sysdate-? )',
                    'commission_profile_set : WHERE status=''N'' AND MODIFIED_ON<sysdate-? ',
                    'transfer_rules  : WHERE rule_type=''P'' AND end_time<sysdate-?',
                    'card_group_details : WHERE card_group_set_id in ( SELECT cgs.card_group_set_id FROM card_group_set cgs WHERE cgs.status=''N'' AND cgs.MODIFIED_ON<sysdate-? )',
                    'card_group_details cgd : WHERE cgd.version NOT IN ( SELECT max(to_number(cgsv.VERSION)) FROM card_group_set_versions cgsv WHERE cgsv.applicable_from<sysdate AND cgsv.card_group_set_id=cgd.card_group_set_id GROUP BY cgsv.card_group_set_id UNION SELECT to_number(cgsv2.version) FROM card_group_set_versions cgsv2 WHERE cgd.card_group_set_id=cgsv2.card_group_set_id AND cgsv2.applicable_from>sysdate UNION SELECT to_number(cgsv3.version) FROM card_group_set_versions cgsv3 WHERE cgd.card_group_set_id=cgsv3.card_group_set_id AND cgsv3.created_on>sysdate-? )',
                    'card_group_set_versions : WHERE card_group_set_id in ( SELECT cgs.card_group_set_id FROM card_group_set cgs  WHERE cgs.status=''N'' AND cgs.MODIFIED_ON<sysdate-? )',
                    'card_group_set_versions cgsv : WHERE cgsv.version NOT IN (SELECT MAX(to_number(cgsv1.version)) FROM card_group_set_versions cgsv1 WHERE cgsv1.applicable_from<sysdate AND cgsv.card_group_set_id=cgsv1.card_group_set_id GROUP BY cgsv1.card_group_set_id UNION SELECT to_number(cgsv2.version) FROM card_group_set_versions cgsv2 WHERE cgsv.card_group_set_id=cgsv2.card_group_set_id AND cgsv2.applicable_from>sysdate UNION SELECT to_number(cgsv3.version) FROM card_group_set_versions cgsv3 WHERE cgsv.card_group_set_id=cgsv3.card_group_set_id AND cgsv3.created_on>sysdate-?)',
                    'card_group_set : WHERE status=''N'' AND MODIFIED_ON<sysdate-? '
                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_TXNHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_TXNHISTORY));
             END IF;
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for configuration tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;


    IF(P_USERS='Y') THEN

        QuerySet := STRING_ARRAY
                (
                    ' user_geographies : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' user_phones : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' user_roles : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' user_services : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' channel_users : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' users : where status=''N'' and modified_on < sysdate-? '
                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_TXNHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_TXNHISTORY));
             END IF;
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for users tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;

        IF(P_MISPURGFLAG='Y') THEN

        QuerySet := STRING_ARRAY
                (
                  'MONTHLY_CHNL_TRANS_DETAILS: WHERE TRANS_DATE<sysdate-? ',
                  'MONTHLY_CHNL_TRANS_MAIN : WHERE TRANS_DATE<sysdate-?  ',
                  'DAILY_CHNL_TRANS_DETAILS : WHERE TRANS_DATE<sysdate-?  ',
                  'DAILY_CHNL_TRANS_MAIN : WHERE TRANS_DATE<sysdate-? '

                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_MISPURGHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_MISPURGHISTORY));
             END IF;
            dbms_output.put_line(QuerySet(i));
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for transaction tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;
    P_totTblPrgd1 := P_totTblPrgd;
    P_finalResult:='Y';
    dbms_output.put_line(' Total No of tables purged is ' || P_totTblPrgd1);

END EXPDATAPURGING1;
/


DROP PROCEDURE EXPDATAPURGING;

CREATE OR REPLACE PROCEDURE EXPDATAPURGING
(
 P_TXNFLAG IN VARCHAR2,
 P_TXNHISTORY IN NUMBER,
 P_CONFFLAG IN VARCHAR2,
 P_CONFHISTORY IN NUMBER,
 P_USERS IN VARCHAR2,
 P_USERHISTORY IN NUMBER,
 P_MISPURGFLAG IN VARCHAR2,
 P_MISPURGHISTORY IN NUMBER,
 P_finalResult OUT VARCHAR2,
 P_totTblPrgd OUT NUMBER
)
IS
QuerySet STRING_ARRAY;
P_RESULT VARCHAR2(1);
p_counter NUMBER;
i NUMBER;
BEGIN

    P_RESULT:=null;
    P_totTblPrgd:=0;

    IF(P_TXNFLAG='Y') THEN

        QuerySet := STRING_ARRAY
                (
                  'ADJUSTMENTS: WHERE ADJUSTMENT_DATE<sysdate-? ',
                  'C2S_TRANSFERS : WHERE TRANSFER_DATE<sysdate-? OR RECONCILIATION_DATE<sysdate-? ',
                  'C2S_TRANSFER_ITEMS : WHERE TRANSFER_DATE<sysdate-? OR ENTRY_DATE<sysdate-? ',
                  'CHANNEL_TRANSFERS : WHERE TRANSFER_DATE<sysdate-? OR CANCELLED_ON<sysdate-?',
                  'CHANNEL_TRANSFERS_ITEMS : WHERE TRANSFER_DATE<sysdate-?',
                  'SUBSCRIBER_TRANSFERS : WHERE TRANSFER_DATE<sysdate-? OR RECONCILIATION_DATE<sysdate-?',
                  'TRANSFER_ITEMS : WHERE TRANSFER_DATE<sysdate-? OR ENTRY_DATE<sysdate-?',
                  'USER_DAILY_BALANCES : WHERE BALANCE_DATE<sysdate-?',
                  'USER_BALANCES_HISTORY : WHERE ENTRY_DATE<sysdate-? OR DAILY_BALANCE_UPDATED_ON<sysdate-? OR LAST_TRANSFER_ON<sysdate-?'
                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_TXNHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_TXNHISTORY));
             END IF;
            dbms_output.put_line(QuerySet(i));
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for transaction tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;


    IF(P_CONFFLAG='Y') THEN

        QuerySet := STRING_ARRAY
                (
                  'commission_profile_details : WHERE comm_profile_products_id in (SELECT comm_profile_products_id FROM commission_profile_products cpd1, commission_profile_set cps WHERE cps.comm_profile_set_id =cpd1.comm_profile_set_id AND cps.status=''N'' AND cps.MODIFIED_ON<sysdate-? ) ',
                    'commission_profile_details : WHERE comm_profile_products_id in (SELECT cpp.comm_profile_products_id FROM commission_profile_products cpp WHERE cpp.comm_profile_set_version NOT IN (SELECT max(to_number(cpsv1.comm_profile_set_version)) FROM commission_profile_set_version cpsv1 WHERE cpsv1.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv1.applicable_from<sysdate GROUP BY cpsv1.comm_profile_set_id UNION SELECT to_number(cpsv2.comm_profile_set_version) FROM commission_profile_set_version cpsv2 WHERE cpsv2.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv2.applicable_from>sysdate UNION SELECT to_number(cpsv3.comm_profile_set_version) FROM commission_profile_set_version cpsv3 WHERE cpsv3.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv3.created_on>sysdate-? ))',
                    'commission_profile_products : WHERE comm_profile_set_id in (SELECT cps.comm_profile_set_id FROM commission_profile_set cps WHERE cps.status=''N'' AND cps.MODIFIED_ON<sysdate-? )',
                    'commission_profile_products cpp : WHERE cpp.comm_profile_set_version NOT IN (SELECT max(to_number(cpsv1.comm_profile_set_version)) FROM commission_profile_set_version cpsv1 WHERE cpsv1.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv1.applicable_from<sysdate GROUP BY cpsv1.comm_profile_set_id UNION SELECT to_number(cpsv2.comm_profile_set_version) FROM commission_profile_set_version cpsv2 WHERE cpsv2.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv2.applicable_from>sysdate UNION SELECT to_number(cpsv3.comm_profile_set_version) FROM commission_profile_set_version cpsv3 WHERE cpsv3.comm_profile_set_id=cpp.comm_profile_set_id AND cpsv3.created_on>sysdate-? )',
                    'commission_profile_set_version : WHERE comm_profile_set_id in (SELECT cps.comm_profile_set_id FROM commission_profile_set cps WHERE cps.status=''N'' AND cps.MODIFIED_ON<sysdate-? )',
                    'commission_profile_set_version cpsv: WHERE cpsv.comm_profile_set_version NOT IN (SELECT max(to_number(cpsv1.comm_profile_set_version)) FROM commission_profile_set_version cpsv1 WHERE cpsv1.comm_profile_set_id=cpsv.comm_profile_set_id AND cpsv1.applicable_from<sysdate GROUP BY cpsv1.comm_profile_set_id UNION SELECT to_number(cpsv2.comm_profile_set_version) FROM commission_profile_set_version cpsv2 WHERE cpsv2.comm_profile_set_id=cpsv.comm_profile_set_id AND cpsv2.applicable_from>sysdate UNION SELECT to_number(cpsv3.comm_profile_set_version) FROM commission_profile_set_version cpsv3 WHERE cpsv3.comm_profile_set_id=cpsv.comm_profile_set_id AND cpsv3.created_on>sysdate-? )',
                    'commission_profile_set : WHERE status=''N'' AND MODIFIED_ON<sysdate-? ',
                    'transfer_rules  : WHERE rule_type=''P'' AND end_time<sysdate-?',
                    'card_group_details : WHERE card_group_set_id in ( SELECT cgs.card_group_set_id FROM card_group_set cgs WHERE cgs.status=''N'' AND cgs.MODIFIED_ON<sysdate-? )',
                    'card_group_details cgd : WHERE cgd.version NOT IN ( SELECT max(to_number(cgsv.VERSION)) FROM card_group_set_versions cgsv WHERE cgsv.applicable_from<sysdate AND cgsv.card_group_set_id=cgd.card_group_set_id GROUP BY cgsv.card_group_set_id UNION SELECT to_number(cgsv2.version) FROM card_group_set_versions cgsv2 WHERE cgd.card_group_set_id=cgsv2.card_group_set_id AND cgsv2.applicable_from>sysdate UNION SELECT to_number(cgsv3.version) FROM card_group_set_versions cgsv3 WHERE cgd.card_group_set_id=cgsv3.card_group_set_id AND cgsv3.created_on>sysdate-? )',
                    'card_group_set_versions : WHERE card_group_set_id in ( SELECT cgs.card_group_set_id FROM card_group_set cgs  WHERE cgs.status=''N'' AND cgs.MODIFIED_ON<sysdate-? )',
                    'card_group_set_versions cgsv : WHERE cgsv.version NOT IN (SELECT MAX(to_number(cgsv1.version)) FROM card_group_set_versions cgsv1 WHERE cgsv1.applicable_from<sysdate AND cgsv.card_group_set_id=cgsv1.card_group_set_id GROUP BY cgsv1.card_group_set_id UNION SELECT to_number(cgsv2.version) FROM card_group_set_versions cgsv2 WHERE cgsv.card_group_set_id=cgsv2.card_group_set_id AND cgsv2.applicable_from>sysdate UNION SELECT to_number(cgsv3.version) FROM card_group_set_versions cgsv3 WHERE cgsv.card_group_set_id=cgsv3.card_group_set_id AND cgsv3.created_on>sysdate-?)',
                    'card_group_set : WHERE status=''N'' AND MODIFIED_ON<sysdate-? '
                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_TXNHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_TXNHISTORY));
             END IF;
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for configuration tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;


    IF(P_USERS='Y') THEN

        QuerySet := STRING_ARRAY
                (
                    ' user_geographies : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' user_phones : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' user_roles : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' user_services : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' channel_users : where user_id in (select user_id from users where status=''N'' and modified_on < sysdate-?) ',
                    ' users : where status=''N'' and modified_on < sysdate-? '
                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_TXNHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_TXNHISTORY));
             END IF;
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for users tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;

        IF(P_MISPURGFLAG='Y') THEN

        QuerySet := STRING_ARRAY
                (
                  'MONTHLY_CHNL_TRANS_DETAILS: WHERE TRANS_DATE<sysdate-? ',
                  'MONTHLY_CHNL_TRANS_MAIN : WHERE TRANS_DATE<sysdate-?  ',
                  'DAILY_CHNL_TRANS_DETAILS : WHERE TRANS_DATE<sysdate-?  ',
                  'DAILY_CHNL_TRANS_MAIN : WHERE TRANS_DATE<sysdate-? '

                );

        FOR i in 1..QuerySet.count
        LOOP
            IF(P_MISPURGHISTORY=null) THEN
                QuerySet(i):=REPLACE(QuerySet(i),'?','90');
            ELSE
                QuerySet(i):=REPLACE(QuerySet(i),'?',to_char(P_MISPURGHISTORY));
             END IF;
            dbms_output.put_line(QuerySet(i));
        END LOOP;

        PURGEPROC(QuerySet,p_result,p_counter);

        IF(p_result='Y') THEN
            dbms_output.put_line('Procedure executed successfully for transaction tables.');
            dbms_output.put_line('No of tables purged is ' || p_counter);
        END IF;

        P_totTblPrgd:=P_totTblPrgd+p_counter;

    END IF;

    P_finalResult:='Y';
    dbms_output.put_line(' Total No of tables purged is ' || P_totTblPrgd);

END EXPDATAPURGING;
/


DROP PROCEDURE EMP_CSV;

CREATE OR REPLACE PROCEDURE EMP_CSV AS
  CURSOR c_data IS
    SELECT transfer_id,
           transfer_status
              FROM   c2s_transfers where rownum < 100000;

  v_file  UTL_FILE.FILE_TYPE;
BEGIN
  v_file := UTL_FILE.FOPEN(location     => 'TEMP_DIR',
                           filename     => 'emp_csv.csv',
                           open_mode    => 'w',
                           max_linesize => 32767);   --max_linesize => 32767
  FOR cur_rec IN c_data LOOP
    UTL_FILE.PUT_LINE(v_file,
                      cur_rec.transfer_id    || ',' ||
                      cur_rec.transfer_status);
  END LOOP;
  UTL_FILE.FCLOSE(v_file);

EXCEPTION
  WHEN OTHERS THEN
    UTL_FILE.FCLOSE(v_file);
    RAISE;
END;
/


DROP PROCEDURE C2S_TRANSFERS_DETAILS;

CREATE OR REPLACE PROCEDURE C2S_TRANSFERS_DETAILS(
aiv_date             IN  VARCHAR2
)
IS
   the_date               DATE;
   v_message              VARCHAR2 (500);
   v_messageforlog        VARCHAR2 (500);
   v_sqlerrmsgforlog      VARCHAR2 (500);
   sql_stmt               VARCHAR2 (2000);
   sql_stmt1               VARCHAR2 (2000);
   user_type            C2S_TRANSFER_ITEMS.USER_TYPE%TYPE;
   entry_type           C2S_TRANSFER_ITEMS.ENTRY_TYPE%TYPE; 
   sno_t                C2S_TRANSFER_ITEMS.SNO%TYPE;
   validation_status    C2S_TRANSFER_ITEMS.VALIDATION_STATUS%TYPE;
   sender_status        C2S_TRANSFER_ITEMS.TRANSFER_STATUS%TYPE;
   reciever_status      C2S_TRANSFER_ITEMS.TRANSFER_STATUS%TYPE;
   
   empty_e                VARCHAR2 (2);
SQLEXCEPTION EXCEPTION;
PROCEXCEPTION EXCEPTION;
CURSOR c2s_transfer_cursor(in_date DATE) 
   IS
      SELECT  * FROM C2S_TRANSFERS where TRANSFER_DATE=in_date;
   BEGIN
      the_date:=TO_DATE(aiv_date,'dd/mm/yy');
      sql_stmt := '';
      sql_stmt1 := '';
      empty_e := '';
      FOR c2s IN c2s_transfer_cursor(the_date)
      LOOP
           validation_status := c2s.VALIDATION_STATUS;
           sender_status := c2s.DEBIT_STATUS;
           reciever_status := c2s.CREDIT_STATUS;
         BEGIN
            INSERT INTO C2S_TRANSFERS_OLD VALUES
            (c2s.TRANSFER_ID,c2s.TRANSFER_DATE,c2s.TRANSFER_DATE_TIME,c2s.NETWORK_CODE,c2s.SENDER_ID
            ,c2s.SENDER_CATEGORY,c2s.PRODUCT_CODE,c2s.SENDER_MSISDN,c2s.RECEIVER_MSISDN,c2s.RECEIVER_NETWORK_CODE
            ,c2s.TRANSFER_VALUE,c2s.ERROR_CODE,c2s.REQUEST_GATEWAY_TYPE,c2s.REQUEST_GATEWAY_CODE,c2s.REFERENCE_ID
            ,c2s.SERVICE_TYPE,c2s.DIFFERENTIAL_APPLICABLE,c2s.PIN_SENT_TO_MSISDN,c2s.LANGUAGE,c2s.COUNTRY
            ,c2s.SKEY,c2s.SKEY_GENERATION_TIME,c2s.SKEY_SENT_TO_MSISDN,c2s.REQUEST_THROUGH_QUEUE,c2s.CREDIT_BACK_STATUS
            ,c2s.QUANTITY,c2s.RECONCILIATION_FLAG,c2s.RECONCILIATION_DATE,c2s.RECONCILIATION_BY,c2s.CREATED_ON
            ,c2s.CREATED_BY,c2s.MODIFIED_ON,c2s.MODIFIED_BY,c2s.TRANSFER_STATUS,c2s.CARD_GROUP_SET_ID
            ,c2s.VERSION,c2s.CARD_GROUP_ID,c2s.SENDER_TRANSFER_VALUE,c2s.RECEIVER_ACCESS_FEE,c2s.RECEIVER_TAX1_TYPE
            ,c2s.RECEIVER_TAX1_RATE,c2s.RECEIVER_TAX1_VALUE,c2s.RECEIVER_TAX2_TYPE,c2s.RECEIVER_TAX2_RATE,c2s.RECEIVER_TAX2_VALUE
            ,c2s.RECEIVER_VALIDITY,c2s.RECEIVER_TRANSFER_VALUE,c2s.RECEIVER_BONUS_VALUE,c2s.RECEIVER_GRACE_PERIOD,c2s.RECEIVER_BONUS_VALIDITY
            ,c2s.CARD_GROUP_CODE,c2s.RECEIVER_VALPERIOD_TYPE,c2s.TEMP_TRANSFER_ID,c2s.TRANSFER_PROFILE_ID,c2s.COMMISSION_PROFILE_ID
            ,c2s.DIFFERENTIAL_GIVEN,c2s.GRPH_DOMAIN_CODE,c2s.SOURCE_TYPE,c2s.SUB_SERVICE,c2s.START_TIME
            ,c2s.END_TIME,c2s.SERIAL_NUMBER,c2s.EXT_CREDIT_INTFCE_TYPE,c2s.BONUS_DETAILS,c2s.ACTIVE_USER_ID
            ,c2s.SUBS_SID);
            
             user_type := 'SENDER';
             entry_type := 'DR';
             sno_t := 1;
             IF(validation_status <> '200')
             THEN
                validation_status := '212';
             END IF;   
                
             IF(sender_status <> '200')
             THEN
                sender_status := '212';
             END IF;
             
             IF(reciever_status IS NULL)
             THEN
                reciever_status := '206';
             END IF;
             
            sql_stmt := 'INSERT INTO C2S_TRANSFER_ITEMS ';
            sql_stmt := sql_stmt || ' VALUES (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12,:13,:14,:15,:16,:17,:18,:19,:20,';
            sql_stmt := sql_stmt || ':21,:22,:23,:24,:25,:26,:27,:28,:29,:30,:31,:32,:33,:34,:35,:36,:37,:38,:39,:40)';
         
            EXECUTE IMMEDIATE sql_stmt USING
             c2s.TRANSFER_ID,c2s.SENDER_MSISDN,c2s.TRANSFER_DATE,c2s.TRANSFER_VALUE,c2s.SENDER_PREVIOUS_BALANCE,
             c2s.SENDER_POST_BALANCE,user_type,c2s.TRANSFER_TYPE,entry_type,validation_status,
             c2s.DEBIT_STATUS,c2s.TRANSFER_VALUE,empty_e,empty_e,empty_e,
             empty_e,empty_e,empty_e,empty_e,empty_e,
             sender_status,c2s.TRANSFER_DATE,c2s.TRANSFER_DATE_TIME,c2s.TRANSFER_DATE_TIME,empty_e,
             sno_t,c2s.SENDER_PREFIX_ID,empty_e,empty_e,empty_e,
             c2s.ADJUST_DR_TXN_TYPE,c2s.ADJUST_DR_TXN_ID,c2s.ADJUST_DR_UPDATE_STATUS,empty_e,empty_e,
             empty_e,c2s.ADJUST_VALUE,empty_e,c2s.COUNTRY,c2s.LANGUAGE;
            
            user_type := 'RECIEVER';
            entry_type := 'CR';
            sno_t := 2;
            
            sql_stmt1 := 'INSERT INTO C2S_TRANSFER_ITEMS ';
            sql_stmt1 := sql_stmt1 || ' VALUES (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12,:13,:14,:15,:16,:17,:18,:19,:20,';
            sql_stmt1 := sql_stmt1 || ':21,:22,:23,:24,:25,:26,:27,:28,:29,:30,:31,:32,:33,:34,:35,:36,:37,:38,:39,:40)';

            EXECUTE IMMEDIATE sql_stmt1 USING
             c2s.TRANSFER_ID,c2s.RECEIVER_MSISDN,c2s.TRANSFER_DATE,c2s.TRANSFER_VALUE,c2s.RECEIVER_PREVIOUS_BALANCE,
             c2s.RECEIVER_POST_BALANCE,user_type,c2s.TRANSFER_TYPE,entry_type,c2s.VALIDATION_STATUS,
             c2s.CREDIT_STATUS,c2s.RECEIVER_TRANSFER_VALUE,c2s.INTERFACE_TYPE,c2s.INTERFACE_ID,c2s.INTERFACE_RESPONSE_CODE,
             c2s.INTERFACE_REFERENCE_ID,c2s.SUBSCRIBER_TYPE,c2s.SERVICE_CLASS_CODE,c2s.MSISDN_PREVIOUS_EXPIRY,c2s.MSISDN_NEW_EXPIRY,
             reciever_status,c2s.TRANSFER_DATE,c2s.TRANSFER_DATE_TIME,c2s.TRANSFER_DATE_TIME,c2s.FIRST_CALL,
             sno_t,c2s.PREFIX_ID,c2s.SERVICE_CLASS_ID,c2s.PROTOCOL_STATUS,c2s.ACCOUNT_STATUS,
             empty_e,empty_e,empty_e,c2s.ADJUST_CR_TXN_TYPE,c2s.ADJUST_CR_TXN_ID,
             c2s.ADJUST_CR_UPDATE_STATUS,c2s.ADJUST_VALUE,c2s.RCVR_INTRFC_REFERENCE_ID,c2s.COUNTRY,c2s.LANGUAGE;
                        
            EXCEPTION
                        WHEN sqlexception
                        THEN
                            DBMS_OUTPUT.PUT_LINE(   'sqlexception in C2S_TRANSFERS_DETAILS 1'|| SQLERRM );
                            v_messageforlog :='sqlexception in C2S_TRANSFERS_DETAILS 1' ;
                            v_sqlerrmsgforlog := SQLERRM;
                        RAISE procexception;
                        WHEN OTHERS
                        THEN
                            DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in C2S_TRANSFERS_DETAILS 1'|| SQLERRM);
                            v_messageforlog := 'OTHERS EXCEPTION in C2S_TRANSFERS_DETAILS 1';
                            v_sqlerrmsgforlog := SQLERRM;
                            RAISE procexception;
                    END;
         END LOOP;  
         
         EXCEPTION
        WHEN procexception
        THEN
        DBMS_OUTPUT.PUT_LINE ('procexception in C2S_TRANSFERS_DETAILS 2:' || SQLERRM);
        v_message:='FAILED';
        WHEN OTHERS
        THEN
        DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in C2S_TRANSFERS_DETAILS 2:' || SQLERRM);
        v_message:='FAILED';
        COMMIT;
END C2S_TRANSFERS_DETAILS;
/


DROP PROCEDURE C2STXNMINUTSWISE;

CREATE OR REPLACE PROCEDURE c2sTXNMinutsWise
(
           P_DATE  IN DATE
)
IS
-- p_date should be dd-mon-yy format
   v_cnt varchar2(10);
   v_temp varchar2(25);
   v_h varchar2 (2);
   v_m varchar2 (2);

BEGIN

    EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_C2S_HROURLY_COUNT';

    INSERT INTO TEMP_C2S_HROURLY_COUNT (DATE_MINUTE, TXT_COUNT) SELECT to_char(transfer_date_time,'DD-MON-YY HH24:MI') DATE_MINUTE, count(1) TXT_COUNT from c2s_transfers where transfer_date=P_DATE group by to_char(transfer_date_time,'DD-MON-YY HH24:MI');

    COMMIT;

    for i in 0..23 loop

              if i<10 then
               v_h:='0'||i;
              else
                   v_h:=i;
              end if;

              for j in 0..59 loop

               if j<10 then
                    v_m:='0'||j;
               else
                   v_m:=j;
               end if;

                    v_temp:='';

                    v_temp:=P_DATE||' '||v_h||':'||v_m;

                    --DBMS_OUTPUT.PUT_LINE(v_temp);

                    BEGIN

                    SELECT TXT_COUNT into v_cnt from  TEMP_C2S_HROURLY_COUNT where to_date(DATE_MINUTE,'DD-MON-YY HH24:MI')=to_date(v_temp,'DD-MON-YY HH24:MI');

                    EXCEPTION
                             WHEN NO_DATA_FOUND
                             THEN
                             v_cnt := 0;
                    END;
                   -- EXECUTE IMMEDIATE 'SELECT TXT_COUNT from  TEMP_C2S_HROURLY_COUNT where to_char(DATE_MINUTE,''DD-MON-YY HH24:MI'')=:1' into v_cnt USING to_char(v_temp,'DD-MON-YY HH24:MI');

                    DBMS_OUTPUT.PUT_LINE(v_h||','||v_m||'='||v_cnt);


              end loop;-- Minute loop

        end loop; --Hours Loop

END;
/


DROP PROCEDURE ADJUSTMENT;

CREATE OR REPLACE PROCEDURE adjustment
    (p_msisdn IN varchar2,
    p_transactionNumber IN OUT varchar2,
    p_accountId IN varchar2,
    p_amount IN VARCHAR2,
    p_currency IN VARCHAR2,
    p_imsi IN VARCHAR2,
    p_serviceType IN VARCHAR2,
    p_status OUT varchar2,
    p_balance OUT varchar2
    )IS
    BEGIN
    p_status :='0';
    p_balance :='500000';
    END;
/


