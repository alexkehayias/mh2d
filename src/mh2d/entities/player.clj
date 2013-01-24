(ns mh2d.entities.player
  (:use quil.core)
  (:use [mh2d.input :only [moves]])
  (:use [mh2d.entities.core :only [is-in-bounds
                                   update-entity-position
                                   update-entity-movement]]))

(defrecord Player [id position draw-position moving action]
    ;; :position is the players position on the map
    ;; :draw-position is the players location on canvas
    ;; :moving is the current moving status (defaults to :still)
    ;; :action contains current action and sprite
  )

(defn create-player
  "Return a Player record based on the world passed as an arg."
  [id world]
  (let [world-map (:world-map world)
        start (:start world-map)]
    (map->Player {:id id
                  :position start
                  :draw-position [(/ (width) 2) (/ (height) 2)]
                  :moving :still
                  :action {:kind :still :frame-number 0}})))

(defn update-player-movement
  "Update the world canvas based on the players position.
  If the player is moving that will be accounted for."
  [world]
  (let [player (get-in world [:entities :player])
        direction (:moving player)
        move (moves direction)        
        [player-x player-y] (:position player)
        [width height] (get-in world [:world-map :dimensions])
        update-world (partial assoc-in world [:entities :player])]
    ;; Check if Player is in bounds
    (if (is-in-bounds player-x player-y width height direction)
      (update-world (update-entity-position player move))
      (update-world (update-entity-movement player :still)))))