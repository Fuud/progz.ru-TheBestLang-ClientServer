package ru.progz.fuud.clientserver.server

import java.net.{Socket, ServerSocket}
import ru.progz.fuud.clientserver.utils.Log4jLogger
import ru.progz.fuud.clientserver.utils.Executor._
import java.io._

object Server extends Log4jLogger {
  val RESULT_OK = 0
  val RESULT_FAIL = -1

  val MAX_DATA_LENGTH = 10 * 1024 * 1024
}

class Server(@volatile private var dataStorage: DataStorage) {
  def this() = this (null)

  import Server._

  private var s: Option[ServerSocket] = None;
  @volatile private var stopped = true
  @volatile private var count = 0

  private def processNewConnection(socket: Socket): Unit = {
    thread {
      count += 1
      logger.debug("about to process connection #" + count)

      val data = readData(socket.getInputStream)

      val outputStream: DataOutputStream = new DataOutputStream(socket.getOutputStream)

      def writeResultAndCloseSocket(result: Int): Unit = {
        outputStream.writeInt(result)
        socket.close
      }

      data match {
        case None => writeResultAndCloseSocket(RESULT_FAIL)
        case Some(x) => {
          writeResultAndCloseSocket(RESULT_OK)
          saveValidData(new String(x))
        }
      }
    }
  }

  private def saveValidData(data: String) {
    if (logger.isTraceEnabled) {
      logger.trace("valid data to save: " + data)
    }
    if (dataStorage != null) {
      dataStorage.put(data)
    }
  }

  private def readData(inputStream: InputStream) = {
    val das = new DataInputStream(inputStream)
    val length = das.readInt
    if (length > MAX_DATA_LENGTH) {
      None
    } else {
      // US-ASCII - 7-bit charset. Non-latin characters is not supported
      val reader = new InputStreamReader(inputStream, "US-ASCII")
      val result = new Array[Char](length)
      try {
        val wasRead = reader.read(result)

        if (wasRead != length || !dataIsCorrect(result)) None else Some(result)

      } catch {
        case e: IOException => {
          logger.debug("can not read data from client", e)
          None
        }
      }
    }
  }

  private def dataIsCorrect(data: Array[Char]) = {
    data.forall(c => {
      (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
    })
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

  def setDataStorage(dataStorage: DataStorage) {
    this.dataStorage = dataStorage
  }
}