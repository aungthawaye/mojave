/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.wallet.intercom.client.service;

import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.command.balance.DepositFundCommand;
import org.mojave.core.wallet.contract.command.balance.ReverseWithdrawCommand;
import org.mojave.core.wallet.contract.command.balance.WithdrawFundCommand;
import org.mojave.core.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.core.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.command.position.IncreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.data.BalanceData;
import org.mojave.core.wallet.contract.data.PositionData;
import org.mojave.common.datatype.enums.Currency;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface WalletIntercomService {

    String MODULE_PREFIX = "/wallet-intercom";

    interface BalanceCommand {

        @POST(MODULE_PREFIX + "/balances/deposit-fund")
        Call<DepositFundCommand.Output> depositFund(@Body DepositFundCommand.Input input);

        @POST(MODULE_PREFIX + "/balances/reverse-withdraw")
        Call<ReverseWithdrawCommand.Output> reverseWithdraw(
            @Body ReverseWithdrawCommand.Input input);

        @POST(MODULE_PREFIX + "/balances/withdraw-fund")
        Call<WithdrawFundCommand.Output> withdrawFund(@Body WithdrawFundCommand.Input input);

    }

    interface BalanceQuery {

        @GET(MODULE_PREFIX + "/balances/get-all-balances")
        Call<List<BalanceData>> getAll();

        @GET(MODULE_PREFIX + "/balances/get-by-owner-id-currency")
        Call<List<BalanceData>> getByOwnerIdAndCurrency(@Query("ownerId") WalletOwnerId ownerId,
                                                        @Query("currency") Currency currency);

        @GET(MODULE_PREFIX + "/balances/get-by-balance-id")
        Call<BalanceData> getByWalletId(@Query("balanceId") BalanceId balanceId);

    }

    interface PositionCommand {

        @POST(MODULE_PREFIX + "/positions/commit")
        Call<CommitReservationCommand.Output> commit(@Body CommitReservationCommand.Input input);

        @POST(MODULE_PREFIX + "/positions/decrease")
        Call<DecreasePositionCommand.Output> decrease(@Body DecreasePositionCommand.Input input);

        @POST(MODULE_PREFIX + "/positions/fulfil")
        Call<FulfilPositionsCommand.Output> fulfil(@Body FulfilPositionsCommand.Input input);

        @POST(MODULE_PREFIX + "/positions/increase")
        Call<IncreasePositionCommand.Output> increase(@Body IncreasePositionCommand.Input input);

        @POST(MODULE_PREFIX + "/positions/reserve")
        Call<ReservePositionCommand.Output> reserve(@Body ReservePositionCommand.Input input);

        @POST(MODULE_PREFIX + "/positions/rollback")
        Call<RollbackReservationCommand.Output> rollback(
            @Body RollbackReservationCommand.Input input);

    }

    interface PositionQuery {

        @GET(MODULE_PREFIX + "/positions/get-all-positions")
        Call<List<PositionData>> getAll();

        @GET(MODULE_PREFIX + "/positions/get-by-owner-id-currency")
        Call<List<PositionData>> getByOwnerIdAndCurrency(@Query("ownerId") WalletOwnerId ownerId,
                                                         @Query("currency") Currency currency);

        @GET(MODULE_PREFIX + "/positions/get-by-position-id")
        Call<PositionData> getByPositionId(@Query("positionId") PositionId positionId);

    }

    record Settings(String baseUrl) { }

}
