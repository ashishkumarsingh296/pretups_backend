*****************************************************************
*								*
*	            Pretups 7.0 Selenium Suite for LMS         	*
*								*
*****************************************************************

- Changes to be made before running the suite on PostGres and Oracle:

1. ClassName - DB_Connection.java
	  	
Changes :-
	
	- Line No. 41 and 43 can be interchangeably used for Oracle and PostGres connections respectively.Similarly for line 35 and 36.


2. ClassName - Linux_Connect.java

Changes :-
	
	- At Line No. 48 change the putty credentials with that of current server.

3. ClassName - functions.java
   ClassName - Launchdriver.java

Changes :-
	
	- Change the path for Chrome Driver.

4. ClassName - BonusPointsReference.java (Use when 'OPT_IN_OUT_ALLOW' is False)

Changes :-
	  Following Lines can be interchangeably used for Oracle and Postgres connections.

	- Line No. 47 for Oracle and Line No. 48 for Postgres.
        - Similarly, Line no. 50 and 51 can be interchangeably used according to column name in database.

5. ClassName - O2CTransfer.java

Changes :-
	
	- For Oracle, Uncomment Line No.111 to Line No. 115.
        - For Postgres,Uncomment Line No.106 to Line No.110.
        - Based on O2C Transfer Rule Approval Level Amount and Transaction Amount,comment or uncomment line no. 136 for level of Approvals.
         
6. System_Preferences :-
	
	- 'OPT_IN_OUT_ALLOW' should be True.
        - 'VOUCHER_TRACKING_ALLOWED' should be False.

7. dataFile.properties

Changes :-

        - For Postgres,do mention Schema Name in dbport.For Example, dbport=5432/pretups7_test2.For Oracle,only dbport is required.Example, dbport=1522
        - Change other fields as per requirements.

8. ClassName - BonusPointsNonReference.java (Use when 'OPT_IN_OUT_ALLOW' is False)

Changes :-
	  Following Lines can be interchangeably used for Oracle and Postgres connections.

	- Line No. 53 for Oracle and Line No. 54 for Postgres.
        - Similarly, Line no. 56 and 57 can be interchangeably used according to column name in database.

9. ClassName - BonusPointsTransactionBased.java (Use when 'OPT_IN_OUT_ALLOW' is False)


Changes :-

        - Mention dbid,dbpass,dbip,dbport in line no. 29 to 32.
	  Following Lines can be interchangeably used for Oracle and Postgres connections.

	- Line No. 49 and 50 for Oracle and Line No. 51 and 52 for Postgres.
        - Similarly, Line no. 60 for Oracle and 61 for Postgres.
        - Line No. 63 for Oracle and line no. 64 for Postgres.
        - Line No. 306 to 308 for Oracle and Line No.309 to 310 for Postgres.
        - Line No. 362 to 364 for Oracle and Line No.365 to 366 for Postgres.
        - Line No. 386 for Oracleand Line No. 387 for Postgres.

10. ClassName - baseClass.java

Changes :-

        - Mention dbid,dbpass,dbip,dbport in line no. 33 to 36.
          Following Lines can be interchangeably used for Oracle and Postgres connections.

	- Line No. 43 to 45 for Oracle and Line No. 46 and 47 for Postgres.
        - Line No. 52 for Oracle and line no. 53 for Postgres.
        - Line No. 55 for Oracle and line no. 56 for Postgres.
        - Line No. 583,587,591 for Oracle and Line No.584,588,592 for Postgres.
        - Line No. 628 for Oracle and Line No. 629 for Postgres.
        

11. ClassName - BonusPointsReferenceOptInOptOut.java (Use when 'OPT_IN_OUT_ALLOW' is True)

	
          Following Lines can be interchangeably used for Oracle and Postgres connections.

	- Line No. 48 for Oracle and Line No. 49 for Postgres.
        - Similarly, Line no. 51 for Oracle and 52 for Postgres.

12. ClassName - BonusPointsNonReferenceOptInOptOut.java (Use when 'OPT_IN_OUT_ALLOW' is True)

	
          Following Lines can be interchangeably used for Oracle and Postgres connections.

	- Line No. 54 for Oracle and Line No. 55 for Postgres.
        - Similarly, Line no. 57 for Oracle and 58 for Postgres.

13. ClassName - BonusPointsTransactionBasedOptinout.java (Use when 'OPT_IN_OUT_ALLOW' is True)


	 - Mention dbid,dbpass,dbip,dbport in line no. 29 to 32.
          Following Lines can be interchangeably used for Oracle and Postgres connections.

	- Line No. 49,50 for Oracle and Line No. 51,52 for Postgres.
        - Similarly, Line no. 60 for Oracle and 61 for Postgres.
        - Line No. 63 for Oracle and Line No. 64 for Postgres.
        - Line No. 320 to 322 for Oracle and Line No. 323,324 for Postgres.
        - Similarly, Line no. 377 to 379 for Oracle and 380,381 for Postgres.
        - Line No. 401 for Oracle and 402 for Postgres.

14. ClassName - optinoptout.java

        -Use Line No.36 and 38 for Oracle and Line No. 37 and 39 for Postgres.
        - Mention dbid,dbpass,dbip,dbport in line no. 25 to 28.



15. ClassName - ApproveLMS.java

        - Line No 47, 48. Message needs to be checked.




