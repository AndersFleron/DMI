object PeriodHandler {
  def getCurrentUnixTime: Long = System.currentTimeMillis*1000
  def unixToSubtract(numPeriods: Int, period: String): Long = {
    period match {
      case period if period.equals("t") => 1000000L*60L*60L*numPeriods
      case period if period.equals("d") => 1000000L*60L*60L*24L*numPeriods
      case period if period.equals("u") => 1000000L*60L*60L*24L*7L*numPeriods
      case period if period.equals("m") => 1000000L*60L*60L*24L*31L*numPeriods
      case period if period.equals("å") => 1000000L*60L*60L*24L*365L*numPeriods
    }
  }
  def numberOfPeriods(from: String): Int = {
    val numbers = ("""\d+""".r findAllIn from).toList
    if (numbers.length == 1) {
      numbers.head.toInt
    } else {
      throw new IllegalArgumentException("Kun en periode")
    }
  }

  def convertToUnix(from: String, period: String): Long = {
    getCurrentUnixTime - unixToSubtract(numberOfPeriods(from), period)
  }

  def getPeriod(from: String): Long = {
    from match {
      case from if from.toLowerCase().contains("time") => convertToUnix(from, "t")
      case from if from.toLowerCase().contains("dag") => convertToUnix(from, "d")
      case from if from.toLowerCase().contains("uge") => convertToUnix(from, "u")
      case from if from.toLowerCase().contains("mån") => convertToUnix(from, "m")
      case from if from.toLowerCase().contains("år") => convertToUnix(from, "å")
    }
  }
  def afrundTilTime(unixTime: String): String ={
    (unixTime.toLong - (unixTime.toLong % 3600000000L)).toString
  }
}
