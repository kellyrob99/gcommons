import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.WARN

/**
 * http://logback.qos.ch/manual/groovy.html
 * http://logback.qos.ch/manual/layouts.html
 */

appender( "FILE", FileAppender  ) {
    file   = "gcommons.log"
    append = true
    encoder( PatternLayoutEncoder ) { pattern = "[%date][%-5level] [%logger] - [%msg]%n" }
}

appender( "CONSOLE", ConsoleAppender ) {
    encoder( PatternLayoutEncoder ) { pattern = "[%date][%-5level] [%logger] - [%msg]%n" }
}

root( WARN, [ "CONSOLE", "FILE" ] )
logger( "com.goldin", INFO, [ "CONSOLE", "FILE" ] )
