(ns com.emil.myresten.oracledb.main
  (:require [next.jdbc :as jdbc])
  (:import (java.nio ByteBuffer)
           (java.util UUID)))


(def connection (-> (jdbc/get-datasource {:dbtype         "oracle"
                                          :dbname         "free"
                                          :host           "127.0.0.1"
                                          :port           1521
                                          :user           "system"
                                          :password       "password"
                                          :connectTimeout 60000
                                          :socketTimeout  30000})
                    (jdbc/get-connection)))

(defn create-varchar-uuids-table
  [{db :db}]
  (jdbc/execute! db ["CREATE TABLE varchar_uuids (
                       id VARCHAR(36) NOT NULL,
                       some_data VARCHAR(100) NOT NULL,
                       PRIMARY KEY (id)
                     )"]))

(defn create-byte-uuids-table
  [{db :db}]
  (jdbc/execute! db ["CREATE TABLE byte_uuids (
                       id RAW(16) NOT NULL,
                       some_data VARCHAR(100) NOT NULL,
                       PRIMARY KEY (id)
                     )"]))

(defn insert-data!
  [{db    :db
    table :table
    data  :data}]
  (let [template (str "INSERT INTO " table " VALUES (?, ?)")]
    (jdbc/execute! db [template (:id data) (:some-data data)])))

(defn select-all
  [{db    :db
    table :table}]
  (let [query (str "SELECT * FROM " table)]
    (jdbc/execute! db [query])))

(defn select-one
  [{db    :db
    table :table
    id    :id}]
  (let [template (str "SELECT * FROM " table " WHERE id = ?")]
    (jdbc/execute! db [template id])))

(defn count-table
  [{db    :db
    table :table}]
  (jdbc/execute! db [(str "SELECT COUNT(*) FROM " table)]))

;private static byte[] asBytes(UUID uuid) {
;        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
;        byteBuffer.putLong(uuid.getMostSignificantBits());
;        byteBuffer.putLong(uuid.getLeastSignificantBits());
;        return byteBuffer.array();
;    }
(defn uuid->bytes
  [uuid]
  (let [buffer (ByteBuffer/allocate 16)]
    (.putLong buffer (.getMostSignificantBits uuid))
    (.putLong buffer (.getLeastSignificantBits uuid))
    (.array buffer)))

(defn uuid->string
  [uuid]
  (.toString uuid))

(defn insert-n-rows!
  [{db           :db
    n            :n
    table        :table
    uuid-cast-fn :uuid-cast-fn}]
  (let [uuids (atom [])]
    (dotimes [_ n]
      (let [uuid (uuid-cast-fn (UUID/randomUUID))]
        (insert-data! {:db    db
                       :table table
                       :data  {:id        uuid
                               :some-data "Some test data"}})
        (swap! uuids conj uuid)))
    (deref uuids)))

(defn clear-table
  [{db    :db
    table :table}]
  (jdbc/execute! db [(str "DELETE FROM " table)]))

(defn perform-benchmark!
  [{db                    :db
    table                 :table
    rows                  :rows
    uuid->bytes-or-string :uuid-cast-fn}]
  (do (println "Starting benchmark for" table ", inserting" rows "rows")
      (let [ids (time (insert-n-rows! {:db db :n rows :table table :uuid-cast-fn uuid->bytes-or-string}))
            random-id (rand-nth ids)]
        (println "table count:" (count-table {:db    db
                                              :table table}))
        (println "Select-one:" (with-out-str (time (select-one {:db    db
                                                                :table table
                                                                :id    random-id})))))))



(comment

  (create-varchar-uuids-table {:db connection})
  (create-byte-uuids-table {:db connection})

  (select-all {:db    connection
               :table "varchar_uuids"})
  (select-all {:db    connection
               :table "byte_uuids"})

  (count-table {:db    connection
                :table "varchar_uuids"})
  (count-table {:db    connection
                :table "byte_uuids"})

  (clear-table {:db connection
                :table "varchar_uuids"})
  (clear-table {:db connection
                :table "byte_uuids"})


  (def rows 1000000)
  (perform-benchmark! {:db connection :table "varchar_uuids" :rows rows :uuid-cast-fn uuid->string})
  (perform-benchmark! {:db connection :table "byte_uuids" :rows rows :uuid-cast-fn uuid->bytes})
  )
