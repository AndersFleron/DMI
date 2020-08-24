import org.scalatest.flatspec.AnyFlatSpec
import PeriodHandler._

class PeriodeHandlerSpec extends AnyFlatSpec {

  "numberOfPeriods" should "return the digits of a string as an Int" in {
    assert(numberOfPeriods("12sdjads") == 12)
    assert(numberOfPeriods("dsd99tt") == 99)
    assert(numberOfPeriods("werrr333") == 333)
  }
  it should "produce IllegalArgumentException if more or less than one digit sequence is delivered" in {
    assertThrows[IllegalArgumentException] {
      numberOfPeriods("we23www44")
    }
    assertThrows[IllegalArgumentException]{
      numberOfPeriods("wewe")
    }
  }
  "unixToSubtract" should
    """return the neccessary microseconds needed to retrieve the data
      |defined in the PERIODE environmentvariable""".stripMargin in {
    assertResult(14400000000L) {
      val periode = "4timer"
      unixToSubtract(4, "t")
    }
    assertResult(259200000000L){
      val periode = "3dage"
      unixToSubtract(3, "d")
    }
    assertResult(3628800000000L){
      val periode = "6uger"
      unixToSubtract(6, "u")
    }
    assertResult(21427200000000L){
      val periode = "8måneder"
      unixToSubtract(8, "m")
    }
    assertResult(63072000000000L){
      val periode = "2år"
      unixToSubtract(2, "å")
    }
  }

}
