package io.github.oybek.xo.model.ops

import io.github.oybek.xo.model.{Coord, Outcome, XO}

trait BoardOps[A] {
  def put(board: A, coord: Coord): A
  def outcome(board: A): Option[Outcome]
  def turn(board: A): XO
  def freeCells(board: A): List[Coord]
}

object BoardOps {
  implicit class Syntax[A](val target: A) extends AnyVal {
    def put(coord: Coord)(implicit board: BoardOps[A]): A = board.put(target, coord)
    def outcome(implicit board: BoardOps[A]): Option[Outcome] = board.outcome(target)
    def turn(implicit board: BoardOps[A]): XO = board.turn(target)
    def freeCells(implicit board: BoardOps[A]): List[Coord] = board.freeCells(target)
  }
}
