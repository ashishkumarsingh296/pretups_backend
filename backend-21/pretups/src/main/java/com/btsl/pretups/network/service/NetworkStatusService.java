package com.btsl.pretups.network.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.network.businesslogic.NetworkVO;


@Service
public interface NetworkStatusService {

	List<NetworkVO> loadData(String LoginId)throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;
    public String[] statusArray(List<NetworkVO> networkList);
    public boolean processData(NetworkVO networkVO, String loginId, Model model)throws BTSLBaseException;

	

}
