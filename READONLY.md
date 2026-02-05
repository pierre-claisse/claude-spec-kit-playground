# Commands

```
/speckit.constitution Project constraints and principles (strict, non-negotiable):

- YAGNI above all: implement ONLY what is explicitly requested in the current task. Never add features, patterns, or abstractions "for the future" or "just in case".
- Extreme minimalism: the simplest possible code that works. No unnecessary layering, abstractions, or architectural patterns.
- No performance optimizations, no caching, no pagination, no custom query optimizations.
- No accessibility concerns, no i18n, no localization.
- No advanced security features unless explicitly requested.
- No input validation beyond the absolute minimum required for basic functionality.
- No custom exceptions or global exception handling unless explicitly requested.
- No additional logging beyond framework defaults.
- No DTOs, mappers, or transfer objects unless explicitly requested.
- Avoid separate service layers if direct repository-to-controller interaction suffices.
- Keep all components (controllers, services, repositories, entities) as small and direct as possible.
- Use in-memory database for development; containerized database only for integration tests when required.
- Tests: only basic integration tests when requested. No unit tests unless explicitly asked.
- Code style: stick strictly to framework defaults and conventions. No additional formatting rules, patterns, or enforced "best practices".
- Never suggest, propose, or add anything that increases complexity, even if it is considered a "best practice".

All responses must respect these rules without exception. No explanations, no justifications, no deviations.
```
