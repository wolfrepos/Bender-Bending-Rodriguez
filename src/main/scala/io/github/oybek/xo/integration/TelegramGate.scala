package io.github.oybek.xo.integration

import cats.Parallel
import cats.effect.concurrent.Ref
import cats.effect.{Sync, Timer}
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxApplicativeId, catsSyntaxOptionId, toFunctorOps}
import cats.syntax.flatMap._
import io.github.oybek.xo.model.ops.Board3x3.ops
import io.github.oybek.xo.model.ops.BoardOps.Syntax
import io.github.oybek.xo.model.{Board, Coord}
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, LongPollBot, Methods}
import telegramium.bots._

import scala.concurrent.duration.DurationInt

class TelegramGate[F[_]: Sync: Timer: Parallel](matches: Ref[F, Map[(Long, Int), Board]], api: Api[F]) extends LongPollBot[F](api) {
  override def onMessage(message: Message): F[Unit] = {
    message.text match {
      case s @ (Some("/play_x") | Some("/play_o")) =>
        for {
          _ <- Methods.sendMessage(
            chatId = ChatIntId(message.chat.id),
            text = "Ты хочешь сыграть в крестики-нолики?"
          ).exec(api).void
          _ <- Timer[F].sleep(200.millis)
          board = createBoard(s)
          gameMessage <- Methods.sendMessage(
            chatId = ChatIntId(message.chat.id),
            text = "Ну погнали!",
            replyMarkup = drawBoard(board).some
          ).exec(api)
          _ <- matches.update(_.updated((gameMessage.chat.id, gameMessage.messageId), board))
        } yield ()

      case _ => ().pure[F]
    }
  }

  private def createBoard(s: Option[String]) = {
    if (s.contains("/play_x"))
      Board.empty
    else
      Master.play(Board.empty)
        .map(Board.empty.put)
        .getOrElse(Board.empty)
  }

  override def onCallbackQuery(query: CallbackQuery): F[Unit] =
    query match {
      case CallbackQuery(_, _, Some(message), _, _, Some(data), _) =>
        val x::y::Nil = data.split(",").map(_.toInt).toList
        matches.get.map(_.getOrElse((message.chat.id, message.messageId), Board.empty)).flatMap {
          case board if board.outcome.nonEmpty =>
            Methods.editMessageText(
              chatId = ChatIntId(message.chat.id).some,
              messageId = message.messageId.some,
              text = "Игра завершена!\n/play_x\n/play_o",
              replyMarkup = drawBoard(board).some
            ).exec(api).attempt.void

          case board @ Board(xs, os) if xs.contains(Coord(x, y)) ||
                                        os.contains(Coord(x, y)) =>
            Methods.editMessageText(
              chatId = ChatIntId(message.chat.id).some,
              messageId = message.messageId.some,
              text = "Эта клетка занята!",
              replyMarkup = drawBoard(board).some
            ).exec(api).attempt.void

          case board =>
            val board0 = board.put(Coord(x, y))
            val board1 = Master.play(board0).map(board0.put).getOrElse(board0)
            val text = board1.outcome.fold("Продолжаем")(_ => "Игра завершена!\n/play_x\n/play_o")
            for {
              _ <- matches.update(_.updated((message.chat.id, message.messageId), board1))
              _ <- Methods.editMessageText(
                chatId = ChatIntId(message.chat.id).some,
                messageId = message.messageId.some,
                text = text,
                replyMarkup = drawBoard(board1).some
              ).exec(api).attempt.void
            } yield ()
        }
      case _ => ().pure[F]
    }

  private def drawBoard(board: Board): InlineKeyboardMarkup =
    InlineKeyboardMarkup(
      List.tabulate(3, 3) {
        case (x, y) if board.xs.contains(Coord(x, y)) => InlineKeyboardButton("x", callbackData = s"$x,$y".some)
        case (x, y) if board.os.contains(Coord(x, y)) => InlineKeyboardButton("o", callbackData = s"$x,$y".some)
        case (x, y) => InlineKeyboardButton(text = " ", callbackData = s"$x,$y".some)
      }
    )
}
