package org.mojave.core.settlement.contract.exception;

import org.mojave.component.misc.error.RestErrorResponse;

public class SettlementExceptionResolver {

    public static Throwable resolve(final RestErrorResponse error) {

        final var code = error.code();
        final var extras = error.extras();

        return switch (code) {
            case FspAlreadyExistsInGroupException.CODE ->
                FspAlreadyExistsInGroupException.from(extras);
            case FilterGroupNameRequiredException.CODE ->
                FilterGroupNameRequiredException.from(extras);
            case FilterGroupNameAlreadyExistsException.CODE ->
                FilterGroupNameAlreadyExistsException.from(extras);
            case FilterGroupIdNotFoundException.CODE ->
                FilterGroupIdNotFoundException.from(extras);
            case SettlementDefinitionIdNotFoundException.CODE ->
                SettlementDefinitionIdNotFoundException.from(extras);
            case SettlementDefinitionNameAlreadyExistsException.CODE ->
                SettlementDefinitionNameAlreadyExistsException.from(extras);
            case SettlementDefinitionAlreadyConfiguredException.CODE ->
                SettlementDefinitionAlreadyConfiguredException.from(extras);
            case SettlementProviderIdNotFoundException.CODE ->
                SettlementProviderIdNotFoundException.from(extras);
            case SettlementRecordNotFoundException.CODE ->
                SettlementRecordNotFoundException.from(extras);
            default -> throw new RuntimeException("Unknown exception code: " + code);
        };
    }

}
