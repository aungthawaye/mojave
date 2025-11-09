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

import io.mojaloop.core.wallet.contract.command.position.CommitPositionCommand;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.RollbackPositionCommand;
import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.ReverseFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.data.WalletData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface WalletIntercomService {

    @GET("/positions")
    Call<List<PositionData>> getPositions();

    @GET("/wallets")
    Call<List<WalletData>> getWallets();

    @POST("/wallets/deposit-fund")
    Call<DepositFundCommand.Output> depositFund(@Body DepositFundCommand.Input input);

    @POST("/wallets/withdraw-fund")
    Call<WithdrawFundCommand.Output> withdrawFund(@Body WithdrawFundCommand.Input input);

    @POST("/wallets/reverse-fund")
    Call<ReverseFundCommand.Output> reverseFund(@Body ReverseFundCommand.Input input);

    @POST("/positions/increase")
    Call<IncreasePositionCommand.Output> increasePosition(@Body IncreasePositionCommand.Input input);

    @POST("/positions/decrease")
    Call<DecreasePositionCommand.Output> decreasePosition(@Body DecreasePositionCommand.Input input);

    @POST("/positions/reserve")
    Call<ReservePositionCommand.Output> reservePosition(@Body ReservePositionCommand.Input input);

    @POST("/positions/rollback")
    Call<RollbackPositionCommand.Output> rollbackPosition(@Body RollbackPositionCommand.Input input);

    @POST("/positions/commit")
    Call<CommitPositionCommand.Output> commitPosition(@Body CommitPositionCommand.Input input);

    record Settings(String baseUrl) { }
}
