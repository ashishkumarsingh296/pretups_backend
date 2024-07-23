--Excluded as this is not a part of 7.2.0
--UPDATE PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTR001';
-- PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTR01A';
--UPDATE PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTRDMM';

--commit;

CREATE TABLE CACHE_TYPES
(
  CACHE_CODE            VARCHAR(20) NOT NULL,
  CACHE_NAME            VARCHAR(50)     NOT NULL,
  STATUS                      VARCHAR(1)  NOT NULL,
  CACHE_KEY        VARCHAR(70)     NOT NULL
);

Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CELLID', 'Cell ID Cache', 'Y', 'updatecacheservlet.cellidcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERWALLET', 'User wallet cache', 'Y', 'updatecacheservlet.userwalletcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERSERVICE', 'User services cache', 'Y', 'updatecacheservlet.userservicecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERDEFAULTCONFIG', 'User default config cache', 'Y', 'updatecacheservlet.userdefaultcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERALLWDSTATUS', 'User Allowed Status Cache', 'Y', 'updatecacheservlet.userstatuscache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('TRANSFERRULE', 'Transfer rule cache', 'Y', 'updatecacheservlet.transferrulescache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('TRFPRFPRD', 'Transfer Profile Product Cache', 'Y', 'updatecacheservlet.transferprofileproductcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('TRFPRF', 'Transfer Profile Cache', 'Y', 'updatecacheservlet.transferprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SIMPRF', 'Sim Profile Cache', 'Y', 'updatecacheservlet.simProfilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVSLTRMAPP', 'Service selector mapping cache', 'Y', 'updatecacheservlet.serviceselectormappingecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVPAYMENTMAPP', 'Service payment mapping cache', 'Y', 'updatecacheservlet.servicepaymentmappingcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('REGCONTRL', 'Registration Control Cache', 'Y', 'updatecacheservlet.registrationControlcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVINTFCROUTING', 'Service Interface Routing Cache', 'Y', 'updatecacheservlet.serviceInterfaceroutingecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVINTMAPP', 'Service Interface Mapping Cache', 'Y', 'updatecacheservlet.serviceInterfacemappingcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('REQINTFC', 'Request interface cache', 'Y', 'updatecacheservlet.requestinterfacecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('PAYMNTMETH', 'Payment method cache', 'Y', 'updatecacheservlet.paymentmethodcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVKEYWORD', 'Service keyword cache', 'Y', 'updatecacheservlet.servicekeywordcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVCLASSINFO', 'Service Class Info By Code Cache', 'Y', 'updatecacheservlet.serviceclassbycodecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('ROUTINGCONTL', 'Routing Control Cache', 'Y', 'updatecacheservlet.routingControlcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('PREFERENCE', 'Preference cache', 'Y', 'updatecacheservlet.preferencecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWSERVICE', 'Network Service Cache', 'Y', 'updatecacheservlet.networkservicecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWPRDSRVTYPE', 'Network Product Service Type Cache', 'Y', 'updatecacheservlet.networkproductservicetypecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWPRD', 'Network Product Cache', 'Y', 'updatecacheservlet.networkproductcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWPREFIX', 'Network prefix cache', 'Y', 'updatecacheservlet.networkprefixcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWINTRFCMOD', 'Network interface module cache', 'Y', 'updatecacheservlet.networkinterfacemodulecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NETWORK', 'Network cache', 'Y', 'updatecacheservlet.networkcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MOBILENOPRFINTR', 'Mobile number prefix interfaces cache', 'Y', 'updatecacheservlet.msisdnprefixinterfacemappingcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MESSAGERESOURCE', 'Message Resource', 'Y', 'updatecacheservlet.messageresourcecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MESSAGEGTWCAT', 'Message Gateway For Category Cache', 'Y', 'updatecacheservlet.msggatwayforcatgorycache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MSGGTW', 'Message gateway cache', 'Y', 'updatecacheservlet.messagegatewaycache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MESSAGE', 'Message Cache', 'Y', 'updatecacheservlet.messagecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('LOOKUP', 'Lookup cache', 'Y', 'updatecacheservlet.loockupscache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('LOGGER', 'Logger Config', 'Y', 'updatecacheservlet.loggercache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('LMSPRF', 'Lms Profile Cache', 'Y', 'updatecacheservlet.lmsprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('INTRROUTINGCONTL', 'Interface routing control cache', 'Y', 'updatecacheservlet.interfaceroutingcontrolcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('IATNW', 'IAT network cache', 'Y', 'updatecacheservlet.iatnetworkcachecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('IATCONTRYMAST', 'IAT country master cache', 'Y', 'updatecacheservlet.iatcoutrymastercache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('GROUPTYPEPRF', 'Group type profile cache', 'Y', 'updatecacheservlet.grouptypeprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('FILE', 'File cache', 'Y', 'updatecacheservlet.filecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CURRENCY', 'Currency Cache', 'Y', 'updatecacheservlet.currencycache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CONSTANTS', 'Constant Properties', 'Y', 'updatecacheservlet.constantcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('COMMPRF', 'Commission Profile Cache', 'Y', 'updatecacheservlet.commissionprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CARDGROUP', 'Card Group Cache', 'Y', 'updatecacheservlet.cardgroupcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('BONUSBUNDLE', 'Bonus bundle cache', 'Y', 'updatecacheservlet.bounsboundlecache');

UPDATE pages SET spring_page_url ='/pretups/Channel2ChannelTransferReport.form' where page_code='RPTRWTR001';
UPDATE pages SET spring_page_url ='/pretups/Channel2ChannelTransferReport.form' where page_code='RPTRWTRDMM';
UPDATE pages SET spring_page_url ='/pretups/Channel2ChannelTransferReport.form' where page_code='RPTRWTR01A';

UPDATE pages SET spring_page_url ='/reportsO2C/o2cTransferDetails.form' where page_code='RPTO2CDD01';
UPDATE pages SET spring_page_url ='/reportsO2C/o2cTransferDetails.form' where page_code='RPTO2CDD1A';
UPDATE pages SET spring_page_url ='/reportsO2C/o2cTransferDetails.form' where page_code='RPTO2CDDDM';

UPDATE pages
SET module_code='CHRPTUSR', page_url='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', menu_name='Users Closing Balance', menu_item='Y', sequence_no=26, menu_level='2', application_id='1', spring_page_url='/reports/userClosingBalance.form'
WHERE page_code='URCLOBL001';


UPDATE pages
SET module_code='CHRPTUSR', page_url='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', menu_name='Users Closing Balance', menu_item='N', sequence_no=26, menu_level='2', application_id='1', spring_page_url='/reports/userClosingBalance.form'
WHERE page_code='URCLOBL01A';


UPDATE pages
SET module_code='CHRPTUSR', page_url='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', menu_name='Users Closing Balance', menu_item='Y', sequence_no=26, menu_level='1', application_id='1', spring_page_url='/reports/userClosingBalance.form'
WHERE page_code='URCLOBLDMM';


   
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('INET_REPORT_ALLOWED', 'NET REPORT WITH DATA TABLE ALLOWED', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'TO ENABLE INET REPORT ALONG WITH DATA TABLE', 'Y', 'N', 'C2S', 'TO ENABLE INET REPORT ALONG WITH DATA TABLE;CHANGES MADE DURING SPRING DEVELPOMENT', TIMESTAMP '2018-01-18 00:00:00.000', 'ADMIN',TIMESTAMP  '2018-01-18 00:00:00.000', 'SU0001', NULL, 'Y');

ALTER TABLE cache_types
ADD PRIMARY KEY (cache_code); 

UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACP001';
UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACP01A';
UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACPDMM';


CREATE OR REPLACE FUNCTION pretupsdatabase.userclosingbalance(p_userid character varying, p_startdate timestamp without time zone, p_enddate timestamp without time zone, p_startamt numeric, p_endamt numeric)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$
declare 
p_userCloBalDateWise VARCHAR(4000) DEFAULT '' ;
balDate timestamp without time zone; 
balance numeric ; 
productCode VARCHAR(10);
c_userCloBal CURSOR(p_userId VARCHAR,p_startDate timestamp without time zone,p_endDate timestamp without time zone,p_startAmt numeric,p_endAmt numeric) IS
	   SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC, product_code ASC ;
        BEGIN
	    FOR bal IN c_userCloBal(p_userId,p_startDate,p_endDate,p_startAmt,p_endAmt)
        LOOP
                            balDate:=bal.balance_date;
                            balance:=bal.balance;
                            productCode:=bal.PRODUCT_CODE;
                            p_userCloBalDateWise:=p_userCloBalDateWise||productCode||'::'||balDate||'::'||balance||',';
        END LOOP;
        IF LENGTH(p_userCloBalDateWise) > 0 THEN
         p_userCloBalDateWise:=SUBSTR(p_userCloBalDateWise,0,LENGTH(p_userCloBalDateWise));        
        END IF;
            RETURN p_userCloBalDateWise;
END;
$function$


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, record_count)
 Values
   ('C2SERPDET', TO_DATE('10/26/2016 10:52:05', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('10/25/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('10/26/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'C2S Record Details', 'ET','0');
COMMIT;

update system_preferences
set modified_allowed='N' where preference_code='INET_REPORT_ALLOWED';

COMMIT;