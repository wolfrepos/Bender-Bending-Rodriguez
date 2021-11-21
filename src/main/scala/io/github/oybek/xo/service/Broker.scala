package io.github.oybek.xo.service

import io.github.oybek.xo.model.{Board, Coord}

trait Broker {
  def put(board: Board, coord: Coord): Either[String, Board]
}
