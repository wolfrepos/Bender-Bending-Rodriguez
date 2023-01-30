package io.github.oybek.xo.model

sealed trait XorO
object XorO {
  case object X extends XorO
  case object O extends XorO
}
