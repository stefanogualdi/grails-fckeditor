import org.gualdi.grails.plugins.fckeditor.Fckeditor

/**
 * @author Stefano Gualdi
 */
class FckeditorTagLib {
	static namespace = 'fckeditor'

	def editor = { attrs, body ->
		def editor = new Fckeditor( request, attrs )
		editor.initialValue = body()

		out << editor.create()
	}
}