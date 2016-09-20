package info.lotharschulz

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import info.lotharschulz.MyService.MyService
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MyServiceSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalatestRouteTest with MyService{

  "MyServiceSpec get" should "return my msg" in {
    Get("/hello") ~> Route.seal(route) ~> check {
      status === StatusCodes.OK
      contentType === ContentTypes.`application/json`
      entityAs[String] shouldEqual "{\"msg\":\"my msg\"}"
    }
  }

  "MyServiceSpec post" should "should return a hello msg" in {
    val httpRequest = HttpRequest(
      method = HttpMethods.POST, 
      uri = "/hello",
      entity = HttpEntity(MediaTypes.`application/json`, """{"msg":"bla"}""")) 
    httpRequest ~> Route.seal(route) ~> check{
      status === StatusCodes.Created
      contentType === ContentTypes.`application/json`
      entityAs[String] shouldEqual "hello msg: bla"
    }
  }

  "MyServiceSpec put" should "return a MethodNotAllowed" in {
    Put() ~> Route.seal(route) ~> check {
      status === StatusCodes.MethodNotAllowed
    }
  }
  
  override protected def afterAll():Unit = {
    Await.ready(system.terminate(), Duration.Inf)
    super.afterAll()
  }

}
