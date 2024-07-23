
For SOS Settlement and SOS request API EXTGW there are three ways to hit request
1. By MSISDN-PIN 
2. By LOGINID-PASSWORD
3. By EXTCODE

There are 3 threads accordingly created in jmeter suite.

Keep only one enabled at a time according to combination defined in service keyword.


To execute SOS Settlement API following are the prerequisites :

1. In system_preferences table SOS_SETTLEMENT_TYPE must be "MANUAL"
2. In network_preferences table CHANNEL_SOS_ALLOWED_WALLET must be "PARENT" or "OWNER" for network in which Settlement is to be done. If it is missing from network_preference table then entry must be in system_preferences table with above mentioned default value.



1. Keep DecryptionUtility.jar inside "Jmeter_folder/lib/ext"
2. Edit " user.properties" which resides in bin folder inside jmeter and search for parameter "user.classpath". 
Uncomment it and enter value : Path_to_jmeter/jmeter_folder/lib/ext/ DecryptionUtility.jar
