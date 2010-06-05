package ru.progz.fuud.clientserver.server

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitSuite
import javax.swing._
import org.junit.Test
import org.junit.Assert._
import java.net.{Socket, ServerSocket}
import java.io.{DataInputStream, DataOutputStream}
import Server._

class ServerTest extends JUnitSuite with ShouldMatchers{
  val port: Int = 8888
  val localhost = "127.0.0.1"

  @Test
  def startStop{
    val server = new Server

    server.stop // close should not produce an exception if server is not started

    new ServerSocket(port).close // if port is free should be no exception here

    server.start(port)

    new Socket(localhost, port).close // now we can connect to server

    server.stop

    new ServerSocket(port).close // if port is free should be no exception here
  }

  @Test(timeout=1000)
  def send{
    val server = new Server
    server.start(port)

    val clientSocketToSend = new Socket(localhost, port)

    val outputStream = new DataOutputStream(clientSocketToSend.getOutputStream)
    val inputStream = new DataInputStream(clientSocketToSend.getInputStream)

    val dataToSend = "HelloWorld!!!"
    outputStream.writeUTF(dataToSend)

    assertEquals(RESULT_OK, inputStream.readInt)

    server.stop
  }

}