package ru.progz.fuud.clientserver.server

import java.net.ServerSocket
import scala.actors.Actor._
import java.io.{DataOutputStream, DataInputStream}
import Server._

object Server{
  val RESULT_OK = 0
}

class Server {
  private var s: Option[ServerSocket] = None;

  def start(port: Int = 7777) {
    s = Some(new ServerSocket(port))

    actor{
      loop{
        val socket = s.get.accept
        val inputStream = new DataInputStream(socket.getInputStream)
        val data = inputStream.readUTF
        new DataOutputStream(socket.getOutputStream).writeInt(RESULT_OK)
      }
    }
  }

  def stop {
    s.foreach(_.close)
    s = None
  }
}