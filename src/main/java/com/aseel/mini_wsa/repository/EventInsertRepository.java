package com.aseel.mini_wsa.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aseel.mini_wsa.entity.SecurityEventEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository
public class EventInsertRepository {

    @PersistenceContext 
    private EntityManager entityManager;

    @Transactional 
    public void insert(SecurityEventEntity entity){
        entityManager.persist(entity);
        entityManager.flush();

    }

}
