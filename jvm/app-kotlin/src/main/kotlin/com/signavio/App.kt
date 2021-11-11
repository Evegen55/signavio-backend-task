package com.signavio

fun main(args: Array<String>) {
  val eventlogRows = CSVReader.readFile("samples/Activity_Log_2004_to_2014.csv")

  val begin = System.currentTimeMillis()

  // TODO: Add the call to your solution here

  val end = System.currentTimeMillis()

  println(String.format("Duration: %s milliseconds", end - begin))
}
