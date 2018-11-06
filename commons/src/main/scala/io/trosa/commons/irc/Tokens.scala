package io.trosa.commons.irc

object Tokens {

  sealed trait Token

  case object Channel extends Token
  case object UserMask extends Token
  case object Command extends Token

}
