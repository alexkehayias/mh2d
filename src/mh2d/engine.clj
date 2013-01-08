(ns mh2d.engine
  (:use quil.core)
  (:require [mh2d.world :as world])
  (:use [mh2d.input :only [moves update-entity-movement]])
  (:use [mh2d.world :only (->World)])
  (:use [mh2d.sprite :only [sprite]])
  (:import [mh2d.world World]))

(defrecord Player
    ":position is the players position on the map
    :draw-position is the players location on canvas
    :moving is the current moving status (defaults to :still)
    :action contains current action and sprite"
    [id position draw-position moving action])

(defprotocol Entity
  (tick [world]
    "Update the world based on a tick for this entity."))

(extend-type Player Entity
  (tick [world] ;;TODO update frame-number
    nil))

(defn create-player
  "Return a Player record based on the world passed as an arg."
  [world]
  (let [world-map (:world-map world)
        start (:start world-map)]
    (map->Player {:id :player
                  :position start
                  :draw-position [(/ (width) 2) (/ (height) 2)]
                  :moving :still
                  :action {:kind :player-still :frame-number 0}})))

(defn setup
  "Setup for the Processing sketch. Establishes the initial world."
  []
  (let [world (world/generate-world)
        player (create-player world)
        world (assoc-in world [:entities :player] player)]
    (set-state! :world world)
    (no-stroke)
    (smooth)
    (frame-rate 60)))

(defn draw-entity
  "Draws an entity to the canvas. ONLY has side effects."
  [id world]
  (let [entity (get-in world [:entities id])
        [x y] (:draw-position entity)
        action (:action entity)
        kind (:kind action)
        frame-number (:frame-number action)
        sprite (sprite entity kind frame-number)
        [img updated-frame-num] sprite]
    (image-mode :center)
    (image img x y)
    (assoc-in world [:entities (:id entity) :action :frame-number] updated-frame-num)))

(defn show-frame-rate [world]
  (text-size 18)  
  (fill 0)
  (text (str "FR: "(current-frame-rate)) 10 25)
  world)

(defn show-player-xy [world]
  (let [player (get-in world [:entities :player])
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
      (show-player-xy))
  world)

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

(defn update-player-movement
  "Updates the start-x and start-y based on the :movement atom"
  [world]
  (let [player (get-in world [:entities :player])
        direction (:moving player)
        move (moves direction)        
        [player-x player-y] (:position player)
        [width height] (:dimensions world)
        update-world (partial assoc-in world [:entities :player])]
    ;; Check if Player is in bounds
    (if (is-in-bounds player-x player-y width height direction)
      (update-world (update-entity-position player move))
      (update-world (update-entity-movement player :still)))))

(defn draw-background
  "Draw the background"
  [world]
  (fill 200)
  (rect 0 0 (width) (height))
  world)

(defn draw
  "Loops the game according to the setup function. Updates a
  World record and resets it."
  []
  (let [world (state :world)]
    (clear-frame)
    (->> world
         (draw-background)
         (update-player-movement)
         (world/draw-world)
         (draw-entity :player)
         (set-state! :world) 
         (dev-middleware))))