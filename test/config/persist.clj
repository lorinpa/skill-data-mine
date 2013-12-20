(ns config.persist
  (:require  [skill-data-mine.persist :refer :all]
))

;; Note! Datomic must be running.
;; To load test data, run the test name space "init_test_data"

(defn init-and-connect-to-test-db [ function-list ]
  (println "performing test setup for persist")
   (def conn (init-db "datomic:free://localhost:4334/test-job-posts"))
   (def snapshot-description "test-sample2-rss.xml")
   (function-list)
  )

(defn connect-to-test-db [ function-list ]
  (println "performing test setup for persist")
   (def conn (get-connection "datomic:free://localhost:4334/test-job-posts"))
   (def snapshot-description "test-sample2-rss.xml")
   (function-list)
  )




