# ISO 20022 Roadmap

## Overview

ISO 20022 is the global standard for financial messaging, providing a common platform for the development of messages using a modeling methodology and XML/JSON syntax. This document outlines Mojave's planned support for ISO 20022 messaging.

## Current Status

**Implementation Status:** Planned (Not Yet Implemented)

Mojave currently implements FSPIOP v2.0 as its primary protocol. ISO 20022 support is on the roadmap for future releases to enable broader interoperability with global payment systems.

## ISO 20022 Standard Overview

### What is ISO 20022?

ISO 20022 is a multi-part International Standard prepared by ISO Technical Committee TC68 Financial Services. It defines:

- **Modeling Methodology:** How to model financial business processes
- **Repository:** Central dictionary of business elements
- **Message Definitions:** Standard message schemas for various financial domains
- **Syntax:** XML and JSON representations

### Key Benefits

**1. Rich Data Model**
- Structured and unstructured data support
- Extended remittance information
- Detailed party identification
- Purpose codes and references

**2. Global Adoption**
- SWIFT migrating to ISO 20022
- Real-time payment systems (FedNow, TARGET Instant Payment Settlement)
- Cross-border payments (SWIFT gpi)
- Securities, treasury, cards, forex markets

**3. Interoperability**
- Common business language
- Reduces translation complexity
- Enables straight-through processing
- Supports regulatory reporting

**4. Extensibility**
- Market-specific extensions
- Regional customizations
- Future-proof design

## Relevant ISO 20022 Message Sets

### Payment Initiation (pain)

**pain.001 - Customer Credit Transfer Initiation**
- Initiate credit transfers
- Payer initiates payment
- Rich remittance information

**pain.008 - Customer Direct Debit Initiation**
- Initiate direct debits
- Creditor-initiated collection

### Payment Clearing and Settlement (pacs)

**pacs.008 - FI to FI Customer Credit Transfer**
- Financial institution to financial institution
- Interbank credit transfers
- Most relevant for Mojave

**pacs.002 - Payment Status Report**
- Status of payment instructions
- Acceptance, rejection, pending

**pacs.004 - Payment Return**
- Return of funds
- Reason codes for returns

**pacs.028 - FI to FI Payment Status Request**
- Query payment status
- Investigation requests

### Account Management (acmt)

**acmt.023 - Identity Verification Request**
- Party identity verification
- KYC information exchange

## Mojave ISO 20022 Integration Plan

### Phase 1: Message Translation Layer (v2.0)

**Objective:** Support ISO 20022 messages while maintaining FSPIOP core

**Approach:**
```
ISO 20022 → Adapter → FSPIOP → Mojave Core
```

**Components:**
1. **ISO 20022 Ingress Adapter**
   - Parse ISO 20022 XML/JSON
   - Validate against schemas
   - Extract business data

2. **Message Translator**
   - Map ISO 20022 to FSPIOP
   - Handle data enrichment
   - Manage reference conversions

3. **FSPIOP Egress**
   - Generate FSPIOP messages
   - Route to existing core

**Messages Supported:**
- pacs.008 (Credit Transfer) → POST /transfers
- pacs.002 (Status Report) → PUT /transfers
- pacs.004 (Return) → PUT /transfers/error

### Phase 2: Native ISO 20022 Support (v3.0)

**Objective:** First-class ISO 20022 message handling

**Approach:**
```
ISO 20022 → ISO 20022 Rail → Mojave Core (Enhanced)
```

**Components:**
1. **ISO 20022 Rail Module**
   - Similar architecture to FSPIOP rail
   - ISO 20022-specific controllers
   - Message validation
   - Business rule enforcement

2. **Enhanced Core Modules**
   - Extended party information
   - Rich remittance data
   - Purpose codes
   - Structured addresses

3. **Dual Protocol Support**
   - FSPIOP and ISO 20022 simultaneously
   - Protocol negotiation
   - Message format conversion

**Messages Supported:**
- Full pacs message set
- pain.001 for payment initiation
- acmt.023 for identity verification

### Phase 3: Advanced Features (v4.0)

**Objective:** Full ISO 20022 ecosystem integration

**Features:**
1. **Cross-Border Payments**
   - Multi-currency with forex
   - Correspondent banking integration
   - Regulatory reporting

2. **Request to Pay**
   - pain.013/pain.014 messages
   - Merchant-initiated requests
   - Payment mandates

3. **Investigation & Resolution**
   - camt.029 (Resolution of Investigation)
   - camt.056 (FI to FI Payment Cancellation Request)
   - Dispute management

4. **Reporting**
   - camt.053 (Bank to Customer Statement)
   - camt.054 (Bank to Customer Debit/Credit Notification)
   - Regulatory reports

## Technical Considerations

### Message Validation

**XML Schema Validation:**
```java
SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
Schema schema = factory.newSchema(new File("pain.001.001.09.xsd"));
Validator validator = schema.newValidator();
validator.validate(new StreamSource(xmlMessage));
```

**Business Rule Validation:**
- IBAN validation
- BIC validation
- Amount limits
- Currency codes
- Purpose codes

### Message Routing

**Routing Rules:**
```
IF message.type = "pacs.008" AND message.currency = "USD"
THEN route_to: US_CLEARING_SYSTEM

IF message.type = "pacs.008" AND message.is_cross_border = true
THEN route_to: CORRESPONDENT_BANK
```

### Data Mapping

**Example: pacs.008 to FSPIOP:**
```
ISO 20022 pacs.008                    FSPIOP Transfer
─────────────────────────────────     ────────────────────
GrpHdr.MsgId                      →   transferId
GrpHdr.CreDtTm                    →   Date header
PmtInf.Dbtr.Nm                    →   payer.name
PmtInf.Dbtr.Id                    →   payer.partyIdInfo
CdtTrfTxInf.Cdtr.Nm               →   payee.name
CdtTrfTxInf.Cdtr.Id               →   payee.partyIdInfo
CdtTrfTxInf.IntrBkSttlmAmt        →   amount
CdtTrfTxInf.IntrBkSttlmDt         →   expiration
CdtTrfTxInf.PmtId.EndToEndId      →   transactionId
```

## Migration Strategy

### For Existing FSPs

**Backward Compatibility:**
- FSPIOP remains fully supported
- No forced migration
- Gradual adoption supported

**Dual Protocol Operation:**
- FSPs can support both protocols
- Hub translates between protocols
- Transparent to end users

### For New FSPs

**Protocol Choice:**
- Select FSPIOP or ISO 20022
- Configurable at onboarding
- Can change later

## Benefits for Mojave Ecosystem

**1. Global Interoperability**
- Connect to SWIFT network
- Integrate with real-time payment systems worldwide
- Support correspondent banking

**2. Enhanced Data**
- Richer transaction information
- Better regulatory compliance
- Improved reconciliation

**3. Future-Proof**
- Aligned with global standards
- Industry-wide adoption
- Long-term viability

**4. Market Access**
- Access to ISO 20022-only networks
- Corporate treasury integration
- International trade finance

## Timeline

**v2.0 (2026 Q3):** Message translation layer, pacs.008 basic support
**v3.0 (2027 Q1):** Native ISO 20022 rail, dual protocol support
**v4.0 (2027 Q3):** Advanced features, cross-border, request-to-pay

## See Also

- [FSPIOP v2.0 Implementation](fspiop-v2-implementation.md) - Current protocol
- [Mojave Introduction](../01-overview/mojave-introduction.md) - Project roadmap
- [Architecture Overview](../01-overview/architecture-overview.md) - System design

## External Resources

- [ISO 20022 Official Website](https://www.iso20022.org/)
- [SWIFT ISO 20022 Program](https://www.swift.com/standards/iso-20022)
- [FedNow ISO 20022 Implementation](https://www.frbservices.org/financial-services/fednow)
