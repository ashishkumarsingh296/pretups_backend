package com.restapi.networkadmin.loyaltymanagement.responseVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagesPofileDetailsResponseVO {
    private List<MessagesPofileDetailsResponseVO> messagesDetailsList;
}
