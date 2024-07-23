CREATE OR REPLACE PROCEDURE PRETUPS650_TEST."NETWORK_DAILY_CLOSING_STOCK" (
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
p_wallet_created NETWORK_STOCKS.WALLET_CREATED%TYPE;
p_wallet_returned NETWORK_STOCKS.WALLET_RETURNED%TYPE;
p_wallet NETWORK_STOCKS.wallet_balance%TYPE;
p_wallet_sold NETWORK_STOCKS.wallet_sold%TYPE;
p_last_txn_no NETWORK_STOCKS.last_txn_no%TYPE;
p_last_txn_type NETWORK_STOCKS.last_txn_type%TYPE;
p_last_txn_stock NETWORK_STOCKS.last_txn_balance%TYPE;
p_previous_stock NETWORK_STOCKS.previous_balance%TYPE;
p_wallet_type NETWORK_STOCKS.wallet_type%TYPE;
p_daily_stock_updated_on DATE;


q_created_on DATE;
dayDifference NUMBER (5):= 0;
startCount NUMBER(3);
dateCounter DATE;




sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 CURSOR network_stock_list_cur IS
        SELECT network_code,network_code_for,product_code,wallet_created,
        wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,last_txn_balance,previous_balance,
        wallet_type,daily_stock_updated_on
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
                    p_wallet_created:=network_stock_records.wallet_created;
                    p_wallet_returned:=network_stock_records.wallet_returned;
                    p_wallet:=network_stock_records.wallet_balance;
                    p_wallet_sold:=network_stock_records.wallet_sold;
                    p_last_txn_no:=network_stock_records.last_txn_no;
                    p_last_txn_type:=network_stock_records.last_txn_type;
                    p_last_txn_stock:=network_stock_records.last_txn_balance;
                    p_previous_stock:=network_stock_records.previous_balance;
                    p_wallet_type:=network_stock_records.wallet_type;
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
                        (wallet_date,product_code,network_code,network_code_for,wallet_created,wallet_returned,wallet_balance,
                        wallet_sold,last_txn_no,last_txn_type,last_txn_balance,previous_balance,wallet_type,created_on)
                      VALUES(dateCounter,p_product_code,p_network_code,p_network_code_for,p_wallet_created,
                      p_wallet_returned,p_wallet,p_wallet_sold,p_last_txn_no,p_last_txn_type,p_last_txn_stock,
                      p_previous_stock,p_wallet_type,q_created_on);
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
