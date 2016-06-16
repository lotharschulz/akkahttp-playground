package info.lotharschulz

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import info.lotharschulz.MyService.MyService
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MyServiceSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalatestRouteTest with MyService{

  "MyServiceSpec" should "return smth" in {
    Get("/hello") ~> Route.seal(route) ~> check {
      status === StatusCodes.OK
      contentType === ContentTypes.`text/html(UTF-8)`
      entityAs[String] shouldEqual "<p>hi akka http</p>"
    }
  }

  override protected def afterAll():Unit = {
    Await.ready(system.terminate(), Duration.Inf)
    super.afterAll()
  }

}
