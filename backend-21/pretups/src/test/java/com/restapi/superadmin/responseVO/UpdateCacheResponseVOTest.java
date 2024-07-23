package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.pretups.common.CacheVO;

import java.util.ArrayList;

import org.junit.Test;

public class UpdateCacheResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateCacheResponseVO}
     *   <li>{@link UpdateCacheResponseVO#setCacheList(ArrayList)}
     *   <li>{@link UpdateCacheResponseVO#setCountF(int)}
     *   <li>{@link UpdateCacheResponseVO#setCountS(int)}
     *   <li>{@link UpdateCacheResponseVO#setFileAttachment(String)}
     *   <li>{@link UpdateCacheResponseVO#setFileName(String)}
     *   <li>{@link UpdateCacheResponseVO#setInstanceList(ArrayList)}
     *   <li>{@link UpdateCacheResponseVO#setMsg(String[])}
     *   <li>{@link UpdateCacheResponseVO#setMsgF(String[])}
     *   <li>{@link UpdateCacheResponseVO#setRedis(String)}
     *   <li>{@link UpdateCacheResponseVO#toString()}
     *   <li>{@link UpdateCacheResponseVO#getCacheList()}
     *   <li>{@link UpdateCacheResponseVO#getCountF()}
     *   <li>{@link UpdateCacheResponseVO#getCountS()}
     *   <li>{@link UpdateCacheResponseVO#getFileAttachment()}
     *   <li>{@link UpdateCacheResponseVO#getFileName()}
     *   <li>{@link UpdateCacheResponseVO#getInstanceList()}
     *   <li>{@link UpdateCacheResponseVO#getMsg()}
     *   <li>{@link UpdateCacheResponseVO#getMsgF()}
     *   <li>{@link UpdateCacheResponseVO#getRedis()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateCacheResponseVO actualUpdateCacheResponseVO = new UpdateCacheResponseVO();
        ArrayList<CacheVO> cacheList = new ArrayList<>();
        actualUpdateCacheResponseVO.setCacheList(cacheList);
        actualUpdateCacheResponseVO.setCountF(3);
        actualUpdateCacheResponseVO.setCountS(3);
        actualUpdateCacheResponseVO.setFileAttachment("File Attachment");
        actualUpdateCacheResponseVO.setFileName("foo.txt");
        ArrayList<InstanceLoadVO> instanceList = new ArrayList<>();
        actualUpdateCacheResponseVO.setInstanceList(instanceList);
        String[] msg = new String[]{"Msg"};
        actualUpdateCacheResponseVO.setMsg(msg);
        String[] msgF = new String[]{"Msg F"};
        actualUpdateCacheResponseVO.setMsgF(msgF);
        actualUpdateCacheResponseVO.setRedis("Redis");
        String actualToStringResult = actualUpdateCacheResponseVO.toString();
        assertSame(cacheList, actualUpdateCacheResponseVO.getCacheList());
        assertEquals(3, actualUpdateCacheResponseVO.getCountF());
        assertEquals(3, actualUpdateCacheResponseVO.getCountS());
        assertEquals("File Attachment", actualUpdateCacheResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualUpdateCacheResponseVO.getFileName());
        assertSame(instanceList, actualUpdateCacheResponseVO.getInstanceList());
        assertSame(msg, actualUpdateCacheResponseVO.getMsg());
        assertSame(msgF, actualUpdateCacheResponseVO.getMsgF());
        assertEquals("Redis", actualUpdateCacheResponseVO.getRedis());
        assertEquals("UpdateCacheResponseVO [instanceList=[]]UpdateCacheResponseVO [cacheList=[]]", actualToStringResult);
    }
}

