(ns skill-data-mine.core
  (:use skill-data-mine.rss)
  (:use skill-data-mine.report)
  (:use skill-data-mine.persist)
  (:use skill-data-mine.datetimeUtil)
)
(use '[datomic.api :only [q db] :as d])
(use 'clojure.java.io)

(defn file-exists? [filepath]
  (if (.exists (as-file filepath)) true 
  (throw (Exception. (format "File Does Not Exist: %s "  filepath)))))



(defn valid-command-line? [conn args]
    (println "Checking command line.")
    (let [ num-args (count args) ]
      (if (every? true? (seq [ (= num-args 6) (= (nth args 0) "-in") (= (nth args 2) "-name") (= (nth args 4) "-d")] ))
        (do (try 
           (file-exists? (nth args 1))
           (snapshot-description-not-exists? conn (nth args 3))
           (valid-cl-date? (nth args 5))
           (catch Exception e 
             (do (println (format "Exception: %s" (.getMessage e)))
               (System/exit 0)))))
        (do
          (println "Error: Invalid command line.")
          (println "Usage: -in INPUTFILE -name NAME-OF-SNAPSHOT -d yyyy-MM-dd ")
          (println "Example: -in \"data/12-20-13-rss.xml\" -name \"Dec-20-13\" -d \"2013-07-04\"  " )
          (System/exit 0) )  
      )))

(defn -main
  "Entry Point to Program"
  [& args]
  (let [   conn (d/connect "datomic:free://localhost:4334/job-posts")
           valid   (valid-command-line? conn args)
           file-to-process (nth args 1)
           snapshot-description (nth args 3)
           date-time (make-time-inst (nth args 5))]
            (println "Processing File")
            (dist-node-elems (get-xml-from-file file-to-process ))
            (let [ num-nodes (count @nodeList)
                  cat-list (get-distinct-category-list)]
                (println (format "got %d jobs" num-nodes ))
                (println (format "got %d skills" (count cat-list)))
                (println "Persisting Data")
                (persist-rss-data conn @nodeList cat-list date-time  snapshot-description)
                (display-top-stats-chart conn snapshot-description 10 (get-job-total conn snapshot-description))
            )))


