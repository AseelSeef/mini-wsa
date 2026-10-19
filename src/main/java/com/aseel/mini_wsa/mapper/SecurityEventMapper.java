package com.aseel.mini_wsa.mapper;

import org.springframework.stereotype.Component;

import com.aseel.mini_wsa.dto.EnrichedSecurityEvent;
import com.aseel.mini_wsa.entity.SecurityEventEntity;
import com.aseel.mini_wsa.model.GeoLocation;
import com.aseel.mini_wsa.model.SecurityEvent;
import com.aseel.mini_wsa.model.SecurityRule;



@Component 
public class SecurityEventMapper {

    public SecurityEventEntity toEntity(SecurityEvent event){
        SecurityEventEntity entity = new SecurityEventEntity();
        entity.setEventId(event.getEventId());
        entity.setTimestamp(event.getTimestamp());
        entity.setConfigId(event.getConfigId());
        entity.setPolicyId(event.getPolicyId());
        entity.setClientIp(event.getClientIp());
        entity.setHostname(event.getHostname());
        entity.setPath(event.getPath());
        entity.setMethod(event.getMethod());
        entity.setStatusCode(event.getStatusCode());
        entity.setUserAgent(event.getUserAgent());
        entity.setRuleCategory(event.getRule().getCategory());
        entity.setAction(event.getAction());
        entity.setGeoCity(event.getGeoLocation()!=null ? event.getGeoLocation().getCity() : null);
        entity.setGeoCountry(event.getGeoLocation()!=null ? event.getGeoLocation().getCountry() : null);
        entity.setRuleId(event.getRule().getId());
        entity.setRuleName(event.getRule().getName());
        entity.setRuleMessage(event.getRule().getMessage());
        entity.setRuleSeverity(event.getRule().getSeverity());
        entity.setRequestSize(event.getRequestSize());
        entity.setResponseSize(event.getResponseSize());
        return entity;
    }

    public EnrichedSecurityEvent toResponse(SecurityEventEntity entity) {
        EnrichedSecurityEvent response = new EnrichedSecurityEvent();

        response.setEventId(entity.getEventId());
        response.setTimestamp(entity.getTimestamp());
        response.setConfigId(entity.getConfigId());
        response.setPolicyId(entity.getPolicyId());
        response.setClientIp(entity.getClientIp());
        response.setHostname(entity.getHostname());
        response.setPath(entity.getPath());
        response.setMethod(entity.getMethod());
        response.setStatusCode(entity.getStatusCode());
        response.setUserAgent(entity.getUserAgent());
        response.setAction(entity.getAction());
        response.setRequestSize(entity.getRequestSize());
        response.setResponseSize(entity.getResponseSize());

        response.setRule(new SecurityRule(
            entity.getRuleId(),
            entity.getRuleName(),
            entity.getRuleMessage(),
            entity.getRuleSeverity(),
            entity.getRuleCategory()
        ));

        if (entity.getGeoCountry() != null || entity.getGeoCity() != null) {
            response.setGeoLocation(new GeoLocation(
                entity.getGeoCountry(),
                entity.getGeoCity()
            ));
        }

        response.setAttackType(entity.getAttackType());
        response.setThreatScore(entity.getThreatScore());
        response.setReceivedAt(entity.getReceivedAt());

        return response;
    }
}
