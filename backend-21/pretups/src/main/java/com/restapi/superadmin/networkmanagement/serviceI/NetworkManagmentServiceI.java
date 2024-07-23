package com.restapi.superadmin.networkmanagement.serviceI;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.restapi.superadmin.networkmanagement.requestVO.ModifyNetworkRequestVO;
import com.restapi.superadmin.networkmanagement.responseVO.NetworkListResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public interface NetworkManagmentServiceI {

    /**
     * @param con
     * @param loginId
     * @param responseSwag
     * @return
     * @throws BTSLBaseException
     * @throws SQLException
     */


    public NetworkListResponseVO viewNetworkList(Connection con, String loginId, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;


    /**
     * @param con
     * @param loginId
     * @param responseSwag
     * @return
     * @throws BTSLBaseException
     * @throws SQLException
     */


    public NetworkListResponseVO viewNetworkStatusList(Connection con, String loginId, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;


    /**
     * @param con
     * @param loginId
     * @param responseSwag
     * @param networkList
     * @return
     * @throws BTSLBaseException
     * @throws SQLException
     */

    public BaseResponse modifyNetworkDetails(Connection con, String loginId, ModifyNetworkRequestVO requestVO, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;


    /**
     * @param con
     * @param loginId
     * @param responseSwag
     * @param networkDetail
     * @return
     * @throws BTSLBaseException
     * @throws SQLException
     */

    public BaseResponse modifyNetworkDetail(Connection con, String loginId, NetworkVO requestVO, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;

    /**
     * @param con
     * @param responseSwag
     * @return
     * @throws BTSLBaseException
     * @throws SQLException
     */

    public BaseResponse loadServiceSetList(Connection con, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;


    /**
     * @param con
     * @param loginId
     * @param responseSwag
     * @param networkDetail
     * @return
     * @throws Integer
     * @throws SQLException
     */

    public int updateNetworkDetail(Connection con, String loginId, NetworkVO requestVO, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;


}
