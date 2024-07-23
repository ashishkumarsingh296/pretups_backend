package com.restapi.oauth.services;

/*import com.comviva.mfs.common.cryptography.TextEncryptor;
import com.comviva.mfs.common.util.MFSConfiguration;
import com.comviva.mfs.sync.entity.NonceRecord;
import com.comviva.mfs.sync.exception.InvalidNonceException;
import com.comviva.mfs.sync.repository.NonceRecordRepository;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import static java.lang.String.format;


@Service
public class NonceValidatorService {
    @Autowired
    private NonceRecordRepository nonceRecordRepository;

/*    @Autowired
    private MFSConfiguration configuration;
*/
    public void validateNonce(String nonce) {
    
        Optional<NonceRecord> nonceRecord = nonceRecordRepository.findById(nonce);
        if(nonceRecord.isPresent()){
            throw new InvalidNonceException();
        }
        NonceRecord newNonceRecord = new NonceRecord();
        newNonceRecord.setNonceId(nonce);
        newNonceRecord.setCreatedOn(new Date());
        nonceRecordRepository.save(newNonceRecord);
    }

}
