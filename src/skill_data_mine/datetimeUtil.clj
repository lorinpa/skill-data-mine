(ns skill-data-mine.datetimeUtil
  (:use [clj-time.core :only (date-time)])
  (:use clj-time.format)
)


;; creates a date-time value acceptable
;; to datomic's txInstant type
(defn make-time-inst 
  ([y m d] 
  (let [time-str (format "%s \"%s\"" "#inst" (date-time y m d)) 
        time-inst (read-string time-str)] 
    time-inst))
  ([date-str]
    (let  [ fmt (formatter "yyyy-MM-dd")
               time-str (format "%s \"%s\"" "#inst" (parse fmt date-str)) 
               time-inst (read-string time-str)]
      time-inst)))


;; used to validate command line 
;; date time argument
(defn valid-cl-date? [date-str]
  (let [fmt (formatter "yyyy-MM-dd" )] 
  (try (parse fmt date-str)
      (catch Exception e
        (throw (Exception. (format "%s : Is not a valid date. Usage \"-d yyyy-MM-dd\". Examples: -d \"2013-07-04\" or -d \"2013-7-4\"" date-str)))
      ))))
