(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `compute-disqualification-expiry` / `director-conviction-
  disqualifying?` are THIS vertical's own new ground-truth recompute,
  grounding GRD's flagship governor check
  (`marketentry.governor/director-conviction-disqualifying-
  violations`): the Public Procurement and Disposal of Public Property
  Act, 2014 (Act No. 39 of 2014) s.17(1)(f) (own primary text, see
  `marketentry.facts`) disqualifies a supplier/contractor/consultant/
  service provider where the person, or any of its directors or
  officers, has been convicted of fraud/financial impropriety/false
  statements 'within a period of two years preceding the commencement
  of the procurement proceedings'.

  Dates are plain ISO-8601 \"YYYY-MM-DD\" strings -- deliberately no
  external date/calendar library and no host date API (`java.time` /
  `js/Date`), the SAME technique this family's Barbados sibling
  established for its own (different) date-shaped check: a
  conviction's disqualifying-expiry date is computed by bumping the
  4-digit year prefix (a calendar '2 years later, same month/day'
  reading of the statute) and compared with plain string `compare`,
  which sorts zero-padded ISO-8601 dates in chronological order.

  This is a DIFFERENT check SHAPE from every prior sibling this repo's
  family has implemented: not a turnover-scaled formula (Bulgaria), not
  a flat statutory threshold (Albania), not a boolean registry-
  membership read (Azerbaijan/Armenia), not a 3-tier vendor-class
  contract-value classification (Antigua and Barbuda), not a dual,
  independently-escalating authority ladder (Dominica) -- and even
  against Barbados's own DATE-recompute (Suppliers Register 3-year
  VALIDITY window), the polarity is the MIRROR IMAGE: Barbados asks
  whether a registration date's forward-looking privilege window is
  still open (`submission-date <= registration-date + 3y` -> still
  valid, OK); this asks whether a conviction date's backward-looking
  DISQUALIFICATION window is still open (`submission-date <=
  conviction-date + 2y` -> STILL disqualifying, HARD hold). Same date-
  arithmetic technique, opposite qualifying/disqualifying direction --
  grounded in the Act's own s.17(1)(f) rather than copied from a
  sibling's citation.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real Public Procurement Board system. It builds the
  RECORD an operator would keep, not the act of submitting a portal
  registration itself (that is `marketentry.operation`'s
  `:filing/submit`, always human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def disqualification-window-years
  "Public Procurement and Disposal of Public Property Act, 2014 (Act
  No. 39 of 2014) s.17(1)(f)'s own numeric lookback window: a
  conviction 'within a period of TWO YEARS preceding the commencement
  of the procurement proceedings' disqualifies."
  2)

(defn compute-disqualification-expiry
  "The ground-truth date on which a `conviction-date` (\"YYYY-MM-DD\")
  STOPS disqualifying a supplier/contractor/consultant/service provider
  under s.17(1)(f) -- 2 calendar years later, same month/day."
  [conviction-date]
  (when (and conviction-date (>= (count conviction-date) 5))
    (let [year (#?(:clj Integer/parseInt :cljs js/parseInt) (subs conviction-date 0 4))
          rest (subs conviction-date 4)]
      (str (+ year disqualification-window-years) rest))))

(defn director-conviction-disqualifying?
  "Does `engagement`'s own declared `:director-conviction-date` STILL
  disqualify it under s.17(1)(f) as of its own declared
  `:submission-date` -- i.e. does `submission-date` fall ON OR BEFORE
  `conviction-date + 2 years`? A nil/missing conviction date is never
  disqualifying here (no conviction on file for this person or its
  directors/officers)."
  [{:keys [director-conviction-date submission-date]}]
  (boolean
   (when-let [expiry (compute-disqualification-expiry director-conviction-date)]
     (when submission-date
       (<= (compare submission-date expiry) 0)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
