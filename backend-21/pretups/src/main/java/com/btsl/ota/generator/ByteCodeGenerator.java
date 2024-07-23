package com.btsl.ota.generator;

/**
 * @(#)ByteCodeGenerator.java
 *                            Copyright(c) 2003, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 *                            This Class is used for Generating ByteCode
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 *                            Gaurav Garg 05/11/2003 Initial Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 */
// Length of only four tags are fixed they are 50, 51, 55, 70 for which
// convert2Length function is used and in all other cases lenght converter
// function
// has been used 3 function are in this class and one is in OtaMessage(Encrypt)

import java.util.ArrayList;

// import com.btsl.ota.util.ByteCodeGeneratorUtil;
// import com.btsl.ota.util.SIMServicesUtil;
import org.apache.log4j.Logger;

import com.btsl.common.BTSLBaseException;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.util.SimUtil;

public class ByteCodeGenerator {
	private static Logger logger = Logger.getLogger(ByteCodeGenerator.class.getName());

    public static void main(String[] args) {
        final String methodName = "main";
        try {
        //    org.apache.log4j.PropertyConfigurator.configure(ByteCodeGeneratorI.FILE_PATH);
            logger = Logger.getLogger(ByteCodeGenerator.class.getName());
            // ApplicationResourses.load("D:\\csmsh\\configfiles\\Application.props");
            // System.out.println(ApplicationResourses.getProperty("csms.project.title"));
            // org.apache.log4j.PropertyConfigurator.configure("D:\\csmsh\\configfiles\\LogConfig.props");
            logger = Logger.getLogger(ByteCodeGenerator.class.getName());
            /*
             * try
             * {
             * testMethod();
             * }
             * catch(com.btsl.common.BaseException bex)
             * {
             * java.util.ArrayList _errorMessageList=new java.util.ArrayList();
             * logger.error("loadOrderStatusList() BaseException bex="+bex);
             * _errorMessageList.addAll(BTSLUtil.generateMessage(bex.getInfo()));
             * System.out.println(_errorMessageList);
             * }
             */
           // org.apache.log4j.PropertyConfigurator.configure(ByteCodeGeneratorI.FILE_PATH);
            logger = Logger.getLogger(ByteCodeGenerator.class.getName());
            ArrayList test = new ArrayList();
            // ServicesVO sVO = new ServicesVO();
            /*
             * sVO = new ServicesVO();
             * sVO.setPosition(2);
             * sVO.setStatus("y");
             * sVO.setServiceID("25");
             * sVO.setMajorVersion("25");
             * sVO.setMinorVersion("100");
             * sVO.setLabel1("aa");
             * sVO.setLabel2("0909");
             * sVO.setLength(100);
             * sVO.setOffSet(100);
             * sVO.setOperation(SIMServicesUtil.CHANGE_TITLE);
             * test.add(sVO);
             */
            ServicesVO sVO1 = new ServicesVO();
            sVO1.setPosition(1);
            sVO1.setStatus("Y");
            sVO1.setValidityPeriod(5);
            sVO1.setPositionList("5,7,9");
            sVO1.setDescription("6");
            sVO1.setServiceID("25");
            sVO1.setMajorVersion("25");
            sVO1.setMinorVersion("100");
            sVO1.setLabel1("auuuuuuuuuuuua");
            sVO1.setLabel2("0909");
            sVO1.setLength(100);
            sVO1.setOffSet(100);
            sVO1.setOperation(ByteCodeGeneratorI.VALIDITY_PERIOD);
            sVO1.setByteCode("9898");

            sVO1.setTypeOfEnquiry("59");
            test.add(sVO1);
            ByteCodeGenerator k = new ByteCodeGenerator();
            SimProfileVO s = new SimProfileVO();
            String updateTId = k.generateByteCode(test, false, s);
            System.out.println(" " + updateTId);
        } catch (Exception e) {
            logger.error("Exception=" + e);

        }
    }

    // Constructor Declation
    public ByteCodeGenerator() {
        super();
    }

    /**
     * This method is used add new Service
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    private String addNewService(ServicesVO serviceVO, SimProfileVO simProfileVO) {
    	if(logger.isDebugEnabled()){
        logger.debug("addNewService Entering .............." + serviceVO.getPosition() + " " + serviceVO.getStatus() + " " + serviceVO.getServiceID() + "   " + serviceVO.getMajorVersion() + "  " + serviceVO.getMinorVersion() + "  " + serviceVO.getLabel1() + "  " + serviceVO.getLabel2());
    	}
        StringBuffer addNewServiceBuf = new StringBuffer();
        try {
            String buffer = null;
            String menuPosition = SimUtil.menuPosition(serviceVO.getPosition(), simProfileVO);
            String activationStatus = SimUtil.activationStatus(serviceVO.getStatus());
            String serviceId = SimUtil.serviceID(serviceVO.getServiceID());
            String majorVersion = SimUtil.version(serviceVO.getMajorVersion());
            String minorVersion = SimUtil.version(serviceVO.getMinorVersion());
            String lang1 = SimUtil.stringToByteConverter(serviceVO.getLabel1());
            String lang2 = null;
            String byteCode = serviceVO.getByteCode();
            int langOption = SimUtil.langFinder(serviceVO.getLabel1().trim(), serviceVO.getLabel2());
            String length = SimUtil.intToShort(serviceVO.getLength());
            String offset = SimUtil.intToShort(serviceVO.getOffSet());
            // The given below option has to be tested as this has to be asked
            // from abhijit
            // now i am supposing that i am getting hex Values
            if (langOption == 1) {
                lang2 = serviceVO.getLabel2();
            }
            addNewServiceBuf.append(menuPosition);
            addNewServiceBuf.append(activationStatus);
            addNewServiceBuf.append(serviceId);
            addNewServiceBuf.append(majorVersion);
            addNewServiceBuf.append(minorVersion);
            addNewServiceBuf.append(SimUtil.inlineTagUniCode(lang1, lang2, langOption));
            addNewServiceBuf.append(offset);
            addNewServiceBuf.append(length);
            // Commented By Amit Ruwali(if wml size is large)
            if(logger.isDebugEnabled()){
            logger.debug("Size of Generated Byte code=" + byteCode.length() / 2);
            }
            // if(byteCode.length()/2>ByteCodeGeneratorI.BYTECODESIZE)
            // throw new
            // Exception("The size of the generated Service Exceeds the required limit "+ByteCodeGeneratorI.BYTECODESIZE);

            addNewServiceBuf.append(ByteCodeGeneratorI.BYTECODETAG);
            // Now this buffer is used to store lenght of Byte Code
            buffer = SimUtil.convertTo2DigitLength((byteCode.length() / 2));// "2
                                                                            // is
                                                                            // included
                                                                            // because
                                                                            // each
                                                                            // byte
                                                                            // takes
                                                                            // two
                                                                            // characters
                                                                            // to
                                                                            // store
            addNewServiceBuf.append(buffer);
            addNewServiceBuf.append(byteCode);
            // Now this buffer is used to store the length of Whole of this
            // addNewServiceBuf Buffer
            buffer = SimUtil.convertTo2DigitLength((addNewServiceBuf.toString().length() / 2));// "2
                                                                                               // is
                                                                                               // included
                                                                                               // because
                                                                                               // each
                                                                                               // byte
                                                                                               // takes
                                                                                               // two
                                                                                               // characters
                                                                                               // to
                                                                                               // store
            addNewServiceBuf.insert(0, buffer);
            addNewServiceBuf.insert(0, ByteCodeGeneratorI.MENU_TAG);
            // SimUtil.display(addNewServiceBuf.toString());
            SimUtil.finalCheck(addNewServiceBuf.toString());
        } catch (Exception e) {
            logger.error("addNewService Exception " + e);
            
        }
        logger.debug("addNewService Exiting ..............." + addNewServiceBuf.toString());
        return addNewServiceBuf.toString();
    }

    /**
     * This method is used delete Service
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    private String deleteService(ServicesVO serviceVO, SimProfileVO simProfileVO)  {
    	if(logger.isDebugEnabled()){
    		StringBuffer msg=new StringBuffer("");
        	msg.append("deleteService Entering ..............");
        	msg.append(serviceVO.getPosition());
        	msg.append("  ");
        	msg.append(serviceVO.getServiceID());       
        	msg.append(" ");
        	msg.append(serviceVO.getMajorVersion());
        	msg.append("  ");
        	msg.append(serviceVO.getMinorVersion());
        	
        	String message=msg.toString();
    		logger.debug(message);
    	}
        StringBuffer deleteServiceBuf = new StringBuffer();
        try {
            String menuPosition = null;
            String serviceId = null;
            String majorVersion = null;
            String minorVersion = null;
            menuPosition = SimUtil.menuPosition(serviceVO.getPosition(), simProfileVO);
            serviceId = SimUtil.serviceID(serviceVO.getServiceID());
            majorVersion = SimUtil.version(serviceVO.getMajorVersion());
            minorVersion = SimUtil.version(serviceVO.getMinorVersion());
            deleteServiceBuf.append(menuPosition);
            deleteServiceBuf.append(serviceId);
            deleteServiceBuf.append(majorVersion);
            deleteServiceBuf.append(minorVersion);
            String buffer = SimUtil.lengthConverter((deleteServiceBuf.toString().length() / 2));// "2
                                                                                                // is
                                                                                                // included
                                                                                                // because
                                                                                                // each
                                                                                                // byte
                                                                                                // takes
                                                                                                // two
                                                                                                // characters
                                                                                                // to
                                                                                                // store
            deleteServiceBuf.insert(0, buffer);
            deleteServiceBuf.insert(0, ByteCodeGeneratorI.DELETE_TAG);
            // SimUtil.display(deleteServiceBuf.toString());
            SimUtil.finalCheck(deleteServiceBuf.toString());
        } catch (Exception e) {
            logger.error("deleteService Exception " + e);
            
        }
        if(logger.isDebugEnabled()){
        logger.debug("deleteService Exiting ..............." + deleteServiceBuf.toString());
        }
        return deleteServiceBuf.toString();
    }

    /**
     * This method is used activate and deactivate services
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    private String activateDeactivateService(ServicesVO serviceVO, SimProfileVO simProfileVO)  {
    	if(logger.isDebugEnabled()){
        logger.debug("activateDeactivateService Entering .............." + serviceVO.getOperation() + "  " + serviceVO.getPositionList());
        
    	}
    	StringBuffer activateDeactivateServiceBuf = new StringBuffer();
        try {
            String value = null;
            boolean isActivate = true;// isActivate true in case of activation
                                      // and false in case of deactivation
            if (serviceVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.ACTIVATE)) {
                isActivate = true;
            } else if (serviceVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.DEACTIVATE)) {
                isActivate = false;
            }
            if (isActivate) {
                value = "01";
            } else {
                value = "00";
            }
            String menuPositionList = SimUtil.menuPositionList(serviceVO.getPositionList(), simProfileVO);
            // here length is divided by 2 because of double length and one is
            // added because activation and deactivation state is also added
            String hexLength = SimUtil.lengthConverter(((menuPositionList.length() / 2) + 1));
            activateDeactivateServiceBuf.append(hexLength);
            activateDeactivateServiceBuf.append(value);
            activateDeactivateServiceBuf.append(menuPositionList);
            activateDeactivateServiceBuf.insert(0, ByteCodeGeneratorI.ACT_DEACT_TAG);
            // SimUtil.display(activateDeactivateServiceBuf.toString());
            SimUtil.finalCheck(activateDeactivateServiceBuf.toString());
        } catch (Exception e) {
            logger.error("activateDeactivateService :: Exception  " + e);
           
        }
        if(logger.isDebugEnabled()){
        	 logger.debug("activateDeactivateService :: Exiting ..............." + activateDeactivateServiceBuf.toString());
        }
       
        return activateDeactivateServiceBuf.toString();
    }

    /**
     * This method is used Change Menu Name
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    private String changeMenuName(ServicesVO serviceVO, SimProfileVO simProfileVO) {
    	if(logger.isDebugEnabled())
    	{
    		StringBuffer msg=new StringBuffer("");
        	msg.append("changeMenuName :: Entering ..............");
        	msg.append(serviceVO.getStatus());
        	msg.append("  ");
        	msg.append(serviceVO.getPosition());
        	msg.append(" ");
        	msg.append(serviceVO.getServiceID());
        	msg.append(" ");
        	msg.append(serviceVO.getMajorVersion());
        	msg.append(" ");
        	msg.append(serviceVO.getMinorVersion());
        	msg.append(" ");
        	msg.append(serviceVO.getLength());
        	msg.append(" ");
        	msg.append(serviceVO.getOffSet());
        	msg.append(" ");
        	msg.append( serviceVO.getLabel1());
        	msg.append(" ");
        	msg.append( serviceVO.getLabel2());
        	
        	String message=msg.toString();
    		logger.debug(message);
    	}
    	StringBuffer changeMenuNameBuf = new StringBuffer();
        try {
            String buffer = null;
            String activationStatus = SimUtil.activationStatus(serviceVO.getStatus());
            String menuPosition = SimUtil.menuPosition(serviceVO.getPosition(), simProfileVO);
            String serviceId = SimUtil.serviceID(serviceVO.getServiceID());
            String majorVersion = SimUtil.version(serviceVO.getMajorVersion());
            String minorVersion = SimUtil.version(serviceVO.getMinorVersion());
            String length = SimUtil.intToShort(serviceVO.getLength());
            String offset = SimUtil.intToShort(serviceVO.getOffSet());
            String lang1 = SimUtil.stringToByteConverter(serviceVO.getLabel1());
            String lang2 = null;
            int langOption = SimUtil.langFinder(serviceVO.getLabel1().trim(), serviceVO.getLabel2());
            if (langOption == 1) {
                lang2 = serviceVO.getLabel2();
            }
            changeMenuNameBuf.append(menuPosition);
            changeMenuNameBuf.append(activationStatus);
            changeMenuNameBuf.append(serviceId);
            changeMenuNameBuf.append(majorVersion);
            changeMenuNameBuf.append(minorVersion);
            changeMenuNameBuf.append(offset);
            changeMenuNameBuf.append(length);
            changeMenuNameBuf.append(SimUtil.menuTitleLang(lang1, lang2, langOption, simProfileVO));
            // Now this buffer is used to store the length of Whole of this
            // changeMenuNameBuf Buffer
            buffer = SimUtil.lengthConverter((changeMenuNameBuf.toString().length() / 2));// "2
                                                                                          // is
                                                                                          // included
                                                                                          // because
                                                                                          // each
                                                                                          // byte
                                                                                          // takes
                                                                                          // two
                                                                                          // characters
                                                                                          // to
                                                                                          // store
            changeMenuNameBuf.insert(0, buffer);
            changeMenuNameBuf.insert(0, ByteCodeGeneratorI.CHAGEMENUNAMETAG);
            // SimUtil.display(changeMenuNameBuf.toString());
            SimUtil.finalCheck(changeMenuNameBuf.toString());
        } catch (Exception e) {
            logger.error("changeMenuName :: Exception " + e);
           
        }
        if(logger.isDebugEnabled()){
        logger.debug("changeMenuName :: Exiting ..............." + changeMenuNameBuf.toString());
        }
        return changeMenuNameBuf.toString();
    }

    /**
     * This method is used generateByteCode
     * 
     * @param listOfServiceVOS
     *            ArrayList
     * @parma isEncrypt boolean
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws BTSLBaseException 
     * @throws Exception
     */
    public String generateByteCode(ArrayList listOfServiceVOS, boolean isEncrypt, SimProfileVO simProfileVO) throws BTSLBaseException {
    	if(logger.isDebugEnabled()){
        logger.debug("generateByteCode :: Entering ..............");
    	}
        StringBuffer generateByteCodeBuf = new StringBuffer();
        java.util.Iterator itr = listOfServiceVOS.iterator();
        while (itr.hasNext()) {
            ServicesVO sVO = (ServicesVO) itr.next();
            String operation = sVO.getOperation();
            if (operation.equalsIgnoreCase(ByteCodeGeneratorI.ADD)) {
                generateByteCodeBuf.append(addNewService(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.DELETE)) {
                generateByteCodeBuf.append(deleteService(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.ACTIVATE)) {
                generateByteCodeBuf.append(activateDeactivateService(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.DEACTIVATE)) {
                generateByteCodeBuf.append(activateDeactivateService(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.CHANGE_TITLE)) {
                generateByteCodeBuf.append(changeMenuName(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_PARAMETERS)) {
                generateByteCodeBuf.append(updateParameters(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SMSC)) {
                generateByteCodeBuf.append(updateSMSCDESCVP(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SHORTCODE)) {
                generateByteCodeBuf.append(updateSMSCDESCVP(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.VALIDITY_PERIOD)) {
                generateByteCodeBuf.append(updateSMSCDESCVP(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_TID)) {
                generateByteCodeBuf.append(updateTID(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.SIM_ENQUIRY)) {
                generateByteCodeBuf.append(simEnquiry(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_LANG_FILE)) {
                generateByteCodeBuf.append(updateLangFile(sVO, simProfileVO));
            } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.SENT_TEST_CARD)) {
                generateByteCodeBuf.append(sVO.getByteCode());// This is new
                                                              // version for
                                                              // testing
                return generateByteCodeBuf.toString();
            } else {
                throw new BTSLBaseException(this, "generateByteCode", ByteCodeGeneratorI.EXP_OPERATIONNOTSUPPORTED + " " + operation);
            }
        }
        String hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
        generateByteCodeBuf.insert(0, hexlength);
        generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
        if(logger.isDebugEnabled()){
        logger.debug("generateByteCode Exiting ..............." + generateByteCodeBuf.toString());
        }
        return generateByteCodeBuf.toString();
    }

    /**
     * This method is used Update Parmeters
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    private String updateParameters(ServicesVO serviceVO, SimProfileVO simProfileVO)  {
    	if(logger.isDebugEnabled()){
        logger.debug("updateParameters Entering .............." + serviceVO.getPosition() + "  " + serviceVO.getStatus());
    	}
        StringBuffer updateParametersBuf = new StringBuffer();
        try {
            updateParametersBuf.append(ByteCodeGeneratorI.UPDATEPARAMETETSTAG);
            updateParametersBuf.append(ByteCodeGeneratorI.fixedLenth02);
            updateParametersBuf.append(SimUtil.updateParameters(serviceVO.getPosition()));
            updateParametersBuf.append(SimUtil.activationStatus(serviceVO.getStatus()));
            // SimUtil.display(updateParametersBuf.toString());
            SimUtil.finalCheck(updateParametersBuf.toString());
        } catch (Exception e) {
            logger.error("updateParameters Exception " + e);
           
        }
        if(logger.isDebugEnabled()){
        logger.debug("updateParameters Exiting ..............." + updateParametersBuf.toString());
        }
        return updateParametersBuf.toString();
    }

    /**
     * This method is used Update Transaction ID
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    private String updateTID(ServicesVO serviceVO, SimProfileVO simProfileVO) {
    	if(logger.isDebugEnabled()){
        logger.debug("updateTID Entering .............." + serviceVO.getDescription());
    	}
        StringBuffer updateTIDBuf = new StringBuffer();
        try {
            updateTIDBuf.append(ByteCodeGeneratorI.UPDATETIDTAG);
            updateTIDBuf.append(ByteCodeGeneratorI.fixedLenth09);
            updateTIDBuf.append(SimUtil.updateTID(serviceVO.getDescription()));
            // SimUtil.display(updateTIDBuf.toString());
            SimUtil.finalCheck(updateTIDBuf.toString());
        } catch (Exception e) {
            logger.error("updateTID Exception " + e);
           
        }
        if(logger.isDebugEnabled()){
        logger.debug("updateTID Exiting ..............." + updateTIDBuf.toString());
        }
        return updateTIDBuf.toString();
    }

    // In is function is used for SIM Enquiry
    /*
     * SIM Enquiry
     * Tag(1), len(1), Parameter(TLV)
     * 57 Len TLV
     * There can be 4 types of SIM Enquiry
     * 1)Type-1 (Service Data for all services): Tag-58
     * e.g: 57 01 58
     * Response: keyword, Fixed Data, Status Bytes(80 bytes), Transaction ID(7
     * Bytes)
     * e.g ADM F0101 A 80Bytes Tra-ID
     * The 80 Bytes will contain data for all 20 records. i.e 4 bytes for each
     * record.
     * The 4 bytes will contain the following data. (Activation Status,Service
     * ID-1 Byte,Version No-2 Bytes)
     * Fixed data = 3 Bytes (F +SIM Applet Version+Lang Preference)
     * 2)Type-2 (Complete Menu File for specific positions): Tag-59, Len, Menu
     * Pos List
     * e.g: 59 01 01
     * Response: keyword, Fixed Data, Status Bytes(49 or 98 Bytes), Transaction
     * ID(7 Bytes)
     * e.g: ADM F0101 B 49Bytes Tra ID
     * Breakup of 49 Bytes:
     * Menu Position(1),Activation Status(1),Service ID(1),Version
     * No(2),Bytecode Offset(2),
     * Bytecode Len(2),Menu Title(40)
     * Fixed data = 3 Bytes (F +SIM Applet Version+Lang Preference)
     * 3)Type-3 (Settings): Tag-60
     * e.g: 57 01 60
     * Response:keyword, Fixed Data, Flags/parameters(10), SMS Settings1(20),
     * SMS-2(20), SMS-3(20)
     * e.g: ADM F0101 C 10Bytes(flags) 20Bytes(SMS1) 20Bytes(SMS2) 20Bytes(SMS3)
     * Fixed data = 3 Bytes (F +SIM Applet Version+Lang Preference)
     * 4)Type-4 (Bytecode Info): Tag-61
     * e.g: 57 01 61
     * Response: keyword, Lang Preference, Positions(80 bytes), Transaction ID(7
     * Bytes)
     * e.g: ADM F0101 D 80Bytes Tra-ID
     * The 80 Bytes will contain data for all 20 records. i.e 4 bytes for each
     * record.
     * The first 2 bytes of each 4 byte chunk represent the offset in bytecode
     * file
     * and the second 2 bytes represent the length of bytecode.
     * For example in the first 4 bytes of the 80 bytes the initial 2 bytes are
     * offset position of
     * the service at Menu Position one and the next 2 bytes repreent the length
     * of that service.
     * Fixed data = 3 Bytes (F +SIM Applet Version+Lang Preference)
     */
    /**
     * This method is used for SIM Enquiry
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws BTSLBaseException 
     * @throws Exception
     */
    private String simEnquiry(ServicesVO serviceVO, SimProfileVO simProfileVO) throws BTSLBaseException {
    	if(logger.isDebugEnabled()){
        logger.debug("simEnquiry  Entering ..............");
    	}
        StringBuffer simEnquiryBuf = new StringBuffer();
        try {
            int typeOfEnquiry = Integer.parseInt(serviceVO.getTypeOfEnquiry());
            switch (typeOfEnquiry) {
            case 58: // 58
                simEnquiryBuf.append(ByteCodeGeneratorI.SIM_ENQUIRYTAG);// 57
                simEnquiryBuf.append(ByteCodeGeneratorI.fixedLenth01);// 01
                simEnquiryBuf.append(ByteCodeGeneratorI.ALLSERVICESTAG);// 58
                break;
            case 59: // 59
                simEnquiryBuf.append(ByteCodeGeneratorI.SPECIFICSERVICESTAG);// 59
                String simList = SimUtil.simEnquiryList(serviceVO.getPositionList(), simProfileVO);
                simEnquiryBuf.append(SimUtil.lengthConverter(simList.length() / 2));// Length
                                                                                    // can
                                                                                    // be
                                                                                    // 1
                                                                                    // or
                                                                                    // 2
                simEnquiryBuf.append(simList);// one or two variable
                simEnquiryBuf.insert(0, SimUtil.lengthConverter(simEnquiryBuf.toString().length() / 2));
                simEnquiryBuf.insert(0, ByteCodeGeneratorI.SIM_ENQUIRYTAG);// 57
                break;
            case 60: // 60
                simEnquiryBuf.append(ByteCodeGeneratorI.SIM_ENQUIRYTAG);// 57
                simEnquiryBuf.append(ByteCodeGeneratorI.fixedLenth01);// 01
                simEnquiryBuf.append(ByteCodeGeneratorI.SETTINGSTAG);// 60
                break;
            case 61: // 61
                simEnquiryBuf.append(ByteCodeGeneratorI.SIM_ENQUIRYTAG);// 57
                simEnquiryBuf.append(ByteCodeGeneratorI.fixedLenth01);// 01
                simEnquiryBuf.append(ByteCodeGeneratorI.ALLBYTECODEINFOTAG);// 61
                break;
            default: {
                logger.error("simEnquiry Exception..............(Invalid Option)" + typeOfEnquiry);
            }
            }
        } catch (Exception e) {
            logger.error("simEnquiry::Invalid Request For SIM enquiry", e);
            throw new BTSLBaseException(this, "simEnquiry", "simEnquiry::Invalid Request For SIM enquiry");
        }
        // SimUtil.display(simEnquiryBuf.toString());
        SimUtil.finalCheck(simEnquiryBuf.toString());
        if(logger.isDebugEnabled()){
        logger.debug("simEnquiry Exiting ..............." + simEnquiryBuf.toString());
        }
        return simEnquiryBuf.toString();
    }

    /**
     * This method is used to Generate ByteCode
     * 
     * @param listOfServiceVOS
     *            ArrayList
     * @param isEncrytp
     *            boolean
     * @param simProfileVO
     *            SimProfileVO
     * @return ArrayList
     * @throws Exception
     */
    public ArrayList generateByteCodeArr(ArrayList listOfServiceVOS, boolean isEncrypt, SimProfileVO simProfileVO)  {
    	if(logger.isDebugEnabled()){
        logger.debug("generateByteCodeArr Entering ..............");
    	}
        StringBuffer generateByteCodeBuf = new StringBuffer();
        ArrayList generateByteCodeArr = new ArrayList();
        try {
            String hexlength = null;
            java.util.Iterator itr = listOfServiceVOS.iterator();
            while (itr.hasNext()) {
                ServicesVO sVO = (ServicesVO) itr.next();
                String operation = sVO.getOperation();
                if (operation.equalsIgnoreCase(ByteCodeGeneratorI.ADD)) {
                    logger.info(" generateByteCodeArr:: Entering ByteCodeGeneratorI.ADD..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(addNewService(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.DELETE)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.DELETE..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(deleteService(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.ACTIVATE)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.ACTIVATE..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(activateDeactivateService(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.DEACTIVATE)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.DEACTIVATE..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(activateDeactivateService(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.CHANGE_TITLE)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.CHANGE_TITLE..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(changeMenuName(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_PARAMETERS)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.UPDATE_PARAMETERS..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(updateParameters(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SMSC)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.UPDATE_SMSC..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(updateSMSCDESCVP(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_TID)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.UPDATE_TID..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(updateTID(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SHORTCODE)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.UPDATE_SHORTCODE..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(updateSMSCDESCVP(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.VALIDITY_PERIOD)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.VALIDITY_PERIOD..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(updateSMSCDESCVP(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.SIM_ENQUIRY)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.SIM_ENQUIRY..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(simEnquiry(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_LANG_FILE)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.ByteCodeGeneratorI.UPDATE_LANG_FILE..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(updateLangFile(sVO, simProfileVO));
                    hexlength = SimUtil.convertTo2DigitLength(generateByteCodeBuf.toString().trim().length() / 2);
                    generateByteCodeBuf.insert(0, hexlength);
                    generateByteCodeBuf.insert(0, ByteCodeGeneratorI.ADMIN_TAG);
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.SENT_TEST_CARD)) {
                    logger.info("generateByteCodeArr:: Entering ByteCodeGeneratorI.ByteCodeGeneratorI.SENT_TEST_CARD..............");
                    generateByteCodeBuf.delete(0, generateByteCodeBuf.toString().length());
                    generateByteCodeBuf.append(sVO.getByteCode());
                    generateByteCodeArr.add(generateByteCodeBuf.toString());
                } else {
                    logger.error("generateByteCodeArr:: Exception ..............." + ByteCodeGeneratorI.EXP_OPERATIONNOTSUPPORTED + " " + operation);

                    throw new BTSLBaseException(ByteCodeGeneratorI.EXP_OPERATIONNOTSUPPORTED + " " + operation);
                }

            }
        } catch (Exception e) {
            logger.error("generateByteCodeArr :: Exception ..............." + e);
           
        }
        if(logger.isDebugEnabled()){
        logger.debug("generateByteCodeArr :: Exiting :: Size..............." + generateByteCodeArr.size());
        }
        return generateByteCodeArr;
    }

    /**
     * This method is used for update SMSC , Port , VP
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws BTSLBaseException 
     * @throws Exception
     */
    private String updateSMSCDESCVP(ServicesVO serviceVO, SimProfileVO simProfileVO) throws BTSLBaseException {
    	if(logger.isDebugEnabled()){
        logger.debug("updateSMSCDESCVP Entering .............." + serviceVO.getOperation());
    	}
        int update = 0;// 01 for SMSC 02 for Port and 03 for VP
        StringBuffer updateSMSCDESCVP = new StringBuffer();
        if (serviceVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SMSC)) {
            update = 1;
        } else if (serviceVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SHORTCODE)) {
            update = 2;
        } else if (serviceVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.VALIDITY_PERIOD)) {
            update = 3;
        }
        if (serviceVO.getPosition() > 3 || serviceVO.getPosition() < 1) {
            throw new BTSLBaseException("updateSMSCDESCVP :: Position for" + serviceVO.getOperation() + " should range 1-3");
        } else {
            updateSMSCDESCVP.append("0" + update);
            updateSMSCDESCVP.append("0" + serviceVO.getPosition());
        }
        try {
            switch (update) {
            case 1:
                updateSMSCDESCVP.append(SimUtil.smscGatewayNo(serviceVO.getSmscGatewayNo()));
                break;
            case 2:
                updateSMSCDESCVP.append(SimUtil.smscGatewayNo(serviceVO.getSmscGatewayNo()));
                break;
            case 3:
                logger.info("**************ValidityPeriod****************" + serviceVO.getValidityPeriod());
                logger.info("**************ValidityPeriod****************" + SimUtil.vP(serviceVO.getValidityPeriod()));
                updateSMSCDESCVP.append(SimUtil.vP(serviceVO.getValidityPeriod()));
                break;
             default:
            	 if(logger.isDebugEnabled()){
            		 logger.debug("Default Value " + update);
            	 }
            }
            updateSMSCDESCVP.insert(0, SimUtil.lengthConverter(updateSMSCDESCVP.toString().trim().length() / 2));
            updateSMSCDESCVP.insert(0, ByteCodeGeneratorI.UPDATESMSCPORTVPTAG);
            SimUtil.display(updateSMSCDESCVP.toString());
            SimUtil.finalCheck(updateSMSCDESCVP.toString());
        } catch (Exception e) {
            logger.error("updateSMSCDESCVP Exception " + e);
            throw new BTSLBaseException(this, "updateSMSCDESCVP", "");
        }
        if(logger.isDebugEnabled()){
        logger.debug("updateSMSCDESCVP Exiting ..............." + updateSMSCDESCVP.toString());
        }
        return updateSMSCDESCVP.toString();
    }

    /**
     * This method is used for update Language File(Unicode File)
     * 
     * @param serviceVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws BTSLBaseException 
     * @throws Exception
     */
    private String updateLangFile(ServicesVO serviceVO, SimProfileVO simProfileVO) throws BTSLBaseException {
    	if(logger.isDebugEnabled()){
        logger.debug("updateLangFile Entering ..............");
    	}
        if (serviceVO.getPosition() < 1 || serviceVO.getPosition() > 5 || serviceVO.getPosition() == 4) {
            throw new BTSLBaseException(this, "updateLangFile", "This Option is not valid for updateLangFile (0-5) except 4");
        }
        StringBuffer updateLangFile = new StringBuffer();
       
        try {
            switch (serviceVO.getPosition()) {
            case 1:
                updateLangFile.append("0" + ByteCodeGeneratorI.APPLETTITLE01);
                /*
                 * length = ByteCodeGeneratorI.APPLETTITLE.length()/2;
                 * if(length>21)
                 * throw new Exception(
                 * "updateLangFile :: Exception :: Plz check interface file as the Update File parameter exceeds limit of 20 bytes"
                 * );
                 * updateLangFile.append(ByteCodeGeneratorI.APPLETTITLE);
                 */
                break;
            case 2:
                updateLangFile.append("0" + ByteCodeGeneratorI.LANGMENU02);
                /*
                 * length = ByteCodeGeneratorI.LANGMENU.length()/2;
                 * if(length>21)
                 * throw new Exception(
                 * "updateLangFile :: Exception :: Plz check interface file as the Update File parameter exceeds limit of 20 bytes"
                 * );
                 * //updateLangFile.append(SimUtil.lengthConverter(length));
                 * updateLangFile.append(ByteCodeGeneratorI.LANGMENU);
                 */
                break;
            case 3:
                updateLangFile.append("0" + ByteCodeGeneratorI.SMSDISPLAY03);
                /*
                 * length = ByteCodeGeneratorI.SMSDISPLAY.length()/2;
                 * if(length>21)
                 * throw new Exception(
                 * "updateLangFile :: Exception :: Plz check interface file as the Update File parameter exceeds limit of 20 bytes"
                 * );
                 * //updateLangFile.append(SimUtil.lengthConverter(length));
                 * updateLangFile.append(ByteCodeGeneratorI.SMSDISPLAY);
                 */
                break;
            case 5:
                updateLangFile.append("0" + ByteCodeGeneratorI.LANGUAGENAME05);
                /*
                 * length = ByteCodeGeneratorI.LANGUAGENAME.length()/2;
                 * if(length>21)
                 * throw new Exception(
                 * "updateLangFile :: Exception :: Plz check interface file as the Update File parameter exceeds limit of 20 bytes"
                 * );
                 * //updateLangFile.append(SimUtil.lengthConverter(length));
                 * updateLangFile.append(ByteCodeGeneratorI.LANGUAGENAME);
                 */
                break;
            default: {
                logger.error("updateLangFile Exception..............(Invalid Option)" + serviceVO.getPosition());
            }
            }
            boolean flag = SimUtil.isValid2Lang(serviceVO.getLangMenuData(), simProfileVO);
            if (!flag) {

            	 throw new BTSLBaseException("updateLangFile :: Exception :: Plz check interface file as the Update File parameter isn't correct");
            }
            updateLangFile.append(serviceVO.getLangMenuData());
            logger.debug("updateLangFile param [" + serviceVO.getPosition() + "]  value = " + serviceVO.getLangMenuData());

            updateLangFile.insert(0, SimUtil.lengthConverter(updateLangFile.toString().length() / 2));
            updateLangFile.insert(0, ByteCodeGeneratorI.UPDATELANGFILETAG);
        } catch (Exception e) {
            logger.error("updateLangFile :: Exception :: Invalid Request For updateLangFile", e);
            throw new BTSLBaseException(this, "updateLangFile","updateLangFile :: Exception :: Invalid Request For updateLangFile");
        }
       
        SimUtil.finalCheck(updateLangFile.toString());
        if(logger.isDebugEnabled()){
        logger.debug("updateLangFile Exiting  ::..............." + updateLangFile.toString());
        }
        return updateLangFile.toString();
    }
}
