(ns skill-data-mine.rss
  (:require [clojure.xml :as xml])
   (:use skill-data-mine.persist)
)

(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)

(def categoryList (ref[]))
(def nodeList (ref[]))
(defrecord Node [title pubDate link job-key categories ])

;; removes duplicates from the categoryList 
;; returns new distinct list
(defn get-distinct-category-list []
  (distinct @categoryList))

(defn addNode [node] 
  (dosync (alter nodeList conj node)))

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

(defn add-to-category-list [cat-vec]
  (doseq [cat cat-vec]
    (dosync (alter categoryList conj cat ))
))

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
         categories (extract-categories data-vec)
         ]
      (addNode (create-node title pubDate link job-key categories ) )
      (add-to-category-list categories)
   ))

(defn dispath-node-elem [elem] 
  (cond (= :item (:tag elem)) (parse-elem elem) ))


(defn dist-node-elems [data] 
  (doseq [ n data] (dispath-node-elem n)))

(defn get-xml-from-file [file_uri ]
  (let [ d (xml-seq  (xml/parse (java.io.File. file_uri))) ] d ))

(defn get-xml-from-url [url]
  (let [t (xml-seq (xml/parse (java.io.ByteArrayInputStream. (.getBytes (slurp url))))) ]  t))
