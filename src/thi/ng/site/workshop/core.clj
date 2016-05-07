(ns thi.ng.site.workshop.core
  (:require
   [clojure.string :as str]
   [hiccup.core :refer [html]]
   [clojure.java.io :as io]
   [clojure.edn :as edn]))

(def workshops
  (->> ["ws-ldn-9.edn"
        "ws-ldn-10.edn"
        "ws-ldn-11.edn"]
       (mapv #(edn/read-string (slurp (str "resources/workshops/" %))))))

(def prev-workshops
  (->> ["ws-ldn-8.edn"
        "ws-ldn-7.edn"
        "ws-beo-1.edn"
        "ws-ldn-6.edn"
        "ws-ldn-5.edn"
        "ws-bln-1.edn"
        "ws-ldn-4.edn"
        "ws-ldn-3.edn"
        "ws-ldn-2.edn"
        "ws-ldn-1.edn"]
       (mapv #(edn/read-string (slurp (str "resources/workshops/" %))))))

(defn shopify-button
  [bt]
  [:div
   [:div
    {:data-embed_type                          :product
     :data-shop                                (:store bt)
     :data-product_name                        (:product bt)
     :data-product_handle                      (:handle bt)
     :data-has_image                           :false
     :data-display_size                        :compact
     :data-redirect_to                         :checkout
     :data-buy_button_text                     "Book now"
     :data-buy_button_out_of_stock_text        "Out of Stock"
     :data-buy_button_product_unavailable_text "Unavailable"
     :data-button_background_color             "ff0099"
     :data-button_text_color                   "ffffff"}]
   [:noscript
    [:a.bt {:href (str "https://store-thi-ng.myshopify.com/cart/" (:cart bt))
            :target "_blank"} "Book now"]]])

(defn workshop-section
  [i ws]
  [:section {:id (:id ws)}
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
               (map-indexed
                (fn [i disc]
                  [:tr
                   [:th (if (zero? i) "Discount:")]
                   [:td.discount
                    (:percent disc) (if-let [n (:num disc)] (str " for first " n " participants"))
                    [:br] "Checkout code: " [:strong (:id disc)]]])
                (if (sequential? disc) disc [disc]))))
           [:tr [:th] [:td (if (:soldout ws) [:strong "SOLD OUT"] (shopify-button (:shopify ws)))]]))]]
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
        (when-let [repo (:url ws)]
          [:tr [:th "Repository:"] [:td [:a {:href (:url ws)} "Github repo"]]])
        (when-let [rep (:report ws)]
          [:tr [:th "Report:"] [:td [:a {:href (:url rep)} (:title rep)]]])]]]
     [:div.col2
      [:ul
       (map-indexed (fn [ii t] [:li t]) (:topics ws))]]]
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
    [:p "All workshops run from 10am - 5.45pm, incl. 1h lunch break and short coffee breaks (5 mins) every 2 hours (based on group decisions on the day)."]
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
   (current-workshops workshops)
   (previous-workshops prev-workshops)
   (bio-component)
   (tandc-component)])

(defn main
  []
  (let [tpl  (slurp "resources/public/workshop.html")
        body (html (main-panel))
        body (str/replace tpl "{{{app}}}" body)]
    (spit "resources/public/workshop-static.html" body)))

(main)
