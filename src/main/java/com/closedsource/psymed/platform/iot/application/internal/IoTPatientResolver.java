package com.closedsource.psymed.platform.iot.application.internal;

import com.closedsource.psymed.platform.iam.domain.model.valueobjects.Roles;
import com.closedsource.psymed.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import com.closedsource.psymed.platform.profiles.domain.model.queries.GetPatientProfileByAccountIdQuery;
import com.closedsource.psymed.platform.profiles.domain.model.valueobjects.AccountId;
import com.closedsource.psymed.platform.profiles.domain.services.PatientProfileQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IoTPatientResolver {

    private final AccountRepository accountRepository;
    private final PatientProfileQueryService patientProfileQueryService;

    public IoTPatientResolver(
        AccountRepository accountRepository,
        PatientProfileQueryService patientProfileQueryService
    ) {
        this.accountRepository = accountRepository;
        this.patientProfileQueryService = patientProfileQueryService;
    }

    public Long resolveAuthenticatedPatientId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        var username = authentication.getName();
        var account = accountRepository.findByUserName(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not found"));

        if (account.getRole() != Roles.ROLE_PATIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients can claim IoT devices");
        }

        var profileQuery = new GetPatientProfileByAccountIdQuery(new AccountId(account.getId()));
        return patientProfileQueryService.handle(profileQuery)
            .map(profile -> profile.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient profile not found"));
    }
}
