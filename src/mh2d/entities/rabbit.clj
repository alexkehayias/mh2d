(ns mh2d.entities.rabbit)

(defrecord Rabbit [id position draw-position moving action])

(defn create-rabbit
  []
  (map->Rabbit {:position [0 0]
                :moving :still
                :action {:kind :player-still :frame-number 0}}))