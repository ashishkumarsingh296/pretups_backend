		
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CAPV107', 'OPT2CHNL', '/jsp/channeltransfer/viewPackageTransferApproval.jsp', 'Approve Level 1', 'N', 
    2, '2', '1', '/jsp/channeltransfer/viewPackageTransferApproval.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CAPV108', 'OPT2CHNL', '/jsp/channeltransfer/approvePackageProductDetails.jsp', 'Approve Level 1', 'N', 
    2, '2', '1', '/jsp/channeltransfer/viewPackageTransferApproval.jsp');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF010', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF011', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF013', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF014', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF016', 'OPT2CHNL', '/jsp/channeltransfer/packageTransferDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/packageTransferDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF017', 'OPT2CHNL', '/jsp/channeltransfer/packageProductDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/packageProductDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF018', 'OPT2CHNL', '/jsp/channeltransfer/packageProductDetailsConfirm.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/packageProductDetailsConfirm.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF019', 'OPT2CHNL', '/jsp/channeltransfer/O2CpackageFinal.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/O2CpackageFinal.jsp');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF011', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF010', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF014', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF013', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF018', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF019', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF017', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF016', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('APV1O2CTRF', 'O2CAPV107', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('APV1O2CTRF', 'O2CAPV108', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD001', 'VOMSDWN', '/voucherDownload.do?method=userSearchAttribute', 'VOMS Download', 'Y', 
    5, '2', '1', '/channelTransferEnquiryAction.do?method=userSearchAttribute');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD001A', 'VOMSDWN', '/voucherDownload.do?method=userSearchAttribute', 'VOMS Download', 'N', 
    5, '2', '1', '/channelTransferEnquiryAction.do?method=userSearchAttribute');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD002', 'VOMSDWN', '/jsp/channeltransfer/enquirySearchAttribute.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquirySearchAttribute.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD003', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferSearchUser.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferSearchUser.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD004', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferList.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferList.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD005', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferView.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferView.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD006', 'VOMSDWN', '/voucherDownload.do?method=channelUserEnquiry', 'VOMS Download', 'N', 
    5, '2', '1', '/channeltransfer/O2Cenquiry.form');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD006A', 'VOMSDWN', '/voucherDownload.do?method=channelUserEnquiry', 'VOMS Download', 'N', 
    5, '2', '1', '/channeltransfer/O2Cenquiry.form');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD007', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferList.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferList.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD008', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferView.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferView.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD01', 'VOMSDWN', '/stock/o2cEnq_input.action?serviceType=O2C', 'VOMS Download', 'N', 
    1, '2', '2', '/stock/o2cEnq_input.action?serviceType=O2C');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD1DM', 'VOMSDWN', '/stock/o2cEnq_input.action?serviceType=O2C', 'VOMS Download', 'N', 
    1, '1', '2', '/stock/o2cEnq_input.action?serviceType=O2C');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHDDMM', 'VOMSDWN', '/voucherDownload.do?method=userSearchAttribute', 'VOMS Download', 'N', 
    5, '1', '1', '/channelTransferEnquiryAction.do?method=userSearchAttribute');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHDDMM2', 'VOMSDWN', '/voucherDownload.do?method=channelUserEnquiry', 'VOMS Download', 'N', 
    5, '1', '1', '/channeltransfer/O2Cenquiry.form');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD001A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD004', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD005', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD006', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD006A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD007', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD008', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD01', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD1DM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHDDMM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHDDMM2', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD001', '1');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'DOWNVOUCH', 'Voucher download', 'Voucher Download', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');
   
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'DOWNVOUCH', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMADVB001', 'VOUBUNDLE', '/addVoucherBundleAction.do?method=loadVoucherBundleList', 'Add voucher bundle', 'Y', 
    1, '2', '1', '/addVoucherBundleAction.do?method=loadVoucherBundleList');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMADVBDMM', 'VOUBUNDLE', '/addVoucherBundleAction.do?method=loadVoucherBundleList', 'Add voucher bundle', 'Y', 
    1, '1', '1', '/addVoucherBundleAction.do?method=loadVoucherBundleList');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMVWVB001', 'VOUBUNDLE', '/viewVoucherBundleAction.do?method=viewVoucherBundles', 'View voucher bundle', 'Y', 
    3, '2', '1', '/viewVoucherBundleAction.do?method=viewVoucherBundles');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMVWVBDMM', 'VOUBUNDLE', '/viewVoucherBundleAction.do?method=viewVoucherBundles', 'View voucher bundle', 'Y', 
    3, '1', '1', '/viewVoucherBundleAction.do?method=viewVoucherBundles');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVB001', 'VOUBUNDLE', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify', 'Modify voucher bundle', 'Y', 
    2, '2', '1', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVBDMM', 'VOUBUNDLE', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify', 'Modify voucher bundle', 'Y', 
    2, '1', '1', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVB002', 'VOUBUNDLE', '/jsp/voucherbundle/modifyVoucherBundle.jsp', 'Modify voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/modifyVoucherBundle.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMVWVB002', 'VOUBUNDLE', '/jsp/voucherbundle/viewVoucherBundles.jsp', 'View voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/viewVoucherBundles.jsp');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'VOUADDBUN', 'Add voucher bundle', 'Voucher bundle', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'VOUVWBUN', 'View voucher bundle', 'Voucher bundle', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'VOUMODBUN', 'Modify voucher bundle', 'Voucher bundle', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUADDBUN', 'VMADVB001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVB002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUADDBUN', 'VMADVBDMM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUVWBUN', 'VMVWVB001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUVWBUN', 'VMVWVBDMM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVB001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVBDMM', '1');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'VOUADDBUN', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'VOUVWBUN', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'VOUMODBUN', '1');

Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('ALL', 'VOMS_BUNID', 'ALL', 200, TO_DATE('12/10/2019 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', NULL);
Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('ALL', 'VOMS_DETID', 'ALL', 216, TO_DATE('10/14/2019 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', NULL);

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('ADDVOUBUN', 'Add Voucher Bundle', 'Add Voucher Bundle', NULL, 'com.btsl.voms.voucherbundle.web.VoucherBundleForm', 
    'configfiles/restservice', '/rest/voucherBundle/addVoucherBundle', 'N', 'Y', 'VOUADDBUN');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('MODVOUBUN', 'Modify Voucher Bundle', 'Modify Voucher Bundle', NULL, 'com.btsl.voms.voucherbundle.web.VoucherBundleForm', 
    'configfiles/restservice', '/rest/voucherBundle/modifyVoucherBundle', 'N', 'Y', 'VOUMODBUN');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('IS_BUN_PRE_ID_NULL_ALLOW', 'IS_BUN_PRE_ID_NULL_ALLOW', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'IS_BUN_PRE_ID_NULL_ALLOW', 'N', 
    'N', 'C2S', 'IS_BUN_PRE_ID_NULL_ALLOW', TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', 'IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', 'N', 
    'N', 'C2S', 'IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMADVB002', 'VOUBUNDLE', '/jsp/voucherbundle/confirmAddVoucherBundle.jsp', 'Add voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/confirmAddVoucherBundle.jsp');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUADDBUN', 'VMADVB002', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVB003', 'VOUBUNDLE', '/jsp/voucherbundle/confirmModifyVoucherBundle.jsp', 'Modify voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/confirmModifyVoucherBundle.jsp');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVB003', '1');

Insert into KEY_VALUES
   (KEY, VALUE, TYPE, TEXT1)
 Values
   ('BCA Format', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterBCA', 'VCH_FRMT', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterBCA');
Insert into KEY_VALUES
   (KEY, VALUE, TYPE, TEXT1)
 Values
   ('Print Vendor Format', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterPrintVendor', 'VCH_FRMT', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterPrintVendor');



 COMMIT;