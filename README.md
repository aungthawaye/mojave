# Mojave (Mojaloop Java Edition)
Mojaloop implementation in Java

## Diagrams (PlantUML)
- PlantUML source files are kept under `documentation/mojaloop/*.puml`.
- Example: `documentation/mojaloop/transfers.puml` contains a Mojaloop transfer sequence diagram.

Render options:
1) Using Kroki (no Java required):
   - curl -H "Accept: image/svg+xml" --data-binary @documentation/mojaloop/transfers.puml https://kroki.io/plantuml/svg > documentation/astro/public/transfers.svg
   - Then include `/transfers.svg` in Astro docs.
2) Using local PlantUML (requires Java):
   - brew install plantuml   # macOS (or use your package manager)
   - plantuml -tsvg documentation/mojaloop/transfers.puml
