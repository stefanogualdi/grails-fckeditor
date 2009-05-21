package org.gualdi.grails.utils

/**
 * @author Stefano Gualdi
 */
class BrowserDetector {
	public static final UNKNOWN = 'unknown'
    public static final SAFARI = 'safari'
	public static final CAMINO = 'camino'
    public static final OPERA = 'opera'
    public static final KONQUEROR = 'konqueror'
    public static final IE = 'ie'
    public static final FIREFOX = 'firefox'
    public static final NETSCAPE = 'netscape'
    public static final MOZILLA = 'mozilla'
    public static final CHROME = 'chrome'
    public static final ICEWEASEL = 'iceweasel'

	// NB: Order of the items is important
	public static final DETECT_STRINGS = [
        [browserId:'chrome', versionId:'chrome', browserName: CHROME],
        [browserId:'iceweasel', versionId:'iceweasel', browserName: ICEWEASEL],
        [browserId:'safari', versionId:'version', browserName: SAFARI],
        [browserId:'camino', versionId:'camino', browserName: CAMINO],
        [browserId:'opera', versionId:'opera', browserName: OPERA],
        [browserId:'konqueror', versionId:'konqueror', browserName: KONQUEROR],
        [browserId:'msie', versionId:'msie', browserName: IE],
        [browserId:'firefox', versionId:'firefox', browserName: FIREFOX],
        [browserId:'netscape6', versionId:'netscape6', browserName: NETSCAPE],
        [browserId:'netscape', versionId:'netscape', browserName: NETSCAPE],
        [browserId:'gecko', versionId:'rv', browserName: MOZILLA]
    ]
		
	String ua
    String browser
    Float version
    
	BrowserDetector(userAgentStr) {
        this.browser = UNKNOWN
        this.version = 0.0

		this.ua = userAgentStr
        if (this.ua) {
            ua = this.ua.toLowerCase()
            for( brw in DETECT_STRINGS ) {
            	if ( ua.indexOf(brw.browserId) > -1) {
            		this.browser = brw.browserName
            		def vIdx = ua.indexOf(brw.versionId)
            		vIdx = vIdx + brw['versionId'].size()
           			def vStr = ua[(vIdx + 1)..(vIdx + 3)]
            		try {
            			this.version = Float.parseFloat(vStr)
            		}
            		catch (NumberFormatException nfe) {
            			this.version = 0.0
            		}
            		break;
            	}
            }
        }
	}
    
    def isCompatible(browsersMap) {
 		return (browsersMap.containsKey(this.browser) && (this.version >= browsersMap[this.browser]))
	}
}
