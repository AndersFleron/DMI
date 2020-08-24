import org.scalatest.flatspec.AnyFlatSpec

class TransformerSpec extends AnyFlatSpec {
  "afrundTilTime" should "return any unix timestamp in us since epoch rounded down to nearest hour" in {
    assertResult("1598220000000000"){
      val begin = "1598220473000000"
      afrundTilTime(begin)
    }
  }
}
