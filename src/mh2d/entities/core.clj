(ns mh2d.entities.core
  (:use quil.core)
  (:use [mh2d.sprite :only [sprite]]))

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