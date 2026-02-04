# Participant Module

## Overview

The Participant module manages all entities that participate in the payment network: Financial Service Providers (FSPs), the Hub, Oracles, and Settlement Service Providers (SSPs). This module provides the foundational identity and configuration management for the entire system.

## Domain Model

### Fsp (Financial Service Provider)

**Aggregate Root:** Fsp

**Source:** `/Users/aungthawaye/Development/Jdev/mojave/modules/core/participant/domain/src/main/java/org/mojave/core/participant/domain/model/fsp/Fsp.java`

```java
@Entity
public class Fsp extends JpaEntity<FspId> {
    protected FspId id;
    protected FspCode code;              // Unique identifier
    protected String name;
    protected ActivationStatus activationStatus;
    protected TerminationStatus terminationStatus;
    protected Set<FspCurrency> currencies;
    protected Set<FspEndpoint> endpoints;
    protected Hub hub;

    // Rich behavior methods
    public void activate() {
        this.activationStatus = ActivationStatus.ACTIVE;
        this.currencies.forEach(FspCurrency::activate);
        this.endpoints.forEach(FspEndpoint::activate);
    }

    public void deactivate() {
        this.activationStatus = ActivationStatus.INACTIVE;
        this.currencies.forEach(FspCurrency::deactivate);
        this.endpoints.forEach(FspEndpoint::deactivate);
    }

    public FspCurrency addCurrency(Currency currency) {
        Objects.requireNonNull(currency);
        final var supportedCurrency = new FspCurrency(this, currency);
        this.currencies.add(supportedCurrency);
        return supportedCurrency;
    }

    public boolean isCurrencySupported(Currency currency) {
        return this.currencies.stream()
            .anyMatch(sc -> sc.getCurrency() == currency && sc.isActive());
    }

    public void terminate() {
        this.activationStatus = ActivationStatus.INACTIVE;
        this.terminationStatus = TerminationStatus.TERMINATED;
    }
}
```

### FspCurrency

**Entity:** Child of Fsp aggregate

```java
@Entity
public class FspCurrency {
    protected FspCurrencyId id;
    protected Fsp fsp;
    protected Currency currency;
    protected ActivationStatus activationStatus;

    public void activate() {
        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public void deactivate() {
        this.activationStatus = ActivationStatus.INACTIVE;
    }
}
```

### FspEndpoint

**Entity:** Child of Fsp aggregate

```java
@Entity
public class FspEndpoint {
    protected EndpointId id;
    protected Fsp fsp;
    protected EndpointType type;        // TRANSFERS, QUOTES, PARTIES, etc.
    protected String baseUrl;
    protected ActivationStatus activationStatus;

    public void changeBaseUrl(String newUrl) {
        Objects.requireNonNull(newUrl);
        this.baseUrl = newUrl;
    }
}
```

**Endpoint Types:**
- TRANSFERS: Transfer operations
- QUOTES: Quote requests
- PARTIES: Party lookup
- PARTICIPANTS: Participant info
- INBOUND: General inbound
- OUTBOUND: General outbound

## Key Commands

### CreateFsp

```java
public interface CreateFspCommand {
    Output execute(Input input);

    record Input(
        HubId hubId,
        FspCode code,
        String name
    ) {}

    record Output(
        FspId fspId,
        FspCode code,
        String name,
        Instant createdAt
    ) {}
}
```

### AddFspCurrency

```java
public interface AddFspCurrencyCommand {
    Output execute(Input input);

    record Input(
        FspId fspId,
        Currency currency
    ) {}

    record Output(
        FspCurrencyId fspCurrencyId,
        Currency currency,
        ActivationStatus status
    ) {}
}
```

### AddFspEndpoint

```java
public interface AddFspEndpointCommand {
    Output execute(Input input);

    record Input(
        FspId fspId,
        EndpointType type,
        String baseUrl
    ) {}

    record Output(
        EndpointId endpointId,
        EndpointType type,
        String baseUrl
    ) {}
}
```

### ChangeFspEndpoint

```java
public interface ChangeFspEndpointCommand {
    Output execute(Input input);

    record Input(
        FspId fspId,
        EndpointType type,
        String newBaseUrl
    ) {}
}
```

### ActivateFsp

```java
public interface ActivateFspCommand {
    Output execute(Input input);

    record Input(FspId fspId) {}
}
```

**Cascading Activation:**
When FSP is activated:
1. FSP activation status → ACTIVE
2. All currencies → ACTIVE
3. All endpoints → ACTIVE

### DeactivateFsp

```java
public interface DeactivateFspCommand {
    Output execute(Input input);

    record Input(FspId fspId) {}
}
```

**Cascading Deactivation:**
When FSP is deactivated:
1. FSP activation status → INACTIVE
2. All currencies → INACTIVE
3. All endpoints → INACTIVE

## Participant Store (Cache)

**Purpose:** High-performance access to participant data

```java
@Service
public class ParticipantStore {

    private final RedisTemplate<String, FspData> redisTemplate;
    private final FspRepository fspRepository;

    public FspData getFspData(FspCode code) {
        String key = "participant:fsp:" + code.value();

        // Try cache first
        FspData cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        // Load from database
        Fsp fsp = fspRepository.findByCode(code)
            .orElseThrow(() -> new FspNotFoundException(code));

        FspData data = fsp.convert();

        // Cache for 1 hour
        redisTemplate.opsForValue().set(key, data, Duration.ofHours(1));

        return data;
    }

    public Map<EndpointType, FspEndpointData> getEndpoints(FspCode code) {
        FspData fsp = getFspData(code);
        return fsp.endpoints();
    }

    public boolean supportsCurrency(FspCode code, Currency currency) {
        FspData fsp = getFspData(code);
        return Arrays.stream(fsp.currencies())
            .anyMatch(c -> c.currency() == currency && c.isActive());
    }
}
```

**Cache Keys:**
- `participant:fsp:{code}` → Full FSP data
- `participant:endpoints:{code}` → Endpoint map
- `participant:currencies:{code}` → Currency array

## See Also

- [Participant Management](../../product/03-features/participant-management.md) - Feature details
- [Participants and Roles](../../product/02-core-concepts/participants-and-roles.md) - Concepts
