(ns tic-tac-toe.core
  (:require [clojure.core.async :as async :refer [<! >!]]
            [clojure.string :as str]))


(defonce channel (async/chan (async/sliding-buffer 1)))

(def fill-chan
  (async/go-loop []
    (>! channel (rand-int 9))
    (<! (async/timeout 5000))
    (recur)))

(defn full-board?
  [board]
  (every? (fn [x] (not (= x "."))) board))

(defn row->string
  [row]
  (str/join (interpose "|" row)))


(defn handle-message
  [msg]
  #_(println msg))

(def drain-chan
  (async/go-loop [acc 0]
    (handle-message  (<! channel))
    (recur (inc acc))))

(defn new-game
  []
  {:board (vec (repeat 9 "."))
   :player "X"})

(defn new-player
  [player]
  (if (= player "X")
    "O"
    "X"))

(defn make-move
  [{:keys [board player] :as game} move]
  (if (= "." (nth board move))
    {:board (assoc board move player)
     :player (new-player player)}
    game))

(defn print-board
  [board]
  (->> board
       (partition 3)
       (map row->string)
       (interpose "------")
       (interpose "\n")
       (str/join)
       (println)))

(def winning-poitions
  [[0 1 2] [3 4 5] [6 7 8]
   [0 3 6] [1 4 7] [2 4 8]
   [0 4 8] [2 4 6]])

(defn find-winner
  [board positions]
  (let [items (map (fn [pos] (nth board pos)) positions)]
    (if (apply = items) (first items) nil)))