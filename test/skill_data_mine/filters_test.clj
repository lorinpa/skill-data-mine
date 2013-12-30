(ns skill-data-mine.filters-test
  (:require [clojure.test :refer :all] 
            [skill-data-mine.filters :refer :all] 
  ))



(deftest test-are-contained
  (testing "tests whether we can determine if all the values in search-list are contained in grid"
   (let [ grid ["x"  "a" "b" ]  search-list ["a" "b"]]
      (is (are-contained? grid search-list)))))


(deftest test-are-not-contained
  (testing "tests whether we can determine if all the values in search-list are not contained in grid"
   (let [ grid ["x"  "z" "b" ]  search-list ["a" "b"]]
      (is (not (are-contained? grid search-list))))))



(deftest test-count-rows-that-contain
  (testing "tests whether we can count how many rows contain both 'a' and 'b'"
   (let [ grid [ ["a" "b"] ["a" "b"] ["x"  "z" "b" ]] search-list ["a" "b"]]
      (is (= (how-many-contain grid search-list) 2 )))))
