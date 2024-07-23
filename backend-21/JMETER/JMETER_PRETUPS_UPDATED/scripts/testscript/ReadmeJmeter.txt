Checklist Points 

##NOTE: ResultCSVPath and ResultXLSPath should be mentioned using forward slash.
For CSV to XLS conversion copy and paste ExcelWriter.jar from lib folder to inside lib folder of apache jmeter software.

A) Before running Transaction.jmx and  Transaction_part2.jmx suite below point should performed.

 1. Default Transfer profile associated with channel user should have
    a. Maximum Transfer Count should be very high(approx 100000).
	b. SUBSCRIBER_OUT_COUNT and SUBSCRIBER_IN_COUNT should be very high(approx 10000)

 2. Balance of the channel users defined in MSISDN for C2C should be ver high(approx 100000)
 
 3. Default commission profile for channel users should not have any Tax and additional commissions on transactions.
 
 4. MSISDN defined for LMS should belong to user associated with LMS profile only and no other services and have some loyalty
    points.
	
 5. Transfer rule for both products should be defined.

 6. All Services must be allowed to users.

 7. Commission profile should be defined for both products(Etopup and Post-etopup)
 
 8. If Transaction.jmx doesn't run at one go, then try 
    a. First Enable all modules till C2C Withdraw EXTGW_negative and disable remaining and run it.
	b. Second Enable modules after C2C Withdraw EXTGW_negative till Postpaid Bill payment USSD_negative and disable remainig and run it.
	c. Lastly Enable the rest modules(Only api's which are given in enabled state) except modules covered in step a and b.
 

B) For BatchScheduleRecharge.jmx

 1. only positive testcase are enabled in the suite.

 2. First run all positive testcases disabling all negative scenarios.

 3. Second run all negative testcases disabling all positive scenarios. 
 
C) For ChangeNetwork.jmx

  1. Please Change newLanguage1Message and newLanguage2Message according to total no. of networks. It should contain comma separated values
     under quotes for each network.  

D) For Vietnam_EXTGW_Final_Decrypted PIN_PVG.jmx

  1. Java Home should be of 64-bit architecture.
  2. Heap size in jmeter should be set to 80% of available physical memory.
  3. Vouchers of All status(EN,PE,DA,S,ST,CU,WH,OH) should be present in sufficient amount(10).
  
  
