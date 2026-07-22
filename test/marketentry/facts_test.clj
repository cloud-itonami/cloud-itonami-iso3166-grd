(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest grd-has-spec-basis
  (let [sb (facts/spec-basis "GRD")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "GRD")))
    (is (some? (facts/business-registration-spec-basis "GRD")))
    (is (some? (facts/director-conviction-window-spec-basis "GRD")))))

(deftest grd-rep-spec-basis-is-populated
  (testing "s.17(1)(e)-(f) / Part VIII debarment extends to directors/officers -- genuinely populated, unlike ATG's honest nil"
    (is (some? (facts/rep-spec-basis "GRD")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "GRD")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "GRD" all)))
    (is (not (facts/required-evidence-satisfied? "GRD" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["GRD" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))
