package info.lotharschulz

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import info.lotharschulz.misc.log
import spray.json.{CompactPrinter, DefaultJsonProtocol}

// import spray.json.JsValue

final case class Hello(msg: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val printer = CompactPrinter
  implicit val helloFormat = jsonFormat1(Hello)
}

object MyService extends Directives with JsonSupport {
  val theroute =
    path("hello") {
      post {
        entity(as[Hello]) {
          hello: Hello => complete {
            s"hello msg: ${hello.msg}"
          }
        }
        /*
      entity(as[JsValue]) {
        json => complete (s"hello msg: ${json.asJsObject.fields("msg")}")
      }
      */
      } ~
        get {
          complete(Hello("my msg"))
        }
    }
    path("/"){
      get{
        complete("{\"welcome\":\"message\"}")
      }
    }

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    // "0.0.0.0" instead of "localhost" 'cause of
    // http://stackoverflow.com/questions/27806631/docker-rails-app-fails-to-be-served-curl-56-recv-failure-connection-reset/27849860#27849860
    val logroute = log.logRequestResult(Logging.InfoLevel, theroute)
    val bindingFuture = Http().bindAndHandle(logroute, "0.0.0.0", 8181)

    println(s"Server online at http://localhost:8181/hello")
  }

  trait MyService {
    val route = theroute
  }

}

