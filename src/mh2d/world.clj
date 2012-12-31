(ns mh2d.world
  (:use quil.core))

(defrecord World [world-map start-x start-y])

(defrecord Tile [x y boundary image])

(defn world-map
  "Test world map vector. Returns a vector of Tile records."
  []
  (for [x (range 0 300 30)
        y (range 0 200 30)]
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
  (text (str map-x) (+ x 2) (+ y 8))
  (text (str map-y) (+ x 2) (+ y 16)))

(defn draw-tile [tile]
  (let [x (:x tile)
        y (:y tile)
        boundary (:boundary tile)
        [start-x start-y] (deref (state :start))
        end-x (+ start-x x)
        end-y (+ start-y y)
        [player-x player-y] (state :player-position)]
    ;; Check if Player is in bounds
    ;; (if-not (boolean (some #{player-x} (range start-x (+ start-x 30)))) 
    ;;   (reset! (state :moving) :still))
    ;; (if (true? boundary)
    ;;   (reset! (state :moving) :still))
    (draw-grid end-x end-y 30 x y)))

(defn draw-world
  "Draw the tiles from the world map of a World record."
  [world]
  (doseq [tile (:world-map world)]
    (draw-tile tile)))

(defn get-offset
  "Translate the player position to canvas offset"
  []
  
  )

(defn read-tile-spec
  "Read in a file that contains the spec and output
  coordinate pair map."
  [spec]
  nil)