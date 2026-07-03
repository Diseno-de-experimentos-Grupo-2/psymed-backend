package com.closedsource.psymed.platform.iam.application.internal.commandservices;

import com.closedsource.psymed.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.closedsource.psymed.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.closedsource.psymed.platform.iam.domain.model.aggregate.Account;
import com.closedsource.psymed.platform.iam.domain.model.commands.SignInCommand;
import com.closedsource.psymed.platform.iam.domain.model.commands.SignUpCommand;
import com.closedsource.psymed.platform.iam.domain.service.AccountCommandService;
import com.closedsource.psymed.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {
    private final AccountRepository accountRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public AccountCommandServiceImpl(AccountRepository accountRepository, HashingService hashingService, TokenService tokenService) {
        this.accountRepository = accountRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<Account> handle(SignUpCommand command) {
        if(accountRepository.existsByUserName(command.username()))
            throw new RuntimeException("User already exists");

        var account = new Account(command, hashingService.encode(command.password()));

        try{
            var accountCreated = accountRepository.save(account);
            return Optional.of(accountCreated);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public Optional<ImmutablePair<Account, String>> handle(SignInCommand command) {
        var account = accountRepository.findByUserName(command.username());
        if (account.isEmpty()) {
            return Optional.empty();
        }
        var user = account.get();
        if (!hashingService.matches(command.password(), user.getPassword())) {
            return Optional.empty();
        }
        var token = tokenService.generateToken(user.getUserName());
        return Optional.of(ImmutablePair.of(user, token));
    }
}
