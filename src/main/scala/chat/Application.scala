//package chat
//
//import akka.NotUsed
//import akka.actor.{ActorSystem, PoisonPill, Props}
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model.ws.{Message, TextMessage}
//import akka.http.scaladsl.server.Directives.{get, handleWebSocketMessages, path}
//import akka.io.IO
//import akka.stream.{ActorMaterializer, OverflowStrategy}
//import akka.stream.scaladsl.{Flow, Sink, Source}
//import chat.Server
//import chat.ChatRoomActor
//import com.typesafe.config.ConfigFactory
//
//import scala.concurrent.Await
//import scala.io.StdIn
//
//
//class Application() extends ApplicationLifecycle with Logging {
//
//  private[this] var started: Boolean = false
//
//  private val applicationName = "chat"
//
//  implicit val actorSystem = ActorSystem(s"$applicationName-system")
//
//  def start() {
//    logger.info(s"Starting $applicationName Service")
//
//    if (!started) {
//      started = true
//
//      implicit val system = ActorSystem("heimdallr", ConfigFactory.load())
//      implicit val materializer = ActorMaterializer()
//
//      val chatRoom = system.actorOf(Props(new ChatRoomActor), "chat")
//
//      def newUser(): Flow[Message, Message, NotUsed] = {
//        // new connection - new user actor
//        val userActor = system.actorOf(Props(new UserActor(chatRoom)))
//
//        val incomingMessages: Sink[Message, NotUsed] =
//          Flow[Message].map {
//            // transform websocket message to domain message
//            case TextMessage.Strict(text) => UserActor.IncomingMessage(text)
//            // PoisonPill asynchronously stops disconnected user actor
//          }.to(Sink.actorRef[UserActor.IncomingMessage](userActor, PoisonPill))
//
//        val outgoingMessages: Source[Message, NotUsed] =
//          Source.actorRef[UserActor.OutgoingMessage](10000, OverflowStrategy.fail)
//            .mapMaterializedValue { outActor =>
//              // give the user actor a way to send messages out
//              userActor ! UserActor.Connected(outActor)
//              NotUsed
//            }.map(
//            // transform domain message to web socket message
//            (outMsg: UserActor.OutgoingMessage) => TextMessage(outMsg.text))
//
//        // then combine both to a flow
//        Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
//      }
//
//
//      val route =
//        path("chat") {
//          get {
//            handleWebSocketMessages(newUser())
//          }
//        }
//
//      val binding = Await.result(Http().bindAndHandle(route, "0.0.0.0", 8080), 30.seconds)
//
//
//    }
//  }
//
//  def stop() {
//    logger.info(s"Stopping $applicationName Service")
//
//    if (started) {
//      started = false
//      ActorSystem.shutdown()
//    }
//  }
//
//}
