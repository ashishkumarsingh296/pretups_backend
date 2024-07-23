package com.restapi.oauth.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NonceRecordRepository extends JpaRepository<NonceRecord,String> {
}
