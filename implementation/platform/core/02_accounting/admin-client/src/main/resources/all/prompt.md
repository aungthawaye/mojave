## Creating retrofit methods
Write retrofit methods inside `AccountingAdminService` class.
- Inside `AccountingAdminService.AccountCommand`, create the methods according to the controllers inside `io.mojaloop.core.accounting.admin.controller.api.command.account`
- Inside `AccountingAdminService.AccountQuery`, create the methods according to the methods inside `AccountQueryController`.
- Inside `AccountingAdminService.ChartCommand`, create the methods according to the controllers inside `io.mojaloop.core.accounting.admin.controller.api.command.chart`
- Inside `AccountingAdminService.ChartQuery`, create the methods according to the methods inside `ChartQueryController` & `ChartEntryQueryController`.
- Inside `AccountingAdminService.DefinitionCommand`, create the methods according to the controllers inside `io.mojaloop.core.accounting.admin.controller.api.command.definition`
- Inside `AccountingAdminService.DefinitionQuery`, create the methods according to the methods inside `FlowDefinitionQueryController`.
- Inside `AccountingAdminService.LedgerCommand`, create the methods according to the controllers inside `io.mojaloop.core.accounting.admin.controller.api.command.ledger`

## Creating the Invoker classes
- Create Invoker classes in `io.mojaloop.core.accounting.admin.client.api.command`. Refer to PostTransactionInvoker.java.
- And implement the missing **Invoker in the above package.