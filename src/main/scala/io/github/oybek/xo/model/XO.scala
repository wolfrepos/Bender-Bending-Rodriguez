package io.github.oybek.xo.model

sealed trait XO
object XO {
  case object X extends XO
  case object O extends XO
}
