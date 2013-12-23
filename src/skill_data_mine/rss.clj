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

(defn extract-categories [data-vec]
  (vec (let [rl (range 0 (count data-vec)) 
              f (for [n rl] 
                  (cond (= (keyword (nth data-vec n)) :category) 
                      (first (nth data-vec (+ n 1))))) ] 
      (filter (fn [a] (not (nil? a))) f))) 
)

(defn map-to-node [mp cl]
  (Node. (:title mp) (:pubDate mp) (:link mp) (extract-job-key (:link mp))  cl ))

(defn create-node [title pubDate link job-key categories ]
  (Node. title pubDate link job-key categories))


(defn parse-elem [elem]
   (let [ content (vec (:content elem))
     len (count content)
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
