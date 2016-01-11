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
  [{:title    "Interactive DIY Synth & embedded GUIs: Getting started with ARM programming (2 days)"
    :date     "23 - 24 January 2016"
    :loc      "North London"
    :audience "Beginner/intermediate embedded device programming"
    :capacity 7
    :soldout  true
    :id       "WS-LDN-4"
    :price    (list "£320.00 (+ 20% VAT in UK only)," [:br] "(includes STM32F746G discovery board, £40)")
    :topics   ["ARM Cortex-M overview" "Embedded C with Eclipse & GCC" "Debugging" "Hardware Abstraction Layer" "GPIO" "Multitasking & interrupts" "Interactive touchscreen GUIs" "Digital Audio & DSP fundamentals" "USB device basics" "MIDI over USB" "Generative music techniques"]
    :shopify  {:store    "thi-ng-store.myshopify.com"
               :product  "Interactive DIY Synth &amp; embedded GUIs: Getting started with ARM programming"
               :handle   "interactive-diy-synth-embedded-guis-getting-started-with-arm-programming"
               :cart     "9775120325:1"
               :discount {:id "WS-LDN-4-EARLY" :num 3 :percent "25%"}}
    :extras   [:div
               [:p [:img {:src "/img/workshop/stm32f7-disco.jpg" :alt "STM32F746-DISCO board" :width "100%"}]]
               [:p "We'll be using the STM32F746 discovery board, featuring a 200MHz Cortex-M7 CPU, 1MB Flash, 340KB RAM, 4.3\" LCD touchscreen, 2x USB, 44.1kHz audio, 2x microphone, microSD card, Ethernet"]
               [:p "Work-in-progress sound samples: " [:a {:href "https://soundcloud.com/forthcharlie/sets/stm32f4"} "soundcloud.com/forthcharlie/sets/stm32f4"]]
               [:iframe
                {:width "100%" :height "450" :scrolling "no" :frameBorder "no"
                 :src "https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/playlists/159061174&amp;auto_play=false&amp;hide_related=false&amp;show_comments=true&amp;show_user=true&amp;show_reposts=false&amp;visual=true"}]]
    :desc     [:div
               "This workshop is NOT related to Clojure and instead will introduce you to the exciting world of programming embedded devices outside the world of Arduino, working 'bare-metal' with the much more powerful ARM Cortex processor family."
               [:p " We will use the " [:a {:href "http://www.st.com/web/en/catalog/tools/FM116/SC959/SS1532/LN1848/PF261641"} "STM32F746 discovery board"] " (included in workshop fee) and " [:strong "learn how to create a fully featured application: Our own polyphonic stereo MIDI synthesizer, sequencer with an interactive touchscreen GUI to control the instrument"] " - all of which you can take home later. This is not just going to be a lo-fi noise box!"]
               [:p "Commented source code scaffolding will be provided to maximize time for experimentation."]
               [:h3 "Scheduled topics"]
               [:ul
                [:li "Introduction to ARM CPU family and the STM32 development board"]
                [:li "Open source toolchain setup (Eclipse, GCC, OpenOCD, ST-Link)"]
                [:li "Overview/review of important C language concepts"]
                [:li "Basic examples (clock config, timers, GPIO, 8x LED, LCD display)"]
                [:li "Overview of multi-tasking via interrupts"]
                [:li "Digital audio introduction, theory & experimentation"]
                [:li "Intro to USB device classes & file systems (play/record WAV files)"]
                [:li "Synthesizer DSP graph overview, audio/music theory, experimentation"]
                [:li "Introduction to MIDI & integrating with synthesizer / sequencer"]
                [:li "Generative music composition techniques (scales, cellular automata etc.)"]
                [:li "Manipulate the synth via touchscreen GUI controls"]
                [:li "Project development / making music"]]
               [:h3 "Requirements"]
               [:p "This workshop is going to be fast paced, but primarily intended for beginners to embedded development. Previous programming experience in Arduino/C/C++ or Processing/Java/Python etc. is desired, but coding will be kept to a minimum for time reasons"]
               [:p "All participants need to bring the following items:"]
               [:ul
                [:li "OSX / Linux / Windows7+ laptop"]
                [:li "Headphones"]
                [:li "USB Memory stick (MS-DOS formatted)"]
                [:li "USB OTG cable"]
                [:li "USB MIDI controller if you have one (e.g. Korg NanoKey / NanoKontrol2)"]]
               [:p "Included in workshop fee:"]
               [:ul
                [:li [:a {:href "http://www.st.com/web/catalog/tools/FM116/CL1620/SC959/SS1532/LN1848/PF261641"} "STM32F7-DISCOVERY development board"]]
                [:li "Food & hot drinks"]]
               [:p "Please also see " [:a {:href "#tandc"} "Terms & Conditions"] " below."]]}
   {:title    "thi.ng x Studio NAND - Clojure(script) data visualization workshop (3 days)"
    :date     "17 - 19 February 2016"
    :id       "WS-BLN-1"
    :loc      "Berlin Mitte"
    :audience "Intermediate Clojure knowledge"
    :capacity 12
    :price    "£445 professionals, £150 students (+ 20% VAT, UK only)"
    :topics   ["Clojure / Clojurescript concepts" "Live coding workflow" "Interactive SVG & WebGL visualization" "Reactive SPA (using Reagent)" "Shape generation/manipulation" "Shader composition" "Linked Data basics & queries" "core.async dataflow graphs"]
    :shopify  {:store    "thi-ng-store.myshopify.com"
               :product  "thi.ng x Studio NAND - Clojure(script) workshop, Berlin"
               :handle   "thi-ng-x-studionand-clojurescript-workshop-berlin"
               :cart     "10035927621:1"
               :capacity 8
               :discount nil #_{:id "WS-BLN-1" :num 2 :percent "25%"}}
    :extras   [:p "If you're a student, please first " [:a {:href "mailto:k@thi.ng?subject=WS-BLN-1 student discount"} "get in touch via email"] " before ordering. Valid student card is required to be eligble for discount. Only 2 student places available."]
    :desc     [:div
               "In this workshop, held in partnership with " [:a {:href "http://nand.io"} "Studio NAND"] ", we will focus on creating small 2D/3D visualizations in both Clojure & Clojurescript, i.e. with & without the browser (using " [:a {:href "http://electron.atom.io/"} "Elektron"] "). Using various projects from the thi.ng collection, this workshop will introduce you to many concepts, techniques related to Clojure, WebGL/GLSL, data modeling and dataflow, in a practical creative coding context:"
               [:ul
                [:li "Tooling & REPL-driven workflow alternatives"]
                [:li "Overview of Clojure concepts (collections, higher-order functions, protocols, polymorphism, macros)"]
                [:li "Overview of " [:a {:href "http://thi.ng/"} "thi.ng"] " projects used for workshop"]
                [:li "Concurrency basics (promises, delays, futures, agents, atoms, refs)"]
                [:li "Dataflow core.async introduction"]
                [:li "Using graph data structures, theory, linked data & code representations"]
                [:li "Importing & querying Linked Data sets/graphs using a DSL"]
                [:li "Setting up a simple server (using components)"]
                [:li "Building reactive UI with "
                 [:a {:href "https://github.com/bhauman/lein-figwheel"} "Figwheel"] " and "
                 [:a {:href "http://reagent-project.github.io"} "Reagent"]]
                [:li "Representing & transforming DOM fragments in Clojure(script)"]
                [:li "Geometry, vector algebra & WebGL introduction, Clojurescript examples"]
                [:li "Shape & mesh generation / manipulation"]
                [:li "Working with color spaces, mapping colors"]
                [:li "Creating visualizations with SVG & WebGL"]
                [:li "WebGL shaders, composing shaders from re-usable fragments"]
                [:li "Event handling & event busses using core.async"]
                [:li "Animating & interacting with a 3D scene"]
                [:li "Using an Entity Component System for flexible state handling"]]
               [:p "All participants need to bring the following items:"]
               [:ul
                [:li "OSX / Linux / Windows7+ laptop"]
                [:li "Java JDK 7+ installed"]]
               [:p "Please also see " [:a {:href "#tandc"} "Terms & Conditions"] " below."]]}])

(def prev-workshops
  [{:title    "Special workshop: DIY Synth - Getting started with bare-metal ARM programming"
    :date     "5-6 December 2015"
    :url      "https://github.com/thi-ng/ws-ldn-3"
    :id       "WS-LDN-3"
    :loc      "North London"
    :audience "Beginner/intermediate embedded device programming"
    :topics   ["ARM Cortex-M overview" "Embedded C programming with Eclipse & GCC toolchain" "Debugging" "Hardware Abstraction Layer" "GPIO" "Multitasking & interrupts" "USB device basics" "Digital Audio & DSP fundamentals" "MIDI over USB" "Music theory" "Generative music techniques"]
    :report   {:url "https://soundcloud.com/forthcharlie/sets/stm32f4" :title "Audio examples (Soundcloud)"}}
   {:title    "Data visualization with Clojure(script) & thi.ng (Level 2)"
    :date     "11-13 November 2015"
    :url      "https://github.com/thi-ng/ws-ldn-2/"
    :id       "WS-LDN-2"
    :loc      "London"
    :audience "Intermediate Clojure knowledge"
    :topics   ["Advanced Clojure / Clojurescript concepts" "Channel based concurrency (CSP / core.async)" "Graphs" "Linked Data basics & queries" "Async server setup & components" "Interactive SVG heatmaps" "Live coding" "SPA w/ Reactive UIs (using Reagent)" "Query visualization"]
    :report   {:url "https://medium.com/@thi.ng/workshop-report-building-linked-data-heatmaps-with-clojurescript-thi-ng-102e0581225c" :title "Workshop report (blog post)"}}
   {:title    "Data visualization with Clojure(script) & thi.ng (Level 1)"
    :date     "2-4 November 2015"
    :url      "https://github.com/thi-ng/ws-ldn-1/"
    :id       "WS-LDN-1"
    :loc      "London"
    :audience "Beginner, intermediate Clojure knowledge"
    :topics   ["Clojure / Clojurescript core concepts" "Workflow & toolchain options" "Collections & sequences" "Data transformations w/ transducers" "Protocols" "Data visualization techniques, mapping & charting" "CSV parsing & transformation" "2D/3D geometry generation & manipulation" "SVG generation / mapping" "React.js / Reagent examples" "WebGL basics"]}])

(defn on-js-reload
  [])

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
  [:section {:key (str "ws" i) :id (:id ws)}
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
        [:tr [:th "Price:"] [:td (if-let [p (:price ws)] [:strong (:price ws)] "TBC")]]
        (when (or (:soldout ws) (:shopify ws))
          (list
           (when-not (:soldout ws)
             (when-let [disc (-> ws :shopify :discount)]
               [:tr
                [:th "Discount:"]
                [:td.discount
                 (:percent disc) " off" (if-let [n (:num disc)] (str " for first " n " participants"))
                 [:br] "Checkout code: " [:strong (:id disc)]]]))
           [:tr [:th] [:td (if (:soldout ws) [:strong "SOLD OUT"] [shopify-button (:shopify ws)])]]))]]
      (:extras ws)]
     [:div.col2 (:desc ws)]]
    [:div.space]]
   [:div.topics
    [:div.limit.list (str/join " | " (:topics ws))]]
   [:div.space]])

(defn current-workshops
  [workshops]
  [:div
   [:div.limit
    [:h1 "Upcoming workshops"]
    "The schedules for each workshop state the topics aimed and prepared for. They're realistic estimates based on similar workshops taught in the past. However, each workshop group is individual and unpredictable until the event and so please consider these as a roadmap, which of course we will try to follow as closely as possible!"]
   (map-indexed workshop-section workshops)])

(defn workshop-summary
  [i ws]
  [:section {:key (str "ws-summary" i)}
   [:div.limit
    [:h2.title (:title ws)]
    [:div.row
     [:div.col2
      [:table
       [:tbody
        [:tr [:th "Date:"] [:td (:date ws)]]
        [:tr [:th "Location:"] [:td (:loc ws)]]
        [:tr [:th "Audience:"] [:td (:audience ws)]]
        [:tr [:th "Repository:"] [:td [:a {:href (:url ws)} "Github repo"]]]
        (when-let [rep (:report ws)]
          [:tr [:th "Report:"] [:td [:a {:href (:url rep)} (:title rep)]]])]]]
     [:div.col2
      [:ul
       (map-indexed (fn [ii t] [:li {:key (str "wsum" i "-" ii)} t]) (:topics ws))]]]
    [:div.space]]])

(defn previous-workshops
  [workshops]
  [:div
   [:div.limit
    [:h1 "Previous workshops"]
    "You can find details of past workshops below. For each workshop, all key exercises are recorded in a public Github repository for future reference:"]
   (map-indexed workshop-summary workshops)])

(defn bio-component
  []
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
   [:div.space]])

(defn tandc-component
  []
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
    [:p "If food & drinks are included in workshop fee (as per workshop description), you'll need to inform us of any special dietary requirements at least 48h before the workshop start."]
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
   [:div.space]])

(defn main-panel
  []
  [:div
   [current-workshops workshops]
   [previous-workshops prev-workshops]
   [bio-component]
   [tandc-component]])

(defn main
  []
  (when (not @(subscribe [:inited?]))
    (dispatch-sync [:init-app]))
  (.initializeTouchEvents js/React true)
  (reagent/render-component
   [main-panel] (.getElementById js/document "app")))

(main)
