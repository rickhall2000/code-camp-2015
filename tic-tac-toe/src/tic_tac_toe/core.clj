(ns tic-tac-toe.core
  (:require [clojure.core.async :as async :refer [<! >!]]
            [clojure.string :as str]))


(defonce channel (async/chan (async/sliding-buffer 1)))

(defn handle-message
  [msg]
  #_(println (* msg msg)))

(def drain-chan
  (async/go-loop [a 0]
    (handle-message  (<! channel))
    (recur (inc a))))

(def fill-chan
  (async/go-loop []
    (>! channel (rand-int 9))
    (<! (async/timeout 5000))
    (recur)))

(defn new-game
  []
  {:board (vec (repeat 9 "."))
   :player "X"})

(defn row->str
  [row]
  (str/join (interpose "|" row)))

(defn print-board
  [board]
  (->> board
       (partition 3)
       (map row->str)
       (interpose "-----")
       (interpose "\n")
       (str/join)
       (println)))

(defn next-player
  [player]
  (if (= "X" player)
    "O"
    "X"))

(defn move
  [{:keys [board player] :as game} move]
  (if (= "." (nth board move))
    {:board (assoc board move player)
     :player (next-player player)}
    game))

(defn destruct
  [ [a b c & d] ]
  d)


