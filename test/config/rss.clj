(ns config.rss
  (:require  [skill-data-mine.rss :refer :all]
))
  

(defn load-parse-sample2 [ function-list ]
  (println "performing test setup")
  (def test-element "
     {:tag :item, :attrs nil, 
     :content [{:tag :guid, :attrs {:isPermaLink true}, :content [http://test-site.com/jobs/555666/entry-level-developer-test4]} 
     {:tag :link, :attrs nil, :content [http://test-site.com/jobs/555666/entry-level-developer-test4]} 
     {:tag :category, :attrs nil, :content [css]} 
     {:tag :category, :attrs nil, :content [javascript]} 
     {:tag :category, :attrs nil, :content [html]} 
     {:tag :title, :attrs nil, :content [\"Four\"]} 
     {:tag :description, :attrs nil, :content [Sample 4]} 
     {:tag :pubDate, :attrs nil, :content [\"Thu, 04 Dec 2013 14:44:52 Z\"]} 
     {:tag :a10:updated, :attrs nil, :content [\"2013-12-05T12:44:52Z\"]}]}"
  )
  (let [  file-contents  (get-xml-from-file "data/test-sample2-rss.xml")
          data-vec (dist-node-elems file-contents)
          nodes (map #(:node %1) data-vec)
          categories (distinct (flatten (map #(:categories %1) data-vec)))
        ]
       (def nodeList nodes)
       (def categoryList categories)
       (function-list)
   ))

