(ns ^:figwheel-always thi.ng.site.main.core
  (:require-macros
   [reagent.ratom :refer [reaction]]
   [cljs-log.core :refer [debug info warn]])
  (:require
   [thi.ng.site.main.handlers :as handlers]
   [clojure.string :as str]
   [cljsjs.react :as react]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :refer [subscribe dispatch dispatch-sync]]))

(def project-img-base "/img/projects/")

(def projects
  {:babel    {:name    "thi.ng/babel"
              :version "0.3.2.1"
              :stars   30
              :lit     true
              :loc     98
              :tags    ["template" "literate programming" "emacs" "clojure" "clojurescript"]
              :bg-uri  "babel/hero.jpg"
              :bg-pos  "50% 65%"
              :desc    "Project template for polyglot, literate programming with Emacs & Org-mode"}
   :color    {:name    "thi.ng/color"
              :version "1.0.0"
              :stars   21
              :lit     true
              :loc     1742
              :tags    ["color" "dataviz" "interop" "conversion" "clojure" "clojurescript"]
              :bg-uri  "color/hero.jpg"
              :bg-pos  "50% 100%"
              :desc    "Color space conversions (RGB, HSV, HSL, CSS, CMYK, HCY, YUV etc.), presets & gradients"}
   :crypto   {:name    "thi.ng/crypto"
              :version "1.0.0-SNAPSHOT"
              :target  [:clj]
              :stars   2
              :loc     178
              :tags    ["encryption" "interop" "clojure"]
              :bg-uri  "crypto/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    "GPG keypair generation, encryption & decryption facilities"}
   :domus    {:name    "thi.ng/domus"
              :version "0.2.0"
              :stars   1
              :lit     true
              :loc     505
              :tags    ["dom" "async" "formatting" "interop" "clojurescript"]
              :bg-uri  "domus/hero.jpg"
              :bg-pos  "50% 0%"
              :desc    "DOM generation, manipulation, async event bus and browser utilities"}
   :dstruct  {:name    "thi.ng/dstruct"
              :version "0.1.1"
              :stars   1
              :lit     true
              :loc     473
              :tags    ["graph" "collections" "binary" "clojure" "clojurescript"]
              :bg-uri  "dstruct/hero.jpg"
              :bg-pos  "50% 35%"
              :desc    "Data structures & utilities: Disjoint sets, Interval Trees, collection helpers, binary data I/O"}
   :fourier  {:name    "thi.ng/fourier"
              :version "0.1.0"
              :stars   0
              :lit     true
              :loc     392
              :tags    ["audio" "analysis" "dataviz" "clojure"]
              :bg-uri  "fourier/hero.jpg"
              :bg-pos  "50% 60%"
              :desc    "Audio frequency analysis & visualization"}
   :geom     {:name    "thi.ng/geom"
              :version "0.0.881"
              :stars   182
              :lit     true
              :loc     13926
              :tags    ["2d" "3d" "math" "analysis" "animation" "fabrication" "dataviz" "generative" "mesh" "svg" "matrix" "physics" "voxel" "webgl" "clojure" "clojurescript"]
              :bg-uri  "geom/hero.jpg"
              :bg-pos  "50% 0%"
              :desc    "Comprehensive computational 2d / 3d geometry & visualization library"
              :module-base "https://github.com/thi-ng/geom/blob/master/"
              :modules [{:id   "geom-core"
                         :uri  "src/index.org"
                         :desc "protocols, vector algebra, intersections"}
                        {:id   "geom-meshops"
                         :uri  "src/index.org"
                         :desc "mesh operations, I/O, subdivisions"}
                        {:id   "geom-physics"
                         :uri  "src/index.org"
                         :desc "verlet physics engine, behaviors"}
                        {:id   "geom-svg"
                         :uri  "src/index.org"
                         :desc "SVG export & 3d rendering w/ software shaders"}
                        {:id   "geom-types"
                         :uri  "src/index.org"
                         :desc "2d / 3d geometry types"}
                        {:id   "geom-viz"
                         :uri  "src/index.org"
                         :desc "data visualization & charting"}
                        {:id   "geom-voxel"
                         :uri  "src/index.org"
                         :desc "sparse voxel trees, iso-surface generator"}
                        {:id   "geom-webgl"
                         :uri  "src/index.org"
                         :desc "WebGL rendering, shader lib & type conversions"}]}
   :fabric   {:name    "thi.ng/fabric"
              :version "0.0.376"
              :stars   19
              :lit     true
              :loc     4796
              :tags    ["dsl" "declarative" "async" "graph" "linked data" "query" "parser" "server" "dataviz" "clojure" "clojurescript"]
              :bg-uri  "fabric/hero.jpg"
              :bg-pos  "50% 15%"
              :desc    "Signal/Collect inspired Compute graph infrastructure, fact graph, query engine & linked data server"
              :module-base "https://github.com/thi-ng/fabric/blob/master/"
              :modules [{:id   "fabric-core"
                         :uri  "README.org"
                         :desc "protocols, compute graph core types"}
                        {:id   "fabric-facts"
                         :uri  "README.org"
                         :desc "fact graph, query engine, query DSL, query visualization, fact parsers"}
                        {:id   "fabric-ld"
                         :uri  "README.org"
                         :desc "linked data server & query enpoint"}]}
   :luxor    {:name    "thi.ng/luxor"
              :version "0.3.1"
              :stars   56
              :lit     true
              :loc     1309
              :tags    ["dsl" "3d" "graph" "conversion" "rendering" "clojure" "luxrender"]
              :bg-uri  "luxor/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    "Scene graph compiler, generator & mesh exporter for rendering with Luxrender"}
   :math     {:name    "thi.ng/math"
              :version "0.1.4"
              :stars   3
              :lit     true
              :loc     906
              :tags    ["interpolation" "math" "macros" "binary" "interop" "clojure" "clojurescript"]
              :bg-uri  "math/hero.jpg"
              :bg-pos  "50% 80%"
              :desc    "Useful math ops, bit manipulation, macro definitions to simplify complex equations"}
   :morpho   {:name    "thi.ng/morphogen"
              :version "0.1.1"
              :stars   74
              :lit     true
              :loc     895
              :tags    ["dsl" "generative" "fabrication" "3d" "mesh" "clojure" "clojurescript"]
              :bg-uri  "morphogen/hero.jpg"
              :bg-pos  "50% 40%"
              :desc    "3d form evolution through tree-based transformations"}
   :ndarray  {:name    "thi.ng/ndarray"
              :version "0.3.0"
              :stars   9
              :lit     true
              :loc     813
              :tags    ["matrix" "math" "2d" "3d" "4d" "macros" "clojure" "clojurescript"]
              :bg-uri  "ndarray/hero.jpg"
              :bg-pos  "50% 40%"
              :desc    "Multidimensional primitive arrays with almost zero-cost view transformations, isoline extraction"}
   :raymarch {:name    "thi.ng/raymarchcl"
              :version "0.1.0"
              :stars   106
              :loc     894
              :tags    ["rendering" "voxel" "opencl"  "clojure"]
              :bg-uri  "raymarchcl/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    [:div "Experimental OpenCL voxel raymarch renderer using "
                        [:a {:href "http://thi.ng/simplecl"} "thi.ng/simplecl"]]}
   :sgraph   {:name    "thi.ng/shadergraph"
              :version "0.1.1"
              :stars   11
              :lit     true
              :loc     554
              :tags    ["rendering" "graph" "gpu" "webgl" "macros" "clojurescript"]
              :bg-uri  "shadergraph/hero.jpg"
              :bg-pos  "50% 33%"
              :desc    "Composable WebGL / GLSL shader library & dependency graph resolution"}
   :simplecl {:name    "thi.ng/simplecl"
              :version "0.2.2"
              :stars   11
              :loc     701
              :tags    ["dsl" "interop" "opencl" "gpu" "clojure"]
              :bg-uri  "simplecl/hero.jpg"
              :bg-pos  "50% 0%"
              :desc    "OpenCL wrapper & highlevel processing pipeline for GPU computing"}
   :strf     {:name    "thi.ng/strf"
              :version "0.2.1"
              :stars   1
              :lit     true
              :loc     209
              :tags    ["formatting" "parser" "date" "clojure" "clojurescript"]
              :bg-uri  "strf/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    "Extensible string formatting, date formatting, number parsers"}
   :structg  {:name    "thi.ng/structgen"
              :version "0.2.1"
              :stars   4
              :loc     507
              :tags    ["interop" "binary" "parser" "conversion" "opencl"]
              :bg-uri  "structgen/hero.jpg"
              :bg-pos  "50% 60%"
              :desc    "Interop library for working with native C structs and binary formats"}
   :trio     {:name    "thi.ng/trio"
              :version "0.1.0"
              :stars   39
              :lit     true
              :loc     3066
              :tags    ["dsl" "graph" "linked data" "parser" "query" "declarative"]
              :bg-uri  "trio/hero.jpg"
              :bg-pos  "50% 40%"
              :desc    "Simple, extensible triplestore API and powerful SPARQL-inspired query engine"}
   :tarrays  {:name    "thi.ng/typedarrays"
              :version "0.1.2"
              :stars   2
              :lit     true
              :loc     133
              :tags    ["interop" "binary" "webgl" "clojurescript"]
              :bg-uri  "typedarrays/hero.jpg"
              :bg-pos  "50% 40%"
              :desc    "Clojurescript wrapper & convenience functions for JavaScript typed arrays"}
   :tweeny   {:name    "thi.ng/tweeny"
              :version "0.1.0-SNAPSHOT"
              :stars   5
              :loc     122
              :tags    ["interpolation" "animation" "math" "clojure" "clojurescript"]
              :bg-uri  "tweeny/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    "Keyframe interpolation of arbitrary nested values, completely customizable"}
   :validate {:name    "thi.ng/validate"
              :version "0.1.3"
              :stars   20
              :loc     383
              :tags    ["dsl" "validation" "declarative" "collections" "clojure" "clojurescript"]
              :bg-uri  "validate/hero.jpg"
              :bg-pos  "50% 80%"
              :desc    "Composable data validation & correction for structured data"}
   :xerror   {:name    "thi.ng/xerror"
              :version "0.1.0"
              :stars   0
              :loc     17
              :tags    ["error handling" "clojure" "clojurescript"]
              :bg-uri  "xerror/hero.jpg"
              :bg-pos  "50% 75%"
              :desc    "Error throwing functions for Clojure & Clojurescript"}})

(def project-sort-criteria
  {:loc   "by size"
   :stars "by popularity"
   :name  "by a-z"})

(def all-tags
  (->> projects
       (into #{} (mapcat (comp :tags val)))
       (reduce
        (fn [acc t] (assoc acc t t))
        (sorted-map))
       (#(assoc % "!all" "all"))))

(def counts
  ["zero" "one" "two" "three" "four" "five" "six" "seven" "eight" "nine" "ten"])

(defn on-js-reload []
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(defn project-modules
  [name base modules]
  [:div "This project contains " (counts (count modules)) " sub-modules:"
   [:dl
    (mapcat
     (fn [{:keys [id uri desc]}]
       [[:dt {:key (str name id)} [:a {:href (str base id "/" uri)} id]]
        [:dd {:key (str name id "desc")} desc]])
     modules)]])

(defn project-tag
  [name]
  (fn [t]
    [:span.tag
     {:key (str name "-" t)
      :on-click (fn [e]
                  (.preventDefault e)
                  (dispatch [:dd-change :tag-filter t]))} t]))

(defn project-section
  [i {:keys [name modules] :as proj}]
  (let [cls (if (even? i) "even" "odd")]
    [:section {:id name :class cls}
     [:div.section-bg
      {:style {:background-image (str "url(" project-img-base (:bg-uri proj) ")")
               :background-position (:bg-pos proj)}}
      [:div.bg-fade-right]
      [:h2 [:a {:href (str "//" name)} name]]]
     [:p (:desc proj)]
     [:p (->> (:tags proj) (sort) (map (project-tag name)))]
     [:div.row
      [:div (if modules {:class "col2"} {})
       [:table
        [:tr
         [:th "Leiningen:"]
         [:td
          [:input
           {:readOnly true
            :type     :text
            :value    (str "[" name " \"" (:version proj) "\"]")
            :on-click (fn [e] (.select (.-target e)))}]]]
        [:tr
         [:th "X-Ref:"]
         [:td
          [:a {:href (str "https://crossclj.info/doc/" name "/latest/index.html")}
           "CrossCLJ"] " / "
          [:a {:href (str "http://clojars.org/" name)}
           "clojars.org"]]]
        [:tr
         [:th [:abbr {:title "Literate programming format"} "Org-mode:"]]
         [:td (if (:lit proj) "yes" "no")]]
        [:tr
         [:th [:abbr {:title "Source Lines of Code"} "SLOC:"]]
         [:td (:loc proj)]]
        [:tr
         [:th "GitHub stars:"]
         [:td (:stars proj)]]]]
      [:div.col2
       (when modules
         (project-modules name (:module-base proj) modules))]]
     [:div.space "\u00a0"]]))

(defn dropdown
  [id opts]
  (let [dd (subscribe [id])
        top (str (* (dec (count opts)) 19) "px")]
    (fn [_ opts]
      (info id (:sel @dd))
      [:div.dropdown
       (if (:open? @dd)
         {:class "dd-open" :style {:margin-top (str "-" top)}}
         {:class "dd-closed"})
       (if (:open? @dd)
         (map
          (fn [[sel opt]]
            [:div
             {:key (str id "-" sel)
              :style {:top top}
              :on-click (fn [e] (.preventDefault e) (dispatch [:dd-change id sel]))}
             opt])
          opts)
         [:div
          {:on-click (fn [e] (.preventDefault e) (dispatch [:dd-open id]))}
          (get opts (:sel @dd))])])))

(defn main-panel
  []
  (let [psort   (subscribe [:project-sort])
        tfilter (subscribe [:tag-filter])]
    (fn []
      (let [order    (:sel @psort)
            tag      (:sel @tfilter)
            projects (if (= "!all" tag)
                       (vals projects)
                       (filter #((set (:tags %)) tag) (vals projects)))
            projects (sort-by order projects)
            projects (if (= :name order) projects (reverse projects))]
        [:div
         [:h1#projects "projects "
          [:div.dd-wrapper
           [dropdown :project-sort project-sort-criteria]
           [:small " / "]
           [dropdown :tag-filter all-tags]]]
         (map-indexed
          (fn [i p] (with-meta [project-section i p] {:key (:name p)}))
          projects)]))))

(defn main
  []
  (when (not @(subscribe [:inited?]))
    (dispatch-sync [:init-app]))
  (.initializeTouchEvents js/React true)
  (reagent/render-component
   [main-panel] (.getElementById js/document "app")))

(main)
