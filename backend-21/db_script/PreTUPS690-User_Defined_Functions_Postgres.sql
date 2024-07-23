--Script for function USERCLOSINGBALANCE 
CREATE OR REPLACE FUNCTION USERCLOSINGBALANCE (p_userId  VARCHAR,p_startDate DATE,p_endDate DATE,p_startAmt integer,p_endAmt integer)
RETURNS VARCHAR
AS $p_userCloBalDateWise$
declare 
p_userCloBalDateWise VARCHAR(4000) DEFAULT '' ; balDate DATE; balance integer ; productCode VARCHAR(10);
c_userCloBal CURSOR(p_userId VARCHAR,p_startDate DATE,p_endDate DATE,p_startAmt integer,p_endAmt integer) IS
	   SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC;
        BEGIN
	    FOR bal IN c_userCloBal(p_userId,p_startDate,p_endDate,p_startAmt,p_endAmt)
        LOOP
                            balDate:=bal.balance_date;
                            balance:=bal.balance;
                            productCode:=bal.PRODUCT_CODE;
                            p_userCloBalDateWise:=p_userCloBalDateWise||productCode||':'||balDate||':'||balance||',';
        END LOOP;
        IF LENGTH(p_userCloBalDateWise) > 0 THEN
         p_userCloBalDateWise:=SUBSTR(p_userCloBalDateWise,0,LENGTH(p_userCloBalDateWise)-1);
        END IF;
            RETURN p_userCloBalDateWise;
END;
$p_userCloBalDateWise$ LANGUAGE PLPGSQL;