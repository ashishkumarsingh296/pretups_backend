package com.btsl.pretups.model.interfaces;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.ListValueVO;

@Component
@Scope(value = "request")
public class InterfaceVO implements Serializable {

	private String webServiceType;
	private ArrayList<ListValueVO> interfaceCategoryList;
	private String interfaceCategoryType;

	public String getInterfaceCategoryType() {
		return interfaceCategoryType;
	}

	public void setInterfaceCategoryType(String interfaceCategoryType) {
		this.interfaceCategoryType = interfaceCategoryType;
	}

	public ArrayList<ListValueVO> getInterfaceCategoryList() {
		return interfaceCategoryList;
	}

	public void setInterfaceCategoryList(ArrayList<ListValueVO> interfaceCategoryList) {
		this.interfaceCategoryList = interfaceCategoryList;
	}

	public String getWebServiceType() {
		return webServiceType;
	}

	public void setWebServiceType(String webServiceType) {
		this.webServiceType = webServiceType;
	}

}
