package info.lotharschulz

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import info.lotharschulz.MyService.MyService
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MyServiceSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalatestRouteTest with MyService{

  "MyServiceSpec get" should "return my msg" in {
    Get("/hello") ~> Route.seal(route) ~> check {
      status === StatusCodes.OK
      //contentType === ContentTypes.`text/html(UTF-8)`
      contentType === ContentTypes.`application/json`
      entityAs[String] shouldEqual "{\"msg\":\"my msg\"}"
    }
  }

  "MyServiceSpec post" should "should return a hello msg" in {
    Post("/hello", "new msg") ~> Route.seal(route) ~> check{
      status === StatusCodes.Created
      contentType === ContentTypes.`application/json`
      // @TODO test content payload
      // entityAs[String] shouldEqual "hello's msg: new msg"
    }
  }

  override protected def afterAll():Unit = {
    Await.ready(system.terminate(), Duration.Inf)
    super.afterAll()
  }

}
