package io.github.oybek.xo.model

sealed trait Outcome
object Outcome {
  case object Xwin extends Outcome
  case object Owin extends Outcome
  case object Draw extends Outcome
}
