package info.lotharschulz.misc

import akka.event.Logging.LogLevel
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.{HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteResult.Complete
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LogEntry, LoggingMagnet}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink

import scala.concurrent.{ExecutionContext, Future}

// http://stackoverflow.com/questions/32475471/how-does-one-log-akka-http-client-requests/32862348#32862348
object log {

  def logRequestResult(level: LogLevel, route: Route)
                      (implicit m: Materializer, ex: ExecutionContext) = {
    def myLoggingFunction(logger: LoggingAdapter)(req: HttpRequest)(res: Any): Unit = {
      val entry = res match {
        case Complete(resp) =>
          entityAsString(resp.entity).map(data ⇒ LogEntry(s"${req.method} ${req.uri}: ${resp.status} \n entity: $data", level))
        case other =>
          Future.successful(LogEntry(s"$other", level))
      }
      entry.map(_.logTo(logger))
    }
    DebuggingDirectives.logRequestResult(LoggingMagnet(log => myLoggingFunction(log)))(route)
  }

  def entityAsString(entity: HttpEntity)
                    (implicit m: Materializer, ex: ExecutionContext): Future[String] = {
    entity.dataBytes
      //.map(_.decodeString(entity.contentType().charset().value))
      .map(_.decodeString("utf-8"))
      .runWith(Sink.head)
  }

}
