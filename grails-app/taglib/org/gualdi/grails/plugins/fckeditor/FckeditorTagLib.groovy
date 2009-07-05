package org.gualdi.grails.plugins.fckeditor

/**
 * @author Stefano Gualdi
 */
class FckeditorTagLib {
	static namespace = 'fckeditor'

    def config = { attrs ->
        def cfg = new FckeditorConfig(request)
        try {
            cfg.addConfigItem(attrs)
        }
        catch (Exception e) {
            throwTagError(e.message)
        }
    }

	def editor = { attrs, body ->
		def editor = new Fckeditor(request, attrs)
		editor.initialValue = body

        editor.renderEditor(out)
	}

    def fileBrowser = { attrs, body ->
        out << "<a href="
        out << "\""
        out << fileBrowserLink(attrs)
        out << "\""
        out << " "
        if (attrs.target) {
            out << "target="
            out << "\""
            out << attrs.target
            out << "\""
            out << " "
        }
        out << ">"
        out << body()
        out << "</a>"
    }

    def fileBrowserLink = { attrs ->
        def editor = new Fckeditor(request, attrs)
        def type = attrs.type ?: 'File'
        def browser = attrs.browser ?: 'default' // (default|extended)

        out << editor.createFileBrowser(type, browser)
    }
}
