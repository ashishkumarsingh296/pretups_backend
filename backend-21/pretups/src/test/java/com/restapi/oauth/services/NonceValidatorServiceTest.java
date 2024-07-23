package com.restapi.oauth.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {NonceValidatorService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NonceValidatorServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @MockBean
    private NonceRecordRepository nonceRecordRepository;

    @Autowired
    private NonceValidatorService nonceValidatorService;

    /**
     * Method under test: {@link NonceValidatorService#validateNonce(String)}
     */
    @Test
    public void testValidateNonce() {
        NonceRecord nonceRecord = new NonceRecord();
        nonceRecord.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        nonceRecord.setNonceId("42");
        Optional<NonceRecord> ofResult = Optional.of(nonceRecord);
        when(nonceRecordRepository.findById(Mockito.<String>any())).thenReturn(ofResult);
        thrown.expect(InvalidNonceException.class);
        nonceValidatorService.validateNonce("Nonce");
        verify(nonceRecordRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test: {@link NonceValidatorService#validateNonce(String)}
     */
    @Test
    public void testValidateNonce2() {
        NonceRecord nonceRecord = new NonceRecord();
        nonceRecord.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        nonceRecord.setNonceId("42");
        when(nonceRecordRepository.save(Mockito.<NonceRecord>any())).thenReturn(nonceRecord);
        when(nonceRecordRepository.findById(Mockito.<String>any())).thenReturn(Optional.empty());
        nonceValidatorService.validateNonce("Nonce");
        verify(nonceRecordRepository).save(Mockito.<NonceRecord>any());
        verify(nonceRecordRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test: {@link NonceValidatorService#validateNonce(String)}
     */
    @Test
    public void testValidateNonce3() {
        when(nonceRecordRepository.findById(Mockito.<String>any())).thenThrow(new InvalidNonceException());
        thrown.expect(InvalidNonceException.class);
        nonceValidatorService.validateNonce("Nonce");
        verify(nonceRecordRepository).findById(Mockito.<String>any());
    }
}

