
import java.io._
import DataHandler._
import Writer._
import upickle.core.NoOpVisitor

/**
 *  Indlæser observationer fra DMI's DB, med apikey defineret i environment variablen APIKEY,
 *  300.000 observationer af gangen, transformerer dem til formatet:
 *  time stempler -> meterologiske parametre -> (summeret værdi, antal observationer denne time),
 *  indtil der ikke er flere observationer inden for den periode der er defineret af PERIODE environmet variable.
 *  Endelig skrives data i JSON format til GZIP fil defineret af environment variablen FILEPATH.
 */

object Main  {
  def main(args: Array[String]): Unit = {
    try {
      // Indlæs første batch
      var jsonData = getData(0)
      // Dan det indledende dataformat med det hentede data
      var completedata = buildData(jsonData)
      // Antal observationer eller max antal observationer
      val maxLength = 300000
      var currentLength = jsonData.arr.length
      var offset = currentLength
      while (currentLength == maxLength) {
        // Hent batch med offset for at få næste del af data
        jsonData = getData(offset)
        currentLength = jsonData.arr.length
        offset += currentLength
        completedata = addData(completedata, jsonData)
        ujson.transform(completedata, NoOpVisitor)
      }
      writeToGzipFile(completedata)
    }
    catch {
      case e: IOException => e.printStackTrace
      case e: ujson.ParseException => e.printStackTrace
      case e: ujson.IncompleteParseException => e.printStackTrace
    }
    }
  }

