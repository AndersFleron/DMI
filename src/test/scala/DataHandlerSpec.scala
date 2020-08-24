import org.scalatest.flatspec.AnyFlatSpec
import DataHandler._

class DataHandlerSpec extends AnyFlatSpec {
  val testJson: ujson.Value = ujson.Arr(
    ujson.Obj("parameterId" -> "a", "timeObserved" -> 1598177800000000F, "value" -> 2),
    ujson.Obj("parameterId" -> "b", "timeObserved" -> 1598177800000000F, "value" -> 4),
    ujson.Obj("parameterId" -> "c", "timeObserved" -> 1598131000000000F, "value" -> 6),
    ujson.Obj("parameterId" -> "a", "timeObserved" -> 1598131000000000F, "value" -> 8),
    ujson.Obj("parameterId" -> "b", "timeObserved" -> 1598131000000000F, "value" -> 10),
    ujson.Obj("parameterId" -> "c", "timeObserved" -> 1598131000000000F, "value" -> 12),
    ujson.Obj("parameterId" -> "a", "timeObserved" -> 1598131000000000F, "value" -> 14),
  )

  "buildData" should "return a ujson.Value with structure: observed -> param -> (value-> sum(value), counts -> count)" in {
    assertResult(22){
      val data = buildData(testJson)
      data("1598130000000000")("a")("value").num
    }
    assertResult(2){
      val data = buildData(testJson)
      data("1598130000000000")("a")("counts").num
    }
  }
  "updateData" should "update the given completeData withh the given currentData so the existing values gets summed and counts gets incremented" in {
    assertResult(3){
      var data = buildData(testJson)
      val toUpdate = ujson.Obj(
        "parameterId" -> "a",
        "timeObserved" -> "1598130000000000",
        "value" -> 4)
      data = updateData(
        data,
        toUpdate("timeObserved").toString,
        toUpdate("parameterId").str,
        toUpdate("value").num)
      data("1598130000000000")("a")("counts").num
    }
    assertResult(1){
      var data = buildData(testJson)
      val toUpdate = ujson.Obj(
        "parameterId" -> "k",
        "timeObserved" -> "1598130000000000",
        "value" -> 4)
      data = updateData(
        data,
        toUpdate("timeObserved").toString,
        toUpdate("parameterId").str,
        toUpdate("value").num)
      data("1598130000000000")("k")("counts").num
    }
  }
  "putParam" should "return a ujson.Value with value -> value and counts -> 1" in {
    assert(putParam(23.0)("counts").num == 1)
    assert(putParam(12345.0)("value").num == 12345.0)
  }

  "putObserved" should "return a ujson.Value with param -> (value-> value, counts -> 1)" in {
    assertResult(123){
      val putted = putObserved("thisParam", 123)
      putted("thisParam")("value").num
    }
    assertResult(true){
      val putted = putObserved("thisParam", 123)
      putted.obj.keySet("thisParam")
    }
  }

}
