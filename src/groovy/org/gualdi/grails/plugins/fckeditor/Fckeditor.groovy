package org.gualdi.grails.plugins.fckeditor

import org.gualdi.grails.utils.BrowserDetector
import org.gualdi.grails.utils.PathUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.web.taglib.GroovyPageTagBody
import org.apache.log4j.Logger

/**
* @author Stefano Gualdi
*/
class Fckeditor {
    private final Logger log = Logger.getLogger(getClass())
    
	static final DEFAULT_BASEDIR = "/uploads/"
	static final DEFAULT_FILEBROWSER = "default"
	static final DEFAULT_USERSPACE = ""
	static final DEFAULT_INSTANCENAME = "editor"
	static final DEFAULT_TOOLBAR = "Default"
	static final DEFAULT_WIDTH = "100%"
	static final DEFAULT_HEIGHT = "200"

	// Generic errors
	static final ERROR_NOERROR = 0
	static final ERROR_CUSTOM = 1

	// Connector errors
	static final ERROR_FOLDER_EXISTS = 101
	static final ERROR_INVALID_FOLDER_NAME = 102
	static final ERROR_NO_CREATE_PERMISSIONS = 103
	static final ERROR_INVALID_FILE_NAME = 104
	static final ERROR_CANNOT_DELETE = 105
	static final ERROR_UNKNOWN = 110

	// Uploader errors
	static final ERROR_FILE_RENAMED = 201
	static final ERROR_INVALID_FILE_TYPE = 202
	static final ERROR_NO_UPLOAD_PERMISSIONS = 203

	// Mapping for config parameters encoding 
	static final PARAMS_CONFIG_MAPPING = [["&","%26"],
	                                      ["=","%3D"],
	                                      ["\"","%22"]
	]
	
	static final COMPATIBLE_BROWSERS = [(BrowserDetector.IE): 5.5,
	                                    (BrowserDetector.FIREFOX): 1.5,
	                                    (BrowserDetector.SAFARI): 3.0,
	                                    (BrowserDetector.OPERA): 9.5,
	                                    (BrowserDetector.NETSCAPE): 7.1,
	                                    (BrowserDetector.CAMINO): 1.0,
                                        (BrowserDetector.CHROME): 0.2,
                                        (BrowserDetector.ICEWEASEL): 2.0
	                                    ]
	
	def contextPath
	def basePath
	def instanceName
	def fileBrowser
    def userSpace
	def toolbar
	def width
	def height
	def initialValue // = ""

	def configField

	def request

	Fckeditor(request, attrs) {
		def config = ConfigurationHolder.config.fckeditor

		this.request = request
		this.contextPath = request.contextPath
		this.basePath = getPluginResourcePath(this.contextPath)

		// Base configuration
		instanceName = attrs.name ? attrs.remove('name') : DEFAULT_INSTANCENAME
		fileBrowser = attrs.fileBrowser ? attrs.remove('fileBrowser') : DEFAULT_FILEBROWSER
		userSpace = attrs.userSpace ? PathUtils.sanitizePath(attrs.remove('userSpace')) : DEFAULT_USERSPACE
        toolbar = attrs.toolbar ? attrs.remove('toolbar') : DEFAULT_TOOLBAR
		width = attrs.width ? attrs.remove('width') : DEFAULT_WIDTH
		height = attrs.height ? attrs.remove('height') : DEFAULT_HEIGHT

		// Dynamically create connector configuration
		def tempConfig = [:]

		def resources = FckeditorConfig.getResourceTypes()

		resources.each { type ->
			// Filemanager url and connector
			def key = "${type}BrowserURL"
			def typeStr = ""
			if ( type != 'Link') {
				typeStr = "Type=${type}&"
			}
            def value = "${basePath}/js/fckeditor/editor/filemanager/browser/${fileBrowser}/browser.html?${typeStr}Connector=${contextPath}/fckconnector${userSpace ? '?userSpace='+ userSpace : ''}"
			tempConfig[key] = value

			// File Browser is enabled?
			key = "${type}Browser"
			value = config.upload."${type.toLowerCase()}".browser
			tempConfig[key] = value ? 'true' : 'false'
		}

		resources.each { type ->
			def key = "${type}UploadURL"
			def value = "${contextPath}/fckuploader?Type=${type == 'Link' ? 'File' : type}${userSpace ? '&userSpace='+ userSpace : ''}"
			tempConfig[key] = value

			// Upload tab is enabled?
			key = "${type}Upload"
			value = config.upload."${type.toLowerCase()}".upload
			tempConfig[key] = value ? 'true' : 'false'

			def tmp
			['Allowed', 'Denied'].each { act ->
				key = "${type}Upload${act}Extensions"
				def list = config.upload."${type.toLowerCase()}"."${act.toLowerCase()}" as ArrayList
				tmp = list.join('|')
				if (tmp) {
					value = ".(${tmp})\$"
				}
				else {
					value = ""
				}
				tempConfig[key] = value
			}
		}

        // Check if extra config is present
        def extraCfg = new FckeditorConfig(request).config
        if (extraCfg) {
            tempConfig += extraCfg
        }

		configField = tempConfig.collect { key, value -> "${key}=${encodeConfig(value)}" }.join('&')
	}
    
    def renderEditor(out) {
        def skipBrowserCheck = ConfigurationHolder.config.fckeditor.skipBrowserCheck ?: false

		def userAgent = request.getHeader("user-agent")
		def bd = new BrowserDetector(userAgent)

		if (skipBrowserCheck || bd.isCompatible(COMPATIBLE_BROWSERS)) {
    		out << "<div><input type=\"hidden\" id=\"${instanceName}\" name=\"${instanceName}\" value=\""
    		if (!(initialValue instanceof String)) {

                if (initialValue instanceof GroovyPageTagBody) {
                    log.debug "IS GroovyPageTagBody"
                    out << initialValue()?.encodeAsHTML()
                }
                else {
                    log.debug "IS GroovyPage"
                    initialValue() // Grails 1.1.x bugs write out immediately without encodeAsHTML support
                }
    		}
            else {
                log.debug "IS String"
    		    out << initialValue.encodeAsHTML()
    		}
    		out << """
    		"><input type="hidden" id="${instanceName}___Config" value="${configField}"/>
    			<iframe id="${instanceName}___Frame" src="${basePath}/js/fckeditor/editor/fckeditor.html?InstanceName=${instanceName}&Toolbar=${toolbar}" width="${width}" height="${height}" frameborder="no" scrolling="no"></iframe>
    		</div>
    		"""
    	}
        else {
            out << """
    		<div>
    			<textarea name="${instanceName}" rows="4" cols="40" style="width: 100%; height: 200px; wrap="virtual">
    	    """
    		if (!(initialValue instanceof String)) {
                if (initialValue instanceof GroovyPageTagBody) {
                    out << initialValue()
                }
                else {
                    initialValue() // Grails 1.1.x bugs write out immediately without encodeAsHTML support
                }
    		}
            else {
    		    out << initialValue.encodeAsHTML()
    		}
    		out << """</textarea>
    		    </div>
		    """
		}
	}

    def createFileBrowser(type, browser) {
        def typeStr = ""
        if ( type != 'Link') {
            typeStr = "Type=${type}&"
        }
        def html = "${this.basePath}/js/fckeditor/editor/filemanager/browser/${browser}/browser.html?${typeStr}Connector=${this.contextPath}/fckconnector${userSpace ? '?userSpace='+ userSpace : ''}"
        return html
    }

	private String encodeConfig(txt) {
		def result = txt.toString()
		for (r in PARAMS_CONFIG_MAPPING ) {
			result = result.replaceAll(r[0], r[1])
		}
		return result
	}

	private String getPluginResourcePath(String contextPath) {
		String pluginName = "fckeditor"
		String pluginVersion = PluginManagerHolder?.pluginManager?.getGrailsPlugin(pluginName)?.version
		return "${contextPath}/plugins/${pluginName.toLowerCase()}-$pluginVersion"
	}
}
