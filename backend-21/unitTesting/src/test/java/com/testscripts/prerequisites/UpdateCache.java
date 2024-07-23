package com.testscripts.prerequisites;

import org.testng.annotations.Test;

import com.Features.CacheUpdate;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.CacheController;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

// @author lokesh.kontey
@ModuleManager(name = Module.PREREQUISITE_UPDATECACHE)
public class UpdateCache extends BaseTest {

    @Test
    @TestManager(TestKey = "PRETUPS-863")
    public void Test_UpdateCache() {
        final String methodName = "Test_UpdateCache";
        Log.startTestCase(methodName);

        CacheUpdate cacheupdate = new CacheUpdate(driver);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PUPDATECACHE");
        currentNode = test.createNode(CaseMaster1.getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
        cacheupdate.updateCache(CacheController.CacheI.TransferRulesCache(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT(), CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.COMMISSION_PROFILE(), CacheController.CacheI.PreferenceCache());

        Log.endTestCase(methodName);
    }

    public void updateCache() {
        CacheUpdate cacheupdate = new CacheUpdate(driver);
        cacheupdate.updateCache(CacheController.CacheI.TransferRulesCache(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT(), CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.COMMISSION_PROFILE(), CacheController.CacheI.PreferenceCache());
    }
}
