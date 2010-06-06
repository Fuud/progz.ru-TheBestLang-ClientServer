package ru.progz.fuud.clientserver.utils

import org.apache.log4j.Logger

trait Log4jLogger{
  val logger = Logger.getLogger(getClass)
}