(ns ^:figwheel-always thi.ng.site.workshop.core
  (:require-macros
   [reagent.ratom :refer [reaction]]
   [cljs-log.core :refer [debug info warn]])
  (:require
   [thi.ng.site.workshop.handlers :as handlers]
   [clojure.string :as str]
   [cljsjs.react :as react]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :refer [subscribe dispatch dispatch-sync]]))

(def workshops
  [{:title    "Data visualization with Clojure(script) & thi.ng (Level 1)"
    :date     "2 - 4 November 2015"
    :loc      "London, venue TBC"
    :audience "Beginner, intermediate Clojure knowledge"
    :capacity 10
    :price    "£575.00 (+ 20% VAT in UK only)"
    :topics   ["Clojure / Clojurescript core concepts" "Workflow & toolchain options" "Collections & sequences" "Data transformations w/ transducers" "Concurrency basics" "Protocols" "Data visualization techniques & charting" "CSV & JSON I/O" "2D/3D geometry generation & manipulation" "SVG generation" "Render exports" "Offline animation"]
    :shopify  {:store    "thi-ng-store.myshopify.com"
               :product  "Data visualization with Clojure, Clojurescript &amp; thi.ng (Level 1)"
               :handle   "data-visualization-with-clojure-clojurescript-thi-ng-level-1"
               :cart     "6860514181:1"
               :discount {:id "EARLY-LDN-1" :percent "15%" :num 5}}
    :desc     [:div
               "This workshop is aimed at people interested in using Clojure both in & outside the browser and learn how to deal with important aspects of producing, readable, considered visual representations of complex data."
               [:p
                "Over the course of the workshop we will develop a small project from scratch, producing a number of data visualizations in different formats and touch on all the important concepts to realise this mission:"]
               [:h3 "Scheduled topics"]
               [:ul
                [:li "Clojure language concepts & syntax, basic macros"]
                [:li "Clojure vs. Clojurescript comparison, planning split executions, trade-offs"]
                [:li "Development environment/toolchain setup"]
                [:li "Mutable and immutable data types, pros & cons"]
                [:li "Sequence abstractions & processing, laziness"]
                [:li "Representing structured data & custom types"]
                [:li "Polymorphic functions & protocols"]
                [:li "Composing processes using transducers"]
                [:li "Working with libraries"]
                [:li "Working with external data sources (GitHub, Twitter, data.gov.uk etc.)"]
                [:li "Parsing & serializing data (CSV, EDN, JSON, XML, RDF etc.)"]
                [:li "Transforming datasets into geometry"]
                [:li "Fundamental 2D/3D vector algebra (spaces, vectors, matrices, quaternions)"]
                [:li "Shape representation, manipulation & refinement"]
                [:li "Coordinate system transformations, axises, mapping, 2D -> 3D transforms"]
                [:li "Working with color (theory, color spaces, perception, cultural semantics)"]
                [:li "Using the thi.ng/geom visualization package"]
                [:li "Generating 2D/3D SVG outputs in Clojure & Clojurescript"]
                [:li "Layering information, shared axes, using size and/or colors"]
                [:li "Exporting 3D geometry/meshes (e.g. for 3D printing)"]
                [:li "Timebased data & interpolation"]
                [:li "Generating animated assets for post-production"]
                [:li "Rendering 3D data in high-def w/ open source Luxrender"]
                [:li "Last, but not least: Have fun!"]]
               [:h3 "Requirements"]
               [:p "Basic programming knowledge is expected, Clojure knowledge is optional."]]}

   {:title    "Data visualization with Clojure(script) & thi.ng (Level 2)"
    :date     "11-13 November 2015"
    :loc      "London, venue TBC"
    :audience "Intermediate Clojure knowledge"
    :capacity 10
    :price    "£575.00 (+ 20% VAT in UK only)"
    :topics   ["Advanced Clojure / Clojurescript concepts" "Macros" "DSL" "Channel based concurrency (CSP)" "Graphs" "Linked Data basics & queries" "Async server setup & components" "Interactive visualization" "Live coding" "SPA w/ Reactive UIs (using Reagent)" "SVG" "WebGL toolchain" "Realtime animation"]
    :shopify  {:store    "thi-ng-store.myshopify.com"
               :product  "Data visualization with Clojure, Clojurescript &amp; thi.ng (Level 2)"
               :handle   "data-visualization-with-clojure-clojurescript-thi-ng-level-2"
               :cart     "6860600709:1"
               :discount {:id "EARLY-LDN-2" :percent "15%" :num 5}}
    :desc     [:div
               "In this workshop we will deal with more complex data and build an interactive, responsive, browser based visualization app using " [:a {:href "http://linkeddata.org/"} "Linked data"] ". This task will involve creating a simple server backend and our frontend will be built as " [:a {:href "http://en.wikipedia.org/wiki/Single-page_application"} "Single-page application"] " using WebGL, Reagent, a Clojurescript wrapper around React.js."
               [:h3 "Scheduled topics"]
               [:ul
                [:li "Brief review of Clojure concepts"]
                [:li "Processing data in parallel and/or concurrently (promises, delays, futures, agents)"]
                [:li "Organizing code around core.async channels"]
                [:li "Brief graph theory & representations in code form"]
                [:li "Introduction to " [:a {:href "http://thi.ng/fabric"} "thi.ng/fabric"] " compute graph"]
                [:li "Using graphs as caching network to compute data efficiently"]
                [:li "Linked Data (LD) introduction & " [:a {:href "http://lov.okfn.org/dataset/lov"} "common vocabularies"]]
                [:li "Mapping CSV data to graphs using LD vocabularies"]
                [:li "Defining macros to simplify boilerplate & create a " [:abbr {:title "Domain Specific Language"} "DSL"]]
                [:li "Importing & querying Linked Data sets/graphs using a DSL"]
                [:li "Using Graphviz to debug queries & LD datasets"]
                [:li "Introduction to component driven workflow"]
                [:li "Setting up a simple LD server (using components)"]
                [:li "Building a UI with "
                 [:a {:href "https://github.com/bhauman/lein-figwheel"} "Figwheel"] ", "
                 [:a {:href "http://reagent-project.github.io"} "Reagent"] ", "
                 [:a {:href "https://github.com/Day8/re-frame/"} "Re-frame"] " (and " [:a {:href ""} "React.js"] ")"]
                [:li "Representing & transforming DOM fragments in Clojure(script)"]
                [:li "Event handling & event busses using core.async"]
                [:li "Routing UI state changes to view components, adding responsive features"]
                [:li "WebGL introduction, Clojurescript examples"]
                [:li "Visualizing data in SVG & WebGL"]
                [:li "Composing WebGL shaders from re-usable fragments"]]
               [:h3 "Requirements"]
               [:p "Intermediate Clojure knowledge is required in the interest of the whole group."]]}])

(defn on-js-reload []
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(defn shopify-button
  [bt]
  [:div
   [:div
    {:data-embed_type              :product
     :data-shop                    (:store bt)
     :data-product_name            (:product bt)
     :data-product_handle          (:handle bt)
     :data-has_image               false
     :data-display_size            :compact
     :data-redirect_to             :checkout
     :data-buy_button_text         "Book now"
     :data-button_background_color "ff0099"
     :data-button_text_color       "ffffff"}]
   #_[:script
      {:dangerouslySetInnerHTML {:__html "document.getElementById('ShopifyEmbedScript');"}}]
   [:noscript
    [:a.bt {:href (str "https://store-thi-ng.myshopify.com/cart/" (:cart bt))
            :target "_blank"} "Book now"]]])

(defn workshop-section
  [i ws]
  [:section
   [:div.limit
    [:h2.title (:title ws)]
    [:div.row
     [:div.col2
      [:table
       [:tbody
        [:tr [:th "Date:"] [:td (:date ws)]]
        [:tr [:th "Location:"] [:td (:loc ws)]]
        [:tr [:th "Audience:"] [:td (:audience ws)]]
        [:tr [:th "Capacity:"] [:td (:capacity ws) " participants"]]
        [:tr [:th "\u00a0"] [:td]]
        [:tr [:th "Price:"] [:td [:strong (:price ws)]]]
        (when-let [disc (-> ws :shopify :discount)]
          [:tr
           [:th "Discount:"]
           [:td.discount
            (:percent disc) " off for first " (:num disc) " participants"
            [:br] "Checkout code: " [:strong (:id disc)]]])
        [:tr [:th] [:td [shopify-button (:shopify ws)]]]]]]
     [:div.col2 (:desc ws)]]
    [:div.space]]
   [:div.topics
    [:div.limit.list (str/join " | " (:topics ws))]]
   [:div.space]])

(defn main-panel
  []
  [:div
   [:div.limit
    [:h1 "Upcoming workshops"]
    "The schedules for each workshop state the topics aimed and prepared for. They're realistic estimates based on similar workshops taught in the past. However, each workshop group is individual and unpredictable until the event and so please consider these as a roadmap, which of course we will try to follow as closely as possible!"]
   (map-indexed
    (fn [i p] (with-meta [workshop-section i p] {:key (str "ws" i)}))
    workshops)
   [:div.limit
    [:h1#teacher "Teacher"]
    [:div.row
     [:div.col2
      [:img {:src "/img/workshop/toxi.jpg" :alt "Karsten Schmidt portrait"}]
      [:br "Photo by " [:a {:href "http://manomine.net"} "Manomine"]]]
     [:div.col2
      [:h2 "Karsten Schmidt"]
      [:p "Karsten is an award-winning London based computational
designer and researcher merging code, design, art & craft skills.
Originally from East Germany and starting in the deep end of the early
8-bit demo scene (6502 assembly), for the past 27+ years he’s been
adopting a completely trans-disciplinary way of working and has been
laterally involved in a wide range of creative disciplines. With his
practice PostSpectacular, he’s been actively exploring current
possibilities at the intersection of software development, design, art
and education and has been applying this mixture hands-on to a variety
of fields: from ARM assembly & embedded programming to architecture,
branding, generative design, data visualization, digital fabrication,
games, interactive installations, motion graphics & music."]
      [:p "Karsten has been using Clojure daily since 2011, after
working with Java for 15 years. Since 2009 he's been teaching, (often
highly intensive) creative coding workshops internationally at various
universities, incl. ETH Zurich, CIID Copenhagen, UID Umeå, Bezalel
Academy Jerusalem, HEAD Geneva, UCP Porto."]
      [:p "When not creating, he regularly travels the world
consulting, lecturing and teaching workshops about coding, open source
and employing code as creative tool. He is a prolific contributor (and
founder) of several large open source projects, was an early
contributor to the Processing.org project and several books about
programming and digital design. His work has been featured and
exhibited internationally, including MoMA & Whitney New York, London
Design Museum, Barbican Centre. His work is part of the Victoria &
Albert Museum's permanent collection."]]]
    [:div.space]]
   [:div#tandc
    [:div.row.limit
     [:h1 "Terms & Conditions"]
     [:h2 "Cancellation"]
     "Tickets are refundable under the following conditions:"
     [:ul
      [:li "100% refund, if cancelled within 5 days of booking"]
      [:li "50% refund, if cancelled between 6-10 days of booking"]
      [:li [:strong "No refund, if cancelled after 10 days since booking"]]]
     "Of course, you're entitled to a full refund should a workshop be cancelled by the teacher."
     [:h2 "Venue details"]
     "Participants will be notified of the venue address and travel options at least 1 week prior to each workshop."
     [:h2 "Daily schedule & sustenance"]
     [:p "All workshops run from 10am - 5.30pm, incl. 1h lunch break and short coffee breaks (5 mins) every 2 hours (based on group decisions on the day)."]
     [:p "Food & drinks will be supplied (incl. in workshop fee). Any special dietary requirements must be notified at least 48h before the workshop start."]
     [:h2 "Materials"]
     [:p "All participants are required to bring their own laptop (OSX, Linux or Windows 7+). UK power points will be supplied."]
     [:p "All examples, exercises and other digital materials created during the workshop will be shared with participants at the end of the workshop."]
     [:h2 "Payments"]
     [:p "All payment processing is handled securely by " [:a {:href "http://shopify.com"} "Shopify"] "."]
     [:p "Accepted payment methods:"]
     [:span.fa-2x
      [:i.fa.fa-cc-visa] " "
      [:i.fa.fa-cc-mastercard] " "
      [:i.fa.fa-cc-amex] " "
      [:i.fa.fa-cc-paypal]]]
    [:div.row.limit
     [:h1 "Contact"]
     [:p
      "Get in touch via "
      [:a {:href (str "mailto:" "k" \@ "thi" \. "ng?subject" "=Workshop")} "email"]
      ", "
      [:a {:href "https://twitter.com/toxi"} "Twitter"]
      " or "
      [:a {:href "https://tinyletter.com/thi-ng"} "subscribe to the newsletter"] "."]]
    [:div.space]]])

(defn main
  []
  (when (not @(subscribe [:inited?]))
    (dispatch-sync [:init-app]))
  (.initializeTouchEvents js/React true)
  (reagent/render-component
   [main-panel] (.getElementById js/document "app")))

(main)
