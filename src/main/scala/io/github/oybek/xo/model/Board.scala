package io.github.oybek.xo.model

import io.github.oybek.xo.model.Outcome.{Draw, Win}
import io.github.oybek.xo.model.XorO.{O, X}

case class Board(xs: List[Coord], os: List[Coord]) {
  def turn: XorO =
    if (xs.length == os.length) X else O

  def put(coord: Coord): Board =
    turn match {
      case X => Board(coord :: xs, os)
      case O => Board(xs, coord :: os)
    }

  def freeCells: List[Coord] =
    List
      .tabulate(3, 3)(Coord)
      .flatten
      .filterNot(c => xs.contains(c) || os.contains(c))

  def outcome: Option[Outcome] =
    if (winCoords(xs).isDefined) Some(Win(X))
    else if (winCoords(os).isDefined) Some(Win(O))
    else if (freeCells.isEmpty) Some(Draw)
    else None

  def winCoords(coords: List[Coord]): Option[List[Coord]] =
    coords.combinations(3).find { coord =>
      coord.forall(_.x == coord.head.x) ||
        coord.forall(_.y == coord.head.y) ||
        coord.toSet == Set(Coord(0, 0), Coord(1, 1), Coord(2, 2)) ||
        coord.toSet == Set(Coord(0, 2), Coord(1, 1), Coord(2, 0))
    }

  override def toString: String =
    List.tabulate(3, 3) {
      case (x, y) =>
        if (xs.contains(Coord(x, y))) 'x'
        else if (os.contains(Coord(x, y))) 'o'
        else '.'
    }.map(_.mkString).mkString
}

object Board {
  def empty: Board = Board(Nil, Nil)
}
