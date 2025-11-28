/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.wallet.intercom.client.service;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.command.position.CommitReservationCommand;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.FulfilPositionsCommand;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.RollbackReservationCommand;
import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.ReverseFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.fspiop.spec.core.Currency;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface WalletIntercomService {

    interface WalletCommand {

        @POST("/wallets/deposit-fund")
        Call<DepositFundCommand.Output> depositFund(@Body DepositFundCommand.Input input);

        @POST("/wallets/reverse-fund")
        Call<ReverseFundCommand.Output> reverseFund(@Body ReverseFundCommand.Input input);

        @POST("/wallets/withdraw-fund")
        Call<WithdrawFundCommand.Output> withdrawFund(@Body WithdrawFundCommand.Input input);

    }

    interface WalletQuery {

        @GET("/wallets/get-all-wallets")
        Call<List<WalletData>> getAll();

        @GET("/wallets/get-by-owner-id-currency")
        Call<List<WalletData>> getByOwnerIdAndCurrency(@Query("ownerId") WalletOwnerId ownerId,
                                                       @Query("currency") Currency currency);

        @GET("/wallets/get-by-wallet-id")
        Call<WalletData> getByWalletId(@Query("walletId") WalletId walletId);

    }

    interface PositionCommand {

        @POST("/positions/commit")
        Call<CommitReservationCommand.Output> commit(@Body CommitReservationCommand.Input input);

        @POST("/positions/decrease")
        Call<DecreasePositionCommand.Output> decrease(@Body DecreasePositionCommand.Input input);

        @POST("/positions/fulfil")
        Call<FulfilPositionsCommand.Output> fulfil(@Body FulfilPositionsCommand.Input input);

        @POST("/positions/increase")
        Call<IncreasePositionCommand.Output> increase(@Body IncreasePositionCommand.Input input);

        @POST("/positions/reserve")
        Call<ReservePositionCommand.Output> reserve(@Body ReservePositionCommand.Input input);

        @POST("/positions/rollback")
        Call<RollbackReservationCommand.Output> rollback(@Body RollbackReservationCommand.Input input);

    }

    interface PositionQuery {

        @GET("/positions/get-all-positions")
        Call<List<PositionData>> getAll();

        @GET("/positions/get-by-owner-id-currency")
        Call<List<PositionData>> getByOwnerIdAndCurrency(@Query("ownerId") WalletOwnerId ownerId,
                                                         @Query("currency") Currency currency);

        @GET("/positions/get-by-position-id")
        Call<PositionData> getByPositionId(@Query("positionId") PositionId positionId);

    }

    record Settings(String baseUrl) { }

}
