package nstic.web

import edu.gatech.gtri.trustmark.v1_0.FactoryLoader
import edu.gatech.gtri.trustmark.v1_0.impl.util.TrustmarkMailClientImpl
import edu.gatech.gtri.trustmark.v1_0.util.TrustmarkMailClient
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import nstic.TATPropertiesHolder

import javax.servlet.ServletException

class ErrorController {

    def springSecurityService

    def binaryIds = []  // not thread safe, but for demo purposes

    @Secured("ROLE_ADMIN")
    def index() {
        log.debug("Loading error index page (for testing)...")
    }

    @Secured("ROLE_ADMIN")
    def generate500() {
        log.warn("Admin User[@|cyan ${springSecurityService.currentUser}|@] is triggering a Error 500 event intentionally...")
        throw new ServletException("Disregard - this message autogenerated by user: ${springSecurityService.currentUser}")
    }//end generate500()

    @Secured("permitAll")
    def error500() {
        def exception = request.exception
        def cause = exception?.cause
        while( cause?.cause )
            cause = cause?.cause

        log.error("The system generated an exception[" + exception?.toString() + "] caused by: [${cause?.toString()}]");
        withFormat{
            html{
                [cause: cause, exception: exception]
            }
            xml {
                render(contentType: 'application/xml',
                        text: "<error code=\"500\">\n"+
                                "    <exception class=\"${exception.class.name}\"><![CDATA[${exception.message}]]></exception>\n"+
                                (cause ?
                                        "    <cause class=\"${cause.class.name}\"><![CDATA[${cause.message}]]></cause>\n" : "" )+
                                "</error>")
            }
            json {
                render(contentType: 'application/json',
                        text: "{'code': 500, exception: "+
                                "{message: '${exception.message}', class: '${exception.class.name}'}"+
                                (cause ?
                                        ", cause: {class: '${cause.class.name}', message: '${cause.message}'}" : "" )+
                                "}")
            }
        }
    }

    @Secured("permitAll")
    def notFound404(){
        log.warn("User[@|yellow ${springSecurityService.currentUser ?: request.remoteAddr}|@] has requested unknown page: ${request.getAttribute('javax.servlet.error.request_uri')}")
    }//end notFound404

    @Secured("permitAll")
    def notAuthorized401(){
        log.warn("User[@|red ${springSecurityService.currentUser ?: request.remoteAddr}|@] has requested unauthorized page: ${request.getAttribute('javax.servlet.error.request_uri')}")
    }//end notAuthorized401

}//end ErrorController()
