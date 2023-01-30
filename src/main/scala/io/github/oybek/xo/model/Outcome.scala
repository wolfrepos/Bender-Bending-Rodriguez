package io.github.oybek.xo.model

sealed trait Outcome
object Outcome {
  case class Win(who: XorO) extends Outcome
  case object Draw extends Outcome
}
