package io.github.oybek.xo

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits.catsSyntaxFlatMapOps
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.github.oybek.xo.Main.F
import io.github.oybek.xo.config.Config
import io.github.oybek.xo.integration.TelegramGate
import io.github.oybek.xo.model.Board
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger
import telegramium.bots.high.BotApi

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main extends IOApp {
  type F[+T] = IO[T]

  def run(args: List[String]): F[ExitCode] =
    Config.load.fold(
      error => log.error(s"Could not load config file: $error").as(ExitCode.Error),
      config =>
        log.info(s"loaded config: $config") >>
        resources(config)
          .use(httpClient => assembleAndLaunch(config, httpClient))
          .as(ExitCode.Success)
    )

  private def assembleAndLaunch(config: Config, httpClient: Client[F]): IO[Unit] = {
    val client = Logger(logHeaders = false, logBody = false)(httpClient)
    val api    = BotApi[F](client, s"https://api.telegram.org/bot${config.tgBotApiToken}")
    for {
      matches <- Ref.of[F, Map[(Long, Int), Board]](Map.empty)
      _ <- new TelegramGate[F](matches, api).start().void
    } yield ()
  }

  private def resources[F[_]: Timer: ConcurrentEffect](config: Config): Resource[F, Client[F]] =
    BlazeClientBuilder[F](global)
      .withResponseHeaderTimeout(FiniteDuration(telegramResponseWaitTime, TimeUnit.SECONDS))
      .resource

  private val telegramResponseWaitTime = 60L
  private val log = Slf4jLogger.getLoggerFromName[F]("application")
}
