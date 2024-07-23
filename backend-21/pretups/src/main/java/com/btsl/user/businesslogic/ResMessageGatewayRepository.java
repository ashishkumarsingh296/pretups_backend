package com.btsl.user.businesslogic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.btsl.user.businesslogic.entity.ResMessageGateway;



/**
 * Repository of ResMessageGatewayRepository interface.
 *
 * @author VENKATESAN.S
 */
@Repository
public interface ResMessageGatewayRepository extends JpaRepository<ResMessageGateway, String> {

    @Query("SELECT RMG FROM ResMessageGateway RMG WHERE RMG.gatewayCode=:gatewayCode")
    ResMessageGateway getDataById(@Param("gatewayCode") String gatewayCode);
}
