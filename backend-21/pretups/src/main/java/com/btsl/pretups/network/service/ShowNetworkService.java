package com.btsl.pretups.network.service;

import java.io.IOException;

import com.btsl.pretups.network.businesslogic.NetworkVO;

public interface ShowNetworkService {

	NetworkVO showData(String networkCode) throws IOException;

}
