package io.github.oybek.xo.model.ops

import io.github.oybek.xo.integration.Master
import io.github.oybek.xo.model.{Board, Coord}
import org.scalatest.funsuite.AnyFunSuite

class MasterSpec extends AnyFunSuite {
  test("debug") {
    val board = Board(
      List(Coord(1, 0), Coord(0, 1), Coord(1, 2)),
      List(Coord(0, 0), Coord(1, 1))
    )
    println(Master.play(board))
  }
}
