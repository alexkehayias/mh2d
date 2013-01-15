(ns mh2d.engine
  (:use quil.core)
  (:require [mh2d.world :as world])
  (:use [mh2d.state :only [game-state]])  
  (:use [mh2d.entities.player :only [create-player
                                     update-player-movement]])
  (:use [mh2d.entities.core :only [draw-entities]]))

(defn setup
  "Setup for the Processing sketch. Establishes the initial world."
  []
  (let [world (world/generate-world)
        player (create-player :player world)
        world (assoc-in world [:entities :player] player)]
    (reset! game-state {:world world})
    (no-stroke)
    (smooth)
    (frame-rate 60)))

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
  "Threads a World to all dev middleware functions.
  All middleware must take a world as an arg and return a world."
  [world]
  (-> world
      (show-frame-rate)
      (show-player-xy))
  world)

(defn clear-frame
  "Clear the canvas frame."
  []
  (background 255))

(defn draw-background
  "Draw the background"
  [world]
  (fill 200)
  (rect 0 0 (width) (height))
  world)

(defn draw
  "Loops the game according to the setup function. Updates a
  World record by threading it through functions that return
  a new World."
  []
  (clear-frame)
  (let [world (:world @game-state)
        update-game-state (partial swap! game-state assoc-in [:world])]
    (-> world
        draw-background
        update-player-movement
        world/draw-world
        draw-entities
        dev-middleware
        update-game-state)))