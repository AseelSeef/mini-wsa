package com.aseel.mini_wsa.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aseel.mini_wsa.entity.SecurityEventEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecurityEventRepository extends JpaRepository<SecurityEventEntity, String>{

    long countByClientIpAndTimestampBetween(String clientIp, Instant from, Instant to);

    @Query("""
        SELECT COUNT(e)
        FROM SecurityEventEntity e
        WHERE (:configId IS NULL OR e.configId = :configId)
        AND e.timestamp >= :from
        AND e.timestamp < :to
        """)
    long countForSummary(@Param("configId") Integer configId,@Param("from") Instant from,@Param("to") Instant to);

    @Query("""
        SELECT e.ruleCategory, COUNT(e), AVG(e.threatScore)
        FROM SecurityEventEntity e
        WHERE (:configId IS NULL OR e.configId = :configId)
        AND e.timestamp >= :from
        AND e.timestamp < :to
        GROUP BY e.ruleCategory
        """)
    List<Object[]> summarizeCategories(@Param("configId") Integer configId,@Param("from") Instant from,@Param("to") Instant to);

     @Query("""
        SELECT e.action, COUNT(e)
        FROM SecurityEventEntity e
        WHERE (:configId IS NULL OR e.configId = :configId)
        AND e.timestamp >= :from
        AND e.timestamp < :to
        GROUP BY e.action
        """)
    List<Object[]> summarizeActions(@Param("configId") Integer configId,@Param("from") Instant from,@Param("to") Instant to);

    @Query("""
        SELECT e.clientIp, COUNT(e), AVG(e.threatScore)
        FROM SecurityEventEntity e
        WHERE (:configId IS NULL OR e.configId = :configId)
        AND e.timestamp >= :from
        AND e.timestamp < :to
        GROUP BY e.clientIp
        ORDER BY COUNT(e) DESC, e.clientIp ASC
        """)
    List<Object[]> findTopAttackers(@Param("configId") Integer configId,@Param("from") Instant from,@Param("to") Instant to, Pageable pageable);

    @Query("""
        SELECT e.path, COUNT(e)
        FROM SecurityEventEntity e
        WHERE (:configId IS NULL OR e.configId = :configId)
        AND e.timestamp >= :from
        AND e.timestamp < :to
        GROUP BY e.path
        ORDER BY COUNT(e) DESC, e.path ASC
        """)
    List<Object[]> findTopTargetedPaths(@Param("configId") Integer configId,@Param("from") Instant from,@Param("to") Instant to, Pageable pageable);

}
