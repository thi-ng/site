(ns ^:figwheel-always thi.ng.site.core
  (:require-macros
   [reagent.ratom :refer [reaction]]
   [cljs-log.core :refer [debug info warn]])
  (:require
   [thi.ng.site.handlers :as handlers]
   [clojure.string :as str]
   [cljsjs.react :as react]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :refer [subscribe dispatch dispatch-sync]]))

(def projects
  {:babel    {:name    "thi.ng/babel"
              :version "0.3.2.1"
              :target  [:emacs]
              :stars   30
              :lit     true
              :loc     98
              :tags    ["template" "literate programming"]
              :bg-uri  "/img/projects/babel/hero.jpg"
              :bg-pos  "50% 50%"
              :desc    "Project template for polyglot, literate programming with Emacs & Org-mode"}
   :color    {:name    "thi.ng/color"
              :version "1.0.0"
              :target  [:clj :cljs]
              :stars   21
              :lit     true
              :loc     1742
              :tags    ["color" "dataviz" "interop" "conversion"]
              :bg-uri  "/img/projects/color/hero.jpg"
              :bg-pos  "50% 100%"
              :desc    "Color space conversions (RGB, HSV, HSL, CSS, CMYK, HCY, YUV etc.), presets & gradients"}
   :crypto   {:name    "thi.ng/crypto"
              :version "1.0.0-SNAPSHOT"
              :target  [:clj]
              :stars   2
              :loc     178
              :tags    ["encryption" "interop"]
              :bg-uri  "/img/projects/crypto/hero.jpg"
              :bg-pos  "50% 50%"
              :desc    "GPG keypair generation, encryption & decryption facilities"}
   :domus    {:name    "thi.ng/domus"
              :version "0.2.0"
              :target  [:cljs]
              :stars   1
              :lit     true
              :loc     505
              :tags    ["dom" "async" "formatting" "interop"]
              :bg-uri  "/img/projects/domus/hero.jpg"
              :bg-pos  "50% 0%"
              :desc    "DOM generation, manipulation, async event bus and browser utilities"}
   :dstruct  {:name    "thi.ng/dstruct"
              :version "0.1.1"
              :target  [:clj :cljs]
              :stars   1
              :lit     true
              :loc     473
              :tags    ["graph" "collections" "binary"]
              :bg-uri  "/img/projects/dstruct/hero.jpg"
              :bg-pos  "50% 50%"
              :desc    "Data structures & utilities: Disjoint sets, Interval Trees, collection helpers, binary data I/O"}
   :fourier  {:name    "thi.ng/fourier"
              :version "0.1.0"
              :target  [:clj]
              :stars   0
              :lit     true
              :loc     392
              :tags    ["audio" "analysis" "dataviz"]
              :bg-uri  "/img/projects/fourier/hero.jpg"
              :bg-pos  "50% 50%"
              :desc    "Audio frequency analysis & visualization"}
   :geom     {:name    "thi.ng/geom"
              :version "0.0.881"
              :target  [:clj :cljs]
              :stars   182
              :lit     true
              :loc     13926
              :tags    ["2d" "3d" "math" "analysis" "animation" "fabrication" "dataviz" "generative" "mesh" "svg" "matrix" "physics" "voxel" "webgl"]
              :bg-uri  "/img/projects/geom/hero.jpg"
              :bg-pos  "50% 0%"
              :desc    "Comprehensive computational 2d / 3d geometry & visualization library"
              :modules [{:id   "geom-core"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-core/src/index.org"
                         :desc "core module"}
                        {:id   "geom-meshops"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-meshops/src/index.org"
                         :desc "mesh operations, I/O, subdivisions"}
                        {:id   "geom-physics"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-physics/src/index.org"
                         :desc "verlet physics engine"}
                        {:id   "geom-svg"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-svg/src/index.org"
                         :desc "SVG export & 3d rendering w/ software shaders"}
                        {:id   "geom-types"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-types/src/index.org"
                         :desc "2d / 3d geometry types"}
                        {:id   "geom-viz"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-viz/src/index.org"
                         :desc "data visualization & charting"}
                        {:id   "geom-voxel"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-voxel/src/index.org"
                         :desc "sparse voxel trees, iso-surface generator"}
                        {:id   "geom-webgl"
                         :uri  "https://github.com/thi-ng/geom/blob/master/geom-webgl/src/index.org"
                         :desc "WebGL rendering, shader lib & type conversions"}]}
   :fabric   {:name    "thi.ng/fabric"
              :version "0.0.376"
              :target  [:clj :cljs]
              :stars   19
              :lit     true
              :loc     4796
              :tags    ["dsl" "declarative" "async" "graph" "linked data" "query" "parser" "server" "dataviz"]
              :bg-uri  "/img/projects/fabric/hero.jpg"
              :bg-pos  "50% 0%"
              :desc    "Signal/Collect inspired Compute graph infrastructure, fact graph, query engine & linked data server"
              :modules [{:id   "fabric-core"
                         :uri  "https://github.com/thi-ng/fabric/blob/master/fabric-core/README.org"
                         :desc "compute graph core module"}
                        {:id   "fabric-facts"
                         :uri  "https://github.com/thi-ng/fabric/blob/master/fabric-facts/README.org"
                         :desc "fact graph, query engine, query visualization, fact parsers"}
                        {:id   "fabric-ld"
                         :uri  "https://github.com/thi-ng/fabric/blob/master/fabric-ld/README.org"
                         :desc "linked data server & query enpoint"}]}
   :luxor    {:name    "thi.ng/luxor"
              :version "0.3.1"
              :target  [:clj :lux]
              :stars   56
              :lit     true
              :loc     1309
              :tags    ["dsl" "3d" "graph" "conversion" "rendering"]
              :bg-uri  "/img/projects/luxor/hero.jpg"
              :bg-pos  "50% 60%"
              :desc    "Scene graph compiler, generator & mesh exporter for rendering with Luxrender"}
   :math     {:name    "thi.ng/math"
              :version "0.1.4"
              :target  [:clj :cljs]
              :stars   3
              :lit     true
              :loc     906
              :tags    ["interpolation" "math" "macros" "binary" "interop"]
              :bg-uri  "/img/projects/math/hero.jpg"
              :bg-pos  "50% 80%"
              :desc    "Useful math ops, bit manipulation, macro definitions to simplify complex equations"}
   :morpho   {:name    "thi.ng/morphogen"
              :version "0.1.1"
              :target  [:clj :cljs]
              :stars   74
              :lit     true
              :loc     895
              :tags    ["dsl" "generative" "fabrication" "3d" "mesh"]
              :bg-uri  "/img/projects/morphogen/hero.jpg"
              :bg-pos  "50% 50%"
              :desc    "3d form evolution through tree-based transformations"}
   :ndarray  {:name    "thi.ng/ndarray"
              :version "0.3.0"
              :target  [:clj :cljs]
              :stars   9
              :lit     true
              :loc     813
              :tags    ["matrix" "math" "2d" "3d" "4d" "macros"]
              :bg-uri  "/img/projects/ndarray/hero.jpg"
              :bg-pos  "50% 40%"
              :desc    "Multidimensional arrays with almost zero-cost view transformations"}
   :raymarch {:name    "thi.ng/raymarchcl"
              :version "0.1.0"
              :target  [:clj :opencl]
              :stars   106
              :loc     894
              :tags    ["rendering" "voxel" "opencl"]
              :bg-uri  "/img/projects/raymarchcl/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    "Experimental OpenCL voxel raymarch renderer using thi.ng/simplecl"}
   :sgraph   {:name    "thi.ng/shadergraph"
              :version "0.1.1"
              :target  [:clj :cljs :glsl]
              :stars   11
              :lit     true
              :loc     554
              :tags    ["rendering" "graph" "gpu" "webgl" "macros"]
              :bg-uri  "/img/projects/shadergraph/hero.jpg"
              :bg-pos  "50% 33%"
              :desc    "Composable WebGL / GLSL shader library & dependency graph resolution"}
   :simplecl {:name    "thi.ng/simplecl"
              :version "0.2.2"
              :target  [:clj :opencl]
              :stars   11
              :loc     701
              :tags    ["dsl" "interop" "opencl" "gpu"]
              :bg-uri  "/img/projects/simplecl/hero.jpg"
              :bg-pos  "50% 0%"
              :desc    "OpenCL wrapper & highlevel processing pipeline for GPU computing"}
   :strf     {:name    "thi.ng/strf"
              :version "0.2.1"
              :target  [:clj :cljs]
              :stars   1
              :lit     true
              :loc     209
              :tags    ["formatting" "parser" "date"]
              :bg-uri  "/img/projects/strf/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    "Extensible string formatting, date formatting, number parsers"}
   :structg  {:name    "thi.ng/structgen"
              :version "0.2.1"
              :target  [:clj :opencl]
              :stars   4
              :loc     507
              :tags    ["interop" "binary" "parser" "conversion" "opencl"]
              :bg-uri  "/img/projects/structgen/hero.jpg"
              :bg-pos  "50% 50%"
              :desc    "Interop library for working with native C structs and binary formats"}
   :trio     {:name    "thi.ng/trio"
              :version "0.1.0"
              :target  [:clj :cljs]
              :stars   39
              :lit     true
              :loc     3066
              :tags    ["dsl" "graph" "linked data" "parser" "query" "declarative"]
              :bg-uri  "/img/projects/trio/hero.jpg"
              :bg-pos  "50% 33%"
              :desc    "Simple, extensible triplestore API and powerful SPARQL-inspired query engine"}
   :tarrays  {:name    "thi.ng/typedarrays"
              :version "0.1.2"
              :target  [:cljs]
              :stars   2
              :lit     true
              :loc     133
              :tags    ["interop" "binary" "webgl"]
              :bg-uri  "/img/projects/typedarrays/hero.jpg"
              :bg-pos  "50% 50%"
              :desc    "Clojurescript wrapper & convenience functions for JavaScript typed arrays"}
   :tweeny   {:name    "thi.ng/tweeny"
              :version "0.1.0-SNAPSHOT"
              :target  [:clj :cljs]
              :stars   5
              :loc     122
              :tags    ["interpolation" "animation" "math"]
              :bg-uri  "/img/projects/tweeny/hero.jpg"
              :bg-pos  "50% 70%"
              :desc    "Keyframe interpolation of arbitrary nested values, completely customizable"}
   :validate {:name    "thi.ng/validate"
              :version "0.1.3"
              :target  [:clj :cljs]
              :stars   20
              :loc     383
              :tags    ["dsl" "validation" "declarative" "collections"]
              :bg-uri  "/img/projects/validate/hero.jpg"
              :bg-pos  "50% 80%"
              :desc    "Composable data validation & correction for structured data"}
   :xerror   {:name    "thi.ng/xerror"
              :version "0.1.0"
              :target  [:clj :cljs]
              :stars   0
              :loc     17
              :tags    ["error handling"]
              :bg-uri  "/img/projects/xerror/hero.jpg"
              :bg-pos  "50% 50%"
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

(def targets
  {:clj    "Clojure"
   :cljs   "Clojurescript"
   :opencl "OpenCL"
   :lux    "Luxrender"
   :glsl   "GLSL"
   :emacs  "Emacs & Org-mode"})

#_(def clients
  ["Barbican" "Google" "Open Data Institute" "Unicredit" "Nike" "Leeds College of Music" "Resonate Festival" "SAC Städelschule Frankfurt" "FNB" "Victoria & Albert Museum" "Moving Brands"])

#_(enable-console-print!)

(defn on-js-reload []
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(defn project-modules
  [name modules]
  [:div "This project contains several sub-modules:"
   [:ul
    (map-indexed
     (fn [i {:keys [id uri desc]}]
       [:li {:key (str name id)}
        [:a {:href uri} id] " - " desc])
     modules)]])

(defn project-section
  [i {:keys [name bg-uri bg-pos desc modules version stars target tags lit loc]}]
  (let [cls (if (even? i) "even" "odd")]
    [:section {:class cls}
     [:div.section-bg
      {:style {:background-image (str "url(" bg-uri ")")
               :background-position bg-pos
               }}
      [:div.bg-fade-right]
      [:h2 [:a {:href (str "//" name)} name]]]
     [:p desc]
     [:p (map
          (fn [t]
            [:span.tag
             {:key (str name "-" t)
              :on-click (fn [e] (.preventDefault e) (dispatch [:dd-change :tag-filter t]))} t])
          (sort tags))]
     [:div.row
      [:div (if modules {:class "col2"} {})
       [:table
        [:tr [:th "Leiningen:"] [:td "[" name " \"" version "\"]"]]
        [:tr [:th "Target:"] [:td (str/join ", " (map targets target))]]
        [:tr [:th "Literate format:"] [:td (if lit "yes" "no")]]
        [:tr [:th [:abbr {:title "Source Lines of Code"} "SLOC:"]] [:td loc]]
        [:tr [:th "GitHub stars:"] [:td stars]]
        ]]
      [:div.col2
       (when modules
         (project-modules name modules))]]
     [:div.space "\u00a0"]]))

(defn dropdown
  [id opts]
  (let [dd (subscribe [id])
        top (str (* (dec (count opts)) 19) "px")]
    (fn [_ opts]
      (info id (:sel @dd))
      [:div.dropdown
       (if (:open? @dd)
         {:class "dd-open" :style {:margin-top (str "-" top)}
          }
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

#_(defn header
  []
  [:header
   [:img#logo {:src "/img/logo-anim-white.min.svg" :alt "thi.ng logo"}]])

#_(defn intro
  []
  [:div#intro
   [:div.row
    [:p [:strong "An open source collection of 20+ computational design tools for Clojure & Clojurescript"]]]
   [:div#news
    "Sign up for intensive 3-day " [:a {:href "http://workshop.thi.ng"} [:span "workshops in London!"] [:span.link-alt "Yes, let's do it!"]]]
   [:div.row
    [:div.col2
     [:p "In active development since 2012, and totalling over 32,000 lines of code, the libraries address concepts related to many design displines, from " [:strong "animation, generative design, digital fabrication, data analysis / validation / visualization, 2d / 3d geometry, meshing, voxel modeling & rendering, linked data graphs & querying, encryption etc."]]]
    [:div.col2
     [:p "Many of the thi.ng projects (especially the larger ones) are written in a "
      [:a {:href "https://en.wikipedia.org/wiki/Literate_programming"} "literate programming style"]
      " and include extensive documentation, diagrams and tests. All projects are licensed under the "
      [:a {:href "http://www.apache.org/licenses/LICENSE-2.0"} "Apache Software License 2.0"] "."]]
    [:div.row
     [:div.col2
      [:p "A project by " [:a {:href "https://twitter.com/toxi"} "Karsten Schmidt"] ", who is currently available for work."]]]]
   [:div.space "\u00a0"]])

#_(defn client-list
  []
  [:div#clients
   "Used in projects for: "
   [:span.list
    (->> clients
         (sort)
         (str/join " | "))]])

#_(defn heatmap
  []
  [:div#heatmap
   [:h1 "activity"]
   [:div [:img {:src "/img/all-commits.svg"}]]])

#_(defn footer
  []
  [:footer
   [:a {:href "https://github.com/thi-ng/"} [:i.fa.fa-github] " thi-ng"]
   " | "
   [:a {:href "https://twitter.com/thing_clj"} [:i.fa.fa-twitter] " @thing_clj"]
   " | "
   [:a {:href "https://twitter.com/toxi"} [:i.fa.fa-twitter] " @toxi"]
   " | © 2015 Karsten Schmidt"])

(defn main-panel
  []
  (let [psort   (subscribe [:project-sort])
        tfilter (subscribe [:tag-filter])]
    (fn []
      (let [order     (:sel @psort)
            tag       (:sel @tfilter)
            projects' (if (= "!all" tag)
                        (vals projects)
                        (filter #((set (:tags %)) tag) (vals projects)))
            projects' (sort-by order projects')
            projects' (if (= :name order) projects' (reverse projects'))]
        [:div
         [:h1#projects "projects "
          [:div.dd-wrapper
           [dropdown :project-sort project-sort-criteria]
           [:small " / "]
           [dropdown :tag-filter all-tags]]]
         (map-indexed
          (fn [i p] (with-meta [project-section i p] {:key (:name p)}))
          projects')]))))

(defn main
  []
  (dispatch-sync [:init-app])
  (.initializeTouchEvents js/React true)
  (reagent/render-component
   [main-panel] (.getElementById js/document "app")))

(main)
