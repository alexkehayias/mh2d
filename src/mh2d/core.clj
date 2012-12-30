(ns mh2d.core
  (:use quil.core)
  (:require [mh2d.engine :as engine]))

(defsketch gameloop
  :title "Experimental secret stuff!"
  :renderer :java2d
  :setup engine/setup
  :key-pressed engine/key-press
  :key-released engine/key-release
  :draw engine/draw
  :size [400 300])

(sketch-stop gameloop)
(sketch-start gameloop)