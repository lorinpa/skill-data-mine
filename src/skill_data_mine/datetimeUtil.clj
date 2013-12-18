(ns skill-data-mine.datetimeUtil
  (:use [clj-time.core :only (date-time)])  
)


;; creates a date-time value acceptable
;; to datomic's txInstant type
(defn make-time-inst [y m d] 
  (let [time-str (format "%s \"%s\"" "#inst" (date-time y m d)) 
        time-inst (read-string time-str)] 
    time-inst))

