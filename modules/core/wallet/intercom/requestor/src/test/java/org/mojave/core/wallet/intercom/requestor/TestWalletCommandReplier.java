package org.mojave.core.wallet.intercom.requestor;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.nats.CommandResponse;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.core.wallet.contract.command.balance.ReverseBalanceWithdrawCommand;
import org.mojave.core.wallet.contract.command.balance.WithdrawBalanceCommand;
import org.mojave.core.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.core.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.command.position.IncreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.ReversalFailedInWalletException;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.contract.exception.position.FailedToRollbackReservationException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class TestWalletCommandReplier {

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public TestWalletCommandReplier(final Connection connection,
                                    final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleCreateWalletCommand)
            .subscribe(CreateWalletCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleDepositBalanceCommand)
            .subscribe(DepositBalanceCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleWithdrawBalanceCommand)
            .subscribe(WithdrawBalanceCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleReverseBalanceWithdrawCommand)
            .subscribe(ReverseBalanceWithdrawCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleIncreasePositionCommand)
            .subscribe(IncreasePositionCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleDecreasePositionCommand)
            .subscribe(DecreasePositionCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleReservePositionCommand)
            .subscribe(ReservePositionCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleFulfilPositionsCommand)
            .subscribe(FulfilPositionsCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleCommitReservationCommand)
            .subscribe(CommitReservationCommand.SUBJECT_NAME);

        this.connection.createDispatcher(this::handleRollbackReservationCommand)
            .subscribe(RollbackReservationCommand.SUBJECT_NAME);
    }

    private void handleCreateWalletCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, CreateWalletCommand.Input.class);

            return new CreateWalletCommand.Output(new WalletId(input.walletOwnerId().getId()));
        });
    }

    private void handleDepositBalanceCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, DepositBalanceCommand.Input.class);

            throw new WalletNotFoundException(
                input.walletOwnerId(), input.currency(), input.tag());
        });
    }

    private void handleWithdrawBalanceCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, WithdrawBalanceCommand.Input.class);

            throw new WalletNotFoundException(
                input.walletOwnerId(), input.currency(), input.tag());
        });
    }

    private void handleReverseBalanceWithdrawCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, ReverseBalanceWithdrawCommand.Input.class);

            throw new ReversalFailedInWalletException(input.withdrawId());
        });
    }

    private void handleIncreasePositionCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, IncreasePositionCommand.Input.class);

            throw new WalletNotFoundException(
                input.walletOwnerId(), input.currency(), input.tag());
        });
    }

    private void handleDecreasePositionCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, DecreasePositionCommand.Input.class);

            throw new WalletNotFoundException(
                input.walletOwnerId(), input.currency(), input.tag());
        });
    }

    private void handleReservePositionCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, ReservePositionCommand.Input.class);

            throw new WalletNotFoundException(
                input.walletOwnerId(), input.currency(), input.tag());
        });
    }

    private void handleFulfilPositionsCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, FulfilPositionsCommand.Input.class);

            throw new WalletNotFoundException(
                input.payeeWalletOwnerId(), input.currency(), input.tag());
        });
    }

    private void handleCommitReservationCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, CommitReservationCommand.Input.class);

            throw new FailedToCommitReservationException(input.reservationId());
        });
    }

    private void handleRollbackReservationCommand(final Message message) {

        this.reply(message, messageData -> {

            final var input = this.objectMapper.readValue(
                messageData, RollbackReservationCommand.Input.class);

            throw new FailedToRollbackReservationException(input.reservationId());
        });
    }

    private void reply(final Message message, final Replier replier) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            return;
        }

        try {

            final var output = replier.reply(message.getData());
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

        } catch (final Exception exception) {

            final var error = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(error));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

    @FunctionalInterface
    private interface Replier {

        Object reply(byte[] messageData) throws Exception;
    }

}
