(ns stat-vis.core
  (:require [clj-http.client :as client]
            [ring.adapter.jetty :as r]
            [stat-vis.px-web.conversion :refer :all]
            [stat-vis.line-chart.svg :refer :all])
  (:gen-class))

(defn template
  [contents]
  (str "<style>polyline {fill:none; stroke:#5881d8; stroke-width:3}</style>"
       contents))

(defn handler
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (template (xml 600 400))})

(defn -main
  [& args]
  (r/run-jetty handler {:port 9099}))
