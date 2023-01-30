package io.github.oybek.xo.integration

import io.github.oybek.xo.model.Outcome.{Draw, Win}
import io.github.oybek.xo.model.{Board, Coord, Outcome}

import scala.collection.mutable
import scala.util.Random

object Master {
  def play(board: Board): Option[Coord] = {
    val outcomes = board.freeCells.map(coord => (whoWins(board.put(coord)), coord))

    val (winCoords, drawCoords, _) =
      outcomes.foldLeft((List.empty[Coord], List.empty[Coord], List.empty[Coord])) {
        case ((win, draw, loss), (Win(one), coord)) =>
          if (one == board.turn)
            (coord :: win, draw, loss)
          else
            (win, draw, coord :: loss)
        case ((win, draw, loss), (Draw, coord)) => (win, coord :: draw, loss)
      }

    Random.shuffle(winCoords).headOption orElse Random.shuffle(drawCoords).headOption
  }

  private def memoize[K, V](f: K => V): K => V = new mutable.HashMap[K, V]() {
    override def apply(key: K): V = getOrElseUpdate(key, f(key))
  }

  private val whoWins: Board => Outcome =
    memoize { board =>
      board.outcome.getOrElse {
        board
          .freeCells
          .map(board.put)
          .map(whoWins)
          .minBy {
            case Win(one) if one == board.turn => 1
            case Draw => 2
            case _ => 3
          }
      }
    }

  // initialize memo
  whoWins(Board.empty)
}
