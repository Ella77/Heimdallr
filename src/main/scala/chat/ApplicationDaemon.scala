//package chat
//
//import org.apache.commons.daemon._
//
//
//trait ApplicationLifecycle {
//  def start(): Unit
//  def stop(): Unit
//}
//
//
//abstract class AbstractApplicationDaemon extends Daemon {
//  def application: ApplicationLifecycle
//
//  def init(daemonContext: DaemonContext) {}
//
//  def start() = application.start()
//
//  def stop() = application.stop()
//
//  def destroy() = application.stop()
//}
//
//
//class ApplicationDaemon() extends AbstractApplicationDaemon {
//  def application = new Application
//}
//
//
//object ServiceApplication extends App {
//
//  val application = createApplication()
//
//  def createApplication() = new ApplicationDaemon
//
//  private[this] var cleanupAlreadyRun: Boolean = false
//
//  def cleanup(){
//    val previouslyRun = cleanupAlreadyRun
//    cleanupAlreadyRun = true
//    if (!previouslyRun) application.stop()
//  }
//
//  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
//    def run() {
//      cleanup()
//    }
//  }))
//
//  application.start()
//}