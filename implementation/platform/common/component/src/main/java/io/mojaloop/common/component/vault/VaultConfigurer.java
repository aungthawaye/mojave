package io.mojaloop.common.component.vault;

public class VaultConfigurer {

    public static Vault configure(Settings settings) {

        return new Vault(settings.address(), settings.token(), settings.enginePath());
    }

    public record Settings(String address, String token, String enginePath) {

        public static Settings withEnv() {

            var address = System.getenv("VAULT_ADDR");
            var token = System.getenv("VAULT_TOKEN");
            var enginePath = System.getenv("VAULT_SECRET_PATH");

            return new Settings(address, token, enginePath);
        }

        public static Settings withProperty() {

            var address = System.getProperty("VAULT_ADDR");
            var token = System.getProperty("VAULT_TOKEN");
            var enginePath = System.getProperty("VAULT_SECRET_PATH");

            return new Settings(address, token, enginePath);
        }

        public static Settings withPropertyOrEnv() {

            var address = System.getenv("VAULT_ADDR") == null ? System.getProperty("VAULT_ADDR") : System.getenv("VAULT_ADDR");
            var token = System.getenv("VAULT_TOKEN") == null ? System.getProperty("VAULT_TOKEN") : System.getenv("VAULT_TOKEN");
            var enginePath =
                System.getenv("VAULT_SECRET_PATH") == null ? System.getProperty("VAULT_SECRET_PATH") : System.getenv("VAULT_SECRET_PATH");

            return new Settings(address, token, enginePath);
        }

    }

}
