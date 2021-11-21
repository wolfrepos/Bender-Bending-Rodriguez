package io.github.oybek.xo

import io.github.oybek.xo.model.XO.X
import io.github.oybek.xo.model.ops.Board3x3.ops
import io.github.oybek.xo.model.ops.BoardOps.Syntax
import io.github.oybek.xo.model.{Board, Coord}

object Main extends App {
  println(Board.empty.put(Coord(-1, 0), X))
}
