(ns stat-vis.line-chart.svg
  (:require [stat-vis.px-web.conversion :refer :all]
            [clojure.string :as s]))

(def data (get-data url query))
(def years (map :year data))
(def prices (map :price-per-square-meter data))

(def xs (map #(* % 50) (map #(- % (apply min years)) years)))
(def ys (map #(/ % 7.0) (map #(- % (apply min prices)) prices)))
(def points (zipmap xs (map #(Math/round %) ys)))

(defn unify-price-points
  [xs ys]
  {:x xs
   :y ys})

(def points (map unify-price-points xs (map #(Math/round %) ys)))

(defn make-point-string
  [point]
    (str (:x point)
       ","
       (:y point)))

(defn stringify-points
  [points]
  (s/join " " (map make-point-string points)))

(defn make-line
  [string]
  (str "<polyline points=\"" string "\" />"))

(defn xml
  "svg 'template', which also flips the coordinate system"
  [width height]
  (str "<svg height=\"" height "\" width=\"" width "\">"
       "<g transform=\"translate(0," height ")\">"
       "<g transform=\"rotate(-45)\">"
       (make-line (stringify-points points))       
       "</g></g>"
       "</svg>"))


