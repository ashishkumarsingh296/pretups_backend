CREATE OR REPLACE PACKAGE BODY PRETUPS650_TEST."PKG_BALANCE_MISMATCH" 
 AS
    PROCEDURE sp_chnl_users_balance_mismatch (
       v_errorcode   OUT   VARCHAR2,
       v_message     OUT   VARCHAR2
    )
    IS
 /*  The logic of calculation is add all the IN channel transactions of the channel user and
    subtract all OUT channel transactions, C2S recharges and available balance.
     The values of errror code will be as follows
 */
 
       --cursor for getting balance of all the channel users
       CURSOR cursor1
       IS
          SELECT user_id, product_code, balance
            FROM user_balances;
 
       mismatchstring   VARCHAR2 (200);
       userid           user_balances.user_id%TYPE;
       productcode      user_balances.product_code%TYPE;
       tmpbalance       user_balances.balance%TYPE;
       currentbalance   user_balances.balance%TYPE;
    BEGIN
       SET TRANSACTION READ ONLY;
       v_errorcode := '3563';
       v_message :=
             'Balance mismatch process executed for user balance. Exception while executing the 
   process.'
          || mismatchstring;
       mismatchstring := '';
 
       FOR t_r IN cursor1
       LOOP
          BEGIN
             userid := t_r.user_id;
             productcode := t_r.product_code;
             currentbalance := -t_r.balance;
             --(Total IN transactions-Total OUT transactions-balance) should be zero
             DBMS_OUTPUT.put_line (   'Initial userid ='
                                   || userid
                                   || ' currentbalance='
                                   || currentbalance
                                  );
 
             BEGIN
                --getting O2C and C2C IN transactions of a particular channel user and a particular product
                SELECT NVL (SUM (cti.approved_quantity), 0)
                  INTO tmpbalance
                  FROM channel_transfers ct, channel_transfers_items cti
                 WHERE ct.status = 'CLOSE'
                   AND ct.transfer_id = cti.transfer_id
                   AND ct.to_user_id = userid
                   AND cti.product_code = productcode;
 
                -- DBMS_OUTPUT.put_line ('C2C IN  tmpbalance='||tmpbalance);
                currentbalance := currentbalance + tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3560';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting IN channel transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                   RAISE mainexception;
             END;
 
             BEGIN
                --getting O2C and C2C OUT transaction of a channel user and a particular product
                SELECT NVL (SUM (cti.approved_quantity), 0)
                  INTO tmpbalance
                  FROM channel_transfers ct, channel_transfers_items cti
                 WHERE ct.status = 'CLOSE'
                   AND ct.transfer_id = cti.transfer_id
                   AND ct.from_user_id = userid
                   AND cti.product_code = productcode;
 
                -- DBMS_OUTPUT.put_line ('C2C OUT tmpbalance='||tmpbalance);
                currentbalance := currentbalance - tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3561';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting OUT channel transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                   RAISE mainexception;
             END;
 
             BEGIN
                --getting total C2S OUT transactions of a channel user and a particular product
                SELECT NVL
                          (SUM
                              (CASE
                                  WHEN (cs.transfer_status = '250')
                                     THEN (CASE
                                              WHEN (credit_back_status = '200'
                                                   )
                                                 THEN 0
                                              ELSE (cs.quantity)
                                           END
                                          )
                                  ELSE cs.quantity
                               END
                              ),
                           0
                          )
                  INTO tmpbalance
                  FROM c2s_transfers cs
                 WHERE cs.transfer_status <> '206'
                   AND cs.sender_id = userid
                   AND cs.product_code = productcode;
 
                -- DBMS_OUTPUT.put_line ('C2S tmpbalance='||tmpbalance);
 
                /*    SELECT NVL(SUM(transfer_value),0) into tmpbalance
                   FROM c2s_transfers
                   WHERE (transfer_status='200' or transfer_status='205')
                   AND sender_id =userid
                   AND product_code=productcode;
                */
                currentbalance := currentbalance - tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3562';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting recharge transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                   RAISE mainexception;
             END;
 
             BEGIN
                --getting differential transactions of a particular channel user and a particular product
                SELECT NVL (SUM ((CASE
                                     WHEN (stock_updated = 'Y')
                                        THEN d.transfer_value
                                     ELSE 0
                                  END
                                 )
                                ),
                            0
                           )
                  INTO tmpbalance
                  FROM adjustments d
                 WHERE d.entry_type = 'CR'
                   AND d.user_id = userid
                   AND d.product_code = productcode;
 
                -- DBMS_OUTPUT.PUT_LINE('diffAmt='||tmpbalance);
                currentbalance := currentbalance + tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3574';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting Differential transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                   RAISE mainexception;
             END;
 
             BEGIN
                --getting differential transactions of a particular channel user and a particular product
                SELECT NVL (SUM ((CASE
                                     WHEN (stock_updated = 'Y')
                                        THEN d.transfer_value
                                     ELSE 0
                                  END
                                 )
                                ),
                            0
                           )
                  INTO tmpbalance
                  FROM adjustments d
                 WHERE d.entry_type = 'DR'
                   AND d.user_id = userid
                   AND d.product_code = productcode;
 
                -- DBMS_OUTPUT.PUT_LINE('diffDrAmt='||tmpbalance);
                currentbalance := currentbalance - tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3574';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting Differential transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                   RAISE mainexception;
             END;
 
             DBMS_OUTPUT.put_line (   'Final userid='
                                   || userid
                                   || ' currentbalance='
                                   || currentbalance
                                  );
 
             IF currentbalance <> 0
             THEN
                mismatchstring :=
                      mismatchstring
                   || userid
                   || ':'
                   || productcode
                   || ':'
                   || t_r.balance
                   || ',';
             END IF;
          EXCEPTION
             WHEN mainexception
             THEN
                DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                RAISE mainexception;
             WHEN OTHERS
             THEN
                DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                RAISE mainexception;
          END;
       END LOOP;
 
       IF LENGTH (mismatchstring) > 0
       THEN
          v_errorcode := '3517';
          v_message :=
                'Balance mismatch process executed successfully for user balance. Mismatch found.'
             || mismatchstring;
       ELSE
          v_errorcode := '3516';
          v_message :=
             'Balance mismatch process executed successfully for user balance.No mismatch found.';
       END IF;
 
       ROLLBACK;
    EXCEPTION
       WHEN mainexception
       THEN
          ROLLBACK;
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
       WHEN OTHERS
       THEN
          ROLLBACK;
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
    END sp_chnl_users_balance_mismatch;
 
    PROCEDURE sp_net_stocks_balance_mismatch (
       v_errorcode   OUT      VARCHAR2,
       v_message     OUT      VARCHAR2,
       v_from_date   IN OUT   DATE
    )
    IS
 /*  The logic of calculation is add all the IN stock transactions of the network and
    subtract all OUT network transactions of the network.
 */
 
       --cursor for getting all the OUT stock transactions from the network
       CURSOR cur_current_stock
       IS
          SELECT   SUM (WALLET_BALANCE) WALLET_BALANCE, product_code, network_code,
                   network_code_for
              FROM network_stocks
          GROUP BY product_code, network_code, network_code_for;
 
       productcode      network_stocks.product_code%TYPE;
       networkcode      network_stocks.network_code%TYPE;
       networkcodefor   network_stocks.network_code_for%TYPE;
       amount           network_stocks.WALLET_BALANCE%TYPE;
       current_stock    network_stocks.WALLET_BALANCE%TYPE;
       close_stock      network_stocks.WALLET_BALANCE%TYPE;
       txn_amount       network_stocks.WALLET_BALANCE%TYPE;
       errstring        VARCHAR2 (300);
       tempdate         DATE;
       from_date        DATE;
    BEGIN
       SET TRANSACTION READ ONLY;
       errstring := '';
 
       FOR t_r IN cur_current_stock
       LOOP
          BEGIN
             from_date := v_from_date;
             current_stock := t_r.WALLET_BALANCE;
             productcode := t_r.product_code;
             networkcode := t_r.network_code;
             networkcodefor := t_r.network_code_for;
 
             BEGIN
                --getting closing WALLET_BALANCE for the network for a particular product
                SELECT SUM (NVL (WALLET_BALANCE, 0))
                  INTO close_stock
                  FROM network_daily_stocks
                 WHERE WALLET_DATE = from_date - 1
                   AND product_code = productcode
                   AND network_code = networkcode
                   AND network_code_for = networkcodefor;
             EXCEPTION
                WHEN NO_DATA_FOUND
                THEN
                   BEGIN
                      SELECT SUM (WALLET_BALANCE), WALLET_DATE
                        INTO close_stock, tempdate
                        FROM network_daily_stocks
                       WHERE WALLET_DATE =
                                (SELECT MAX (WALLET_DATE)
                                   FROM network_daily_stocks
                                  WHERE product_code = productcode
                                    AND network_code = networkcode
                                    AND network_code_for = networkcodefor)
                         AND product_code = productcode
                         AND network_code = networkcode
                         AND network_code_for = networkcodefor;
 
                      from_date := tempdate + 1;
                   EXCEPTION
                      WHEN OTHERS
                      THEN
                         v_errorcode := '3571';
                         v_message :=
                               'Balance mismatch process executed for network stock. Exception in getting Closing stock transactions for max date.'
                            || SQLERRM;
                         DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                         RAISE mainexception;
                   END;
                WHEN OTHERS
                THEN
                   v_errorcode := '3571';
                   v_message :=
                         'Balance mismatch process executed for network stock. Exception in getting Closing stock transactions.'
                      || SQLERRM;
                   DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                   RAISE mainexception;
             END;
 
             BEGIN
                --getting current stock for the network for a particular product
                SELECT (  NVL
                             (SUM
                                 (CASE
                                     WHEN (nst.entry_type = 'CREATION')
                                        THEN nsti.approved_quantity
                                  END
                                 ),
                              0
                             )
                        + NVL
                             (SUM
                                 (CASE
                                     WHEN (    nst.entry_type <> 'CREATION'
                                           AND nst.entry_type <> 'TRANSFER'
                                          )
                                        THEN nsti.approved_quantity
                                  END
                                 ),
                              0
                             )
                        - NVL
                             (SUM
                                 (CASE
                                     WHEN (nst.entry_type = 'TRANSFER')
                                        THEN nsti.approved_quantity
                                  END
                                 ),
                              0
                             )
                       )
                  INTO txn_amount
                  FROM network_stock_transactions nst,
                       network_stock_trans_items nsti
                 WHERE nst.txn_no = nsti.txn_no
                   AND nst.txn_status = 'CLOSE'
                   AND TRUNC (modified_on) >= from_date
                   AND nsti.product_code = productcode
                   AND nst.network_code = networkcode
                   AND nst.network_code_for = networkcodefor;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3573';
                   v_message :=
                         'Balance mismatch process executed for network stock. Exception in getting current stock transactions.'
                      || SQLERRM;
                   DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                   RAISE mainexception;
             END;
 
             DBMS_OUTPUT.put_line (   'Product Code= '
                                   || productcode
                                   || ' and Network Code= '
                                   || networkcode
                                   || ' and Network Code For= '
                                   || networkcodefor
                                   || ' and Mismatched amount='
                                   || amount
                                  );
             DBMS_OUTPUT.put_line (   'Current Stock= '
                                   || current_stock
                                   || ' and Close Stock= '
                                   || close_stock
                                   || ' and Txn Amount= '
                                   || txn_amount
                                  );
             amount := close_stock + txn_amount - current_stock;
 
             IF amount <> 0
             THEN
                errstring :=
                      errstring
                   || 'Product Code= '
                   || productcode
                   || ' and Network Code= '
                   || networkcode
                   || ' and Network Code For= '
                   || networkcodefor
                   || ' and Amount='
                   || amount
                   || ',';
             END IF;
          EXCEPTION
             WHEN OTHERS
             THEN
                v_errorcode := '3573';
                v_message :=
                   'Balance mismatch process executed for network stock. Exception while executing the loop.';
                DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                RAISE mainexception;
          END;
       END LOOP;
 
       IF (errstring <> NULL OR LENGTH (TRIM (errstring)) > 0)
       THEN
          v_errorcode := '3519';
          v_message :=
             'Balance mismatch process executed successfully for network stock. Mismatch found.';
          v_message := v_message || errstring;
       ELSE
          v_errorcode := '3518';
          v_message :=
             'Balance mismatch process executed successfully for network stock.No mismatch found.';
       END IF;
 
       ROLLBACK;
    EXCEPTION
       WHEN mainexception
       THEN
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
          ROLLBACK;
       WHEN OTHERS
       THEN
          v_errorcode := '3565';
          v_message :=
             'Balance mismatch process executed for network stock. Exception while executing the  main process.';
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
          ROLLBACK;
    END sp_net_stocks_balance_mismatch;
 
    PROCEDURE sp_system_usr_bal_mismatch (
       v_errorcode        OUT      VARCHAR2,
       v_message          OUT      VARCHAR2,
       v_processed_upto   IN       DATE,
       v_amount           OUT      NUMBER
    )
    IS
       currentbalance   user_balances.balance%TYPE;
       closingbalance   user_balances.balance%TYPE;
       channelinamt     user_balances.balance%TYPE;
       channeloutamt    user_balances.balance%TYPE;
       c2soutamt        user_balances.balance%TYPE;
       diffdramt        user_balances.balance%TYPE;
       diffamt          user_balances.balance%TYPE;
       reconamt         user_balances.balance%TYPE;
       errstring        VARCHAR2 (300);
       v_from_date      DATE;
       mainexception    EXCEPTION;
    BEGIN
       SET TRANSACTION READ ONLY;
 
       BEGIN
          --getting max date for which balance of all users is available
          SELECT MIN (balance_date)
            INTO v_from_date
            FROM (SELECT   ub.user_id, MAX (ub.balance_date) balance_date
                      FROM user_daily_balances ub, users u
                     WHERE u.user_id = ub.user_id AND u.status = 'Y'
                        OR (    u.status = 'N'
                            AND TRUNC (u.modified_on) >= v_processed_upto
                           )
                  GROUP BY ub.user_id);
 
          DBMS_OUTPUT.put_line ('v_from_date=' || v_from_date);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3575';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting max from date.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          --getting current balances of the users
          SELECT NVL (SUM (CASE
                              WHEN u.status = 'Y'
                                 THEN balance
                              WHEN (    u.status = 'N'
                                    AND TRUNC (u.modified_on) >=
                                                               v_processed_upto
                                   )
                                 THEN balance
                              ELSE 0
                           END
                          ),
                      0
                     )
            INTO currentbalance
            FROM user_balances ub, users u
           WHERE ub.user_id = u.user_id;
 
          DBMS_OUTPUT.put_line ('currentbalance=' || currentbalance);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3576';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting current balance of the users.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          --getting prev balance
          SELECT NVL (SUM (CASE
                              WHEN u.status = 'Y'
                                 THEN balance
                              WHEN u.status = 'N'
                              AND TRUNC (u.modified_on) >= v_processed_upto
                                 THEN balance
                              ELSE 0
                           END
                          ),
                      0
                     )
            INTO closingbalance
            FROM user_daily_balances ub, users u
           WHERE ub.user_id = u.user_id AND ub.balance_date = v_from_date;
 
          DBMS_OUTPUT.put_line ('closingbalance=' || closingbalance);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3572';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Closing(prev) balance.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 NVL
                    (SUM
                        (CASE
                            WHEN u.status = 'Y'
                               THEN cti.approved_quantity
                            WHEN u.status = 'N'
                            AND TRUNC (u.modified_on) >= v_processed_upto
                               THEN cti.approved_quantity
                            ELSE 0
                         END
                        ),
                     0
                    )
            INTO channelinamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.to_user_id = u.user_id
             --close date instead of transfer date is being used as transfer_date is not being updated at approval time
             -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
             AND TRUNC (ct.close_date) >= v_from_date + 1;
 
          DBMS_OUTPUT.put_line ('channelInAmt=' || channelinamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3560';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channle In transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 NVL
                    (SUM
                        (CASE
                            WHEN u.status = 'Y'
                               THEN cti.approved_quantity
                            WHEN u.status = 'N'
                            AND TRUNC (u.modified_on) >= v_processed_upto
                               THEN cti.approved_quantity
                            ELSE 0
                         END
                        ),
                     0
                    )
            INTO channeloutamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.from_user_id = u.user_id
             --close date instead of transfer date is being used as transfer_date is not being updated at approval time
             -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
             AND TRUNC (ct.close_date) >= v_from_date + 1;
 
          DBMS_OUTPUT.put_line ('channelOutAmt=' || channeloutamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3561';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channel Out transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          SELECT NVL
                    (SUM
                        (CASE
                            WHEN (cs.transfer_status = '250')
                               THEN (CASE
                                        WHEN (credit_back_status != '200')
                                           THEN (CASE
                                                    WHEN u.status = 'N'
                                                    AND TRUNC (u.modified_on) >=
                                                               v_processed_upto
                                                       THEN cs.quantity
                                                    WHEN u.status <> 'N'
                                                       THEN cs.quantity
                                                    ELSE 0
                                                 END
                                                )
                                        ELSE 0
                                     END
                                    )
                            ELSE cs.transfer_value
                         END
                        ),
                     0
                    )
            INTO c2soutamt
            FROM c2s_transfers cs, users u
           WHERE cs.sender_id = u.user_id
             AND cs.transfer_status <> '206'
             AND cs.transfer_date >= v_from_date + 1;
 
          DBMS_OUTPUT.put_line ('c2sOutAmt=' || c2soutamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3562';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting C2S Out transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          SELECT NVL (SUM (CASE
                              WHEN u.status = 'N'
                              AND TRUNC (u.modified_on) >= v_processed_upto
                                 THEN cs.quantity
                              WHEN u.status <> 'N'
                                 THEN cs.quantity
                              ELSE 0
                           END
                          ),
                      0
                     )
            INTO reconamt
            FROM c2s_transfers cs, users u
           WHERE cs.sender_id = u.user_id
             AND cs.reconciliation_flag = 'Y'
             AND cs.reconciliation_date >= v_from_date + 1
             AND cs.transfer_status = '200'
             AND cs.transfer_date < cs.reconciliation_date;
 
          DBMS_OUTPUT.put_line ('reconAmt=' || reconamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3577';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting reconciliation transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT NVL
                    (SUM
                        ((CASE
                             WHEN (stock_updated = 'Y')
                                THEN (CASE
                                         WHEN u.status = 'Y'
                                            THEN d.transfer_value
                                         WHEN u.status = 'N'
                                         AND TRUNC (u.modified_on) >=
                                                               v_processed_upto
                                            THEN d.transfer_value
                                         ELSE 0
                                      END
                                     )
                             ELSE 0
                          END
                         )
                        ),
                     0
                    )
            INTO diffamt
            FROM adjustments d, users u
           WHERE d.user_id = u.user_id
             AND d.entry_type = 'CR'
             AND d.adjustment_date >= v_from_date + 1;
 
          DBMS_OUTPUT.put_line ('diffAmt=' || diffamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT NVL
                    (SUM
                        ((CASE
                             WHEN (stock_updated = 'Y')
                                THEN (CASE
                                         WHEN u.status = 'Y'
                                            THEN d.transfer_value
                                         WHEN u.status = 'N'
                                         AND TRUNC (u.modified_on) >=
                                                               v_processed_upto
                                            THEN d.transfer_value
                                         ELSE 0
                                      END
                                     )
                             ELSE 0
                          END
                         )
                        ),
                     0
                    )
            INTO diffdramt
            FROM adjustments d, users u
           WHERE d.user_id = u.user_id
             AND d.entry_type = 'DR'
             AND d.adjustment_date >= v_from_date + 1;
 
          DBMS_OUTPUT.put_line ('diffDrAmt=' || diffdramt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       v_amount :=
            closingbalance
          + channelinamt
          - channeloutamt
          - c2soutamt
          - reconamt
          - currentbalance
          + diffamt
          - diffdramt;
       DBMS_OUTPUT.put_line ('balance=' || v_amount);
 
       IF v_amount = 0
       THEN
          v_errorcode := '3516';
          v_message :=
             'Balance mismatch process executed successfully for user balance. No mismatch found.';
       ELSE
          v_errorcode := '3517';
          v_message :=
                'Balance mismatch process executed successfully for user balance. Mismatch found for Amount:'
             || v_amount;
       END IF;
 
       ROLLBACK;
    EXCEPTION
       WHEN mainexception
       THEN
          ROLLBACK;
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
       WHEN OTHERS
       THEN
          ROLLBACK;
          v_errorcode := '3563';
          v_message :=
             'Balance mismatch process executed for user balance. Exception while executing the main process.';
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
    END sp_system_usr_bal_mismatch;
 
    PROCEDURE sp_system_total_bal_mismatch (
       v_errorcode        OUT      VARCHAR2,
       v_message          OUT      VARCHAR2,
       v_processed_upto   IN       DATE,
       v_amount           OUT      NUMBER
    )
    IS
       currentbalance   user_balances.balance%TYPE;
       closingbalance   user_balances.balance%TYPE;
       channelinamt     user_balances.balance%TYPE;
       channeloutamt    user_balances.balance%TYPE;
       c2soutamt        user_balances.balance%TYPE;
       diffdramt        user_balances.balance%TYPE;
       diffamt          user_balances.balance%TYPE;
       reconamt         user_balances.balance%TYPE;
       errstring        VARCHAR2 (300);
       v_from_date      DATE;
       mainexception    EXCEPTION;
    BEGIN
       SET TRANSACTION READ ONLY;
 
       BEGIN
          --getting current balances of the users
          SELECT NVL (SUM (balance), 0)
            INTO currentbalance
            FROM user_balances ub;
 
          DBMS_OUTPUT.put_line ('currentbalance=' || currentbalance);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3576';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting current balance of the users.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 NVL (SUM (cti.approved_quantity), 0)
            INTO channelinamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.to_user_id = u.user_id;
 
          DBMS_OUTPUT.put_line ('channelInAmt=' || channelinamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3560';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channle In transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 NVL (SUM (cti.approved_quantity), 0)
            INTO channeloutamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.from_user_id = u.user_id;
 
          DBMS_OUTPUT.put_line ('channelOutAmt=' || channeloutamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3561';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channel Out transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          SELECT NVL
                    (SUM
                        (CASE
                            WHEN (cs.transfer_status = '250')
                               THEN (CASE
                                        WHEN (credit_back_status = '200')
                                           THEN 0
                                        ELSE (cs.quantity)
                                     END
                                    )
                            ELSE cs.quantity
                         END
                        ),
                     0
                    )
            INTO c2soutamt
            FROM c2s_transfers cs
           WHERE cs.transfer_status <> '206';
 
          DBMS_OUTPUT.put_line ('c2sOutAmt=' || c2soutamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3562';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting C2S Out transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT NVL (SUM ((CASE
                               WHEN (stock_updated = 'Y')
                                  THEN d.transfer_value
                               ELSE 0
                            END
                           )
                          ),
                      0
                     )
            INTO diffamt
            FROM adjustments d
           WHERE d.entry_type = 'CR';
 
          DBMS_OUTPUT.put_line ('diffAmt=' || diffamt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT NVL (SUM ((CASE
                               WHEN (stock_updated = 'Y')
                                  THEN d.transfer_value
                               ELSE 0
                            END
                           )
                          ),
                      0
                     )
            INTO diffdramt
            FROM adjustments d
           WHERE d.entry_type = 'DR';
 
          DBMS_OUTPUT.put_line ('diffDrAmt=' || diffdramt);
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
             RAISE mainexception;
       END;
 
       v_amount :=
            channelinamt
          - channeloutamt
          - c2soutamt
          - currentbalance
          + diffamt
          - diffdramt;
       DBMS_OUTPUT.put_line ('balance=' || v_amount);
 
       IF v_amount = 0
       THEN
          v_errorcode := '3516';
          v_message :=
             'Balance mismatch process executed successfully for user balance. No mismatch found.';
       ELSE
          v_errorcode := '3517';
          v_message :=
                'Balance mismatch process executed successfully for user balance. Mismatch found for Amount:'
             || v_amount;
       END IF;
 
       ROLLBACK;
    EXCEPTION
       WHEN mainexception
       THEN
          ROLLBACK;
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
       WHEN OTHERS
       THEN
          ROLLBACK;
          v_errorcode := '3563';
          v_message :=
             'Balance mismatch process executed for user balance. Exception while executing the main process.';
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
    END sp_system_total_bal_mismatch;
 
 --Add by ved
   PROCEDURE sp_chnl_users_bal_mismatch (v_errorcode OUT VARCHAR2,   v_message OUT VARCHAR2,  v_processed_upto   IN DATE, v_amount OUT NUMBER )
    IS
      CURSOR cursor1
      IS
              SELECT user_id, product_code, balance FROM user_balances;
            
           mismatchstring   VARCHAR2 (200);
           userid           user_balances.user_id%TYPE;
           productcode      user_balances.product_code%TYPE;
           tmpbalance       user_balances.balance%TYPE;
           currentbalance   user_balances.balance%TYPE;
           closingbalance   user_balances.balance%TYPE;
           channelinamt     user_balances.balance%TYPE;
           channeloutamt    user_balances.balance%TYPE;
           c2soutamt        user_balances.balance%TYPE;
           diffdramt        user_balances.balance%TYPE;
           diffamt          user_balances.balance%TYPE;
           reconamt         user_balances.balance%TYPE;
           v_from_date      DATE;
           mainexception    EXCEPTION;
           
           BEGIN
                   SET TRANSACTION READ ONLY;
                  FOR t_r IN cursor1
                       LOOP
                          userid := t_r.user_id;
                        productcode := t_r.product_code;
                        currentbalance := -t_r.balance;
          --(Total IN transactions-Total OUT transactions-balance) should be zero
                               DBMS_OUTPUT.put_line ('Initial userid ='|| userid|| ' currentbalance='|| currentbalance||' productcode='||productcode );
                           
                        BEGIN
                        --getting max date for which balance of all users is available
                                  SELECT MIN (balance_date) INTO v_from_date 
                                  FROM (SELECT   ub.user_id, MAX (ub.balance_date) balance_date FROM user_daily_balances ub, users u
                                                      WHERE u.user_id = ub.user_id AND u.status = 'Y' OR (u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto )
                                   GROUP BY ub.user_id);
                                    DBMS_OUTPUT.put_line ('v_from_date=' || v_from_date);
                         EXCEPTION
                        WHEN OTHERS THEN
                               v_errorcode := '3575';
                            v_message :='Balance mismatch process executed for user balance. Exception in getting max from date.' || SQLERRM;
                            DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                            RAISE mainexception;
                         END;
                       
                       BEGIN
             --getting current balances of the users
                             SELECT NVL (SUM (CASE WHEN u.status = 'Y' THEN balance WHEN (u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto ) THEN balance ELSE 0 END),0) INTO currentbalance
                               FROM user_balances ub, users u  WHERE ub.user_id = u.user_id
                             AND u.user_id = userid AND ub.product_code = productcode;
                              DBMS_OUTPUT.put_line (   'userid='|| userid|| ' currentbalance='|| currentbalance|| ' productcode='|| productcode );
                        EXCEPTION
                      WHEN OTHERS THEN
                             v_errorcode := '3576';
                           v_message :='Balance mismatch process executed for user balance. Exception in getting current balance of the users.'|| SQLERRM;
                           DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                           RAISE mainexception;
                   END; 
                 
                 BEGIN
             --getting prev balance
                         SELECT NVL (SUM (CASE WHEN u.status = 'Y' THEN balance WHEN u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto THEN balance ELSE 0 END), 0) INTO closingbalance
                          FROM user_daily_balances ub, users u
                       WHERE ub.user_id = u.user_id AND ub.balance_date = v_from_date
                       AND u.user_id = userid AND ub.product_code = productcode;
                         DBMS_OUTPUT.put_line ('closingbalance=' || closingbalance);
                EXCEPTION
                WHEN OTHERS THEN
                     v_errorcode := '3572';
                     v_message :='Balance mismatch process executed for user balance. Exception in getting Closing(prev) balance.'|| SQLERRM;
                     DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                     RAISE mainexception;
                END;
               
              BEGIN
                      SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                    NVL (SUM (CASE  WHEN u.status = 'Y' THEN cti.approved_quantity WHEN u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto  THEN cti.approved_quantity ELSE 0   END ),  0 )  INTO channelinamt
                    FROM channel_transfers ct, channel_transfers_items cti, users u
                    WHERE ct.status = 'CLOSE'
                    AND ct.transfer_id = cti.transfer_id
                    AND ct.to_user_id = u.user_id
                      --close date instead of transfer date is being used as transfer_date is not being updated at approval time
                    -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
                    AND TRUNC (ct.close_date) >= v_from_date + 1
                    AND u.user_id = userid
                    AND cti.product_code = productcode;
                     DBMS_OUTPUT.put_line ('channelInAmt=' || channelinamt);
             EXCEPTION
             WHEN OTHERS THEN
                     v_errorcode := '3560';
                     v_message :='Balance mismatch process executed for user balance. Exception in getting Channle In transactions.'|| SQLERRM;
                     DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                     RAISE mainexception;
             END;
           
               BEGIN
                       SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                      NVL(SUM(CASE WHEN u.status = 'Y' THEN cti.approved_quantity WHEN u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto THEN cti.approved_quantity ELSE 0 END),0) INTO channeloutamt               FROM channel_transfers ct, channel_transfers_items cti, users u
                      WHERE ct.status = 'CLOSE'  AND ct.transfer_id = cti.transfer_id AND ct.from_user_id = u.user_id
                    --close date instead of transfer date is being used as transfer_date is not being updated at approval time
                    -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
                    AND TRUNC (ct.close_date) >= v_from_date + 1
                    AND u.user_id = userid AND cti.product_code = productcode;
                      DBMS_OUTPUT.put_line ('channelOutAmt=' || channeloutamt);
              EXCEPTION
            WHEN OTHERS THEN
                     v_errorcode := '3561';
                    v_message :='Balance mismatch process executed for user balance. Exception in getting Channel Out transactions.'|| SQLERRM;
                    DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                    RAISE mainexception;
            END;
 
             BEGIN
                       SELECT NVL(SUM(CASE WHEN (cs.transfer_status = '250') THEN (CASE WHEN (credit_back_status != '200') THEN (CASE WHEN u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto THEN cs.quantity WHEN u.status <> 'N' THEN cs.quantity ELSE 0 END) ELSE 0 END) ELSE cs.transfer_value END),0) INTO c2soutamt
                      FROM c2s_transfers cs, users u
                      WHERE cs.sender_id = u.user_id AND cs.transfer_status <> '206' AND cs.transfer_date >= v_from_date + 1
                      AND u.user_id = userid AND cs.product_code = productcode;
                        DBMS_OUTPUT.put_line ('c2sOutAmt=' || c2soutamt);
            EXCEPTION
            WHEN OTHERS THEN
                         v_errorcode := '3562';
                        v_message :='Balance mismatch process executed for user balance. Exception in getting C2S Out transactions.'|| SQLERRM;
                        DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                        RAISE mainexception;
              END;
 
          BEGIN
                         SELECT NVL (SUM (CASE WHEN u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto THEN cs.quantity WHEN u.status <> 'N' THEN cs.quantity ELSE 0 END), 0) INTO reconamt
                    FROM c2s_transfers cs, users u
                    WHERE cs.sender_id = u.user_id
                    AND cs.reconciliation_flag = 'Y'
                    AND cs.reconciliation_date >= v_from_date + 1
                    AND cs.transfer_status = '200'
                    AND cs.transfer_date < cs.reconciliation_date
                    AND u.user_id = userid AND cs.product_code = productcode;
                     DBMS_OUTPUT.put_line ('reconAmt=' || reconamt);
          EXCEPTION
                     WHEN OTHERS THEN
                                  v_errorcode := '3577';
                               v_message :='Balance mismatch process executed for user balance. Exception in getting reconciliation transactions.'|| SQLERRM;
                               DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                               RAISE mainexception;
           END;
 
           BEGIN
             --getting differential transactions of a particular channel user and a particular product
                        SELECT NVL(SUM((CASE WHEN (stock_updated = 'Y') THEN (CASE WHEN u.status = 'Y' THEN d.transfer_value WHEN u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto
                        THEN d.transfer_value ELSE 0 END ) ELSE 0 END ) ), 0 ) INTO diffamt FROM adjustments d, users u WHERE d.user_id = u.user_id
                        AND d.entry_type = 'CR'
                        AND d.adjustment_date >= v_from_date + 1
                       AND u.user_id = userid
                       AND d.product_code = productcode;
                        DBMS_OUTPUT.put_line ('diffAmt=' || diffamt);
             EXCEPTION
             WHEN OTHERS THEN
                            v_errorcode := '3574';
                         v_message :='Balance mismatch process executed for user balance. Exception in getting Differential transactions.'|| SQLERRM;
                         DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                         RAISE mainexception;
            END;
 
            BEGIN
             --getting differential transactions of a particular channel user and a particular product
                     SELECT NVL(SUM((CASE WHEN (stock_updated = 'Y') THEN (CASE WHEN u.status = 'Y' THEN d.transfer_value WHEN u.status = 'N' AND TRUNC (u.modified_on) >= v_processed_upto
                     THEN d.transfer_value ELSE 0 END ) ELSE 0 END )), 0) INTO diffdramt FROM adjustments d, users u WHERE d.user_id = u.user_id
                      AND d.entry_type = 'DR'
                      AND d.adjustment_date >= v_from_date + 1
                      AND u.user_id = userid
                      AND d.product_code = productcode;
                      DBMS_OUTPUT.put_line ('diffDrAmt=' || diffdramt);
             EXCEPTION
             WHEN OTHERS THEN
                            v_errorcode := '3574';
                         v_message :=  'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'|| SQLERRM;
                         DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
                         RAISE mainexception;
            END;
                     v_amount := closingbalance + channelinamt - channeloutamt - c2soutamt - reconamt  - currentbalance + diffamt - diffdramt;
                    DBMS_OUTPUT.put_line ('balance=' || v_amount);
             
            IF v_amount = 0 THEN
                           v_errorcode := '3516';
                        v_message := 'Balance mismatch process executed successfully for user balance. No mismatch found.';
            ELSE
                         mismatchstring :=mismatchstring || userid || ':' || t_r.balance || ',';
                         v_errorcode := '3517';
                         v_message := 'Balance mismatch process executed successfully for user balance. Mismatch found for Amount:'|| v_amount;
            END IF;
            
            IF LENGTH (mismatchstring) >=100 THEN
                 RAISE mainexception;                 
             END IF;
       END LOOP;
        
               IF LENGTH (mismatchstring) > 0  THEN
                     v_errorcode := '3517';
                     v_message :='Balance mismatch process executed successfully for user balance. Mismatch found.' || mismatchstring;
                ELSE
                     v_errorcode := '3516';
                     v_message :=  'Balance mismatch process executed successfully for user balance.No mismatch found.';
                END IF;
                
                   DBMS_OUTPUT.put_line ('v_message=' || v_message);
                   ROLLBACK;
                                
EXCEPTION
WHEN mainexception THEN
          ROLLBACK;
          IF LENGTH (mismatchstring) > 0  THEN
                 v_errorcode := '3517';
                  v_message :='Balance mismatch process executed successfully for user balance. Mismatch found.' || mismatchstring;
          END IF;
          DBMS_OUTPUT.put_line ('v_message=' || v_message);
          DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
 WHEN OTHERS THEN
          ROLLBACK;
          IF LENGTH (mismatchstring) > 0  THEN
                 v_errorcode := '3517';
                 v_message :='Balance mismatch process executed successfully for user balance. Mismatch found.' || mismatchstring;
            END IF;
        DBMS_OUTPUT.put_line ('v_message=' || v_message);
        DBMS_OUTPUT.put_line ('EXCEPTION =' || SQLERRM);
    END sp_chnl_users_bal_mismatch;
 END pkg_balance_mismatch;
/
