(ns mh2d.core
  (:use quil.core)
  (:require [mh2d.engine :as engine])
  (:require [mh2d.input :as input]))

(defsketch gameloop
  :title "Experimental secret stuff!"
  :renderer :java2d
  :setup engine/setup
  :key-pressed input/key-press
  :key-released input/key-release
  :draw engine/draw
  :size [400 300])

(sketch-stop gameloop)
(sketch-start gameloop)