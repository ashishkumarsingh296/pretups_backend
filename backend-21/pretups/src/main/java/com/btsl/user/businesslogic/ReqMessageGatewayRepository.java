package com.btsl.user.businesslogic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.btsl.user.businesslogic.entity.ReqMessageGateway;



/**
 * Repository of ReqMessageGatewayRepository interface.
 *
 * @author VENKATESAN.S
 */
@Repository
public interface ReqMessageGatewayRepository extends JpaRepository<ReqMessageGateway, String> {

    @Query("SELECT RMG FROM ReqMessageGateway RMG WHERE RMG.gatewayCode=:gatewayCode")
    ReqMessageGateway getDataById(@Param("gatewayCode") String gatewayCode);
    
    
}
