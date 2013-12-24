(ns skill-data-mine.rss
  (:require [clojure.xml :as xml])
   (:use skill-data-mine.persist)
)

(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)

(defrecord Node [title pubDate link job-key categories ])

(defn to-string [value] (format "\"%s\"" value))

(defn extract-job-key [data-map]
  (let [job-str (to-string (first (:link data-map))) ]
    (Long. ( (clojure.string/split job-str #"/") 4))))

;; we break down the complex data structure
;; extract the keys and values, then put them together as maps
;; filter the maps so that we are only looking at :category
;; extract the remaining values as a vector
(defn extract-categories [data-vec]
  (let [ key-set (filter #(if (not (vector? %1) ) %1) data-vec) 
         val-set (map first (filter vector? data-vec)) 
         maps (map (fn [a b] (assoc {} a b)) key-set val-set) 
         categories (filter #(if (= (ffirst %1) :category) %1) maps) 
         category-vals (vec (map :category categories ))] 
    category-vals)
)

(defn create-node [title pubDate link job-key categories ]
  (Node. title pubDate link job-key categories))


(defn parse-elem [elem]
   (let [ content (vec (:content elem))
     data-vals  (map (fn [z] (:content z)) content)
     data-keys (map (fn [z] (:tag z)) content)
     data-vec (interleave data-keys data-vals)
     data-map (apply assoc {} data-vec)
     job-key (extract-job-key data-map)
     title (:title data-map)
     pubDate (:pubDate data-map)
     link (:link data-map)
     categories (extract-categories data-vec)]
     (assoc {} :node (create-node title pubDate link job-key categories ) :categories categories)))

(defn dist-node-elems [data]
 (let [ data-vec  (vec
  (for [ n data] (when (= :item (:tag n)) (parse-elem n) ) ))]
   ;; remove any nils 
   (filter #(when (not (nil? %1) ) %1) (distinct data-vec))
))


(defn get-xml-from-file [file_uri ]
  (let [ d (xml-seq  (xml/parse (java.io.File. file_uri))) ] d ))

(defn get-xml-from-url [url]
  (let [t (xml-seq (xml/parse (java.io.ByteArrayInputStream. (.getBytes (slurp url))))) ]  t))
