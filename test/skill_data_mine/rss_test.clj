(ns skill-data-mine.rss-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.core :refer :all]
            [skill-data-mine.rss :refer :all]
            ))


(deftest test-read-rss-file
  (testing "read an rss file"
    (let [ file-contents (get-xml-from-file "data/test-sample-rss.xml")]
      (is 
        (= (not (nil? file-contents))) ))
  ))


(deftest test-extract-node-list
  (testing "read an rss file"
    (let [ file-contents (get-xml-from-file "data/test-sample-rss.xml")]
       (dist-node-elems file-contents)
      (is (> (count @nodeList) 0))
      (is (= (count @nodeList)  2))
      (is (> (count @categoryList) 0))
      (is (- (count @categoryList) 5))
    )))
