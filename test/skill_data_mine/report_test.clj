(ns skill-data-mine.report-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.datetimeUtil :refer :all]
            [skill-data-mine.persist :refer :all]
            [skill-data-mine.report :refer :all]
            )
    (:use config.persist)
)

(use-fixtures :once connect-to-test-db )


(deftest report-job-stats-by-snapshot
  (testing "test we can report all skill percents for one snapshot"
    (println "** job stats for snapshot **")
    (is (not (nil? (pr-str (display-job-stats conn snapshot-description)))))
  ))

(deftest report-top-job-stats-by-snapshot
  (testing "test we can report top 2  skill percents for one snapshot"
    (println "** top 2  stats for snapshot **")
    (is (not (nil? (pr-str (display-top-stats conn snapshot-description 2)))))
  ))
