package org.gualdi.grails.plugins.fckeditor

import org.gualdi.grails.utils.PathUtils

import grails.test.*

class PathUtilsTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testPathSanitization() {
        def path = ".\\/|:?*\"<>'`~finalpath-one.\\/|:?*\"<>'`~"

        def result = PathUtils.sanitizePath(path)
        assertEquals "finalpath-one", result

        path = ".\\/|:?*\"<>'`~finalpath.\\/|:?*\"<>'`~one.\\/|:?*\"<>'`~"
        result = PathUtils.sanitizePath(path)
        assertEquals "finalpathone", result

        path = "../../../../../bin/mkfs"
        result = PathUtils.sanitizePath(path)
        assertEquals "binmkfs", result

        path = "c:\\windows\\command\\format.exe c:"
        result = PathUtils.sanitizePath(path)
        assertEquals "cwindowscommandformatexec", result

        path = "first                  second             third              "
        result = PathUtils.sanitizePath(path)
        assertEquals "firstsecondthird", result
    }

    void testFilenameSplit() {
        def filename = "name.ext"

        def result = PathUtils.splitFilename(filename)

        assertEquals "name", result.name
        assertEquals "ext", result.ext
    }


    void testSlashes() {
        def path = "/home/user/space/"

        def res1 = PathUtils.checkSlashes(path, "L- R-")
        assertEquals "home/user/space", res1

        path = "home/user/space"
        def res2 = PathUtils.checkSlashes(path, "L+ R+")
        assertEquals "/home/user/space/", res2
    }
}