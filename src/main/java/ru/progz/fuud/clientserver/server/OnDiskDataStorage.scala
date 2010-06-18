package ru.progz.fuud.clientserver.server

import java.lang.String
import io.Source
import java.io.{File, FileOutputStream, OutputStreamWriter, BufferedWriter}

class OnDiskDataStorage(file_name: String) extends DataStorage {
  val newLine = "\r\n"
  val os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name), "US-ASCII"))

  def put(data: String) {
    os.write(data)
    os.write(newLine)
  }

  def get = {
    os.flush()
    Source.fromFile(new File(file_name), "US-ASCII").getLines()
  }
}