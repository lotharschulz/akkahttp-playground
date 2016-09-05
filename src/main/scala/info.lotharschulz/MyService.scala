package info.lotharschulz

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

final case class Hello(msg: String)

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val helloFormat = jsonFormat1(Hello) // contains List[Item]
}

object MyService {
  val theroute =
    path("hello") {
      post {
        entity(as[Hello]) {
          hello: Hello => complete {
            s"hello's msg: ${hello.msg}"
          }
        }
      } ~
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<p>hi akka http</p>"))
      }
    }

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route = theroute
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/hello")
    StdIn.readLine("Hit ENTER to stop...") // let it run until user presses return
    //bindingFuture.flatMap(b => b.unbind()).onComplete(s => system.terminate())
    Await.ready(system.terminate(), Duration.Inf)
  }

  trait MyService {
    val route = theroute
  }

}

