package com.restapi.networkadmin.cardgroup.requestVO;

import java.util.List;

import com.restapi.networkadmin.cardgroup.responseVO.VersionDetailsAndDeleteStatusVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardGroupSetVersionListRequestVO {
private List<VersionDetailsAndDeleteStatusVO>	versionVOList;
private String selectCardGroupSetId;
private String selectCardGroupSetVersionId;
}
