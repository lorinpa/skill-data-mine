(ns skill-data-mine.core
  (:use skill-data-mine.rss)
  (:use skill-data-mine.report)
  (:use skill-data-mine.persist)
  (:use skill-data-mine.datetimeUtil)
)
(use '[datomic.api :only [q db] :as d])


(defn -main
  "Entry Point to Program"
  [& args]
  (dist-node-elems (get-xml-from-file "data/12-18-13-rss.xml" ))
  ;(dist-node-elems (get-xml-from-url "http://careers.stackoverflow.com/jobs/feed?searchTerm=java+clojure&location=60610&range=10&distanceUnits=Miles" ))
  (let [ 
              ;conn (d/connect "datomic:free://localhost:4334/jobs")
              conn (init-db "datomic:free://localhost:4334/job-posts")
              num-nodes (count @nodeList)
              cat-list (get-distinct-category-list)
              description "12-18-13-rss.xml"
              ;;num-jobs (get-job-total conn)
            ]
     ;;  (println (format "got %d jobs" num-jobs ))
       (println (format "got %d nodess" num-nodes ))
       (println (format "got %d skills" (count cat-list)))
        (persist-rss-data conn @nodeList cat-list (make-time-inst 2013 12 18) description)
        (let [num-jobs (get-job-total conn description)]
         (display-top-stats-chart conn description 10 num-jobs)
        )
      )
    
)
