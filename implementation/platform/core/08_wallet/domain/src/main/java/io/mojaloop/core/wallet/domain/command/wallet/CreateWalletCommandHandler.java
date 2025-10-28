package io.mojaloop.core.wallet.domain.command.wallet;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.wallet.contract.command.wallet.CreateWalletCommand;
import io.mojaloop.core.wallet.contract.exception.wallet.WalletAlreadyExistsException;
import io.mojaloop.core.wallet.domain.model.Wallet;
import io.mojaloop.core.wallet.domain.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWalletCommandHandler implements CreateWalletCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateWalletCommandHandler.class);

    private final WalletRepository walletRepository;

    public CreateWalletCommandHandler(WalletRepository walletRepository) {

        assert walletRepository != null;
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing CreateWalletCommand with input: {}", input);

        var spec = WalletRepository.Filters
                       .withOwnerId(input.walletOwnerId())
                       .and(WalletRepository.Filters.withCurrency(input.currency()));

        if (this.walletRepository.findOne(spec).isPresent()) {
            LOGGER.info("Wallet already exists for ownerId: {} and currency: {}", input.walletOwnerId(),
                        input.currency());
            throw new WalletAlreadyExistsException(input.walletOwnerId(), input.currency());
        }

        var wallet = new Wallet(input.walletOwnerId(), input.currency(), input.name());
        LOGGER.info("Created Wallet: {}", wallet);

        wallet = this.walletRepository.save(wallet);
        LOGGER.info("Saved Wallet with id: {}", wallet.getId());

        LOGGER.info("Completed CreateWalletCommand with input: {}", input);

        return new Output(wallet.getId());
    }

}
