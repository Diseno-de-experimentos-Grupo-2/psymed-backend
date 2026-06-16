package com.closedsource.psymed.platform.iot.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ClaimDeviceResource(
    @JsonProperty("device_id")
    @JsonAlias("deviceId")
    String deviceId,
    @JsonProperty("pairing_code")
    @JsonAlias("pairingCode")
    String pairingCode
) {}
