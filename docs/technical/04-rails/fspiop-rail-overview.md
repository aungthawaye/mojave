# FSPIOP Rail Overview

## Overview

The FSPIOP rail implements the Financial Services Provider Interoperability Protocol v2.0, providing the interface layer between external FSPs and Mojave's core domain logic.

## Rail Module Structure

```
rail/fspiop/
├── bootstrap/          Security, controllers, configuration
│   ├── api/           FSPIOP endpoint controllers
│   ├── security/      JWS verification, gatekeeper
│   └── config/        Spring configuration
├── component/          FSPIOP-specific utilities
│   ├── headers/       FSPIOP header handling
│   ├── signature/     JWS signing/verification
│   ├── error/         FSPIOP error codes
│   └── handy/         Helper utilities
├── lookup/            Party/participant lookup service
├── quoting/           Quote request/response service
└── transfer/          Transfer prepare/fulfill service
```

## Security: FspiopServiceGatekeeper

```java
public class FspiopServiceGatekeeper {
    public Authentication authenticate(HttpServletRequest request) {
        // 1. Verify request age (prevent replay)
        verifyRequestAge(request);

        // 2. Validate FSPs exist and active
        verifyFsps(request);

        // 3. Verify JWS signature
        return authenticateUsingJws(request);
    }
}
```

## Error Handling

```java
@RestControllerAdvice
public class FspiopServiceControllerAdvice {
    @ExceptionHandler(PositionLimitExceededException.class)
    public ResponseEntity<ErrorInformation> handlePositionLimit(
        PositionLimitExceededException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(FspiopErrors.PAYER_LIMIT_ERROR);
    }
}
```

## See Also

- [FSPIOP Transfer Processing](fspiop-transfer-processing.md)
- [FSPIOP v2 Implementation](../../product/04-payment-specifications/fspiop-v2-implementation.md)
