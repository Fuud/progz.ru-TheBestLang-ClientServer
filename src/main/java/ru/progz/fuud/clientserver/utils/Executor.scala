package ru.progz.fuud.clientserver.utils

import java.util.concurrent._

object Executor {
  private val executor = Executors.newCachedThreadPool

  def thread(block: => Unit) {
    executor.submit(new Runnable() {
      def run = block
    })
  }
}