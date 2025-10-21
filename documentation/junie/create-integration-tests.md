Create integration tests **IT for all the **CommandHandler inside io.mojaloop.core.quoting.domain.command. 
If needed to organize by the same nature and found more than one nature, you need to create a subpackage 
like in io.mojaloop.core.account.domain.command.account too when you create **IT accordingly.

When you create a test, make sure you follow these:
1. All tests must clear the database table before each test is run.
2. Test's code line must be organized by the similar purpose, and code lines must not be packed to each other. It requires good line breaks.
3. Do not use FQNs for class when you need to import. Use the "import" statement.
4. all these **IT must cover all the logic.
5. You must create an integration test for each Command in their related package accordingly
6. **IT name must be **CommandIT.
7. You must not use Domain classes in these **IT.
8. Create a test based on these Commands and for these Commands and using these Commands only.
9. Test method name must be in snake cases.

You can refer to what you have done previously in this package: io.mojaloop.core.participant.domain.command