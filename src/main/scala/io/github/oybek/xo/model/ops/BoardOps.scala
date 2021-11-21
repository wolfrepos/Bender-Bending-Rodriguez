package io.github.oybek.xo.model.ops

import io.github.oybek.xo.model.{Coord, Outcome, XO}

trait BoardOps[A] {
  def put(board: A, coord: Coord, xo: XO): Either[String, A]
  def outcome(board: A): Option[Outcome]
  def turn(board: A): Option[XO]
  def freeCells(board: A): List[Coord]
}

object BoardOps {
  implicit class Syntax[A](val target: A) extends AnyVal {
    def put(coord: Coord, xo: XO)(implicit board: BoardOps[A]): Either[String, A] = board.put(target, coord, xo)
    def outcome(implicit board: BoardOps[A]): Option[Outcome] = board.outcome(target)
    def turn(implicit board: BoardOps[A]): Option[XO] = board.turn(target)
    def freeCells(implicit board: BoardOps[A]): List[Coord] = board.freeCells(target)
  }
}
