package ru.progz.fuud.clientserver.server



trait DataStorage{
  def put(data:String)
  def get:Iterator[String]
}