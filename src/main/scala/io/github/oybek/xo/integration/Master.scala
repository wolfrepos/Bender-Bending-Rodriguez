package io.github.oybek.xo.integration

import io.github.oybek.xo.model.Outcome.{Draw, Owin, Xwin}
import io.github.oybek.xo.model.XO.{O, X}
import io.github.oybek.xo.model.ops.Board3x3.ops
import io.github.oybek.xo.model.ops.BoardOps.Syntax
import io.github.oybek.xo.model.{Board, Coord, Outcome}

import scala.collection.mutable

object Master {
  def play(board: Board): Option[Coord] = {
    val priorities = board.turn match {
      case X => List(Xwin, Draw, Owin)
      case O => List(Owin, Draw, Xwin)
    }
    val outcomes =
      board
        .freeCells
        .map(coord => (whoWins(board.put(coord)), coord))
    priorities.flatMap {
      o1 => outcomes.collectFirst { case (o2, coord) if o1 == o2 => coord }
    }.headOption
  }

  private val whoWinsMemo = mutable.Map.empty[String, Outcome]
  private def whoWins(board: Board): Outcome =
    if (whoWinsMemo.contains(board.show)) {
      whoWinsMemo(board.show)
    } else {
      val outcome =
        board.outcome.getOrElse {
          val checks = board.turn match {
            case X => List(Xwin, Draw, Owin)
            case O => List(Owin, Draw, Xwin)
          }
          val outcomes =
            board
              .freeCells
              .map(coord => whoWins(board.put(coord)))
          checks.find(outcomes.contains).get
        }
      whoWinsMemo.put(board.show, outcome)
      outcome
    }

  whoWins(Board.empty)
}
