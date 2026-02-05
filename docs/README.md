# Mojave Documentation

Welcome to the Mojave instant payment switch documentation. Mojave is a production-ready instant payment system implementing the FSPIOP v2.0 protocol with enterprise-grade architecture.

## Documentation Structure

This documentation is organized into two main sections:

### [Product Documentation](product/README.md)
Business-focused documentation explaining concepts, features, and capabilities.

- **Overview**: Introduction to instant payments and Mojave
- **Core Concepts**: Understanding participants, accounting, wallets, settlement, and transactions
- **Features**: Detailed capabilities by module
- **Payment Specifications**: FSPIOP v2.0 and ISO 20022

### [Technical Documentation](technical/README.md)
Developer-focused documentation covering architecture, implementation details, and integration guides.

- **Architecture**: High-level system design and patterns
- **Core Modules**: Deep dive into each domain module
- **Critical Flows**: End-to-end processing sequences
- **FSPIOP Rail**: Protocol implementation details

## Quick Start

### For Business Stakeholders
Start with [Product Documentation](product/README.md) to understand:
1. [What is an Instant Payment System?](product/01-overview/instant-payment-concept.md)
2. [Mojave Introduction](product/01-overview/mojave-introduction.md)
3. [System Architecture Overview](product/01-overview/architecture-overview.md)

### For Developers
Start with [Technical Documentation](technical/README.md) to understand:
1. [High-Level Architecture](technical/01-architecture/high-level-architecture.md)
2. [Module Structure](technical/01-architecture/module-structure.md)
3. [Transfer Flow (Complete Example)](technical/03-flows/transfer-flow.md)

### For Integrators
Key integration resources:
- [FSPIOP v2.0 Implementation](product/04-payment-specifications/fspiop-v2-implementation.md)
- [FSPIOP Rail Overview](technical/04-rails/fspiop-rail-overview.md)
- [Participant Management](product/03-features/participant-management.md)

## Technology Stack

- **Language**: Java 25
- **Framework**: Spring Boot 4.0.2
- **Database**: MySQL with HikariCP
- **Messaging**: Apache Kafka
- **Caching**: Redis (Redisson)
- **Secrets**: HashiCorp Vault
- **Protocol**: FSPIOP v2.0

## System Capabilities

Mojave provides a complete instant payment infrastructure:

- **Multi-FSP Support**: Connect multiple Financial Service Providers
- **Real-Time Processing**: Instant payment execution with immediate finality
- **Double-Entry Accounting**: Complete ledger with audit trails
- **Position Management**: Liquidity management with Net Debit Cap enforcement
- **Settlement Framework**: Flexible settlement definition and provider matching
- **Protocol Compliance**: FSPIOP v2.0 implementation
- **Scalability**: Event-driven architecture with Kafka
- **Security**: JWS signatures, Vault integration, request validation

## Architecture Highlights

Mojave implements a clean, modular architecture:

- **Domain-Driven Design**: Clear bounded contexts per business domain
- **Hexagonal Architecture**: Port and adapters pattern for each module
- **CQRS**: Separation of commands and queries
- **Event Sourcing**: Transaction audit trail
- **Microservices Ready**: Independent deployable modules

## Documentation Conventions

### Code References
Code examples reference actual source files with paths like:
```
modules/core/accounting/domain/src/main/java/...
```

### Diagrams
Diagrams use Mermaid syntax and are embedded inline for version control.

### Cross-References
Internal links use relative paths for easy navigation.

## Contributing

To contribute to this documentation:

1. Follow the existing structure and formatting
2. Include code examples from actual source files
3. Add mermaid diagrams for complex concepts
4. Cross-reference related documentation
5. Keep business and technical concerns separated

## Additional Resources

- [Main Project README](../README.md)
- [Architecture Assessment](../modules/Mojave_Architecture_Assessment.md)
- [FSPIOP v2.0 Specification](../modules/scheme/fspiop/interface/fspiop_v2.0.yaml)

## Support

For questions or issues:
- Review the appropriate documentation section
- Check the architecture assessment for design decisions
- Examine source code referenced in documentation
- Consult FSPIOP specification for protocol details
