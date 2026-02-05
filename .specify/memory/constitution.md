<!--
SYNC IMPACT REPORT
==================
Version change: N/A → 1.0.0 (initial ratification)
Modified principles: N/A (new constitution)
Added sections:
  - Core Principles (5 principles)
  - Technical Constraints
  - Code Style Requirements
  - Governance
Removed sections: N/A
Templates requiring updates:
  - .specify/templates/plan-template.md: ✅ compatible (Constitution Check section exists)
  - .specify/templates/spec-template.md: ✅ compatible (no principle-specific constraints)
  - .specify/templates/tasks-template.md: ✅ compatible (tests marked optional, aligns with Testing Discipline)
  - .specify/templates/checklist-template.md: ✅ compatible (generic template)
Follow-up TODOs: None
-->

# Claude Spec Kit Playground Constitution

## Core Principles

### I. YAGNI-First Development

Implement ONLY what is explicitly requested in the current task. This principle supersedes all other considerations.

- MUST NOT add features, patterns, or abstractions "for the future" or "just in case"
- MUST NOT anticipate requirements not stated in the current task
- MUST NOT add "improvements" beyond what was asked
- Every line of code MUST serve an explicit, immediate requirement

**Rationale**: Speculative code creates maintenance burden, increases complexity, and often solves problems that never materialize.

### II. Extreme Minimalism

The simplest possible code that works. No unnecessary layering, abstractions, or architectural patterns.

- MUST choose the most direct implementation path
- MUST NOT create helpers, utilities, or abstractions for one-time operations
- MUST NOT add error handling for scenarios that cannot happen
- MUST NOT design for hypothetical future requirements
- Three similar lines of code is better than a premature abstraction

**Rationale**: Minimal code is easier to read, debug, and modify. Complexity MUST be justified by immediate need.

### III. Stripped-Down Features

No optional capabilities unless explicitly requested:

- MUST NOT add performance optimizations, caching, pagination, or custom query optimizations
- MUST NOT add accessibility concerns, i18n, or localization
- MUST NOT add advanced security features beyond basic authentication if specified
- MUST NOT add input validation beyond the absolute minimum for basic functionality
- MUST NOT add custom exceptions or global exception handling
- MUST NOT add logging beyond framework defaults

**Rationale**: These features add complexity. When needed, they will be explicitly requested.

### IV. Direct Architecture

Keep all components as small and direct as possible:

- MUST NOT create DTOs, mappers, or transfer objects unless explicitly requested
- MUST avoid separate service layers if direct repository-to-controller interaction suffices
- MUST NOT add abstraction layers "for testability" or "for flexibility"
- MUST NOT add interfaces for single implementations
- Controllers, services, repositories, and entities MUST be minimal

**Rationale**: Layers exist to solve problems. Without a concrete problem, layers are overhead.

### V. Testing Discipline

Tests are written only when explicitly requested:

- MUST provide only basic integration tests when testing is requested
- MUST NOT write unit tests unless explicitly asked
- MUST use in-memory database for development
- MUST use containerized database only for integration tests when required
- MUST NOT add test infrastructure beyond what the framework provides

**Rationale**: Test code is still code that must be maintained. Test what's requested, nothing more.

## Technical Constraints

Database and infrastructure requirements:

- Development: In-memory database (H2, SQLite, or framework equivalent)
- Integration tests: Containerized database when required (Testcontainers or equivalent)
- No ORM configuration beyond defaults
- No database migrations framework unless explicitly requested
- No external caching layers (Redis, Memcached, etc.)
- No message queues or async processing unless explicitly requested

## Code Style Requirements

Strict adherence to framework defaults and conventions:

- MUST follow framework's default project structure
- MUST use framework's default formatting and style
- MUST NOT add additional formatting rules or linters beyond defaults
- MUST NOT enforce coding patterns not required by the framework
- MUST NOT add comments unless the logic is genuinely non-obvious
- MUST NOT add type annotations beyond what the language/framework requires
- MUST NOT add docstrings to code that wasn't modified

## Governance

This constitution supersedes all other development practices for this project. All implementation decisions MUST comply with these principles.

**Amendment Process**:
1. Propose amendment with clear rationale
2. Document what problem the amendment solves
3. Update constitution via `/speckit.constitution` command
4. Verify no existing code violates new principles

**Compliance**:
- All code changes MUST be verified against these principles before approval
- Violations MUST be corrected immediately
- "Best practice" is NOT justification for violating these principles
- Complexity MUST be justified by explicit, immediate requirements

**Version**: 1.0.0 | **Ratified**: 2026-02-05 | **Last Amended**: 2026-02-05
