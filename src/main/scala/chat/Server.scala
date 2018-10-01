package chat

import akka.NotUsed
import akka.actor._
import akka.http.scaladsl._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.io.StdIn
import com.typesafe.config.ConfigFactory

object Server {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("mychat", ConfigFactory.load())
    implicit val materializer = ActorMaterializer()

    println (system.settings.config.getValue("akka.loggers"))

    val chatRoom = system.actorOf(Props(new ChatRoomActor), "chat")

    def newUser(): Flow[Message, Message, NotUsed] = {
      // new connection - new user actor
      val userActor = system.actorOf(Props(new UserActor(chatRoom)))

      val incomingMessages: Sink[Message, NotUsed] =
        Flow[Message].map {
          // transform websocket message to domain message
          case TextMessage.Strict(text) => UserActor.IncomingMessage(text)
        }.to(Sink.actorRef[UserActor.IncomingMessage](userActor, PoisonPill))

      val outgoingMessages: Source[Message, NotUsed] =
        Source.actorRef[UserActor.OutgoingMessage](10000, OverflowStrategy.fail)
          .mapMaterializedValue { outActor =>
            // give the user actor a way to send messages out
            userActor ! UserActor.Connected(outActor)
            NotUsed
          }.map(
          // transform domain message to web socket message
          (outMsg: UserActor.OutgoingMessage) => TextMessage(outMsg.text))

      // then combine both to a flow
      Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
    }

    val route =
      path("chat") {
        get {
          handleWebSocketMessages(newUser())
        }
      }

    val binding = Await.result(Http().bindAndHandle(route, "0.0.0.0", 8080), 30.seconds)

    // the rest of the sample code will go here
    println("Started server at 127.0.0.1:8080, press enter to kill server")
    StdIn.readLine()
    system.terminate()
  }

}