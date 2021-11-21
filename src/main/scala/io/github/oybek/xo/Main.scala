package io.github.oybek.xo

import io.github.oybek.xo.model.XO.{O, X}
import io.github.oybek.xo.model.ops.Board3x3.ops
import io.github.oybek.xo.model.ops.BoardOps.Syntax
import io.github.oybek.xo.model.{Board, Coord}
import io.github.oybek.xo.service.impl.Master

import scala.io.StdIn

object Main extends App {
  println(Board(Nil, Nil).show)
  LazyList.iterate(Board(Nil, Nil)) {
    board =>
      board.turn match {
        case X =>
          val b = board.put(Master.play(board).get)
          println(b.show)
          b
        case O =>
          val xx::xy::Nil = StdIn.readLine().split(" ").map(_.toInt).toList
          board.put(Coord(xx, xy))
      }
  }.takeWhile(_.outcome.isEmpty).toList
}
