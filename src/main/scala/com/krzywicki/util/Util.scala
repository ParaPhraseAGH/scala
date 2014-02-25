package com.krzywicki.util

import scala.collection.generic.CanBuildFrom
import scala.util.Random

object Util {

  implicit class Shuffled[T, CC[X] <: TraversableOnce[X]](xs: CC[T]) {
    def shuffled(implicit bf: CanBuildFrom[CC[T], T, CC[T]]) = Random.shuffle(xs)
  }

}