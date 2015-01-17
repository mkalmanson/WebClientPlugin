import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import webclient.Get

import java.lang.annotation.Annotation
import java.lang.reflect.Method

class WebClientPluginGrailsPlugin {

    private final Logger log = LoggerFactory.getLogger('grails.plugin.webclient.WebClientPlugin')
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Web Client Plugin Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/web-client-plugin"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def observe = ['services']
    def loadAfter = ['services']

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
        ctx.grailsApplication.serviceClasses.each { serviceClass ->
            replaceAnnotatedMethods(ctx, serviceClass.clazz)
        }
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        try {
            Class clazz
            if(event.source instanceof Class) {
                clazz = event.source
                replaceAnnotatedMethods(event.ctx, clazz)
            } else {
                log.debug "@WebClient: skip change event - ${event.source}"
            }

            if (clazz) {
                log.trace "@WebClient: updating beans config values of class ${event.source}"

                handler.resetClass(clazz)
                handler.initClass(clazz, application)

                log.trace "@WebClient: onChange completed"
            }
        } catch (Throwable e) {
            log.error "@WebClient: exception on processing change event: ${event} - ${e}", e
        }
    }

    def replaceAnnotatedMethods(ApplicationContext ctx, Class clazz) {
        log.debug "@WebClient: replaceAnnotatedMethods $clazz"
        clazz.declaredMethods.each { method ->
            def annotation = getWebClientAnnotation(method)
            if (annotation) {
                log.debug "@WebClient: Found annotation $clazz ${method.name}"
                replaceMethod(ctx, clazz, method, annotation)
            } else {
                log.debug "@WebClient: Did not find annotation $clazz ${method.name}"
            }
        }
    }
    def getWebClientAnnotation(Method method) {
        def types = [Get]
        method.declaredAnnotations.find { it ->
            types.contains(it.annotationType())
        }
    }
    def replaceMethod(ApplicationContext ctx, Class clazz, Method method, Annotation annotation) {
        clazz.metaClass[method.name] = {->
            if (annotation.annotationType() == Get) {
                log.debug "@WebClient: Doing GET"
                Get getAnnotation = (Get) annotation

                ctx.webClientService.get(getAnnotation.url(), getAnnotation.type())
            } else {
                log.debug "@WebClient: Method not found"
            }

        }
    }
    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
