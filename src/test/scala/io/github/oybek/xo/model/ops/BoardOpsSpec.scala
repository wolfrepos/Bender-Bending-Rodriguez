package io.github.oybek.xo.model.ops

import io.github.oybek.xo.model.Outcome.Win
import io.github.oybek.xo.model.XorO.X
import io.github.oybek.xo.model.{Board, Coord}
import org.scalatest.funsuite.AnyFunSuite

class BoardOpsSpec extends AnyFunSuite {
  test("put") {
    assert(
      Board.empty.put(Coord(0, 0)) == Board(Coord(0, 0)::Nil, Nil)
    )
  }

  test("outcome") {
    assert(Board.empty.outcome.isEmpty)
    assert(Board(List(Coord(0, 0), Coord(0, 1), Coord(1, 1)), Nil).outcome.isEmpty)
    assert(Board(List(Coord(0, 0), Coord(0, 1), Coord(0, 2)), Nil).outcome.contains(Win(X)))
  }

  test("turn") {
    assert(Board.empty.turn == X)
  }

  test("freeCells") {
    assert(Board.empty.freeCells.toSet ==
      List.tabulate(3, 3) {
        case (x, y) => Coord(x, y)
      }.flatten.toSet
    )
    assert(
      Board(
        List.tabulate(3, 3) {
          case (x, y) => Coord(x, y)
        }.flatten, Nil
      ).freeCells.isEmpty
    )
  }
}
