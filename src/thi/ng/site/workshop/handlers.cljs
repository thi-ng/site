(ns thi.ng.site.workshop.handlers
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

(register-handler
 :init-app
 (fn [db _]
   (assoc db :inited? true)))

(register-sub
 :inited? (fn [db _] (reaction (:inited? @db))))
