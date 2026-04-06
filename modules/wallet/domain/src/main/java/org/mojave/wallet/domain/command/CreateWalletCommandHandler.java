package org.mojave.wallet.domain.command;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.mojave.wallet.domain.model.Wallet;
import org.mojave.wallet.domain.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateWalletCommandHandler implements CreateWalletCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateWalletCommandHandler.class);

    private final WalletRepository walletRepository;

    private final WalletEngine walletEngine;

    public CreateWalletCommandHandler(final WalletRepository walletRepository,
                                      final WalletEngine walletEngine) {

        Objects.requireNonNull(walletRepository);
        Objects.requireNonNull(walletEngine);

        this.walletRepository = walletRepository;
        this.walletEngine = walletEngine;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CreateWalletCommand : input: ({})", ObjectLogger.log(input));

        final var spec = WalletRepository.Filters
                             .withOwnerId(input.walletOwnerId())
                             .and(WalletRepository.Filters.withCurrency(input.currency()))
                             .and(WalletRepository.Filters.withScenario(input.scenario()));

        final var existing = this.walletRepository.findOne(spec).orElse(null);

        if (existing != null) {

            final var output = new Output(existing.getId());
            LOGGER.info("CreateWalletCommand : output: ({})", ObjectLogger.log(output));

            return output;
        }

        final var wallet = new Wallet(
            input.walletOwnerId(), input.currency(), input.scenario(), input.name());

        final var saved = this.walletRepository.save(wallet);

        try {
            this.walletEngine.createWallet(
                saved.getId(), saved.getCurrency(),
                saved.getCurrency().getScale(), saved.getScenario());

        } catch (final WalletEngine.WalletIdAlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        final var output = new Output(saved.getId());

        LOGGER.info("CreateWalletCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}
