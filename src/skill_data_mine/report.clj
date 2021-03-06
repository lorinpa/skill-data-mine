(ns skill-data-mine.report)

(use 'skill-data-mine.persist)
(use 'clojure.pprint)
(use '(incanter core charts datasets svg))

(defn display-job-stats [conn snapshot-description]
    (pprint (:rows (get-job-stats conn snapshot-description))))

;; text report allows to select the greatest values
;; E.G. top 10
(defn display-top-stats [conn snapshot-description top]
 (let [  rows  (:rows (get-job-stats conn snapshot-description))
         top-recs (take top (reverse (sort-by last rows)))]
       (pprint top-recs)
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



(defn display-skill-percent-history [conn skill]
  (let [jh (get-skill-history conn skill) ] 
    (view (bar-chart (vec (map first jh)) (vec (map last jh))  :title skill :vertical false :x-label "Skill" :y-label "% of Job Postings")) )
)

(defn report-snapshots [conn]
   (println "*** SNAPSHOTS ***")
   (pprint (get-snapshot-descriptions conn)))

(defn report-job-details [conn snapshot-description] 
  (try 
    (snapshot-description-exists? conn snapshot-description)
    (println (format "** Job Details for Snapshot: %s **" snapshot-description ))
    (pprint (get-snapshot-details conn snapshot-description))
    (catch Exception e (println (.getMessage e)))
  )
)

(defn report-skill-history [conn skill]
 (println (format "** Skill History: %s **" skill))
 (pprint (get-skill-history conn skill)))


(defn report-skill-list-history [conn skill-list]
  (println (format "** Skill History:  %s **" skill-list))
  (pprint (map (fn [a] [(:snapshot a) (:percent a)]) (report-skill-list-stats conn ["java" "javascript"]))))
