package ru.progz.fuud.clientserver.utils

import java.util.concurrent._

object Executor extends Log4jLogger{
  private val executor = Executors.newCachedThreadPool

  def thread(block: => Unit) {
    executor.submit(new Runnable() {
      def run = {
        try{
          block
        }catch{
          case t:Throwable => logger.error("unhandled exception", t)
        }
      }
    })
  }
}