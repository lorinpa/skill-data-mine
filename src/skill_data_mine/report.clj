(ns skill-data-mine.report
  (:require [clojure.xml :as xml])
   (:use skill-data-mine.rss)
   (:use skill-data-mine.persist)
)

(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)
(use '(incanter core charts datasets svg))

(defn display-job-stats [conn snapshot-description]
 (let [ res (get-job-skills-freq conn snapshot-description)
        total (get-job-total conn snapshot-description)
       ]
    (doseq [n res]
       (pprint (d/invoke (db conn) :calc-percent (nth n 0) total (int (nth n 1))  ) ))
 ))

;; text report allows to select the greatest values
;; E.G. top 10
(defn display-top-stats [conn snapshot-description top]
 (let [ res (get-job-skills-freq conn snapshot-description)
        total (get-job-total conn snapshot-description)
        top-recs (take top (reverse (sort-by last res)))
       ]
    (doseq [n top-recs]
       (pprint (d/invoke (db conn) :calc-percent (nth n 0) total (int (nth n 1))  ) ) )
 ))

    
;; text report allows to select the greatest values
;; E.G. top 10
(defn display-top-stats-chart [conn snapshot-description top total]
 (let [ title (format "%d Job Postings" total)
        rows (sort-by first (take top (reverse (sort-by last (get-job-skills-freq conn snapshot-description)))))
        skills (map first rows)
        percents (map #(float (* (/ (int (nth % 1)) total  ) 100 ) ) rows) 
       ]
    (view (bar-chart (vec skills) (vec percents)  :title title :vertical false :x-label "Skill" :y-label "% of Job Postings"  ) )
 ))

;; Same as above, but we apply a keyword filter
;; E.G. on jobs that contain either "java" or "clojure"
(defn display-top-stats-chart-keyword-filter [conn snapshot-description top total keyword-vector]
 (let [ title (format "%d Job Postings" total)
        rows (sort-by first (take top (reverse (sort-by last (get-skill-freq-by-snapshot-and-keywords conn snapshot-description keyword-vector)))))
        skills (map first rows)
        percents (map #(float (* (/ (int (nth % 1)) total  ) 100 ) ) rows) 
       ]
    (view (bar-chart (vec skills) (vec percents)  :title title :vertical false :x-label "Skill" :y-label "% of Job Postings"  ) )
 ))



(defn display-skill-percent-history [conn skill]
  (let [jh (get-skill-history conn skill) ] 
    (view (bar-chart (vec (map first jh)) (vec (map last jh))  :title skill :vertical false :x-label "Skill" :y-label "% of Job Postings")) )
)
