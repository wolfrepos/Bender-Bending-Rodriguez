package io.github.oybek.xo.integration

import scala.util.Random

object Texts {
  val text =
    """
      |Ты думаешь ты сможешь победить меня? 🤣
      |
      |Ну ок, выбери за кого ты хочешь сыграть:
      |""".stripMargin

  val cellIsBusy = "Эта.клетка.занята!"
  val draw = "Ничья"
  val lost = "Ты.еще.и.проиграл!"
  def yourTurn = Random.shuffle(List("Твой.ход", "Ходи", "Ты.ходишь", "Ходи.уже", "Я.уже.сходил")).head

  def center(s: String, width: Int = 16): String = {
    val spaceCount = width - s.length()
    val space = "." * (spaceCount/2)
    (if (spaceCount%2 == 1) "`." else "`") + space + s + space + "`"
  }
}
