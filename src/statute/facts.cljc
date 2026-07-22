(ns statute.facts
  "General-law compliance catalog for Grenada (GRD) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/
  -arm/-atg/-brb/-dma's `statute.facts` (ADR-2607141700, cloud-itonami-
  compliance-fact-federation).

  Every entry cites an OFFICIAL Grenada government-hosted URL --
  never fabricated. Grenada's official consolidated-law host is
  `laws.gov.gd` (run out of the Ministerial Complex, Sir Eric M. Gairy
  Botanical Gardens, contact `minister@llca.gov.gd` -- the Ministry of
  Legal Affairs, Labour and Consumer Affairs). Unlike several other
  small-jurisdiction law portals this loop has hit (incomplete TLS
  chains, JS-rendered SPAs, scanned image-only PDFs requiring OCR),
  every `laws.gov.gd` PDF this iteration fetched carried a genuine text
  layer and needed no OCR -- all three entries below were downloaded
  directly via curl (HTTP 200 on every attempt) and read via
  `pdftotext -layout`:

  - Companies Act, Chapter 58A (Act No. 35 of 1994, amended by Act No.
    39 of 1996, Act No. 6 of 1997 and SRO 5 of 2009) -- confirmed via
    its own text: 'ARRANGEMENT OF SECTIONS ... 8. Certificate of
    incorporation', with s.8 itself reading 'Upon receipt of articles
    of incorporation, the Registrar shall issue a certificate of
    incorporation ... and the certificate is conclusive proof of the
    incorporation of the company' -- the SAME section-8-certificate
    shape this catalog's ATG and DMA siblings each independently
    document for their own (different) Companies Acts, consistent with
    all three being OECS-region companies legislation of a shared
    lineage. The Registrar's functions (including this Act) are vested
    in the Corporate Affairs and Intellectual Property Office (CAIPO)
    by the CAIPO Act, Chapter 69A (see `marketentry.facts` for the
    full business-registration/tax two-act finding).
  - Employment Act, Chapter 89 (Act No. 14 of 1999, amended by Act No.
    10 of 2000, SRO 10 of 2000, Act No. 21 of 2000 and SRO 30 of 2011)
    -- confirmed via its own text: establishes the Department of Labour
    and the office of Labour Commissioner (Part II), and its own
    'ARRANGEMENT OF SECTIONS' lists a dedicated Part on 'Discipline and
    Termination of Employment' (s.67 'Termination of employment on
    account of pregnancy', s.84 'Termination allowance') -- a single
    consolidated employment code, the same shape ATG's own Labour Code
    (CAP. 27) uses, rather than DMA's two-separate-statutes (Labour
    Standards Act + Protection of Employment Act) split.
  - Data Protection Act, 2023 (Act No. 1 of 2023) -- confirmed via its
    own text: a modern, EU-GDPR-influenced structure ('ARRANGEMENT OF
    CLAUSES': Part II 'PRIVACY AND DATA PROTECTION PRINCIPLES' --
    Notice and Choice, Disclosure, Security, Retention, Data Integrity,
    Access; Part III 'RIGHTS OF DATA SUBJECTS'), commencing on a day
    fixed by Ministerial Order in the Gazette (s.1(2), same commencement
    mechanism the Public Procurement Act itself uses -- see
    `marketentry.facts`).

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"GRD"
   [{:statute/id "grd.companies-act"
     :statute/title "Companies Act"
     :statute/jurisdiction "GRD"
     :statute/kind :law
     :statute/law-number "Chapter 58A (Act No. 35 of 1994, amended by Act No. 39 of 1996, Act No. 6 of 1997 and SRO 5 of 2009)"
     :statute/url "https://laws.gov.gd/index.php/chapters/c-39a-75d/1087-cap58a-companies-act/download"
     :statute/url-provenance :official-laws-gov-gd
     :statute/enacted-date "1994-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "grd.employment-act"
     :statute/title "Employment Act"
     :statute/jurisdiction "GRD"
     :statute/kind :law
     :statute/law-number "Chapter 89 (Act No. 14 of 1999, amended by Act No. 10 of 2000, SRO 10 of 2000, Act No. 21 of 2000 and SRO 30 of 2011)"
     :statute/url "https://laws.gov.gd/index.php/chapters/e-85-98a/1146-cap89-employment-act/download"
     :statute/url-provenance :official-laws-gov-gd
     :statute/enacted-date "1999-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor :employment :termination}}
    {:statute/id "grd.data-protection-act"
     :statute/title "Data Protection Act, 2023"
     :statute/jurisdiction "GRD"
     :statute/kind :law
     :statute/law-number "Act No. 1 of 2023"
     :statute/url "https://laws.gov.gd/index.php/acts/971-act-no-1-of-2023-data-protection-2023/download"
     :statute/url-provenance :official-laws-gov-gd
     :statute/enacted-date "2023-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:data-protection :privacy}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-grd statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "GRD")) " GRD statutes seeded with an "
                 "official government-hosted citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
