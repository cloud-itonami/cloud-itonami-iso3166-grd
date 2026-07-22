# ADR-0001: Architecture — Grenada market-entry compliance actor (`marketentry`)

**Status**: accepted
**Date**: 2026-07-22

## Context

`cloud-itonami-iso3166-grd` was published as a `:blueprint` (docs +
`blueprint.edn` + `deps.edn`, then a country-level `culture.facts`
catalog in a separate Wave 1 batch) but carried ZERO `src/marketentry`
or `src/statute` content -- its `:public-sector/market-entry-
compliance` domain, declared in `blueprint.edn`, was unimplemented.
This ADR closes that gap, following the pattern established by
`cloud-itonami-iso3166-jpn` (origin) and the OECS/Eastern-Caribbean
siblings `cloud-itonami-iso3166-atg` (Antigua and Barbuda),
`cloud-itonami-iso3166-brb` (Barbados) and `cloud-itonami-iso3166-dma`
(Dominica) -- the simpler, no-`goyoukiki` shape this blueprint also
uses (`blueprint.edn`'s `:required-technologies` does not list
`:ontology`).

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
JPN/ATG/BRB/DMA's harness verbatim (StateGraph node names, governor
hard/escalate contract, phase 0-3 rollout, `Store` protocol with
MemStore + DatomicStore parity) and researching Grenada's own real
market-entry rules from scratch for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`GRD-DFT-000000`, `GRD-SUB-000000`), plus the flagship
  director-conviction-disqualification date recompute (see below).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

### Which Act administers procurement -- investigated against the task's own Dominica-shaped hypothesis, not assumed

The task suggested Grenada might carry a 2021-era "Public Procurement
and Disposal of Public Property Act" the way Dominica does. Direct
investigation (both `laws.gov.gd`'s Acts/2014 listing AND the Public
Procurement Board's own live site, `procurement.gov.gd/procurement-
legislation/`, independently host and agree on the same document) found
instead that Grenada's Act is substantially OLDER: the Public
Procurement and Disposal of Public Property Act, 2014 (Act No. 39 of
2014), downloaded from both sources and read in full via `pdftotext`.
It establishes a Public Procurement Board (s.6, staffed under a Chief
Procurement Officer, s.2/s.7) and a Procurement Review Commission
(s.8). It has been amended twice, both amendment PDFs downloaded and
read directly: Act No. 1 of 2018 (a broad procedural amendment
touching ss.2-3/6-7/11-14/27/29/32-33/39-40/44/55-57/63/67 and Schedule
I -- explicitly NOT ss.17 or 20) and Act No. 2 of 2024 (assented 10
April 2024, in force 12 April 2024, touching only s.4 -- raising the
Act's own de-minimis application floor from $15,000.00 to
$100,000.00). `procurement.gov.gd` itself is a real, live, currently-
maintained e-procurement portal: its own front page lists real,
August-2026-dated tender notices and a running "Contract Awards: 687"
counter, its own `/suppliers-registration/` page names the "In-Tend
Electronic Tendering Site" as the live registration mechanism, and its
own `/debarred-suppliers/` page paraphrases the Act's own s.46 grounds
near-verbatim. Two independently-enacted amendments plus a live,
actively-updated operational portal is strong, directly-observed
evidence the Act is genuinely in force today -- an explicit
commencement-confirmation this ADR draws honestly, the same discipline
Barbados's own catalog applied when it could NOT reach the same
confidence for its own Public Procurement Act, 2021.

### Flagship HARD check: `director-conviction-disqualifying` -- the mirror image of Barbados's date-recompute shape

The Act's own s.17(1) (read directly in the primary PDF text) bars
award of a procurement contract to a supplier, contractor, consultant
or service provider unless several qualifications are met, including,
at (e), that "neither the person nor any of its management have been
debarred from participating in procurement proceedings under Part
VIII", and at (f), that "neither the person nor any of its directors
or officers have been convicted in any country of any criminal offence
related to fraud or financial impropriety or making false statements
or misrepresentations with respect to their qualifications to enter
into a procurement contract, within a period of TWO YEARS preceding
the commencement of the procurement proceedings, or have been
otherwise disqualified." `marketentry.registry/compute-
disqualification-expiry` independently recomputes the date on which a
declared conviction stops disqualifying (conviction date + 2 calendar
years), and `director-conviction-disqualifying?` HARD-holds
`:filing/submit` if the engagement's own declared `:submission-date`
falls on or before that expiry date.

This is a genuinely different check SHAPE than every prior iso3166
sibling: Bulgaria's ЗОП Art. 54(5) de-minimis is a PERCENTAGE-OF-
TURNOVER formula, Albania's Neni 76(2)(c) carve-out is a FLAT STATUTORY
CONSTANT, Azerbaijan's/Armenia's flagship checks are plain BOOLEAN
registry-membership reads, Antigua and Barbuda's is a discrete 3-TIER
THRESHOLD classification, and Dominica's is a DUAL, independently-
escalating AUTHORITY ladder. Against Barbados's own DATE-recompute
(Suppliers Register 3-year validity window) specifically, the
mechanical technique is the SAME (plain ISO-8601 string arithmetic,
year-bump + lexicographic `compare`, no external date library) but the
polarity is INVERTED: Barbados asks whether a forward-looking PRIVILEGE
window (`registration-date + 3y`) is still open as of a later
submission date (still open = OK); this asks whether a backward-looking
DISQUALIFICATION window (`conviction-date + 2y`) is still open as of a
later submission date (still open = HARD hold). A fifth distinct check
shape for the family, reached by reading Grenada's own s.17(1)(f) text
rather than copied from any sibling's citation.

The Act also gives the Board a general debarment power (s.46, grounds:
offence under the Act or another Grenada law, contract breach, false
qualification information, refusal to contract, inability to furnish a
tax/NIS compliance certificate, bankruptcy) with its own review/appeal
path (Procurement Review Commission, then the High Court, ss.48-52) and
a public list of debarred persons (s.53) -- the live `/debarred-
suppliers/` page confirms the mechanism is real and current ("To date
the Procurement Board has not debarred any supplier"). This iteration
deliberately implements the s.17(1)(f) conviction-lookback check as the
flagship rather than a second, boolean-shaped debarment-registry check:
the two-year numeric lookback window is a genuinely distinct DATE shape
this family had not yet implemented, whereas a boolean "is this person
on the debarred list" read would repeat Azerbaijan's/Armenia's already-
implemented shape.

### The one-act-vs-two-acts business-registration/TIN question

The task asked every iteration to investigate, rather than assume,
whether business registration and tax-ID issuance happen in one act or
two. For Grenada this iteration found the SAME clean two-act shape
ATG's own ABIPCO/IRD finding documents, confirmed via the Grenada
Investment Development Corporation (GIDC)'s own "Investment
Facilitation Manual" (`investingrenada.gd`, downloaded directly and
read via `pdftotext`, 126pp -- a genuine Government-of-Grenada
economic-development-agency operational document, used here because no
live, dedicated CAIPO website could be found: `caipo.gov.gd` timed out
on every attempt, and the Ministry of Legal Affairs, Labour and
Consumer Affairs's own site, `llca.gov.gd`, is a bare "Coming Soon"
placeholder). The manual's own "BUSINESS REGISTRATION" and "COMPANY
INCORPORATION" process tables both end with the applicant submitting
the CAIPO registration/incorporation receipt and Certificate "to Inland
Revenue for tax registration" as a SEPARATE, subsequent step; the
manual's own "INCOME TAX REGISTRATION -- TAX IDENTIFICATION NUMBER
(TIN)" table lists "Certificate of Incorporation, Articles of
Incorporation, Memorandum of Association" as required supporting
documentation the applicant must already hold, and confirms the
Inland Revenue Division enters the applicant into its own SIGTAS
(Standard Integrated Government Tax Administration System) database to
assign the TIN.

### `statute.facts` (second, orthogonal catalog)

Three Grenada statutes, all downloaded directly from `laws.gov.gd`
(Grenada's own official consolidated-law portal) via curl and read via
`pdftotext -layout` -- none required OCR (unlike Dominica's scanned
Companies Act, every Grenada PDF this iteration fetched carried a
genuine text layer): the Companies Act, Chapter 58A (Act No. 35 of
1994, whose own s.8 "Certificate of incorporation" matches the same
section-8-certificate shape this catalog's ATG and DMA siblings each
document for their own Companies Acts), the Employment Act, Chapter 89
(Act No. 14 of 1999, a single consolidated employment code establishing
the Department of Labour and Labour Commissioner, the same shape ATG's
Labour Code uses rather than DMA's two-statute split), and the Data
Protection Act, 2023 (Act No. 1 of 2023, a modern GDPR-influenced
structure).

## Consequences

- `src/` now genuinely exists with real, tested, curl/pdftotext-cited
  content for this blueprint's declared domain (`:public-sector/
  market-entry-compliance`) -- moves this repo's
  `manifest/itonami-fleet-audit.edn` `:prod-ready?` signal from `:stub`
  to `:active`.
- The existing `culture.facts` catalog (Wave 1, unrelated batch) is
  untouched.
- The Act's own general debarment power (s.46) and its numeric
  de-minimis application floor (s.4, currently $100,000.00) are genuine,
  verified, NOT-implemented extension points for a future iteration.
- CAIPO's lack of a live, dedicated website (unlike ATG's ABIPCO or
  BRB's CAIPO) is an honest, disclosed gap -- this ADR cites the CAIPO
  Act's own primary text plus GIDC's Investment Facilitation Manual in
  its place, rather than inventing or assuming a URL.
- Sibling country blueprints can continue forking JPN/ATG/BRB/DMA/GRD
  and swapping in their own genuinely-researched `marketentry.facts` /
  `statute.facts` content and whichever flagship check their own law
  actually supports -- this ADR is itself further evidence that even a
  DATE-shaped check family (Barbados, Grenada) has room for a second,
  genuinely distinct member when its polarity and grounding statute
  differ.
