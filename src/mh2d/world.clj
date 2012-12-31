(ns mh2d.world
  (:use quil.core))

(defrecord World [world-map start-x start-y width height])

(defrecord Tile [x y boundary image])

(defn world-map
  "Test world map vector. Returns a vector of Tile records."
  []
  (for [x (range 0 300 25)
        y (range 0 200 25)]
    (->Tile x y true "tile.png")))

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

(defn get-player-offset
  "Translate the player position to canvas offset. At player 0,0
  the offset should be where the player is drawn."
  []
  (let [player (deref (state :player))
        [player-x player-y] (:position player)
        [player-screen-x  player-screen-y] (:draw-position player)
        end-x (+ player-x player-screen-x)
        end-y (+ player-y player-screen-y)]
    [end-x end-y]))

(defn draw-world
  "Draw the tiles from the world map of a World record."
  [world]
  (let [offset (get-player-offset)]
    (doseq [tile (:world-map world)]
      (draw-tile world tile offset))))

(defn read-tile-spec
  "Read in a file that contains the spec and output
  coordinate pair map."
  [spec]
  nil)