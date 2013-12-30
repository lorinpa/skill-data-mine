(ns skill-data-mine.load-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.persist :refer :all]
            [skill-data-mine.rss :refer :all]
            [skill-data-mine.datetimeUtil :refer :all]
            )
    (:use config.persist)
)

(use-fixtures :once init-and-connect-to-test-db )


(deftest test-load-sample-data
  (testing "loading sample rss data and storing in database"
    (dist-node-elems (get-xml-from-file "data/test-sample2-rss.xml" ))
    (let [ 
                data-vec (dist-node-elems (get-xml-from-file  "data/test-sample2-rss.xml" ))
                nodes (map #(:node %1) data-vec)
                num-nodes (count nodes)
                cat-list (distinct (flatten (map #(:categories %1) data-vec)))
              ]
          (println (format "got %d nodess" num-nodes ))
          (println (format "got %d skills" (count cat-list)))
          (persist-rss-data conn nodes cat-list (make-time-inst 2013 12 18) snapshot-description)
        )
     (is (= (get-job-total conn snapshot-description) 3 ))
   ) 
)
