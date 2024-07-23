*****************************************************************
*								*
*	            Pretups 7.0 Selenium Suite for LMS         	*
*								*
*****************************************************************

- Changes to be made before running the suite on PostGres and Oracle:

1. ClassName - DB_Connection.java
	  	
Changes :-
	
	- Line No. 34 to 42 can be interchangeably used for Oracle and PostGres connections respectively.

2. ClassName - Linux_Connect.java

Changes :-
	
	- At Line No. 48 change the putty credentials with that of current server.

3. ClassName - functions.java

Changes :-
	
	- At Line No. 29 change the path for Chrome Driver.

4. ClassName - BonusPointsReference.java

Changes :-
	
	- Line No. 46 and 48 can be interchangeably used for Oracle and PostGres connections respectively.
        - Similarly, Line no. 51 and 53 can be interchangeably used according to column name in database.

5. ClassName - TC_11_O2C_with_approval.java

Changes :-
	
	- For Oracle, Uncomment Line No.103 to Line No. 107.
        - For Postgres,Uncomment Line No.110 to Line No.114.
         
6. System_Preferences :-
	
	- 'OPT_IN_OUT_ALLOW' should be False.

7. dataFile.properties

Changes :-

        - For Postgres,do mention Schema Name in dbport.For Example, dbport=5432/pretups7_test2.For Oracle,only dbport is required.Example, dbport=1522
        - Change other fields as required.



	  




