(ns com.emil.myresten.datomic.db.schema
  (:require [datomic.client.api :as d]))


(def schema [{:db/ident :person/ssn-id
              :db/cardinality :db.cardinality/one
              :db/valueType :db.type/string
              :db/doc "A personal number to query on"}

             {:db/ident       :person/first-name
              :db/cardinality :db.cardinality/one
              :db/valueType   :db.type/string
              :db/doc         "The first name of a person"}

             {:db/ident       :address/name
              :db/cardinality :db.cardinality/one
              :db/valueType   :db.type/string
              :db/doc "The name of the Address"}

             {:db/ident :person/lives-at
              :db/cardinality :db.cardinality/one
              :db/valueType :db.type/ref
              :db/doc "The entity-id of the address a person lives at"}])

(def config {:server-type :datomic-local
             :system "datomic"
             :storage-dir :mem})

(def client (d/client config))

(d/create-database client {:db-name "datomic"
                           :db/type ""})

(def conn (d/connect client {:db-name "datomic"}))

(d/transact conn {:tx-data schema})