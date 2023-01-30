package io.github.oybek.xo.service

import io.github.oybek.xo.model.{Board, Coord, XorO}

trait Player {
  def play(board: Board): Option[Coord]
}
