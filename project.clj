(defproject thi.ng/site "0.1.0-SNAPSHOT"
  {:description   "thi.ng website"
   :url           "http://thi.ng"
   :license       {:name "Apache Software License 2.0"
                   :url "http://www.apache.org/licenses/LICENSE-2.0"}

   :dependencies  [[org.clojure/clojure "1.7.0"]
                   [org.clojure/clojurescript "1.7.122"]
                   [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                   [org.clojars.toxi/re-frame "0.2.0"]
                   [thi.ng/validate "0.1.3"]
                   [thi.ng/domus "0.2.0"]
                   [cljs-log "0.2.2"]
                   [reagent "0.5.1"]]

   :plugins       [[lein-cljsbuild "1.1.0"]
                   [lein-figwheel "0.4.0"]
                   [lein-environ "1.0.0"]]

   :source-paths  ["src"]

   :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

   :profiles      {:prod {:env {:log-level 4}}}

   :cljsbuild     {:builds [{:id           "dev"
                             :source-paths ["src"]
                             :figwheel     {:on-jsload "thi.ng.site.core/on-js-reload" }
                             :compiler     {:main                 thi.ng.site.core
                                            :asset-path           "js/compiled/out"
                                            :output-to            "resources/public/js/compiled/app.js"
                                            :output-dir           "resources/public/js/compiled/out"
                                            :source-map-timestamp true}}
                            {:id           "min"
                             :source-paths ["src"]
                             :compiler     {:output-to     "resources/public/js/compiled/app.js"
                                            :main          thi.ng.site.core
                                            :optimizations :advanced
                                            :pretty-print  false}}]}

   :figwheel      {:css-dirs ["resources/public/css"]}})
