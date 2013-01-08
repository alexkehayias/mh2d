(ns mh2d.entities.core
  (:use quil.core)
  (:use [mh2d.sprite :only [sprite]])
  (:use [mh2d.entities.player :only [get-player-offset]]))

(defn draw-entity
  "Draws an entity to the canvas. Returns an updated World."
  [id world]
  (let [entity (get-in world [:entities id])
        [x y] (map + (:position entity) (get-player-offset world)) 
        action (:action entity)
        kind (:kind action)
        frame-number (:frame-number action)
        sprite (sprite entity kind frame-number)
        [img updated-frame-num] sprite]
    (image-mode :center)
    ;; FIX apply an offset based on the player position so that
    ;; the image doesn't stay fixed
    (image img x y)
    ;; FIX This doesn't get updated since world is not recycled
    (assoc-in world [:entities (:id entity) :action :frame-number] updated-frame-num)))

(defn draw-player
  "Draw the player in the middle of the screen"
  [world]
  (let [entity (get-in world [:entities :player])
        [x y] (:draw-position entity)
        action (:action entity)
        kind (:kind action)
        frame-number (:frame-number action)
        sprite (sprite entity kind frame-number)
        [img updated-frame-num] sprite]
    (image-mode :center)
    ;; FIX apply an offset based on the player position so that
    ;; the image doesn't stay fixed
    (image img x y)
    ;; FIX This doesn't get updated since world is not recycled
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

(defn draw-entities
  "Takes a World record and draws each entity as a side-effect.
  returns world."
  [world]
  (doseq [entity-id (keys (:entities world))]
    ;; FIX World object gets updated on each pass but only the
    ;; original world is passed back
    (if (not= entity-id :player)
      (draw-entity entity-id world)
      (draw-player world)))
  world)