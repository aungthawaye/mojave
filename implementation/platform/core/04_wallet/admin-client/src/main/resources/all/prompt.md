## Creating retrofit methods
Write retrofit methods inside `WalletAdminService` class.
- Inside `WalletAdminService.WalletCommand`, create the methods according to the controllers inside `io.mojaloop.core.wallet.admin.controller.api.command.account`
- Inside `WalletAdminService.WalletQuery`, create the methods according to the methods inside `WalletQueryController`.
- Inside `WalletAdminService.PositionCommand`, create the methods according to the controllers inside `io.mojaloop.core.wallet.admin.controller.api.command.chart`
- Inside `WalletAdminService.PositionQuery`, create the methods according to the methods inside `PositionQueryController`.

## Creating the Invoker classes
- Create Invoker classes in `io.mojaloop.core.wallet.admin.client.api.command` and its sub-packages, and also in `io.mojaloop.core.wallet.admin.client.api.query`. 
- Refer to CreateAccountInvoker, PostTransactionInvoker.java, AccountQueryInvoker.java
- And implement the missing **Invoker in the above package.