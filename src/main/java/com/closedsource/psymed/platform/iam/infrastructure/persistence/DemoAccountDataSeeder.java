package com.closedsource.psymed.platform.iam.infrastructure.persistence;

import com.closedsource.psymed.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.closedsource.psymed.platform.iam.domain.model.aggregate.Account;
import com.closedsource.psymed.platform.iam.domain.model.commands.SignUpCommand;
import com.closedsource.psymed.platform.iam.domain.model.valueobjects.Roles;
import com.closedsource.psymed.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import com.closedsource.psymed.platform.profiles.domain.model.aggregates.PatientProfile;
import com.closedsource.psymed.platform.profiles.domain.model.aggregates.ProfessionalProfile;
import com.closedsource.psymed.platform.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.closedsource.psymed.platform.profiles.domain.model.commands.CreateProfessionalProfileCommand;
import com.closedsource.psymed.platform.profiles.domain.model.valueobjects.AccountId;
import com.closedsource.psymed.platform.profiles.infrastructure.persistence.jpa.repositories.PatientProfileRepository;
import com.closedsource.psymed.platform.profiles.infrastructure.persistence.jpa.repositories.ProfessionalProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds demo accounts for local/Docker development so login works after a fresh database.
 */
@Component
public class DemoAccountDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoAccountDataSeeder.class);

    private static final String DEMO_PROFESSIONAL_USERNAME = "pro";
    private static final String DEMO_PROFESSIONAL_PASSWORD = "propropro";
    private static final String DEMO_PATIENT_USERNAME = "dina";
    private static final String DEMO_PATIENT_PASSWORD = "dinadina";

    private final AccountRepository accountRepository;
    private final HashingService hashingService;
    private final ProfessionalProfileRepository professionalProfileRepository;
    private final PatientProfileRepository patientProfileRepository;

    public DemoAccountDataSeeder(
            AccountRepository accountRepository,
            HashingService hashingService,
            ProfessionalProfileRepository professionalProfileRepository,
            PatientProfileRepository patientProfileRepository
    ) {
        this.accountRepository = accountRepository;
        this.hashingService = hashingService;
        this.professionalProfileRepository = professionalProfileRepository;
        this.patientProfileRepository = patientProfileRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Long professionalProfileId = seedProfessional();
        seedPatient(professionalProfileId);
    }

    private Long seedProfessional() {
        if (accountRepository.existsByUserName(DEMO_PROFESSIONAL_USERNAME)) {
            return accountRepository.findByUserName(DEMO_PROFESSIONAL_USERNAME)
                    .flatMap(acc -> professionalProfileRepository.findByAccountId(new AccountId(acc.getId())))
                    .map(ProfessionalProfile::getId)
                    .orElse(null);
        }

        var command = new CreateProfessionalProfileCommand(
                "Demo", "Professional", "Main St", "Lima", "Peru",
                "pro@psymed.demo", DEMO_PROFESSIONAL_USERNAME, DEMO_PROFESSIONAL_PASSWORD
        );
        var account = saveAccount(DEMO_PROFESSIONAL_USERNAME, DEMO_PROFESSIONAL_PASSWORD, Roles.ROLE_PROFESSIONAL);
        var profile = new ProfessionalProfile(command, new AccountId(account.getId()));
        professionalProfileRepository.save(profile);
        log.info("Seeded demo professional account '{}' / '{}'", DEMO_PROFESSIONAL_USERNAME, DEMO_PROFESSIONAL_PASSWORD);
        return profile.getId();
    }

    private void seedPatient(Long professionalProfileId) {
        if (accountRepository.existsByUserName(DEMO_PATIENT_USERNAME)) {
            return;
        }

        var command = new CreatePatientProfileCommand(
                "Dina", "Demo", "Main St", "Lima", "Peru",
                "dina@psymed.demo", DEMO_PATIENT_USERNAME, DEMO_PATIENT_PASSWORD, professionalProfileId
        );
        var account = saveAccount(DEMO_PATIENT_USERNAME, DEMO_PATIENT_PASSWORD, Roles.ROLE_PATIENT);
        var profile = new PatientProfile(command, new AccountId(account.getId()));
        patientProfileRepository.save(profile);
        log.info("Seeded demo patient account '{}' / '{}'", DEMO_PATIENT_USERNAME, DEMO_PATIENT_PASSWORD);
    }

    private Account saveAccount(String username, String password, Roles role) {
        var signUp = new SignUpCommand(username, password, role.toString());
        return accountRepository.save(new Account(signUp, hashingService.encode(password)));
    }
}
