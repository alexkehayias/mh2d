(ns mh2d.engine
  (:use quil.core)
  (:require [mh2d.world :as world])
  (:use [mh2d.input :only [moves update-entity-movement]])
  (:use [mh2d.world :only (->World)])
  (:use [mh2d.sprite :only [sprite]])
  (:import [mh2d.world World]))

(defrecord Player [id position draw-position image moving action])

(defprotocol Entity
  (tick [world]
    "Update the world based on a tick for this entity."))

(extend-type Player Entity
  (tick [world] ;;TODO update frame-number
    nil))

(defn setup []
  (set-state!
   :player (atom (->Player
                  :player
                  [-80 -80]
                  [(/ (width) 2) (/ (height) 2)]
                  (load-image "player_walking.png")
                  :still
                  {:kind :player-still :frame-number 0} )))
  (no-stroke)
  (smooth)
  (frame-rate 60))

(defn draw-entity
  "Draws an entity to the canvas. ONLY has side effects."
  [entity]
  (let [[x y] (:draw-position entity)
        action (:action entity)
        kind (:kind action)
        frame-number (:frame-number action)
        sprite (sprite entity kind frame-number)
        [img updated-frame-num] sprite]
    (image-mode :center)
    (image img x y)
    ;; TODO use entity ID so this isn't just for the player
    (reset! (state :player) (assoc-in entity [:action :frame-number] updated-frame-num))
    ))

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


(defn update-entity-position
  "Update the :position of a record and return a new record."
  [entity move]
  (update-in entity [:position] #(map + % move)))

(defn is-in-bounds
  "Determine if x, y coords are in bounds based on direction.
  The seemingly arbitrary if test values are to add some padding
  based on the entity image so it looks natural."
  [x y width height direction]
  (case direction
    :left (if (<= x -15)
            (boolean true)
            (boolean false))
    :up (if (<= y 0)
          (boolean true)
          (boolean false))
    :right (if (>= x (+ 10 (- width)))
             (boolean true)
             (boolean false))
    :down (if (>= y (+ 20 (- height)))
            (boolean true)
            (boolean false))
    (boolean true)))

(defn update-movement
  "Updates the start-x and start-y based on the :movement atom"
  [direction world]
  (let [move (moves direction)
        pl (state :player)
        player (deref pl)
        [player-x player-y] (:position player)
        [width height] (:dimensions world)]
    ;; Check if Player is in bounds
    (if (is-in-bounds player-x player-y width height direction)
      (reset! pl (update-entity-position player move))
      (reset! pl (update-entity-movement player :still)))))


(defn draw-background
  "Draw the background"
  []
  (fill 200)

  (rect 0 0 (width) (height)))

(defn draw []
  (let [world (->World (world/world-map) [-50 -50] [300 200])
        player (deref (state :player))
        move (:moving player)]
    (clear-frame)
    (when-not (= move :still) (update-movement move world))
    (draw-background)
    (world/draw-world world)
    (draw-entity (deref (state :player)))
    (dev-middleware world)))