package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class BatchAddUploadCommProVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchAddUploadCommProVO}
     *   <li>{@link BatchAddUploadCommProVO#setAddtnlComStatus(String)}
     *   <li>{@link BatchAddUploadCommProVO#setCategoryCode(String)}
     *   <li>{@link BatchAddUploadCommProVO#setCategoryCodeDesc(String)}
     *   <li>{@link BatchAddUploadCommProVO#setCommissionProfileList(ArrayList)}
     *   <li>{@link BatchAddUploadCommProVO#setDomainList(ArrayList)}
     *   <li>{@link BatchAddUploadCommProVO#setDomainName(String)}
     *   <li>{@link BatchAddUploadCommProVO#setErrorFlag(String)}
     *   <li>{@link BatchAddUploadCommProVO#setErrorList(ArrayList)}
     *   <li>{@link BatchAddUploadCommProVO#setFileName(String)}
     *   <li>{@link BatchAddUploadCommProVO#setLength(int)}
     *   <li>{@link BatchAddUploadCommProVO#setNetworkID(String)}
     *   <li>{@link BatchAddUploadCommProVO#setSequenceNo(String)}
     *   <li>{@link BatchAddUploadCommProVO#setSetID(String)}
     *   <li>{@link BatchAddUploadCommProVO#setSheetName(String)}
     *   <li>{@link BatchAddUploadCommProVO#setShowAdditionalCommissionFlag(String)}
     *   <li>{@link BatchAddUploadCommProVO#setSubServiceCode(String)}
     *   <li>{@link BatchAddUploadCommProVO#setVersion(String)}
     *   <li>{@link BatchAddUploadCommProVO#getAddtnlComStatus()}
     *   <li>{@link BatchAddUploadCommProVO#getCategoryCode()}
     *   <li>{@link BatchAddUploadCommProVO#getCategoryCodeDesc()}
     *   <li>{@link BatchAddUploadCommProVO#getCommissionProfileList()}
     *   <li>{@link BatchAddUploadCommProVO#getDomainList()}
     *   <li>{@link BatchAddUploadCommProVO#getDomainName()}
     *   <li>{@link BatchAddUploadCommProVO#getErrorFlag()}
     *   <li>{@link BatchAddUploadCommProVO#getErrorList()}
     *   <li>{@link BatchAddUploadCommProVO#getFileName()}
     *   <li>{@link BatchAddUploadCommProVO#getLength()}
     *   <li>{@link BatchAddUploadCommProVO#getNetworkID()}
     *   <li>{@link BatchAddUploadCommProVO#getSequenceNo()}
     *   <li>{@link BatchAddUploadCommProVO#getSetID()}
     *   <li>{@link BatchAddUploadCommProVO#getSheetName()}
     *   <li>{@link BatchAddUploadCommProVO#getShowAdditionalCommissionFlag()}
     *   <li>{@link BatchAddUploadCommProVO#getSubServiceCode()}
     *   <li>{@link BatchAddUploadCommProVO#getVersion()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchAddUploadCommProVO actualBatchAddUploadCommProVO = new BatchAddUploadCommProVO();
        actualBatchAddUploadCommProVO.setAddtnlComStatus("Addtnl Com Status");
        actualBatchAddUploadCommProVO.setCategoryCode("Category Code");
        actualBatchAddUploadCommProVO.setCategoryCodeDesc("Category Code Desc");
        ArrayList commissionProfileList = new ArrayList();
        actualBatchAddUploadCommProVO.setCommissionProfileList(commissionProfileList);
        ArrayList domainList = new ArrayList();
        actualBatchAddUploadCommProVO.setDomainList(domainList);
        actualBatchAddUploadCommProVO.setDomainName("Domain Name");
        actualBatchAddUploadCommProVO.setErrorFlag("An error occurred");
        ArrayList errorList = new ArrayList();
        actualBatchAddUploadCommProVO.setErrorList(errorList);
        actualBatchAddUploadCommProVO.setFileName("foo.txt");
        actualBatchAddUploadCommProVO.setLength(3);
        actualBatchAddUploadCommProVO.setNetworkID("Network ID");
        actualBatchAddUploadCommProVO.setSequenceNo("Sequence No");
        actualBatchAddUploadCommProVO.setSetID("Set ID");
        actualBatchAddUploadCommProVO.setSheetName("Sheet Name");
        actualBatchAddUploadCommProVO.setShowAdditionalCommissionFlag("Show Additional Commission Flag");
        actualBatchAddUploadCommProVO.setSubServiceCode("Sub Service Code");
        actualBatchAddUploadCommProVO.setVersion("1.0.2");
        assertEquals("Addtnl Com Status", actualBatchAddUploadCommProVO.getAddtnlComStatus());
        assertEquals("Category Code", actualBatchAddUploadCommProVO.getCategoryCode());
        assertEquals("Category Code Desc", actualBatchAddUploadCommProVO.getCategoryCodeDesc());
        assertSame(commissionProfileList, actualBatchAddUploadCommProVO.getCommissionProfileList());
        assertSame(domainList, actualBatchAddUploadCommProVO.getDomainList());
        assertEquals("Domain Name", actualBatchAddUploadCommProVO.getDomainName());
        assertEquals("An error occurred", actualBatchAddUploadCommProVO.getErrorFlag());
        assertSame(errorList, actualBatchAddUploadCommProVO.getErrorList());
        assertEquals("foo.txt", actualBatchAddUploadCommProVO.getFileName());
        assertEquals(3, actualBatchAddUploadCommProVO.getLength());
        assertEquals("Network ID", actualBatchAddUploadCommProVO.getNetworkID());
        assertEquals("Sequence No", actualBatchAddUploadCommProVO.getSequenceNo());
        assertEquals("Set ID", actualBatchAddUploadCommProVO.getSetID());
        assertEquals("Sheet Name", actualBatchAddUploadCommProVO.getSheetName());
        assertEquals("Show Additional Commission Flag", actualBatchAddUploadCommProVO.getShowAdditionalCommissionFlag());
        assertEquals("Sub Service Code", actualBatchAddUploadCommProVO.getSubServiceCode());
        assertEquals("1.0.2", actualBatchAddUploadCommProVO.getVersion());
    }
}

