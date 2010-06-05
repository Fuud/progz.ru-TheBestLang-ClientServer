package ru.progz.fuud.clientserver.server

import java.net.ServerSocket

class Server {
  private var s: Option[ServerSocket] = None;

  def start(port: Int = 7777) {
    s = Some(new ServerSocket(port))
  }

  def stop {
    s.foreach(_.close)
    s = None
  }
}