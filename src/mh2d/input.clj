(ns mh2d.input
  (:use quil.core)
  (:use [mh2d.state :only [game-state]])
  (:import java.awt.event.KeyEvent))

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

(defn update-entity-movement [entity move]
  (let [entity (assoc-in entity [:moving] move)]
    (if-not (= move :still)
      (assoc-in entity [:action :kind] :player-walking)
      (assoc-in entity [:action :kind] :player-still))))

;; TODO create an abstraction that auto adds an action to
;; key-press and key-release by keyword

(defn get-key
  "Return the keyboard key pressed by the user as a number
  or the letter value of the key."
  []
  (let [raw-key (raw-key)
        the-key-code (key-code)]
    ;; Get the exact key name
    (if (key-name-check raw-key)
      the-key-code
      raw-key)))

(defn key-press
  "Handler when a keyboard key is pressed."
  []
  (let [key-pressed (get-key)
        ;; Check if it's valid otherwise return :still
        move (get valid-keys key-pressed :still)
        world (:world @game-state)
        player (get-in world [:entities :player])]
    ;; TODO handle multiple keys pressed
    ;; WARNING this replaces current state so if it happens
    ;; while other things are calculating it may mess up the world
    (swap! game-state assoc-in [:world :entities :player] (update-entity-movement player move))))

(defn key-release
  "Handler when a keyboard key is pressed."
  ;; TODO multimethod for handling key presses
  []
  (let [key-released (get-key)
        world (:world @game-state)
        player (get-in world [:entities :player])]
    (swap! game-state assoc-in [:world :entities :player] (update-entity-movement player :still))))