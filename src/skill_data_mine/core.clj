(ns skill-data-mine.core
  (:gen-class)
  (:use skill-data-mine.rss)
  (:use skill-data-mine.report)
  (:use skill-data-mine.persist)
  (:use skill-data-mine.datetimeUtil))

(use 'clojure.java.io)


(defn display-command-line-help []
  (println "Functions Provided: 1) Initialize Database, 2) Import Data, 3) Report Data.")
  (println "*** **")
  (println "Intitialize Database Usage: -init-db")
  (println "** **")
  (println "Import Data Usage: -in INPUTFILE -name NAME-OF-SNAPSHOT -d yyyy-MM-dd")
  (println "** **")
  (println "Report Data Usage: -report [OPTIONS]")
  (println "-- Display list of snapshots -report")
  (println "-- Display job details of a single snapshot -report jobs NAME-OF-SNAPSHOT ")
  (println "-- Display history of a skill  -report skill-history SKILL ")
  (println "-- Display history of a skill filtered  -report skill-history SKILL SKILL-FILTER SKILL-FILTER ...")
  (System/exit 0)
)


(defn file-exists? [filepath]
  (if (.exists (as-file filepath)) true 
  (throw (Exception. (format "File Does Not Exist: %s "  filepath)))))


(defn valid-import-command-line? [conn args]
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

(defn process-import-command [conn args]
  (let [  valid   (valid-import-command-line? conn args)
          file-to-process (nth args 1)
          snapshot-description (nth args 3)
          date-time (make-time-inst (nth args 5))
        ]
            (println "Processing File")
            (let [  data-vec (dist-node-elems (get-xml-from-file file-to-process))
                    nodes (map #(:node %1) data-vec)
                    categories (distinct (flatten (map #(:categories %1) data-vec)))]
                (println (format "got %d jobs" (count nodes) ))
                (println (format "got %d skills" (count categories)))
                (println "Persisting Data")
                (persist-rss-data conn nodes categories date-time  snapshot-description)
                (display-top-stats-chart conn snapshot-description 10 (get-job-total conn snapshot-description)))))


(defn process-report-command [conn args]
  (let [ arg-count (count args)]
  (cond 
    (= arg-count 1) (report-snapshots conn)
    (= arg-count 3) 
        (do
            (cond 
              (= (nth args 1) "jobs") (report-job-details conn (nth args 2)) 
              (= (nth args 1) "skill-history") (report-skill-history conn (nth args 2))
              :else (println "Invalid command line. Usage: -report jobs SNAPSHOT or -report skill-history SKILL (OPTIONAL skill filter list*)")
            ))
    (> arg-count 3) (report-skill-list-history conn  (subvec (vec args ) 2))
    :else
    (do
      (println "Available reports:")
      (println "-report jobs SNAPSHOT")
      (println "-report skill-history SKILL"))
  ))
  (System/exit 0))

(defn process-init-command []
  (try
      (init-db "datomic:free://localhost:4334/job-posts")
      (catch Exception e  (println (format "Exception: %s" (.getMessage e)))))
      (System/exit 0))

(defn -main
  "Entry Point to Program"
  [& args]
  (let [ uri "datomic:free://localhost:4334/job-posts" first-arg (nth args 0)]
      (cond
        (= first-arg "-init-db")  (process-init-command) 
        (= first-arg "-in") (process-import-command (get-connection uri) args ) 
        (= first-arg "-report") (process-report-command (get-connection uri) args ) 
        :else (display-command-line-help)
      )))

