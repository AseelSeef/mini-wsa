package com.aseel.mini_wsa.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.aseel.mini_wsa.entity.SecurityEventEntity;
import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository 
public class EventSamplesRepository {

    @PersistenceContext 
    private EntityManager entityManager;


   public long count(
        Integer configId, Instant from, Instant to,
        AttackCategory category, Action action) {

        String jpql = "SELECT COUNT(e) FROM SecurityEventEntity e"
            + buildWhere(configId, from, to, category, action);

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);

        bindParameters(query, configId, from, to, category, action);

        return query.getSingleResult();
    }

    public List<SecurityEventEntity> findSamples(Integer configId, Instant from, Instant to, AttackCategory category,
            Action action, int limit, int offset) {

        String jpql = "SELECT e FROM SecurityEventEntity e"
                + buildWhere(configId, from, to, category, action)
                + " ORDER BY e.timestamp DESC, e.eventId ASC";

        TypedQuery<SecurityEventEntity> query = entityManager.createQuery(jpql, SecurityEventEntity.class);

        bindParameters(query, configId, from, to, category, action);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();

    }

    private String buildWhere(Integer configId, Instant from, Instant to, AttackCategory category, Action action) {

        StringBuilder where = new StringBuilder(" WHERE 1 = 1");

        if (configId != null)
            where.append(" AND e.configId = :configId");
        if (from != null)
            where.append(" AND e.timestamp >= :from");
        if (to != null)
            where.append(" AND e.timestamp < :to");
        if (category != null)
            where.append(" AND e.ruleCategory = :category");
        if (action != null)
            where.append(" AND e.action = :action");

        return where.toString();
    }

    private void bindParameters(
        Query query, Integer configId, Instant from, Instant to,
        AttackCategory category, Action action) {

        if (configId != null) query.setParameter("configId", configId);
        if (from != null) query.setParameter("from", from);
        if (to != null) query.setParameter("to", to);
        if (category != null) query.setParameter("category", category);
        if (action != null) query.setParameter("action", action);
    }


}
