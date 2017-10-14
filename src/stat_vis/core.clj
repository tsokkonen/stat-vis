(ns stat-vis.core
  (:require [clj-http.client :as client]
            [clojure.string :as s]
            [ring.adapter.jetty :as r]
            [stat-vis.px-web.conversion :refer :all])
  (:gen-class))

(defn get-data
  "Returns data as a seq of maps downloaded from url, given query"
  [url query]
  (mapify (rest (parse (:body (client/post url {:body query}))))))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (s/join " " (map :price-per-square-meter (get-data url query)))})

(defn -main
  [& args]
  (r/run-jetty handler {:port 3030}))
