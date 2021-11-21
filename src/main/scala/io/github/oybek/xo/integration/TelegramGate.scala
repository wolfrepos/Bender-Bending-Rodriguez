package io.github.oybek.xo.integration

import cats.Parallel
import cats.effect.{Sync, Timer}
import cats.implicits.toFunctorOps
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, LongPollBot, Methods}
import telegramium.bots.{ChatIntId, Message}

class TelegramGate[F[_]: Sync: Timer: Parallel](api: Api[F]) extends LongPollBot[F](api) {
  override def onMessage(message: Message): F[Unit] =
    Methods.sendMessage(
      chatId = ChatIntId(message.chat.id),
      text = message.text.getOrElse(""),
    ).exec(api).void
}
