(ns mh2d.world
  (:use quil.core)
  (:use [mh2d.entities.rabbit :only [create-rabbit]])
  (:use [mh2d.entities.player :only [get-player-offset]]))

(defrecord World [world-map entities])

(defrecord Tile [x y boundary image])

(defrecord WorldMap [tiles start dimensions])

(defn generate-test-tiles []
  (for [x (range 0 300 25)
        y (range 0 200 25)]
    (->Tile x y true "tile.png")))

(defn generate-world-map
  "Test world map vector. Returns a vector of Tile records."
  []
  (->WorldMap (generate-test-tiles) [-50 -50] [300 200]))

(defn generate-world []
  (->World (generate-world-map) {:rabbit (create-rabbit)}))

(defn draw-grid
  "Draw a box based on x,y coordinates."
  [x y dimension map-x map-y]
  (stroke-weight 1)
  (stroke 255 255 255)
  (fill 10 100 205)
  (rect x y dimension dimension)
  ;; Show the x coord in the grid
  (text-size 8)
  (fill 255)
  (text (str (- map-x)) (+ x 2) (+ y 8))
  (text (str (- map-y)) (+ x 2) (+ y 16)))

(defn draw-tile [world tile offset]
  (let [x (:x tile)
        y (:y tile)
        [offset-x offset-y] offset 
        boundary (:boundary tile)
        end-x (+ x offset-x)
        end-y (+ y offset-y)]
    (draw-grid end-x end-y 25 x y)))

(defn draw-world
  "Draw the tiles from the world map of a World record. Return
  the world"
  [world]
  (let [offset (get-player-offset world)
        tiles (get-in world [:world-map :tiles])]
    (doseq [tile tiles]
      (draw-tile world tile offset))
    world))

(defn read-tile-spec
  "Read in a file that contains the spec and output
  coordinate pair map."
  [spec]
  nil)