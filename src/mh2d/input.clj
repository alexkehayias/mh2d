(ns mh2d.input
  (:use quil.core)
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
        player (deref (state :player))]
    ;; TODO handle multiple keys pressed
    (reset! (state :player) (update-entity-movement player move))))

(defn key-release
  "Handler when a keyboard key is pressed."
  ;; TODO multimethod for handling key presses
  []
  (let [raw-key (raw-key)
        the-key-code (key-code)
        ;; Get the exact key name
        the-key-released (if (key-name-check raw-key)
                           the-key-code
                           raw-key)
        player (deref (state :player))]
    (reset! (state :player) (update-entity-movement player :still))))