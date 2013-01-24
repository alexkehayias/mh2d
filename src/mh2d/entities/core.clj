(ns mh2d.entities.core
  (:use quil.core)
  (:use mh2d.state)
  (:use [mh2d.sprite :only [sprite]]))

(defn get-player-offset
  "Translate the player position to canvas offset. At player 0,0
  the offset should be where the player is drawn."
  [world]
  (let [player (get-in world [:entities :player])
        [player-x player-y] (:position player)
        [player-screen-x  player-screen-y] (:draw-position player)
        end-x (+ player-x player-screen-x)
        end-y (+ player-y player-screen-y)]
    [end-x end-y]))

(defn draw-entity
  "Draws an entity to the canvas. Returns an updated World."
  [id world]
  (let [entity (get-in world [:entities id])
        ;; TODO if there are more conditionals break this out
        [x y] (if (= id :player)
                (:draw-position entity)
                (map + (:position entity) (get-player-offset world)))
        action (:action entity)
        kind (:kind action)
        frame-number (:frame-number action)
        sprite (sprite entity kind frame-number)
        [img updated-frame-num] sprite]
    (image-mode :center)
    (image img x y)
    (assoc-in world [:entities (:id entity) :action :frame-number] updated-frame-num)))

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
    :left (if (<= x -15) true false)
    :up (if (<= y 0) true false)
    :right (if (>= x (+ 10 (- width))) true false)
    :down (if (>= y (+ 20 (- height))) true false)
    true))

(defn draw-entities
  "Takes a World record and draws each entity as a side-effect.
  Returns world with all entities updated."
  [world]
  (loop [world world
         ids (keys (:entities world))]
    (if (seq ids)
      (recur (draw-entity (first ids) world) (rest ids))        
      world)))

(defn update-entity-movement [entity move]
  (let [entity (assoc-in entity [:moving] move)]
    (if-not (= move :still)
      (assoc-in entity [:action :kind] :walking)
      (assoc-in entity [:action :kind] :still))))

(defn move-entity-start
  "Move an entity by a [x,y] value."
  ;; WARNING this replaces current state so if it happens
  ;; while other things are calculating it may mess up the world  
  [entity_id move]
  (let [world (:world @game-state)
        entity (get-in world [:entities entity_id])]
    (swap! game-state assoc-in
           [:world :entities entity_id]
           (update-entity-movement entity move))))

(defn move-entity-end
  "Stop an entity from moving."
  [entity_id]
  (let [world (:world @game-state)
        entity (get-in world [:entities entity_id])]
    (swap! game-state assoc-in
           [:world :entities entity_id]
           (update-entity-movement entity :still))))