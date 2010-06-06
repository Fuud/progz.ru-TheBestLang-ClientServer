package ru.progz.fuud.clientserver.server

import java.net.ServerSocket
import scala.actors.Actor._
import ru.progz.fuud.clientserver.utils.Log4jLogger
import java.io.{IOException, DataOutputStream, DataInputStream}


object Server extends Log4jLogger {
  val RESULT_OK = 0
}

class Server {
  import Server._
  
  private var s: Option[ServerSocket] = None;
  @volatile private var stopped = true

  def start(port: Int = 7777) {
    s = Some(new ServerSocket(port))

    actor {
      loop {
        try {
          val socket = s.get.accept
          val inputStream = new DataInputStream(socket.getInputStream)
          val data = inputStream.readUTF
          new DataOutputStream(socket.getOutputStream).writeInt(RESULT_OK)
        } catch {
          case e: IOException if !stopped => Server.logger.warn("exception while handling connection", e)
          case _ if stopped => exit
        }
      }
    }
  }

  def stop {
    s.foreach(_.close)
    s = None
  }
}