package com.restapi.networkadmin.repositary;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class NTWTransferWebDAOTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link NTWTransferWebDAO#addPromotionalTransferRuleFile(Connection, ArrayList, ArrayList, Locale, String, String, String)}
     */
    @Test
    public void testAddPromotionalTransferRuleFile() throws BTSLBaseException, SQLException {
        NTWTransferWebDAO ntwTransferWebDAO = new NTWTransferWebDAO();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        ArrayList p_transferRuleList = new ArrayList();
        ArrayList p_errorVoList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        ntwTransferWebDAO.addPromotionalTransferRuleFile (com.btsl.util.JUnitConfig.getConnection(), p_transferRuleList, p_errorVoList, Locale.getDefault(),
                "P promotion Level", "P category", "Geodomain Cd");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).close();
    }

    /**
     * Method under test: {@link NTWTransferWebDAO#addPromotionalTransferRuleFile(Connection, ArrayList, ArrayList, Locale, String, String, String)}
     */
    @Test
    public void testAddPromotionalTransferRuleFile2() throws BTSLBaseException, SQLException {
        NTWTransferWebDAO ntwTransferWebDAO = new NTWTransferWebDAO();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        ArrayList p_transferRuleList = new ArrayList();
        ArrayList p_errorVoList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        ntwTransferWebDAO.addPromotionalTransferRuleFile (com.btsl.util.JUnitConfig.getConnection(), p_transferRuleList, p_errorVoList, Locale.getDefault(),
                "P promotion Level", "P category", "Geodomain Cd");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).close();
    }
}

