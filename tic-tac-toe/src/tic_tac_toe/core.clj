(ns tic-tac-toe.core
  (:require [clojure.core.async :as async :refer [<! >!]]
            [clojure.string :as str]))


(defonce channel (async/chan (async/sliding-buffer 1)))

(defn handle-message
  [msg]
  (println msg))

(def drain-chan
  (async/go-loop []
    (handle-message  (<! channel))
    (recur)))

(def fill-chan
  (async/go-loop []
    (>! channel (rand-int 9))
    (<! (async/timeout 5000))
    (recur)))
