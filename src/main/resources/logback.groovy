import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import static ch.qos.logback.classic.Level.*


 /**
 * http://logback.qos.ch/manual/groovy.html
 * http://logback.qos.ch/manual/layouts.html
 */

/*
appender( "FILE", FileAppender  ) {
    file   = "gcommons.log"
    append = true
    encoder( PatternLayoutEncoder ) { pattern = "[%date][%-5level] [%logger] - [%msg]%n" }
}
*/

appender( "CONSOLE", ConsoleAppender ) {
    encoder( PatternLayoutEncoder ) { pattern = "[%date][%-5level] [%logger] - [%msg]%n" }
}

//root( WARN, [ "CONSOLE" ] ) - causes "http://evgeny-goldin.org/youtrack/issue/pl-256"
logger( "org.springframework", WARN, [ "CONSOLE" ] )
logger( "com.goldin",          INFO, [ "CONSOLE" ] )
