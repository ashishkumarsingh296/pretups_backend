Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000417', 'mclass^2&pid^61:3000417:{0},You have a Pending SOS for Settlement so transaction not Allowed.', 'NG', 'mclass^2&pid^61:3000417:{0},You have a Pending SOS for Settlement so transaction not Allowed.', 
    'mclass^2&pid^61:3000417:{0},You have a Pending SOS for Settlement so transaction not Allowed.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('3000417', '0', 'User name');
COMMIT;


Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '300418', 'mclass^2&pid^61:300418:Withdrawal request, against pending SOS, of product(s) {0} is successful,  your new balance of product(s) {1}.', 'NG', 'mclass^2&pid^61:300418:Withdrawal request, against pending SOS, of product(s) {0} is successful,  your new balance of product(s) {1}.', 
    'mclass^2&pid^61:300418:Withdrawal request, against pending SOS, of product(s) {0} is successful,  your new balance of product(s) {1}.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('300418', '0', 'SOS pending Amount (productcode:Amount)');
COMMIT;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('300418', '1', 'User Balances (productcode:Balance)');
COMMIT;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('300418', '2', 'Transaction ID');
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '300419', 'mclass^2&pid^61:300419:Withdrawal request, against pending SOS of user {3}, for product(s) {0} is successful,  your new balance of product(s) {1}. Transfer ID against SOS is {2}'., 'NG', 'mclass^2&pid^61:300419:Withdrawal request, against pending SOS of user {3}, for product(s) {0} is successful,  your new balance of product(s) {1}. Transfer ID against SOS is {2}', 
    'mclass^2&pid^61:300419:Withdrawal request, against pending SOS of user {3}, for product(s) {0} is successful,  your new balance of product(s) {1}. Transfer ID against SOS is {2}', NULL, 'Y', NULL, NULL,NULL);
COMMIT;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('300419', '0', 'SOS pending Amount (productcode:Amount)');
COMMIT;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('300419', '1', 'User Balances (productcode:Balance)');
COMMIT;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('300419', '2', 'Transaction ID');
COMMIT;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('300419', '3', 'User MSISDN');
COMMIT; 



----Adding for SOS Settlement

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000400', 'mclass^2&pid^61:3000400:Invalid message format for SOS Settlement request.', 'NG', 'mclass^2&pid^61:3000400:Invalid message format for SOS Settlement request.', 
    'mclass^2&pid^61:3000400:Invalid message format for SOS Settlement request.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000401', 'mclass^2&pid^61:3000401:Channel SOS feature is not enabled in system.', 'NG', 'mclass^2&pid^61:3000401:Channel SOS feature is not enabled in system.', 
    'mclass^2&pid^61:3000401:Channel SOS feature is not enabled in system.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000402', 'mclass^2&pid^61:3000402:Channel SOS manual settlement is not allowed in system.', 'NG', 'mclass^2&pid^61:3000402:Channel SOS manual settlement is not allowed in system.', 
    'mclass^2&pid^61:3000402:Channel SOS manual settlement is not allowed in system.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000403', 'mclass^2&pid^61:3000403:Invalid SOS Wallet type.', 'NG', 'mclass^2&pid^61:3000403:Invalid SOS Wallet type.', 
    'mclass^2&pid^61:3000403:Invalid SOS Wallet type.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000404', 'mclass^2&pid^61:3000404:There is no pending transaction for settlement.', 'NG', 'mclass^2&pid^61:3000404:There is no pending transaction for settlement.', 
    'mclass^2&pid^61:3000404:There is no pending transaction for settlement.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000405', 'mclass^2&pid^61:3000405:Transaction Id {0} is settled with Mobile number {1}.', 'NG', 'mclass^2&pid^61:3000405:Transaction Id {0} is settled with Mobile number {1}.', 
    'mclass^2&pid^61:3000405:Transaction Id {0} is settled with Mobile number {1}.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('3000405', '0', 'Transaction ID');
COMMIT;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('3000405', '1', 'User MSISDN whom SOS is settled with');
COMMIT; 



Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000406', 'mclass^2&pid^61:3000406:SOS Settlement process has failed. Please try again later.', 'NG', 'mclass^2&pid^61:3000406:SOS Settlement process has failed. Please try again later.', 
    'mclass^2&pid^61:3000406:SOS Settlement process has failed. Please try again later.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000407', 'mclass^2&pid^61:3000407:Either there is no pending transaction for settlement or you are not authorized to settle for this mobile number.', 'NG', 'mclass^2&pid^61:3000407:Either there is no pending transaction for settlement or you are not authorized to settle for this mobile number.', 
    'mclass^2&pid^61:3000407:Either there is no pending transaction for settlement or you are not authorized to settle for this mobile number.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000408', 'mclass^2&pid^61:3000408:Invalid mobile number.', 'NG', 'mclass^2&pid^61:3000408:Invalid mobile number.', 
    'mclass^2&pid^61:3000408:Invalid mobile number.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000409', 'mclass^2&pid^61:3000409:Details not found for given mobile number.', 'NG', 'mclass^2&pid^61:3000409:Details not found for given mobile number.', 
    'mclass^2&pid^61:3000409:Details not found for given mobile number.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '3000414', 'mclass^2&pid^61:3000414:User cannot be deleted since its SOS is pending.', 'NG', 'mclass^2&pid^61:3000414:User cannot be deleted since its SOS is pending.', 
    'mclass^2&pid^61:3000414:User cannot be deleted since its SOS is pending.', NULL, 'Y', NULL, NULL,NULL);
COMMIT;




