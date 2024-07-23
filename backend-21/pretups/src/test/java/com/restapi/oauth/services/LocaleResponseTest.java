package com.restapi.oauth.services;

import static org.junit.Assert.assertSame;

import com.btsl.user.businesslogic.LocaleMasterModal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LocaleResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LocaleResponse}
     *   <li>{@link LocaleResponse#setListlocaleMaster(List)}
     *   <li>{@link LocaleResponse#getListlocaleMaster()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LocaleResponse actualLocaleResponse = new LocaleResponse();
        ArrayList<LocaleMasterModal> listlocaleMaster = new ArrayList<>();
        actualLocaleResponse.setListlocaleMaster(listlocaleMaster);
        assertSame(listlocaleMaster, actualLocaleResponse.getListlocaleMaster());
    }
}

