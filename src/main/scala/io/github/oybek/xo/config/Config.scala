package io.github.oybek.xo.config

import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._

case class Config(tgBotApiToken: String)

object Config {
  def load: Result[Config] =
    ConfigSource.default.load[Config]
}
