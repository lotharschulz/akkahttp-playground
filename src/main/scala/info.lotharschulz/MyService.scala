package info.lotharschulz

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import spray.json.{CompactPrinter, DefaultJsonProtocol}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

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
      } ~
      get {
        complete(Hello("my msg"))
      }
    }

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val bindingFuture = Http().bindAndHandle(theroute, "localhost", 8080)

    println(s"Server online at http://localhost:8080/hello")
    StdIn.readLine("Hit ENTER to stop...") // let it run until user presses return
    //bindingFuture.flatMap(b => b.unbind()).onComplete(s => system.terminate())
    Await.ready(system.terminate(), Duration.Inf)
  }

  trait MyService {
    val route = theroute
  }

}

