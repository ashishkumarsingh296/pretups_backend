package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UpdateCategoryOnlyRespTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateCategoryOnlyResp}
     *   <li>{@link UpdateCategoryOnlyResp#setAgentAllowedTicked(boolean)}
     *   <li>{@link UpdateCategoryOnlyResp#setAgentExistUnderCategory(boolean)}
     *   <li>{@link UpdateCategoryOnlyResp#isAgentAllowedTicked()}
     *   <li>{@link UpdateCategoryOnlyResp#isAgentExistUnderCategory()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateCategoryOnlyResp actualUpdateCategoryOnlyResp = new UpdateCategoryOnlyResp();
        actualUpdateCategoryOnlyResp.setAgentAllowedTicked(true);
        actualUpdateCategoryOnlyResp.setAgentExistUnderCategory(true);
        assertTrue(actualUpdateCategoryOnlyResp.isAgentAllowedTicked());
        assertTrue(actualUpdateCategoryOnlyResp.isAgentExistUnderCategory());
    }
}

