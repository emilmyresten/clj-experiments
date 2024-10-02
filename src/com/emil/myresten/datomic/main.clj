(ns com.emil.myresten.datomic.main
  (:require [datomic.client.api :as d]
            [com.emil.myresten.db.schema :refer [conn]]))


(def initial-data [[:db/add "p" :person/ssn-id "19960319"]
                   [:db/add "p" :person/first-name "Emil"]
                   [:db/add "a" :address/name "Sveavägen"]
                   [:db/add "p" :person/lives-at "a"]])


(defn move
  [{ssn-id    :ssn-id
    address :address}]
  (let [person-id (-> (d/q '[:find ?entity
                          :in $ ?ssn-id
                          :where
                          [?entity :person/ssn-id ?ssn-id]]
                        (d/db conn)
                        ssn-id)
                      (ffirst))
        temp-id "a"
        address-id (-> (d/transact conn {:tx-data [[:db/add temp-id :address/name address]]})
                       (:tempids)
                       (get temp-id))]
    (d/transact conn {:tx-data [[:db/add person-id :person/lives-at address-id]]})))

(comment
  (d/transact conn {:tx-data initial-data})

  (def person-name "Emil")
  (d/q '[:find ?entity
         :in $ ?person-name
         :where
         [?entity :person/first-name ?person-name]]
       (d/db conn)
       person-name)

  (d/q
    '[:find ?name ?e
      :where
      [?e :person/first-name ?name]]
    (d/db conn))

  (d/transact conn {:tx-data [{:person/first-name "John"}]})

  (d/transact conn {:tx-data [[:db/add :person/first-name "Agnes"]]})

  (d/q
    '[:find ?name ?address-name
      :where
      [?person-entity :person/first-name ?name]
      [?address-entity :address/name ?address-name]
      [?person-entity :person/lives-at ?address-entity]]
    (d/db conn))

  (move {:ssn-id "19960319" :address "Wollmar Yxkullsgatan"}))