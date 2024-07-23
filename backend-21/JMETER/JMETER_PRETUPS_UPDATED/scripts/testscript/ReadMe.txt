1. EXTSYS_USR_APRL_LEVEL_REQUIRED preference should be 0 i.e. user approval level should be 0.
2. In case user approval level is 1 or 2, the user is forcefully updated to approved status in DB.
3. Change pin should not be forced for transactions.
4. User Defined variables must be filled before running the scripts.
5. All the scripts must be run in a single go as output of one script acts as input for another.
6. Fill data in EXTGW_USERADD_POSITIVE.csv for Add user API.
7. Time set between multiple C2S transactions must be minimal.