package com.btsl.user.businesslogic;

import java.util.HashMap;

import org.springframework.stereotype.Repository;


/**
 * Data base operations for MessageGateway.
 *
 * @author VENKATESAN.S
 * @date : @date : 20-DEC-2019
 */
@Repository
public interface MessageGatewayCustomRepository {

    HashMap loadMessageGatewayCacheMapWithoutGatway() throws VMSBaseException;

    HashMap loadMessageGatewayMappingCache();

}
