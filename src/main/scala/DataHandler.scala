import java.io.IOException
import PeriodHandler._
import sttp.client.{UriContext, basicRequest, okhttp}
import sttp.model.Uri
import ujson.Value
import ujson.Value.Obj
import upickle.core.NoOpVisitor
import scala.concurrent.duration._

object DataHandler {
  implicit val backend = okhttp.OkHttpSyncBackend()

  /**
   *
   * @param offset - antallet af observationer der skal skippes i kaldet til api
   * @return - valideret ujson.Value trukket fra DMI
   */
  def getData(offset: Int): Value = {
    val periode = sys.env("PERIODE")
    val from = getPeriod(periode)
    val apiKey = sys.env("APIKEY")
    val url: Uri = uri"https://dmigw.govcloud.dk/metObs/v1/observation?api-key=$apiKey&from=$from&offset=$offset&limit=3000000"

    val response = basicRequest
      .get(url)
      .readTimeout(3.minutes)
      .send()

    val jsondata: Value = response.body match {
      case Right(x) => ujson.read(x)
      case Left(x) => throw new IOException
      }
    ujson.transform(jsondata, NoOpVisitor)
    jsondata
  }

  /**
   *
   * @param currentData - Den netop trukne data
   * @return - ujson.Value i formatet time -> param -> Obj(value, counts)
   */
  def buildData(currentData: Value): Value ={
    var completeData: ujson.Value = Obj()
    for(i <- currentData.arr.indices){
      val observed = afrundTilTime(currentData.arr(i)("timeObserved").toString)
      val value = currentData.arr(i)("value").num
      val param = currentData.arr(i)("parameterId").str
      completeData = updateData(completeData, observed, param, value)
    }
    completeData
  }

  /**
   *
   * @param completeData - Den allerede indlæste data
   * @param currentData - Den netop udtrukne data
   * @return - completeData opdateret med det netop udtrukne
   */
  def addData(completeData: Value, currentData: Value): Value = {
    var newCompleteData = completeData
    for(i <- currentData.arr.indices){
      val observed = afrundTilTime(currentData.arr(i)("timeObserved").toString)
      val value = currentData.arr(i)("value").num
      val param = currentData.arr(i)("parameterId").str
      newCompleteData = updateData(completeData, observed, param, value)
    }
    newCompleteData
  }

  /**
   * Tjekker om der er observationer for den pågældende time og ellers tilføjes den, tjekker om det
   * givne parameter er observeret i den givne time og ellers tilføjes den.
   * Ellers opdateres værdien og antal observationer.
   *
   * @param completeData - Data som skal opdateres
   * @param observed - observationstimen
   * @param param - det metereologiske parameter
   * @param value - den observerede værdi
   * @return - opdateret completeData
   */
  def updateData(completeData: Value,
                 observed: String,
                 param: String,
                 value: Double): Value = {

    if (completeData.obj.keySet(observed)){ // Hvis der er nogle observationer i denne time
      if (completeData(observed).obj.keySet(param)){ // Hvis der er nogle observationer denne time af param
        completeData(observed)(param)("value") = completeData(observed)(param)("value").num + value
        completeData(observed)(param)("counts") = completeData(observed)(param)("counts").num + 1
      } else{ // Hvis der ikke er nogle observationer af param i denne time
        completeData(observed).obj.put(
          param, putParam(value))
      }
    } else { // Hvis der ikke er nogle observationer denne time
      completeData.obj.put(
        observed, putObserved(param, value))
    }
    completeData
  }

  /**
   *
   * @param value - observationsværdien
   * @return - ny observation mmed værdien value og 1 count
   */
  def putParam(value: Double): Value ={
    val counts = 1
    Obj(
      "value" -> value,
      "counts" -> counts)
  }

  /**
   *
   * @param param - observationsparameteret
   * @param value - observationsvrdien
   * @return - ny observationstime med en observation, param med værdien value og 1 count
   */
  def putObserved(param: String, value: Double): Value = {
    Obj(
      param -> putParam(value))
  }
}
