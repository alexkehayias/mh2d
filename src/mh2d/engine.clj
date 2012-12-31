(ns mh2d.engine
  (:use quil.core)
  (:require [mh2d.world :as world])
  (:use [mh2d.world :only (->World)])
  (:import [mh2d.world World])
  (:import java.awt.event.KeyEvent))

(defrecord Player [id position image])

(defprotocol Entity
  (tick [world]
    "Update the world based on a tick for this entity."))

(extend-type Player Entity
  (tick [world]
    nil))

(defn setup []
  (set-state!
   :tile-width 30
   ;;:world (atom (world/world-map))
   :bg (create-graphics 300 300 :java2d)
   :player (atom (->Player :player [-50 -50] (load-image "crono_walks.gif"))) 
   :player-position [(/ (width) 2) (/ (height) 2)]
   :moving (atom :still))
  (no-stroke)
  (smooth)
  (frame-rate 60))

(defn draw-character []
  (let [player (deref (state :player))
        [x y] (state :player-position)]
    (image-mode :center)
    (image (:image player) x y 40 40)))

(defn show-frame-rate [world]
  (text-size 18)  
  (fill 0)
  (text (str "FR: "(current-frame-rate)) 10 25)
  world)

(defn show-player-xy [world]
  (let [player (deref (state :player))
        [x y] (:position player)]
    (text-size 18)
    (fill 0)
    (text (str "Player xy: " x "," y) 10 50))
  nil)

(defn dev-middleware
  "Threads a world record to all dev middleware functions.
  All middleware must take a world as an arg and return a world."
  [world]
  (-> world
      (show-frame-rate)
      (show-player-xy)))

(defn clear-frame
  "Clear the frame"
  []
  (background 255))

(def moves {:up [0 5]
            :down [0 -5]
            :left [5 0]
            :right [-5 0]
            :still [0 0]})

(def valid-keys
  ;; Accepts keyboard directional pad and WASD
  {KeyEvent/VK_UP :up
   KeyEvent/VK_DOWN :down
   KeyEvent/VK_LEFT :left
   KeyEvent/VK_RIGHT :right
   \w :up
   \s :down
   \a :left
   \d :right})

(defn key-name-check [raw-key]
  (= processing.core.PConstants/CODED (int raw-key)))

(defn key-press
  "Handler when a keyboard key is pressed."
  []
  (let [raw-key (raw-key)
        the-key-code (key-code)
        ;; Get the exact key name
        the-key-pressed (if (key-name-check raw-key)
                          the-key-code
                          raw-key)
        ;; Check if it's valid otherwise return :still
        move (get valid-keys the-key-pressed :still)
        [start-x start-y] (move moves)]
    ;; TODO handle multiple keys pressed
    (reset! (state :moving) move)))

(defn key-release
  "Handler when a keyboard key is pressed."
  ;; TODO multimethod for handling key presses
  []
  (let [raw-key (raw-key)
        the-key-code (key-code)
        ;; Get the exact key name
        the-key-released (if (key-name-check raw-key)
                          the-key-code
                          raw-key)]
    (reset! (state :moving) :still)))

(defn update-position
  "Update the :position of a record and return a new record."
  [record x y]
  (update-in record [:position] #(map + % [x y])))

(defn update-movement
  "Updates the start-x and start-y based on the :movement atom"
  [direction]
  (let [[x y] (moves direction)
        player (deref (state :player))]
    (reset! (state :player) (update-position player x y))))

(defn draw-background
  "Draw the background"
  []
  (fill 200)
  (rect 0 0 (width) (height)))

(defn draw []
  (let [world (->World (world/world-map) -50 -50)
        direction (deref (state :moving))]
    (clear-frame)
    (update-movement direction)
    (draw-background)
    (world/draw-world world)
    (draw-character)
    (dev-middleware world)))