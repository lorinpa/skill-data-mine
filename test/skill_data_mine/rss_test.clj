(ns skill-data-mine.rss-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.core :refer :all]
            [skill-data-mine.rss :refer :all]
            )
    (:use config.rss)
)

(use-fixtures :once load-parse-sample2)

;; test utility function
(deftest test-to-string 
  (testing
    (let [num-val 5]
    (is (= (class num-val) java.lang.Long))
    (is (= (class (to-string num-val)) java.lang.String )))
  ))

;; tests extraction of a vector of categories
;; data structure comes from xml-seq
(deftest test-extract-categories
  (testing
    (let [ data (vec (read-string "(:guid [\"http://test-site.com/jobs/555666/entry-level-developer-test4\"] 
                            :link [\"http://test-site.com/jobs/555666/entry-level-developer-test4\"] 
                            :category [\"css\"] :category [\"javascript\"] :category [\"html\"] :title [\"Four\"] 
                            :description [Sample 4] :pubDate [\"Thu, 04 Dec 2013 14:44:52 Z\"] 
                            :a10:updated [\"2013-12-05T12:44:52Z\"])" ))
           categories (extract-categories data)
       ]
       (is (= (count categories) 3))
    )))

;; data segment as stored in xml-seq
(deftest test-extract-job-key
  (try
    (testing "extract job key from nested map"
      (let [ data {:link  ["http://test-site.com/jobs/44359/senior-developer-architect-test1"] }]
        (is (= (extract-job-key data) 44359 )))
  (catch Exception e 
    (println (format "Unexpected exception in test-extract-job-key %s" (.getMessage e)))))))

;; second sample file contains a broader distributiion of skill data
(deftest test-distinct-category-list
  (testing "read an rss file"
      (println "testing distinct category list")
      (is (> (count @nodeList) 0))
      (is (= (count @nodeList) 4))
      (is (> (count @categoryList) 0))
      (is (= (count (get-distinct-category-list ))  11) )
    ))


;; test the values in the first job
(deftest test-first-node-values
  (try
    (testing "values parsed for first node"
        (is (= (first (:title (first @nodeList)))  "One"))
        (is (=  (:job-key (first @nodeList))  44359))) 
  (catch Exception e 
    (println (format "Unexpected exception in test-first-node-values %s" (.getMessage e))))))



;; test the values in the third job
(deftest test-third-node-values
  (try
    (testing "values parsed for the last node"
        (is (= (first (:title (nth @nodeList 2 )) )  "Three"))
        (is (=  (:job-key (nth @nodeList 2) ) 333111))
        (is (= (count (:categories (nth @nodeList 2)))  5)) 
  (catch Exception e 
    (println (format "Unexpected exception in test-last-node-values %s" (.getMessage e)))))))

;; an rss item as parsed by xml-seq
;; Note! This will append out global nodeList and categoryList vectors
;; test-element is defined in config.rss 
(deftest test-process-one-rss-item
  (try
    (testing "process one rss item stored by xml-seq"
      (parse-elem (read-string test-element))
      (is (= (first (:title (last @nodeList)))  "Four"))
      (is (=  (:job-key (last @nodeList))  555666))
      (is (= (count (:categories (last @nodeList)))  3)) 

  (catch Exception e 
    (println (format "Unexpected exception in test-extract-job-key %s" (.getMessage e)))))))
