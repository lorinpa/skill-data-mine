(ns skill-data-mine.core-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.core :refer :all]
            [skill-data-mine.persist :refer :all]
            [skill-data-mine.datetimeUtil :refer :all]
            )
   (:use config.persist)
  )

(use '[datomic.api :only [q db] :as d])


(use-fixtures :once connect-to-test-db )

(deftest test-file-exists
  (testing "tests exception is thrown for non-existent file, true for existing file"
  (is (thrown? Exception (file-exists? "data/bogus.txt"))  )
  (is (file-exists? "data/test-sample2-rss.xml"))
))

(deftest test-snapshot-not-exists
  (testing "tests exception is thrown for existing snapshot, true for new"
  (is (thrown? Exception (snapshot-description-not-exists? conn snapshot-description))  )
  (is (snapshot-description-not-exists? conn "nosuchthing"))
))

;; used by command line report request
(deftest test-snapshot-exists
  (testing "tests exception is thrown for non-existent snapshot, true for existing"
  (is (thrown? Exception (snapshot-description-exists? conn "nosuchthing"))  )
  (is (snapshot-description-exists? conn snapshot-description))
))


(deftest test-validate-date
  (testing "tests exception is thrown for invalid date, true for valid date"
  (is (thrown? Exception (valid-cl-date? "2013-12-99"))  )
  (is (valid-cl-date?  "2013-12-21"))
))


(comment
(deftest test-report-command?
  (testing "tests we can determine whether the command line starts with a report command"
    (let [args (vec ["-report"])]
     (is (report-command? args))
    )
  )  
))
