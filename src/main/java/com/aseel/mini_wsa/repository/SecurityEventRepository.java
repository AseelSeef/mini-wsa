package com.aseel.mini_wsa.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aseel.mini_wsa.entity.SecurityEventEntity;

public interface SecurityEventRepository extends JpaRepository<SecurityEventEntity, String>{

    long countByClientIpAndTimestampBetween(String clientIp, Instant from, Instant to);

}
