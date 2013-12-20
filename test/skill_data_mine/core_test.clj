(ns skill-data-mine.core-test
  (:require [clojure.test :refer :all]
            [skill-data-mine.core :refer :all]
            [skill-data-mine.persist :refer :all]
            ))

(use '[datomic.api :only [q db] :as d])



