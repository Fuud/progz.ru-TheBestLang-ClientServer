package ru.progz.fuud.clientserver.server

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import ru.progz.fuud.clientserver.utils.Log4jLogger
import java.lang.String
import java.io.File
import org.junit.Assert._

class OnDiskDataStorageTest extends JUnitSuite with ShouldMatchers with Log4jLogger{
  private def getNewStorage = {
    val filename = File.createTempFile("OnDiskDataStorageTest","").getAbsolutePath
    new OnDiskDataStorage(filename)
  }

  @Test
  def getAndPut{
    val storage = getNewStorage

    val value: String = "Hello World!!!"
    storage.put(value)

    val messages = storage.get.toArray[Object]

    assertArrayEquals(Array(value).toArray[Object], messages)

    val value2: String = "Hello World!!! (2)"
    storage.put(value2)

    val messages2 = storage.get.toArray[Object]

    assertArrayEquals(Array(value, value2).toArray[Object], messages2)
  }

  
}