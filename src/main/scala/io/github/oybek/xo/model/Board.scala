package io.github.oybek.xo.model

case class Board(xs: List[Coord], os: List[Coord])

object Board {
  def empty: Board = Board(Nil, Nil)
}
