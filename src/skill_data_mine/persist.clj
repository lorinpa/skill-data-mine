(ns skill-data-mine.persist)

(use 'skill-data-mine.filters)
(use '[datomic.api :only [q db] :as d])



;; persist a skill to the database 
;; E.G. "programming"
(defn add-skill [conn skill]
  (let [ temp_id (d/tempid :db.part/user) ]
         @(d/transact conn
          [[:db/add temp_id :skill-set/skill skill]])
        ))

;; A job may have zero to many skills. So we need to 
;; methods one where there is a skill list and another
;; signature where the skill list is nil
(defmulti add-job (fn [one two three four five] (class five)))

;; job has skill list
(defmethod add-job clojure.lang.PersistentVector
  [conn entity-id title job-key skill-list]
  @(d/transact conn [{:db/id entity-id 
                      :jobs/title title,
                      :jobs/job-key job-key,
                      :jobs/skill-set skill-list}]))

;; where skill-list is nil(d/transact conn percent-func)
(defmethod add-job nil 
  [conn entity-id title job-key skill-list]
  @(d/transact conn [{:db/id entity-id 
                      :jobs/title title,
                      :jobs/job-key job-key}]))

;; creates the database
;; installs the schema
;; installs the percent function
;; returns a connection to the database
(defn init-db [uri]
  (d/create-database uri)
  (let [conn (d/connect uri) 
        schema-tx (read-string (slurp "schema/schema.dtm"))
        functions-tx (read-string (slurp "schema/functions.dtm"))
  ]
  (d/transact conn schema-tx)
  (d/transact conn functions-tx)
  conn
  )
)

;; connect to existing database
(defn get-connection [uri]
  (d/connect uri))

;; release connection resources 
(defn release-connection [conn]
  (d/release conn))


;; queries the database for a particular job-key (E.G. 33487)
;; if found, returns the database entity id
(defn get-job-entity-id [conn job-key]
 (let [results (q  '[:find ?c :in $ ?t :where [?c :jobs/job-key ?t]]
              (db conn) job-key) ]
         (ffirst results)))


;; If new return temp_id else return current entity id
(defn get-entity-id-for-job-assertion [conn job-key] 
  (let [id (get-job-entity-id conn job-key) ]
    (if (nil? id) 
      (d/tempid :db.part/user)
      id)) )

(defn get-entity-id-vec-for-job-assertion [conn job-key-vec]
  (let [id-vec[] rows
       (for [job-key job-key-vec]
        (conj id-vec (get-entity-id-for-job-assertion conn job-key))   
      )] 
    (vec (flatten rows))
  ))

;; adds a "snapshot" to the database
(defn add-snapshot [conn time-val description job-key-vec]
    (let [  temp_id (d/tempid :db.part/user) 
            job-ref-vec (get-entity-id-vec-for-job-assertion conn job-key-vec) 
         ]
         @(d/transact conn
          [{:db/id temp_id :snapshot/time time-val,
            :snapshot/description description,
            :snapshot/job-set job-ref-vec}
            ])
        ))

;; queries the database for a particular skill (E.G. "programming")
;; if found, returns the database entity id
(defn get-skill-entity-id [conn skill]
 (let [results (q  '[:find ?c :in $ ?t :where [?c :skill-set/skill ?t]]
              (db conn) skill) ]
         (first results)))

;; returns true if the skill can not be found
;; in the database (E.G. do we have "programming" stored in the database)
(defn skill-not-exists? [conn skill] 
   (nil? (get-skill-entity-id conn skill)))

;; Stores the skill in the database it is not currently stored
(defn process-skill [conn skill]
  (when (skill-not-exists? conn skill)
    (add-skill conn skill)))

;; Gets the number of jobs per skill in a particular
;; snapshot. Creates a vector of all skills that
;; appear in a snapshot.
;;
;; E.G. java appeared in 15 jobs posts during 
;; the 12-06-13-rss.xml snapshot, sql 14 times in 
;; same snapshot, etc, wrc,
(defn get-job-skills-freq [conn snapshot-description]
    (let [
        results (q  '[:find ?skill-val (count ?job-set) 
                      :in $ ?val 
                      :where 
                      [?t :snapshot/description ?val]
                      [?t :snapshot/job-set ?job-set]
                      [?job-set :jobs/skill-set ?job-skill-set]
                      [?job-skill-set :skill-set/skill ?skill-val]]
              (db conn) snapshot-description ) ]
        results))

;; returns the total number of jobs stored in the database
;; for a particular snapshot
(defn get-job-total [conn snapshot-description]
     (let [ results (q  '[:find  (count ?js) 
                          :in $ ?val 
                          :where 
                          [?t :snapshot/description ?val]
                          [?t :snapshot/job-set ?js]]
              (db conn) snapshot-description) ]
      (first (first results))))


;; get the number of jobs that contain skill (e.g. "java")
;; for a particular snapshot (e.g. "12-06-13-rss.xml")
(defn skill-freq-by-snapshot [conn snapshot-description skill]
  (ffirst (q '[:find  (count ?job-set)  
               :in $ ?val ?skill-val  
               :where [?t :snapshot/description ?val] 
               [?t :snapshot/job-set ?job-set] 
               [?job-set :jobs/skill-set ?job-skill-set] 
               [?job-skill-set :skill-set/skill ?skill-val]] (db conn) 
             snapshot-description skill)))
;;
;; returns a vector of snapshot descriptions
;; that are stored in the database
(defn get-snapshot-descriptions [conn]
  (vec (map first 
          (vec (sort-by last 
              (q '[:find ?val ?time 
                   :where 
                   [?t :snapshot/description ?val]
                   [?t :snapshot/time ?time]] (db conn)))) )))

(defn get-skill-history [conn skill]
  (let [descriptions (get-snapshot-descriptions conn)]
    (for [d descriptions] 
      (let [freq (skill-freq-by-snapshot conn d skill) tot (get-job-total conn d) percent (* (float (/ freq tot)) 100)] 
        [d percent]))))


;; takes a vector of skill strings
;; and returns a vector of corresponding
;; database entity id's
(defn get-skill-refs [conn skills-vec]
  (let [res-vec[] rl
    (for [skill skills-vec] (conj res-vec (get-skill-entity-id conn skill)))]
  (vec (flatten rl))))


(defn get-snapshot-details [conn description]
   (q '[:find ?title ?job-key (vec ?skill-val) 
               :in $ ?desc 
               :where [?t :snapshot/description ?desc]
               [?t :snapshot/job-set ?job-set]
               [?job-set :jobs/title ?title]
               [?job-set :jobs/job-key ?job-key]
               [?job-set :jobs/skill-set ?skill-ref]
               [?skill-ref :skill-set/skill ?skill-val]] (db conn) description))

;; Adds skills and then jobs to the database
;; Ehancement:
;; - We want to search for the job by job-key
;; if exists assert job with current id
;; if not exists assert with temp_id
;; - Associate the job with the snapshot
;; Lets do the same as we do with skill-set
;;  - We add a get-job-posts-refs which in turn call
;;  - get-job-entity-id
;;  - the result is vector of job entity id's (refs)
;;  - we then add the snapshot as our last step here.
(defn persist-rss-data [conn node-list  cat-list time-val description]
  (doseq [cat cat-list]  (process-skill conn  cat))
  (doseq [node node-list]  (add-job conn (get-entity-id-for-job-assertion conn (:job-key node)) (:title node)
     (:job-key node) (get-skill-refs conn (:categories node))) )
  (add-snapshot conn time-val description (vec (map :job-key node-list))))


(defmulti get-job-keys-by-snapshot-and-skill (fn [one two three] (class three)))

(defmethod get-job-keys-by-snapshot-and-skill java.lang.String
  [conn description skill ]
  (ffirst 
            (q '[:find (vec ?job-key) :in $ ?desc ?skill-name 
                 :where [?t :snapshot/description ?desc]
                 [?t :snapshot/job-set ?job-set]
                 [?job-set :jobs/title ?title]
                 [?job-set :jobs/job-key ?job-key]
                 [?job-set :jobs/skill-set ?skill-ref]  
                 [?skill-ref :skill-set/skill ?skill-val]
                 [ (= ?skill-name ?skill-val) ]]  
               (db conn) description skill)))

;; Not sure why this is returning a nil in the vector.
;; I added the filter at the end to remove the nil element.
(defmethod get-job-keys-by-snapshot-and-skill clojure.lang.PersistentVector
  [conn description skill ]
  (let [result (distinct (flatten
         (for [s skill] 
            (get-job-keys-by-snapshot-and-skill conn description s))))]
    (vec (filter (fn [a] (not (nil? a)) )  result))
  ))


(defmulti get-jobs-by-snapshot-and-skill (fn [one two three] (class three)))

(defmethod get-jobs-by-snapshot-and-skill java.lang.String
  [conn description skill] 
  (q '[:find  ?title ?job-ids (vec ?skill-val) 
       :in $ [?job-ids ...] 
       :where [?c :jobs/job-key ?job-ids]
       [?c :jobs/title ?title]
       [?c :jobs/skill-set ?skill-ref]
       [?skill-ref :skill-set/skill ?skill-val]]  
     (db conn)  (get-job-keys-by-snapshot-and-skill conn description skill)))

(defmethod get-jobs-by-snapshot-and-skill clojure.lang.PersistentVector
  [conn description skill] 
  (q '[:find  ?title ?job-ids (vec ?skill-val) 
       :in $ [?job-ids ...] 
       :where [?c :jobs/job-key ?job-ids]
       [?c :jobs/title ?title]
       [?c :jobs/skill-set ?skill-ref]
       [?skill-ref :skill-set/skill ?skill-val]]  
     (db conn)  (get-job-keys-by-snapshot-and-skill conn description skill)))


;; used to validate command line argument
(defn snapshot-description-not-exists? [conn snapshot-description]
  (let [ num-jobs (count (get-snapshot-details conn snapshot-description)) ]
    (if (= num-jobs 0)  
      true
      (throw (Exception. (format "Decription: %s already exists in the database. Please enter a unique description."  snapshot-description)))
   )))

;; used to validate command line argument
(defn snapshot-description-exists? [conn snapshot-description]
  (let [ num-jobs (count (get-snapshot-details conn snapshot-description)) ]
    (if (>  num-jobs 0)  
      true
      (throw (Exception. (format "Snapshot Decription: %s does not exist, or contains no jobs."  snapshot-description)))
   )))

;; function calculates percents and then
;; packages up results for a report
(defn get-job-stats [conn snapshot-description]
    (let [  res (get-job-skills-freq conn snapshot-description)
            total (get-job-total conn snapshot-description)
            rows 
              (for [n res]
                   (d/invoke (db conn) :calc-percent (nth n 0) total (int (nth n 1))  ) )
         ]
        (assoc {} :rows rows :total total)
))


(defn report-job-skills-freq [ conn snapshot-description ]
    (let [  total (get-job-total conn snapshot-description )
            res (get-job-skills-freq conn snapshot-description )
            rows (for [n res]
                   (d/invoke (db conn) :calc-percent (nth n 0) total (int (nth n 1))  ) )
          ]
       (assoc {} :rows rows :total total)
    ))

(defn count-jobs-with-skill-list [conn snapshot-description skill-list]
  (let [ skill-vectors 
          (map last (q '[:find ?title (vec ?skill) 
                         :in $ ?desc  
                         :with ?job-key 
                         :where [?t :snapshot/description ?desc] 
                         [?t :snapshot/job-set ?job-set]
                         [?job-set :jobs/title ?title] 
                         [?job-set :jobs/job-key ?job-key]
                         [?job-set :jobs/skill-set ?skill-ref]
                         [?skill-ref :skill-set/skill ?skill]] (db conn) snapshot-description))]
      (how-many-contain skill-vectors skill-list)))

(defn skill-list-stat [conn snapshot-description skill-list]
  (let [  total (get-job-total conn snapshot-description)
          times-found (count-jobs-with-skill-list conn snapshot-description skill-list)
          percent (* (float (/ times-found total)) 100)
        ]
  (assoc {} :snapshot snapshot-description :total total :freq times-found :percent percent)))

(defn report-skill-list-stats [conn skill-list]
  (let [ snapshots (get-snapshot-descriptions conn)
         rows (for [snapshot snapshots] (skill-list-stat conn snapshot skill-list)) 
       ]
      rows))
