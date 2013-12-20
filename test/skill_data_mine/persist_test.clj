(ns skill-data-mine.persist-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.datetimeUtil :refer :all]
            [skill-data-mine.persist :refer :all]
            )
    (:use config.persist)
)

(use-fixtures :once connect-to-test-db )


(deftest test-db-connection
  (testing "test the database connection"
    (is (not (nil? conn)))))


(deftest test-entity-id-prep-job-key
  (testing "test that we generate either a new of existing entity id (from the db)"
    (let [ entity-id (get-entity-id-for-job-assertion conn 333111 )]
    (is (not (nil? entity-id))))
  ) )



(deftest test-skill-history-query
  (testing "test query skill frequency history over all snapshots"
    (let [   data-vec (get-skill-history conn "java")]
    (is (vector data-vec))
  ) 
))

;; Note! our data source returns job posts that don't meet our location
;; criteria but not our skill criteria. Thus after a study of the results,
;; I decided to filter the data further. The keyword filter removes any
;; job posts that don't have any of the keywords (removes from results not database)
(deftest test-skill-history-query-with-keyword-filter
  (testing "test query skill frequency history over all snapshots filtered by keyword vector"
    (let [ data-vec (get-skill-history-keyword-filter conn "java" ["java" "clojure"])]
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
    (let [  data-vec (get-skill-freq-by-snapshot-and-keywords  conn snapshot-description  ["java" "clojure"])]
    (is (vector data-vec))
  )
))

(deftest test-snapshot-details-query
  (testing "tests query to get all jobs for a particular snapshot"
    (let [  data-vec (get-snapshot-details  conn snapshot-description )]
    (is (vector data-vec))
    (println (first data-vec))
  )
))

(deftest test-get-exitsting-job-entity-id
  (testing "tests we get an existing job key's entity id"
    (is (not (nil? (get-job-entity-id conn 333111) )))
  ))

(deftest test-get-temp-id
  (testing "tests the generation of temporary id when a new job key is passed as a parm."
    (is (not (nil? (get-entity-id-for-job-assertion conn 9999 ))) )  
  ))

(deftest test-get-skill-refs
  (testing "tests we can get a vector of entity ids for skills that already exist in the database."
    (let [  data-vec (get-skill-refs  conn ["java" "sql"] )]
      (is (vector data-vec)))))

(deftest test-assert-job 
  (testing "test we can assert a  job with 2 skills"
    (let [  skill-refs (get-skill-refs conn ["java" "sql"])
            entity-id (get-entity-id-for-job-assertion conn 777888)]
      (add-job conn entity-id "a test job" 777888 skill-refs)
      (is (not (nil? (get-job-entity-id conn 777888) )))
    )))

(deftest test-identify-new-skill 
  (testing "test we can distiguish between new and existing skill (in database)"
    (is (skill-not-exists? conn "dog walking"))
    (is (not (skill-not-exists? conn "java")) )
  )   
)

(deftest test-get-vector-of-entity-ids
  (testing "given a vector of jobs keys we should get back a vector of entity ids (temporary or existing)"
     (is (= (count (get-entity-id-vec-for-job-assertion conn [43578 333111] )) 2) )  
     (is (= (count (get-entity-id-vec-for-job-assertion conn [012 333111] )) 2)  )
  ))

(deftest test-assert-snapshot 
  (testing "test we can assert a snapshot with one job"
    (let [  skill-refs (get-skill-refs conn ["java" "sql"])
            entity-id (get-entity-id-for-job-assertion conn 999444)
            time-val (make-time-inst 2013 12 20)
            description "Dec 20, 2013 Test Snapshot"
          ]
      (add-job conn entity-id "a test job for 999444" 999444 skill-refs)
      (add-snapshot conn time-val description [999444])
      (is (not (nil? (get-snapshot-details conn description ) )))
      (is (> (.indexOf (get-snapshot-descriptions conn) description ) -1 ))
    )))
