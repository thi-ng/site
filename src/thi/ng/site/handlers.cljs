(ns thi.ng.site.handlers
  (:require-macros
   [reagent.ratom :refer [reaction]]
   [cljs-log.core :refer [debug info warn]])
  (:require
   [clojure.string :as str]
   [cljsjs.react :as react]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :refer [register-handler
                          register-sub
                          subscribe
                          dispatch]]))

(defn close-all-dropdowns
  [db]
  (reduce
   (fn [db id] (assoc-in db [id :open?] false))
   db [:project-sort :tag-filter]))

(register-handler
 :init-app
 (fn [db _]
   (assoc db
          :project-sort {:sel :loc :open? false}
          :tag-filter   {:sel "!all" :open? false}
          )))

(register-handler
 :dd-open
 (fn [db [_ id]]
   (info :dd-open id)
   (-> db
       (close-all-dropdowns)
       (assoc-in [id :open?] true))))

(register-handler
 :dd-change
 (fn [db [_ id sel]]
   (info :dd-change id sel)
   ;;(set! (-> js/window .-location .-hash) "#projects")
   (-> db
       (close-all-dropdowns)
       (assoc-in [id :sel] sel))))

(register-sub
 :project-sort (fn [db _] (reaction (:project-sort @db))))

(register-sub
 :tag-filter (fn [db _] (reaction (:tag-filter @db))))
