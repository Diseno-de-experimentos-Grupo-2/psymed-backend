package com.closedsource.psymed.platform.patientreport.interfaces.rest.transform;

import com.closedsource.psymed.platform.patientreport.domain.model.aggregates.BiologicalFunction;
import com.closedsource.psymed.platform.patientreport.interfaces.rest.resources.BiologicalFunctionResource;

public class BiologicalFunctionResourceFromEntityAssembler {
    public static BiologicalFunctionResource toResourceFromEntity(BiologicalFunction entity) {
        String createdAt = entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant().toString() : null;
        return new BiologicalFunctionResource(entity.getId(), entity.getHunger()
                , entity.getHydration(), entity.getSleep(), entity.getEnergy(), createdAt);
    }
}
