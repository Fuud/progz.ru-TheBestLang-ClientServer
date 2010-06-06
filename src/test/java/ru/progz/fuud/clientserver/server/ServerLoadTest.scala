package ru.progz.fuud.clientserver.server

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import java.net.{ConnectException, Socket}
import ru.progz.fuud.clientserver.utils.Log4jLogger
import collection.mutable.ListBuffer

class ServerLoadTest extends JUnitSuite with ShouldMatchers with Log4jLogger {
  val port: Int = 8888
  val localhost = "127.0.0.1"

  @Test // server should handle unlimited connections
  def maxConnections {
    val server = new Server
    server.start(port)

    val socketsList = ListBuffer[Socket]()

    for (i <- 1 to 10000) {
      try {
        logger.debug("about to create new connection")
        socketsList += new Socket(localhost, port)
        logger.debug("connection successfully created")
        //Thread.sleep(500)
      } catch {
        case e: ConnectException => println("max_connections=" + i); throw e;
      }
    }

    Thread.sleep(10000)

    server.stop
  }

  @Test
  def testActors {
    import scala.actors.Actor._
    for (i <- 1 to 10000) {
      actor {
        val num = i
        //logger.debug(num)
        println(num)
        try {
          Thread.sleep(Integer.MAX_VALUE)
        } catch {
          case e: Throwable => logger.error("error", e)
        }
        logger.debug("exited " + num)
      }
    }
    Thread.sleep(1000)
  }
}