*****************************************************************
*																*
*	            Pretups 7.0 Selenium Suite for VMS         		*
*																*
*****************************************************************

- Changes to be made before running the suite on PostGres and Oracle:

1. ClassName - DB_Connection.java
	  	
Changes :-
	
	- Line No. 41 and 42 can be interchangeably used for Oracle and PostGres connections respectively.

2. ClassName - Linux_Connect.java

Changes :-
	
	- At Line No. 48 change the putty credentials with that of current server.

3. ClassName - VMS_SuperAdmin_Till_Voucher_Initiation.java

Changes :-
	
	- At Line No. 99 change the path for VoucherGenerator.sh script file.


4. System_Preferences :-
	
	- 'VOUCHER_ENABLE_TRACKING' should be True.
	- 'USER_EVENT_REMARKS' should be False.
	- 'VOUCHER_TRACKING_ALLOWED' should be False.

5. For burn rate indicator, in input csv file ('BurnRateIndicator' sheet), put positive scenario values. Pre-requisite for burn rate is: 
	1. Vouchers must be distributed and consumed.
	2. VoucherBurnRateSummary.sh process must be run.
	  




