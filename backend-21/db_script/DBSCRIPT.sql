update system_preferences set DEFAULT_VALUE=6 where PREFERENCE_CODE ='MIN_LOGIN_PWD_LENGTH'

update system_preferences set DEFAULT_VALUE=5 where PREFERENCE_CODE ='MAX_PWD_BLOCK_COUNT'

update system_preferences set DEFAULT_VALUE=30 where PREFERENCE_CODE ='DYS_AFTER_CHANGE_PWD'

update system_preferences set DEFAULT_VALUE='com.btsl.pretups.util.clientutils.VietnamUtil' where PREFERENCE_CODE ='OPERATOR_UTIL_C'
