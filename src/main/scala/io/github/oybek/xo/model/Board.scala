package io.github.oybek.xo.model

case class Board(xs: List[Coord], os: List[Coord]) {
  def show: String =
    List.tabulate(3, 3) {
      case (x, y) =>
        if (xs.contains(Coord(x, y))) 'x'
        else if (os.contains(Coord(x, y))) 'o'
        else '.'
    }.map(_.mkString(" ")).mkString("\n")
}

object Board {
  def empty: Board = Board(Nil, Nil)
}
