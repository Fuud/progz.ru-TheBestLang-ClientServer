package ru.progz.fuud.clientserver.server

import java.io.{IOException, DataOutputStream, DataInputStream}
import java.net.{Socket, ServerSocket}
import ru.progz.fuud.clientserver.utils.Log4jLogger
import ru.progz.fuud.clientserver.utils.Executor._

object Server extends Log4jLogger {
  val RESULT_OK = 0
}

class Server {
  import Server._

  private var s: Option[ServerSocket] = None;
  @volatile private var stopped = true
  @volatile private var count = 0

  def processNewConnection(socket: Socket): Unit = {
    thread {
      count += 1
      logger.debug("about to process connection #" + count)
      val inputStream = new DataInputStream(socket.getInputStream)
      val data = inputStream.readUTF
      new DataOutputStream(socket.getOutputStream).writeInt(RESULT_OK)
    }
  }

  def start(port: Int = 7777) {
    stopped = false;
    s = Some(new ServerSocket(port))

    thread {
      while (!stopped) {
        try {
          logger.debug("will try to accept incoming connection")
          val socket = s.get.accept
          logger.debug("accepted")
          processNewConnection(socket)
        } catch {
          case e: IOException if !stopped => Server.logger.warn("exception while handling connection", e)
          case _ if stopped => { /*ignore*/ }
        }
      }
    }
  }

  def stop {
    stopped = true
    s.foreach(_.close)
    s = None
  }
}