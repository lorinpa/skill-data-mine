(defproject skill-data-mine "dev-1.0"
  :description "Job Skill Correlation Reporter"
  :url "http://public-action.org/mob/index.html"
  :license {:name "GPL 2.0 Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ [org.clojure/clojure "1.5.1"]
                  [com.datomic/datomic-free "0.9.4324"]
                  [incanter/incanter-core "1.5.4"]
                  [incanter/incanter-charts "1.5.4"]
                  [incanter/incanter-svg "1.5.4"]
                  [clj-time "0.6.0"]
                 ]
  :main skill-data-mine.core
  
  )
