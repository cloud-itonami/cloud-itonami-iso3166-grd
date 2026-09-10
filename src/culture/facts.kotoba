(ns culture.facts
  "Country-level regional-culture catalog for Grenada (GRD) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"GRD"
   [{:culture/id "grd.dish.oil-down"
     :culture/name "Oil down"
     :culture/country "GRD"
     :culture/kind :dish
     :culture/summary "The national dish of Grenada: a stew of breadfruit, salted meat, chicken, dumplings, callaloo and other vegetables stewed in coconut milk, herbs and spices."
     :culture/url "https://en.wikipedia.org/wiki/Oil_down"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "grd.dish.dal-puri"
     :culture/name "Dal puri"
     :culture/country "GRD"
     :culture/kind :dish
     :culture/summary "Street food of Grenadian cuisine, served wrapped around a curry, commonly goat."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Grenada"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "grd.dish.fried-bakes"
     :culture/name "Fried bakes"
     :culture/country "GRD"
     :culture/kind :dish
     :culture/summary "Deep-fried dough street food of Grenadian cuisine."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Grenada"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "grd.dish.guava-cheese"
     :culture/name "Guava cheese"
     :culture/country "GRD"
     :culture/kind :dish
     :culture/summary "Preserved guava fruit paste, a traditional sweet of Grenadian cuisine."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Grenada"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "grd.product.nutmeg"
     :culture/name "Nutmeg"
     :culture/country "GRD"
     :culture/kind :product
     :culture/summary "Grenada is known as the 'Island of Spice' for its production of nutmeg and mace; nutmeg is its top export and is depicted on the national flag."
     :culture/url "https://en.wikipedia.org/wiki/Grenada"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "grd.festival.spicemas"
     :culture/name "Spicemas"
     :culture/country "GRD"
     :culture/kind :festival
     :culture/summary "Grenada's local Caribbean carnival, held in early August; the sister island of Carriacou holds a separate carnival in February."
     :culture/url "https://en.wikipedia.org/wiki/List_of_Caribbean_carnivals_around_the_world"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "grd.festival.big-drum"
     :culture/name "Big Drum"
     :culture/country "GRD"
     :culture/kind :festival
     :culture/summary "Musical and spiritual tradition of Carriacou in Grenada, performed at village celebrations called 'Maroons' and at funerals, weddings and religious ceremonies to honour West African ancestors."
     :culture/url "https://en.wikipedia.org/wiki/Big_Drum"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-grd culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "GRD"))
                 " GRD entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
