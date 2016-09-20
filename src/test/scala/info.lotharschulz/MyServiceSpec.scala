package info.lotharschulz

import akka.http.scaladsl.model._
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
      contentType === ContentTypes.`application/json`
      entityAs[String] shouldEqual "{\"msg\":\"my msg\"}"
    }
  }

  "MyServiceSpec post" should "should return a hello msg" in {
    
    /*
        val jsonRequest = ByteString(
      s"""
         |{
         |    "msg":"bla"
         |}
        """.stripMargin)
    Post(uri = "/hello", entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)) ~> Route.seal(route) ~> check{
    */

    val formData = FormData(("msg", "bla")).toEntity
    val headers = Nil
    val httpRequest = HttpRequest(HttpMethods.POST, "/hello", headers, formData) 
    httpRequest ~> Route.seal(route) ~> check{
      status === StatusCodes.Created
      contentType === ContentTypes.`application/json`
      // @TODO test content payload
      // entityAs[String] shouldEqual "hello msg: new msg"
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
