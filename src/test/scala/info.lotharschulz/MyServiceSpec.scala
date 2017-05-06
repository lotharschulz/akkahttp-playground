package info.lotharschulz

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import info.lotharschulz.MyService.MyService
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MyServiceSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalatestRouteTest with MyService{

  "MyServiceSpec GET on /" should "return a welcome message" in {
    Get("/") ~> Route.seal(route) ~> check {
      status shouldBe StatusCodes.OK
      assert(contentType === ContentTypes.`application/json`)
      entityAs[String] shouldBe "{\"status\":\"is up\"}"
    }
  }

  "MyServiceSpec PUT on /" should "return a MethodNotAllowed" in {
    Put("/") ~> Route.seal(route) ~> check {
      assert(status === StatusCodes.MethodNotAllowed)
    }
  }

  "MyServiceSpec GET on /hello" should "return my msg" in {
    Get("/hello") ~> Route.seal(route) ~> check {
      status shouldBe StatusCodes.OK
      assert(contentType === ContentTypes.`application/json`)
      //assert(entityAs[String] === "{\"msg\":\"my msg\"}")
      entityAs[String] shouldEqual "{\"msg\":\"my msg\"}"
    }
  }

  "MyServiceSpec POST on /hello with predefined msg" should "should return the posted msg" in {
    val httpRequest = HttpRequest(
      method = HttpMethods.POST, 
      uri = "/hello",
      entity = HttpEntity(MediaTypes.`application/json`, """{"msg":"bla"}""")) 
    httpRequest ~> Route.seal(route) ~> check{
      status shouldBe StatusCodes.Created
      assert(contentType === ContentTypes.`application/json`)
      entityAs[String] shouldEqual "{\"msg\":\"bla\"}"
    }
  }

  override protected def afterAll():Unit = {
    Await.ready(system.terminate(), Duration.Inf)
    super.afterAll()
  }

}
