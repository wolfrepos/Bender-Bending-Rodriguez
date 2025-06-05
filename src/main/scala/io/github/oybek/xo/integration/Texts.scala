package io.github.oybek.xo.integration

import scala.util.Random

object Texts {
  val text =
    """
      |Хочешь потягаться со мной?
      |Ну ок, выбери за кого ты хочешь сыграть:
      |""".stripMargin

  val cellIsBusy = "Эта клетка занята"
  val draw = "Ничья"
  val lost = "Ты еще и проиграл!"
  def yourTurn = Random.shuffle(List(
    "Хуяк",
    "Опа!",
    "Ты ходишь",
    "Что на это скажешь?",
    "Твой ход",
  )).head

  val gap = "`" + "_" * 20 + "`"
}
