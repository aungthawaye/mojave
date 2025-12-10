Create Postman Collection for all the controllers inside this package `org.mojave.core.wallet.admin.controller`.

Follow the below guidelines:
- Scan the subpackages of `org.mojave.core.wallet.admin.controller` package. And create each subpackage as a folder in the collection.
- If there is no subpackage, then create a folder with the controller name (but do not suffix it with `Controller`). And the folder name must be in the snake case.
- All the API must have sample request and response. If the request's field is date/time, use epoch seconds (not milliseconds). 
- You can see the request's data type by checking the **Command's Input classes.
- Collection file must be put in `resources/all` package. The collection file name must be the same as the maven module's artifactId. 
- For example, if the artifactId is `mod-wallet-admin` then the collection file name must be `wallet-admin.json` (remove `mod`).