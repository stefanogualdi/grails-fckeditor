package org.gualdi.grails.plugins.fckeditor

import org.gualdi.grails.utils.BrowserDetector

import grails.test.*

class BrowserDetectorTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDetection() {
        // See: http://www.useragentstring.com/pages/useragentstring.php
        def uas = [[BrowserDetector.SAFARI,'Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en-gb) AppleWebKit/523.10.6 (KHTML, like Gecko) Version/3.0.4 Safari/523.10.6',3.0],
                   [BrowserDetector.CAMINO,'Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en; rv:1.8.1.6) Gecko/20070809 Camino/1.5.1',1.5],
                   [BrowserDetector.CAMINO,'Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en-US; rv:1.8.0.1) Gecko/20060118 Camino/1.0b2+',1.0],
                   [BrowserDetector.CAMINO,'Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.5b) Gecko/20030917 Camino/0.7+',0.7],
                   [BrowserDetector.OPERA,'Opera/9.20 (Macintosh; Intel Mac OS X; U; en)',9.2],
                   [BrowserDetector.OPERA,'Opera/9.00 (Windows NT 5.1; U; en)',9.0],
                   [BrowserDetector.IE,'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; NeosBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)',6.0],
                   [BrowserDetector.IE,'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; InfoPath.1; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; Dealio Deskball 3.0)',7.0],
                   [BrowserDetector.FIREFOX,'Mozilla/5.0 (X11; U; Darwin Power Macintosh; en-US; rv:1.8.0.12) Gecko/20070803 Firefox/1.5.0.12 Fink Community Edition',1.5],
                   [BrowserDetector.FIREFOX,'Mozilla/5.0 (Windows; U; Windows NT 6.0; en-GB; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11',2.0],
                   [BrowserDetector.KONQUEROR,'Mozilla/5.0 (compatible; Konqueror/4.0; Microsoft Windows) KHTML/4.0.80 (like Gecko)',4.0],
                   [BrowserDetector.KONQUEROR,'Mozilla/5.0 (compatible; Konqueror/3.5; Linux; X11; x86_64) KHTML/3.5.6 (like Gecko) (Kubuntu)',3.5],
                   [BrowserDetector.MOZILLA, 'Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.8.1.8) Gecko/20071009 SeaMonkey/1.1.5',1.8],
                   [BrowserDetector.MOZILLA, 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1b2) Gecko/20060823 SeaMonkey/1.1a',1.8],
                   [BrowserDetector.MOZILLA, 'Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.1) Gecko/20060130 SeaMonkey/1.0',1.8],
                   [BrowserDetector.MOZILLA, 'Mozilla/5.0 (X11; U; FreeBSD i386; en-US; rv:1.7.12) Gecko/20051105',1.7],
                   [BrowserDetector.NETSCAPE, 'Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7.5) Gecko/20050519 Netscape/8.0.1',8.0],
                   [BrowserDetector.NETSCAPE, 'Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.2) Gecko/20040804 Netscape/7.2 (ax)',7.2],
                   [BrowserDetector.NETSCAPE, 'Mozilla/5.0 (Windows; U; WinNT4.0; en-CA; rv:0.9.4) Gecko/20011128 Netscape6/6.2.1',6.2],
                   [BrowserDetector.FIREFOX,'Mozilla/5.0 (X11; U; Darwin Power Macintosh; en-US; rv:1.8.0.12) Gecko/20070803 Firefox/1.AA.0.12 Fink Community Edition',0.0],
                   [BrowserDetector.CHROME,'Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.29 Safari/525.13',0.2],
                   [BrowserDetector.ICEWEASEL,'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1) Gecko/20061024 Iceweasel/2.0 (Debian-2.0+dfsg-1)',2.0],
                   [BrowserDetector.ICEWEASEL,'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.7) Gecko/20061022 Iceweasel/1.5.0.7-g2',1.5],
                   [BrowserDetector.ICEWEASEL,'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1b3pre) Gecko/20090207 Ubuntu/9.04 (jaunty) IceWeasel/3.1b3pre',3.1],
                   [BrowserDetector.ICEWEASEL,'Mozilla/5.0 (X11; U; Linux i686; en-GB; rv:1.9.0.7) Gecko/2009030814 Iceweasel/3.0.9 (Debian-3.0.9-1)',3.0],
                   [BrowserDetector.FIREFOX,'Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; fr; rv:1.9.1b4) Gecko/20090423 Firefox/3.5b4',3.5],
                   [BrowserDetector.FIREFOX,'Mozilla/5.0 (X11; U; Linux i686; en-us; rv:1.9.0.2) Gecko/2008092313 Ubuntu/9.04 (jaunty) Firefox/3.5',3.5],
                   [BrowserDetector.FIREFOX,'Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.1) Gecko/20090624 Firefox/3.5',3.5]

        ]

	uas.each { itm ->
            def bd = new BrowserDetector(itm[1])
            assertEquals( "No match for browser", itm[0], bd.browser)
            assertEquals( "No match for version", "${itm[2]}", "${bd.version}")
        }
    }
}
