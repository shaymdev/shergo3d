(ns shergo3d.plate
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [shergo3d.util :refer :all]
            [unicode-math.core :refer :all]))


;;;;;;;;;;;;;;;;;
;; Switch Hole ;;
;;;;;;;;;;;;;;;;;

(def keyswitch-height 14.4) ;; Was 14.1, then 14.25
(def keyswitch-width 14.4)

(def plate-thickness 4)
(def wall-thickness 2);;need to fine tune this so switches are close to gether

(def old-single-hole
  (let [top-wall (->> (cube (+ keyswitch-width (twice wall-thickness)) wall-thickness plate-thickness)
                      (translate [0
                                  (+ (half wall-thickness) (half keyswitch-height))
                                  (half plate-thickness)]))
        right-wall (->> (cube wall-thickness (+ keyswitch-height (twice wall-thickness)) plate-thickness)
                       (translate [(+ (half wall-thickness) (half keyswitch-width))
                                   0
                                   (half plate-thickness)]))
        side-nub (->> (with-fn 30 (cylinder 1 2.75))
                      (rotate (/ π 2) [1 0 0])
                      (translate [(+ (half keyswitch-width)) 0 1])
                      (hull (->> (cube wall-thickness 2.75 plate-thickness)
                                 (translate [(+ (half wall-thickness) (half keyswitch-width))
                                             0
                                             (half plate-thickness)]))))
        hole-half (union top-wall right-wall (with-fn 100 side-nub))]
    (union hole-half
           (->> hole-half
                (mirror [1 0 0])
                (mirror [0 1 0])))))

(defn translate-switch-x
  [t]
  (translate [(+ keyswitch-height (twice wall-thickness)) 0 0] t))

(defn translate-switch-y
  [t]
  (translate [0 (+ keyswitch-height (twice wall-thickness)) 0] t))

(def column-holes
  (union old-single-hole
         (->> old-single-hole
              translate-switch-x)
         (->> old-single-hole
              translate-switch-x
              translate-switch-x)))

;;offsets assume index finger column is "home"
(def birdie-offset 7.2)
(def ring-offset 1.2)
(def pinkie-offset -7.9)
(def index-reach-offset -2.5)

(def right-plate
  (union (->> column-holes
              (translate [index-reach-offset (- (+ keyswitch-width (twice wall-thickness))) 0]))
         column-holes
         (->> column-holes
              (translate [birdie-offset (+ keyswitch-width (twice wall-thickness)) 0]))
         (->> column-holes
              (translate [ring-offset (* 2 (+ keyswitch-width (twice wall-thickness))) 0]))
         (->> column-holes
              (translate [pinkie-offset (* 3 (+ keyswitch-width (twice wall-thickness))) 0]))
         (->> column-holes
              (translate [pinkie-offset (* 4 (+ keyswitch-width (twice wall-thickness))) 0]))
         ))

(spit "things/switch-hole.scad"
      (write-scad right-plate))

(def dsa-width 18.415)
(half (- dsa-width keyswitch-height))
(half dsa-width)
