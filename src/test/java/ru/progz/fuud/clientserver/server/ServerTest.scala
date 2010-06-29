package ru.progz.fuud.clientserver.server

import org.scalatest.matchers.ShouldMatchers
import org.junit.Test
import org.junit.Assert._
import Server._
import java.io.{OutputStreamWriter, ByteArrayOutputStream, DataInputStream, DataOutputStream}
import java.net.{SocketException, Socket, ServerSocket}
import org.scalatest.FlatSpec

class ServerTest extends FlatSpec with ShouldMatchers {
  val port: Int = 8888
  val localhost = "127.0.0.1"

  @Test(timeout = 10000)
  def startStop {
    val server = new Server

    server.stop // close should not produce an exception if server is not started

    new ServerSocket(port).close // if port is free should be no exception here

    server.start(port)

    new Socket(localhost, port).close // now we can connect to server

    server.stop

    new ServerSocket(port).close // if port is free should be no exception here
  }

  @Test(timeout = 10000)
  def send {
    val server = new Server
    server.start(port)

    val dataToSend = "HelloWorld"
    val result = sendAndGetResult(dataToSend)

    assertEquals(RESULT_OK, result)

    server.stop
  }

  @Test(timeout = 10000)
  def sendInvalidData {
    withServer(server => {
      "server" should "not accept non-latin characters" in {
        val illegalString = "Здравствуй, мир!!!"
        val result = sendAndGetResult(illegalString)
        assertEquals(RESULT_FAIL, result)
      }

      "server" should "not accept data more than 10Mb" in {
        val illegalChars = new Array[Char](10 * 1024 * 1024)
        for (i <- 0 until illegalChars.length) {
          illegalChars(i) = 'a'; }
        val result = sendAndGetResult(new String(illegalChars))
        assertEquals(RESULT_FAIL, result)
      }

    })
  }


  //--------------------------- Internal testes --------------------------

  @Test
  def test_StringToByteArray {
    val str = "Hello World"
    val result = StringToByteArray(str)

    assertEquals(str.length, result.length)

    for (i <- 0 until str.length) {
      assertEquals(str.charAt(i).toInt, result(i))
    }
  }


  //--------------------------- Utils -------------------------------------

  private def withServer(code: Server => Unit) {
    val server = new Server
    server.start(port)

    code(server)

    server.stop
  }

  private def sendAndGetResult(data: String): Int = {
    sendAndGetResult(StringToByteArray(data))
  }

  private def sendAndGetResult(data: Array[Byte]): Int = {
    val clientSocketToSend = new Socket(localhost, port)

    val outputStream = new DataOutputStream(clientSocketToSend.getOutputStream)
    val inputStream = new DataInputStream(clientSocketToSend.getInputStream)

    outputStream.writeInt(data.length)
    try {
      outputStream.write(data)
      val result = inputStream.readInt

      clientSocketToSend.close

      result
    } catch {
      case s: SocketException => RESULT_FAIL   // if data is invalid server may close connection before all buffer will be read
    }


  }

  private def StringToByteArray(s: String) = {
    val bas = new ByteArrayOutputStream(s.length)
    val osw = new OutputStreamWriter(bas, "KOI8-R") // to provide invalid data
    osw.write(s)
    osw.close
    bas.toByteArray
  }
}