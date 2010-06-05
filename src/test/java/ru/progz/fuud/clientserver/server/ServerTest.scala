package ru.progz.fuud.clientserver.server

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitSuite
import javax.swing._
import org.junit.Test
import org.junit.Assert._
import java.net.{Socket, ServerSocket}

class ServerTest extends JUnitSuite with ShouldMatchers{
  val port: Int = 8888

  @Test
  def test_startStop{
    val server = new Server

    server.stop // close should not produce an exception if server is not started

    new ServerSocket(port).close // if port is free should be no exception here

    server.start(port)

    new Socket("127.0.0.1", port).close // now we can connect to server

    server.stop

    new ServerSocket(port).close // if port is free should be no exception here
  }

}