(ns mh2d.engine
  (:use quil.core)
  (:import java.awt.event.KeyEvent))

(defn setup []
  (set-state!
   :background? (atom false)
   :bg (create-graphics 300 300 :java2d)
   :player? (atom false)
   :player (load-image "crono_walks.gif")
   ;; Set the position of where the tiles should be drawn from
   :start-x (atom 0)
   :start-y (atom 0)
   :moving (atom :still))
  (no-stroke)
  (smooth)
  (frame-rate 30))

(defn grid-dimension []
  "Sets the default grid height and width"
  20)

(defn draw-grid
  "Draw a box based on x,y coordinates."
  [x y]
  (stroke-weight 1)
  (stroke 255 255 255)
  (fill 10 100 205)
  (rect x y (grid-dimension) (grid-dimension))
  ;; Show the x coord in the grid
  (text-size 8)
  (fill 255)
  (text (str x) (+ x 2) (+ y 8)))

;; Todo create a func for reading an input file and converting
;; it to tile coordinate map
(defn read-tile-spec
  "Read in a file that contains the spec and output
  coordinate pair map."
  [spec]
  nil)

(defn draw-character []
  (let [p (state :player?)]
    (if-not (deref p)
      (do
        (image-mode :center)
        (image (state :player) (/ (width) 2) (/ (height) 2) 40 40)
        ;;(reset! p true)
        ) 
      nil)))

(defn show-frame-rate []
  (text-size 18)  
  (fill 0)
  (text (str (current-frame-rate)) 10 25))

(defn clear-frame
  "Clear the frame"
  []
  (background 255))

(defn layer
  "Create a new PGraphics layer"
  [w, h]
  (let [l (create-graphics w h :java2d)]
    (.beginDraw l)
    (.background l (float 0))
    (.stroke l (float 0))
    (.fill l 0)
    (.rect l (float 0) (float 0 ) (float 300) (float 300))
    (.line l (float 0) (float 0 ) (float 300) (float 300))
    (.endDraw l)))

(defn tile
  "Tile the canvas by reading in a spec."
  [start-x start-y]
  (doseq [x (range start-x (width) (grid-dimension))
          y (range start-y (height) (grid-dimension))]
    (draw-grid x y)))

(defn draw-background
  "Check the global state if the background was drawn
  if not then draw the background."
  []
  (let [b (state :background?)
        bg (state :bg)]
    (if-not (deref b)
      (do
        (println "Drawing background")
        (.beginDraw bg)
        (.background bg (random 255))
        (.endDraw bg)
        (reset! b true))
      nil)))

(def moves {:up [0 5]
            :down [0 -5]
            :left [5 0]
            :right [-5 0]
            :still [0 0]})

(def valid-keys
  ;; Accepts keyboard directional pad and WASD
  {KeyEvent/VK_UP :up
   KeyEvent/VK_DOWN :down
   KeyEvent/VK_LEFT :left
   KeyEvent/VK_RIGHT :right
   \w :up
   \s :down
   \a :left
   \d :right})

(defn key-name-check [raw-key]
  (= processing.core.PConstants/CODED (int raw-key)))

(defn key-press
  "Handler when a keyboard key is pressed."
  []
  (let [raw-key (raw-key)
        the-key-code (key-code)
        ;; Get the exact key name
        the-key-pressed (if (key-name-check raw-key)
                          the-key-code
                          raw-key)
        ;; Check if it's valid otherwise return :still
        move (get valid-keys the-key-pressed :still)
        [start-x start-y] (move moves)]
    ;; TODO handle multiple keys pressed
    (reset! (state :moving) move)))

(defn key-release
  "Handler when a keyboard key is pressed."
  []
  (let [raw-key (raw-key)
        the-key-code (key-code)
        ;; Get the exact key name
        the-key-released (if (key-name-check raw-key)
                          the-key-code
                          raw-key)]
    (reset! (state :moving) :still)))

(defn update-movement
  "Updates the start-x and start-y based on the :movement atom"
  [direction]
  (let [[x y] (moves direction)]
    (swap! (state :start-x) + x)
    (swap! (state :start-y) + y)))

;; TODO multimethod for handling key presses

(defn draw []
  (clear-frame)
  (update-movement (deref (state :moving)))
  (tile (deref (state :start-x)) (deref (state :start-y)))
  (draw-character)
  (show-frame-rate))