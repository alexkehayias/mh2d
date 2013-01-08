(ns mh2d.sprite
  (:use quil.core)
  (:use [mh2d.world :only [get-player-offset]]))

(def sprite-sheets
  "Hash of vector keywords to spritesheet files"
  [{:name             :player-walking
    :path             "player_walking.png"
    :frame-dimensions [63 100]
    :frame-length     4
    :frame-time       0.3}
   {:name             :player-still
    :path             "player_walking.png"
    :frame-dimensions [63 100]
    :frame-length     0
    :frame-time       0.3}])

(defn sprite-frames
  "Divide an image into rows based on dimensions and frame size.
  Returns a vector of x,y coordinates."
  [dimensions frame-dimensions]
  (let [[x y] dimensions
        [frame-x frame-y] frame-dimensions]
    (for [x (range 0 x frame-x)
          y (range 0 y frame-y)]
      [x y])))

(defn update-entity-frame [entity]
  (update-in entity [:action :frame-number] inc))

(defn get-frame
  ""
  [dimensions frame-dimensions img frame-number]
  (let [frames (vec (sprite-frames dimensions frame-dimensions))
        ;; If frame number is above frame index loop
        frame (get frames frame-number (get frames 0))
        [x y] frame
        [w h] frame-dimensions]
    (.get img x y w h)))

(defn sprite
  "Create a sprite based on a keyword and destination coords.
  Dispatch off of a keyword."
  [entity kind frame-number]
  ;; TODO clean this destructuring up
  (let [sheet (filter #(= kind (:name %)) sprite-sheets)
        sheet (get (vec sheet) 0)
        path (:path sheet)
        img (load-image path)
        dimensions (vector (.width img) (.height img))
        frame-dimensions (:frame-dimensions sheet)
        frame-n (:frame-length sheet)
        frame-number (if (>= frame-number frame-n)
                       0
                       (inc frame-number))]
    [(get-frame dimensions frame-dimensions img frame-number) frame-number] ))

;; TODO make sprites composable by layering them on per frame to
;; generate the correct frame