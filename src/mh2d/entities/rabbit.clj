(ns mh2d.entities.rabbit)

(defrecord Rabbit [id position draw-position moving action])

(defn create-rabbit
  [id]
  (map->Rabbit {:id id
                :position [0 0]
                :moving :still
                :action {:kind :still :frame-number 0}}))