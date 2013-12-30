(ns skill-data-mine.filters)


;; This function inspects a multi-dimensioned collection,
;; parameter collection-to-seach. "collection-to-search" could be
;; a vector of vectors. "collection-to-search" could also be described
;; as a grid or rows and columns
;;
;; The parameter search-list is also a collection (E.G. ["a" "b"]).
;;
;; This function look at each row in "collection-to-search" and determines
;; whether all the value in "search-list" are present in the row.
;;
;; This function returns a list of boolean values.
;;
;; True if both "a" and "b" are present in a row.
;; False if either "a" or "b" are not present in a row.
(defn are-contained? [ collection-to-seach  search-list ] 
  (every? true? (let [ matches 
                      (for [e search-list] 
                          (for [b collection-to-seach] (= b e))) 
                      results (map  #(not (every? false? %1)) matches)] results)))

;; This function counts the number of rows that contain all the terms in "search-list".
;;
;; Parameter "collection-to-seach" is a grid (E.G. a vector of vectors, rows and columns).
;; Parameter "search-list" is a list of values (E.G. ["a" "b"])
;;
;; See are-contained? for further details.
(defn how-many-contain [ collection-to-search  search-list] 
  (count (filter #(if (are-contained? %1 search-list) %1) collection-to-search)))

