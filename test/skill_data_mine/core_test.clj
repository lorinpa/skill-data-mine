(ns skill-data-mine.core-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.core :refer :all]
            [skill-data-mine.persist :refer :all]
            ))

(use '[datomic.api :only [q db] :as d])



(deftest test-db-connection
  (testing "test the database connection"
    (let [conn (d/connect "datomic:free://localhost:4334/job-posts")]
    (is (not (nil? conn))))
  ) 
)

(deftest test-entity-id-prep-job-key
  (testing "test that we generate either a new of existing entity id (from the db)"
    (let [ conn (d/connect "datomic:free://localhost:4334/job-posts")
           entity-id (get-entity-id-for-job-assertion conn 44337 )
         ]
    (is (not (nil? entity-id))))
  ) 
)



(deftest test-skill-history-query
  (testing "test query skill frequency history over all snapshots"
    (let [ conn (d/connect "datomic:free://localhost:4334/job-posts")
           data-vec (get-skill-history conn "java")
         ]
    (is (vector data-vec))
  ) 
))

;; Note! our data source returns job posts that don't meet our location
;; criteria but not our skill criteria. Thus after a study of the results,
;; I decided to filter the data further. The keyword filter removes any
;; job posts that don't have any of the keywords (removes from results not database)
(deftest test-skill-history-query-with-keyword-filter
  (testing "test query skill frequency history over all snapshots filtered by keyword vector"
    (let [ conn (d/connect "datomic:free://localhost:4334/job-posts")
           data-vec (get-skill-history-keyword-filter conn "java" ["java" "clojure"])
         ]
    (is (vector data-vec))
  )
))

;; Tests query. Reports each skill in snapshot. Reports the number of jobs
;; found for each skill.
;; The query filters the job posts for only jobs that contain our original 
;; keyword search (E.G. "java" "clojure")
;;(defn get-skill-freq-by-snapshot-and-keywords [conn snapshot-description keyword-vector] 
(deftest test-skill-frequencies-per-snapshot-with-keyword-filter
  (testing "test query skill frequency history over all snapshots filtered by keyword vector"
    (let [ conn (d/connect "datomic:free://localhost:4334/job-posts")
           data-vec (get-skill-freq-by-snapshot-and-keywords  conn "12-10-13-rss.xml"  ["java" "clojure"])
         ]
    (is (vector data-vec))
  )
))


