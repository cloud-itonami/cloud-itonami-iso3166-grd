# cloud-itonami-iso3166-grd

Open ISO 3166 Blueprint for **GRD**: Grenada -- **`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a Compliance Advisor +
independent **Market-Entry Compliance Governor** to navigate public-
procurement registration, local business/tax registration, and
regulatory-compliance rules in Grenada, so the operator can win and
service a government contract without hiring a full in-house
compliance department.

## Official surface (curl-verified 2026-07-22 -- every `gov.gd` / `laws.gov.gd` / `procurement.gov.gd` / `investingrenada.gd` host fetched cleanly, no TLS/JS-SPA blocker)

- Procurement: `procurement.gov.gd`, the Public Procurement Board's own
  live e-procurement portal (day-to-day operations branded "Central
  Procurement Unit"), established by the Public Procurement and
  Disposal of Public Property Act, 2014 (Act No. 39 of 2014, amended by
  Act No. 1 of 2018 and Act No. 2 of 2024). Suppliers register once,
  fee-free, on the "In-Tend Electronic Tendering Site"; the portal also
  publishes a live Debarred Suppliers register.
- Business registration: the Corporate Affairs and Intellectual
  Property Office (CAIPO), a Registrar established under the Ministry
  of Legal Affairs, Labour and Consumer Affairs by the Corporate
  Affairs and Intellectual Property Office Act, Chapter 69A (Act No. 19
  of 2009) -- issues a Certificate of Incorporation under the Companies
  Act, Chapter 58A (Act No. 35 of 1994). CAIPO has no dedicated live
  website found this session; its real, current operation is confirmed
  instead via the Grenada Investment Development Corporation (GIDC)'s
  own Investment Facilitation Manual.
- Tax: the Inland Revenue Division (IRD), Ministry of Finance, issues a
  Taxpayer Identification Number (TIN) via its SIGTAS database as a
  SEPARATE, subsequent act to CAIPO business registration (a CAIPO
  Certificate of Incorporation is required supporting documentation on
  the IRD's own TIN application).

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD check | `director-conviction-disqualifying` (Public Procurement and Disposal of Public Property Act, 2014 s.17(1)(f): a supplier/contractor/consultant/service provider, or any of its directors/officers, convicted of fraud/financial impropriety/false statements within a TWO-YEAR lookback of the procurement proceedings' commencement is disqualified -- independently recomputed from the engagement's own declared conviction/submission dates, see `docs/adr/0001-architecture.md`) |
| Compliance catalog | `src/statute/facts.cljc` -- Companies Act (Cap. 58A), Employment Act (Cap. 89), Data Protection Act 2023 |
| Tests | `clojure -M:dev:test` |
| Demo | `clojure -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

## No robotics premise -- digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) -- the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set -- it always requires human sign-off.

## What this is NOT

- **Not the government of Grenada.** This blueprint is an independent
  operator the government contracts with or that bids into its
  procurement -- never the government itself, and never an official
  channel.
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Grenada-licensed counsel
  or a registered agent where the law requires licensed
  representation.

## Capability layer

Required capabilities (`blueprint.edn`):

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Grenada:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
