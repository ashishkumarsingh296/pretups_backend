package com.testscripts.uap;

import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkInterface;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class UAP_NetworkInterface extends BaseTest {

	String assignCategory="UAP";
	@SuppressWarnings({ "unused" })
	@DataProvider(name = "interfaceData")
	public Object[][] getDataObjects() {
		String network = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		//QueryRepository queryRepository = new QueryRepository();
		RandomGeneration randomGenerator = new RandomGeneration();
		List<String> interfaces = DBHandler.AccessHandler.getInterfaceList(network);
		Object[][] dataObject = new Object[1][6];
		for (int i = 0; i < interfaces.size(); i++) {
			String[] spiltValues = interfaces.get(i).split(",,");
			dataObject[i][0] = spiltValues[0];
			dataObject[i][1] = spiltValues[1];
			dataObject[i][2] = randomGenerator.randomNumberWithoutZero(2);
			dataObject[i][3] = randomGenerator.randomNumberWithoutZero(3);
			dataObject[i][4] = randomGenerator.randomNumberWithoutZero(3);
			dataObject[i][5] = randomGenerator.randomNumberWithoutZero(3);
			break;
		}
		return dataObject;
	}

	@Test(dataProvider = "interfaceData")
	public void a_addNetworkInterface(String interfaceCategory, String interfaceName, String queueSize,
			String queueTimeOut, String requestTimeOut, String nextQueueRetryInterval) {
		
		Log.startTestCase(this.getClass().getName());
		test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("UNETINTERFACE1").getModuleCode());
		NetworkInterface networkInterface = new NetworkInterface(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETINTERFACE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkInterface.addNetworkInterface(interfaceCategory, interfaceName, queueSize, queueTimeOut,
				requestTimeOut, nextQueueRetryInterval);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETINTERFACE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String addSuccessMsg = MessagesDAO
				.prepareMessageByKey("interfaces.interfacenetwrokmappingdetail.successaddmessage");
		if (addSuccessMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + addSuccessMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	@Test(dataProvider = "interfaceData")
	public void b_modifyNetworkInterface(String interfaceCategory, String interfaceName, String queueSize,
			String queueTimeOut, String requestTimeOut, String nextQueueRetryInterval) {
		
		Log.startTestCase(this.getClass().getName());
		NetworkInterface networkInterface = new NetworkInterface(driver);
		RandomGeneration randomGenerator = new RandomGeneration();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETINTERFACE3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		networkInterface.addNetworkInterface(interfaceCategory, interfaceName, queueSize, queueTimeOut, requestTimeOut,
				nextQueueRetryInterval);

		String newQueueSize = randomGenerator.randomNumberWithoutZero(2);
		String newQueueTimeout = randomGenerator.randomNumberWithoutZero(3);
		String newRequestTimeout = randomGenerator.randomNumberWithoutZero(3);
		String newQueueRetryInterval = randomGenerator.randomNumberWithoutZero(3);

		String message = networkInterface.modifyNetworkInterface(interfaceCategory, interfaceName, queueSize,
				queueTimeOut, requestTimeOut, nextQueueRetryInterval, newQueueSize, newQueueTimeout, newRequestTimeout,
				newQueueRetryInterval);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETINTERFACE4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String modifySuccessMsg = MessagesDAO
				.prepareMessageByKey("interfaces.interfacenetwrokmappingdetail.successeditmessage");
		if (modifySuccessMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + modifySuccessMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	@Test(dataProvider = "interfaceData")
	public void c_deleteNetworkInterface(String interfaceCategory, String interfaceName, String queueSize,
			String queueTimeOut, String requestTimeOut, String nextQueueRetryInterval) {
		
		Log.startTestCase(this.getClass().getName());
		NetworkInterface networkInterface = new NetworkInterface(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETINTERFACE5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		networkInterface.addNetworkInterface(interfaceCategory, interfaceName, queueSize, queueTimeOut, requestTimeOut,
				nextQueueRetryInterval);

		String message = networkInterface.deleteNetworkInterface(interfaceCategory, interfaceName, queueSize,
				queueTimeOut, requestTimeOut, nextQueueRetryInterval);

		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("UNETINTERFACE6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String deleteSuccessMsg = MessagesDAO
				.prepareMessageByKey("interfaces.interfacenetwrokmappingdetail.deletesuccess");
		if (deleteSuccessMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + deleteSuccessMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

}
