/**
 * Created with IntelliJ IDEA.
 * User: dirlt
 * Date: 11/6/13
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */

import com.dirlt.scala.finagle.BasicServerConfig
import com.twitter.logging.config._
import com.twitter.ostrich.admin.config.{TimeSeriesCollectorConfig, StatsConfig, AdminServiceConfig}

new BasicServerConfig {
  serverPort = 8000

  loggers = new LoggerConfig {
    level = Level.DEBUG
    handlers = new ConsoleHandlerConfig
  }

  admin = new AdminServiceConfig {
    httpPort = 8888
    statsNodes = new StatsConfig {
      reporters = new TimeSeriesCollectorConfig
    }
  }
}