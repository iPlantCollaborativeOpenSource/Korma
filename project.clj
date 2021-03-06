(defproject org.iplantc/korma "0.3.0-beta11"
  :description "Tasty SQL for Clojure"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [org.clojure/java.jdbc "0.2.2"]]
  :codox {:exclude [korma.sql.engine korma.sql.fns korma.sql.utils]}
          :dev-dependencies [[postgresql "9.0-801.jdbc4"]])
