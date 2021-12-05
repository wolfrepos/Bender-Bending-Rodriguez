package io.github.oybek.xo.integration

import cats.Parallel
import cats.data.NonEmptyList
import cats.effect.concurrent.Ref
import cats.effect.{Sync, Timer}
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxApplicativeId, catsSyntaxOptionId, toFunctorOps}
import cats.syntax.flatMap._
import io.github.oybek.xo.model.ops.Board3x3.ops
import io.github.oybek.xo.model.ops.BoardOps.Syntax
import io.github.oybek.xo.model.{Board, Coord}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import telegramium.bots._
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, LongPollBot, Methods}

import scala.concurrent.duration.DurationInt
import scala.util.Random

class TelegramGate[F[_]: Sync: Timer: Parallel](matches: Ref[F, Map[(Long, Int), Board]], api: Api[F]) extends LongPollBot[F](api) {
  override def onMessage(message: Message): F[Unit] =
    message.text match {
      case _ => onTextMessage(message)
    }

  override def onCallbackQuery(query: CallbackQuery): F[Unit] =
    query match {
      case CallbackQuery(_, _, Some(message), _, _, Some(data), _) if data == "x" || data == "o" =>
        startNewGame(message, data)

      case CallbackQuery(_, _, Some(message), _, _, Some(data), _) if data.matches("[0-9]+,[0-9]+") =>
        handlePlayerTurn(message, data)
      case _ => ().pure[F]
    }

  private def onTextMessage(message: Message): F[Unit] =
    for {
      _ <- log.info(message.sticker.toString)
      _ <- Methods.sendSticker(ChatIntId(message.chat.id), sticker = Stickers.greeting).exec(api)
      _ <- Timer[F].sleep(500.millis)
      _ <- Methods.sendMessage(
        chatId = ChatIntId(message.chat.id),
        text =
        """
          |Нажми x или o чтобы начать игру
          |
          |Но предупрежу заранее...
          |Ты меня никогда не победишь 😊
          |""".stripMargin,
        replyMarkup =
          InlineKeyboardMarkup(List(List(
            InlineKeyboardButton("x", callbackData = "x".some),
            InlineKeyboardButton("o", callbackData = "o".some)
          ))).some
      ).exec(api).void
    } yield ()

  private def handlePlayerTurn(message: Message, data: String): F[Unit] = {
    val x :: y :: Nil = data.split(",").map(_.toInt).toList
    matches.get.map(_.getOrElse((message.chat.id, message.messageId), Board.empty)).flatMap {
      case board if board.outcome.nonEmpty =>
        onGameEnd(message, board)

      case board@Board(xs, os) if xs.contains(Coord(x, y)) || os.contains(Coord(x, y)) =>
        onBusyCellPress(message, board)

      case board =>
        val board0 = board.put(Coord(x, y))
        val board1 = Master.play(board0).map(board0.put).getOrElse(board0)
        board1.outcome.fold(onGameContinue(message, board1)) { _ =>
          onGameEnd(message, board1)
        }
    }
  }

  private def onGameContinue(message: Message, board1: Board): F[Unit] =
    for {
      _ <- matches.update(_.updated((message.chat.id, message.messageId), board1))
      _ <- Methods.editMessageText(
        chatId = ChatIntId(message.chat.id).some,
        messageId = message.messageId.some,
        text = Random.shuffle(phrases.toList).head,
        replyMarkup = drawBoard(board1).some
      ).exec(api).attempt.void
    } yield ()

  private def onBusyCellPress(message: Message, board: Board): F[Unit] =
    Methods.editMessageText(
      chatId = ChatIntId(message.chat.id).some,
      messageId = message.messageId.some,
      text = "Эта клетка занята!",
      replyMarkup = drawBoard(board).some
    ).exec(api).attempt.void

  private def onGameEnd(message: Message, board: Board): F[Unit] =
    for {
      _ <- Methods.editMessageText(
        chatId = ChatIntId(message.chat.id).some,
        messageId = message.messageId.some,
        text = "Вот и все! Ты меня не выиграл \uD83D\uDE1C",
        replyMarkup = drawBoard(board).some
      ).exec(api).attempt.void
      _ <- Timer[F].sleep(4.seconds)
      _ <- onTextMessage(message)
    } yield ()

  private def startNewGame(message: Message, data: String): F[Unit] =
    for {
      board <- createBoard(data).pure[F]
      gameMessage <- Methods.sendMessage(
        chatId = ChatIntId(message.chat.id),
        text = "Ну погнали!" + (if (data == "x") " Ты ходишь первым" else ""),
        replyMarkup = drawBoard(board).some
      ).exec(api)
      _ <- matches.update(_.updated((gameMessage.chat.id, gameMessage.messageId), board))
    } yield ()

  private def drawBoard(board: Board): InlineKeyboardMarkup =
    InlineKeyboardMarkup(
      List.tabulate(3, 3) {
        case (x, y) if board.xs.contains(Coord(x, y)) => InlineKeyboardButton("x", callbackData = s"$x,$y".some)
        case (x, y) if board.os.contains(Coord(x, y)) => InlineKeyboardButton("o", callbackData = s"$x,$y".some)
        case (x, y) => InlineKeyboardButton(text = " ", callbackData = s"$x,$y".some)
      }
    )

  private def createBoard(s: String) = {
    if (s == "x")
      Board.empty
    else
      Master.play(Board.empty)
        .map(Board.empty.put)
        .getOrElse(Board.empty)
  }

  private val phrases = NonEmptyList.of(
    "Не очень разумно",
    "Неплохой ход!",
    "Ха-ха! Да уже все",
    "Так, надо подумать",
    "Да ваще изян",
    "И это все?",
    "Твой ход братан!",
    "Да все уже"
  )

  private val log = Slf4jLogger.getLoggerFromName[F]("telegram")
}
