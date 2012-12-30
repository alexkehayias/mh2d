(ns mh2d.world
  (:use quil.core))

(defrecord World [coll start-x start-y])

(defrecord Tile [x y boundary? image])

(defn world-map
  "Test world map vector. Returns a vector of Tile records."
  []
  (for [x (range 0 200 30)
        y (range 0 200 30)]
    (->Tile x y false "tile.png")))

(defn draw-grid
  "Draw a box based on x,y coordinates."
  [x y dimension]
  (stroke-weight 1)
  (stroke 255 255 255)
  (fill 10 100 205)
  (rect x y dimension dimension)
  ;; Show the x coord in the grid
  (text-size 8)
  (fill 255)
  (text (str x) (+ x 2) (+ y 8))
  (text (str y) (+ x 2) (+ y 16)))

(defn draw-tile [tile]
  (let [x (:x tile)
        y (:y tile)
        start-x (deref (state :start-x))
        start-y (deref (state :start-y))]
    (draw-grid (+ start-x x) (+ start-y y) 30)))

(defn draw-world
  "Draw the tiles from the world map"
  [world-map]
  (doseq [tile world-map]
    (draw-tile tile)))

(defn read-tile-spec
  "Read in a file that contains the spec and output
  coordinate pair map."
  [spec]
  nil)

(defn tile
  "Tile the canvas by reading in a spec."
  [start-x start-y grid-dimension]
  ;; TODO update this to loop through the world map
  (doseq [x (range start-x 300 grid-dimension)
          y (range start-y 200 grid-dimension)]
    (draw-grid x y grid-dimension)))