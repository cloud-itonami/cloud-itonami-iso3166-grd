# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Grenada

## Classification

- Repository: `cloud-itonami-iso3166-grd`
- ISO 3166: `GRD` (Grenada)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Grenada public
  contract
- a foreign SME or civic-tech vendor entering the public sector in
  Grenada for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation
  and now needs public-sector market access

## Offer

- registration walkthrough for `procurement.gov.gd` (the Public
  Procurement Board / Central Procurement Unit's own live e-procurement
  portal, mandatory for public procurement above the Public Procurement
  and Disposal of Public Property Act, 2014's own de-minimis floor --
  currently $100,000.00, raised from $15,000.00 by Act No. 2 of 2024),
  including one-time supplier registration on the In-Tend Electronic
  Tendering Site
- business/tax registration checklist: Certificate of Incorporation
  from the Corporate Affairs and Intellectual Property Office (CAIPO,
  Companies Act Chapter 58A), followed by Taxpayer Identification
  Number (TIN) registration with the Inland Revenue Division via its
  SIGTAS database -- a separate, subsequent act
- director/officer qualification screening: independent verification
  that no director or officer of the operator has a conviction still
  within the Public Procurement and Disposal of Public Property Act,
  2014 s.17(1)(f) two-year disqualification lookback, before any filing
  submission
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone -- it must be corrected
  against a cited official source first
- a director/officer conviction still within the s.17(1)(f) two-year
  disqualification lookback window, independently recomputed from the
  engagement's own declared conviction and submission dates, is a HARD
  hold on `:filing/submit` -- never trusted from a self-reported
  "qualified" claim
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Grenada-licensed counsel or a registered agent

## Boundary with adjacent actors (read before forking)

- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) -- a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis -- the two compose (fork a COFOG-function
  blueprint AND this one to operate in Grenada).
