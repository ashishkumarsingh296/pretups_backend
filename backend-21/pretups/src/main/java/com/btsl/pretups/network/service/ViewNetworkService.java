package com.btsl.pretups.network.service;

import java.io.IOException;
import java.util.List;

import com.btsl.pretups.network.businesslogic.NetworkVO;



public interface ViewNetworkService {

    List<NetworkVO> loadData(String LoginId,String status,String networkCode) throws IOException;

}
