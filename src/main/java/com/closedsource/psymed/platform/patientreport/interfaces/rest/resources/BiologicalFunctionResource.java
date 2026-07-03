package com.closedsource.psymed.platform.patientreport.interfaces.rest.resources;

public record BiologicalFunctionResource(Long id, Integer hunger,
                                         Integer hydration, Integer sleep,
                                         Integer energy, String createdAt) {

}
