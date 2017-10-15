(ns stat-vis.px-web.conversion
  (:require [clj-http.client :as client]
            [clojure.string :as s]))

(def url "http://pxnet2.stat.fi/PXWeb/api/v1/en/StatFin/asu/ashi/statfin_ashi_pxt_004.px")

(def query "{
             \"Query\": [
                       {
                        \"code\": \"Neljännes\",
                        \"selection\": {
                                      \"filter\": \"item\",
                                      \"values\": [
                                                 \"0\"
                                                 ]
                                      }
                        },
                       {
                        \"code\": \"Talotyyppi\",
                        \"selection\": {
                                      \"filter\": \"item\",
                                      \"values\": [
                                                 \"2\"
                                                 ]
                                      }
                        },
                       {
                        \"code\": \"Postinumero\",
                        \"selection\": {
                                      \"filter\": \"item\",
                                      \"values\": [
                                                 \"00510\"
                                                 ]
                                      }
                        },
                       {
                        \"code\": \"Rakennusvuosi\",
                        \"selection\": {
                                      \"filter\": \"item\",
                                      \"values\": [
                                                 \"8\"
                                                 ]
                                      }
                        },
                       {
                        \"code\": \"Tiedot\",
                        \"selection\": {
                                      \"filter\": \"item\",
                                      \"values\": [
                                                 \"Mean\"
                                                 ]
                                      }
                        }
                       ],
             \"response\": {
                          \"format\": \"csv\"
                          }
             }")

(def tbl-keys [:year :quarter :home-type :number-of-rooms
               :postal-code :data-item :sum-item :price-per-square-meter])

(defn de-quote
  [str]
  (s/replace str #"\"" ""))

(defn str->int
  [str]
  (Integer. (de-quote str)))

(def conversions {:year str->int :quarter de-quote :home-type de-quote
                   :number-of-rooms de-quote
                   :postal-code de-quote :data-item de-quote
                   :sum-item de-quote :price-per-square-meter str->int})

(defn convert
  [tbl-key value]
  ((get conversions tbl-key) value))

(defn parse
  "Converts a CSV into rows of columns"
  [string]
  (map #(s/split % #",")
       (s/split string #"\r\n")))

(defn mapify
  "Returns a seq of maps"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [tbl-key value]]
                   (assoc row-map tbl-key (convert tbl-key value)))
                 {}
                 (map vector tbl-keys unmapped-row)))
       rows))

(defn get-data
  "Returns data as a seq of maps downloaded from url, given query"
  [url query]
  (mapify (rest (parse (:body (client/post url {:body query}))))))
