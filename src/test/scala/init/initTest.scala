package init
import org.scalatest._
import commands.init

class initTest  extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  describe("If you run init") {
    describe("and there is not yet a sgit repository ") {
      it("it should return true and create a directory sgitRepo.") {
        var firstInit = init.initDirectory()
        assert(firstInit === true)
      }
    }

    describe("and there is already a sgit repository ") {
      it("it should return false and not create a directory sgitRepo.") {
        var sndInit = init.initDirectory()
        assert(sndInit === false)
      }
    }
  }
}
