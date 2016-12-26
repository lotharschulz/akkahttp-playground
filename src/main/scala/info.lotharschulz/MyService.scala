package info.lotharschulz

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import info.lotharschulz.misc.log
import spray.json.{CompactPrinter, DefaultJsonProtocol}

// import spray.json.JsValue

final case class Hello(msg: String)
final case class Health(status: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val printer = CompactPrinter
  implicit val helloFormat = jsonFormat1(Hello)
  implicit val welcomeFormat = jsonFormat1(Health)
}

object MyService extends Directives with JsonSupport {
  val theroute =
    pathSingleSlash{
      get{
        complete(Health("is up"))
      }
    } ~
      path("hello") {
        post {
          entity(as[Hello]) {
            //hello: Hello => complete { s"hello msg: ${hello.msg}" }
            hello: Hello => complete { StatusCodes.Created -> Hello({hello.msg}) }
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

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
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

