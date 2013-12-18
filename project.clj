(defproject job-skill "dev-1.0"
  :description "Job Skill Correlation Reporter"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ [org.clojure/clojure "1.5.1"]
                  [com.datomic/datomic-free "0.9.4324"]
                  [incanter/incanter-core "1.5.4"]
                  [incanter/incanter-charts "1.5.4"]
                  [incanter/incanter-svg "1.5.4"]
                  [clj-time "0.6.0"]
                 ]
  :main job-skill.core
  
  )
