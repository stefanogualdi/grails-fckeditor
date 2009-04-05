package org.gualdi.grails.plugins.fckeditor

import grails.test.*

class FckeditorServiceTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testResourceTypes() {

        def fckeditorService = new FckeditorService()

        def result = ['Image', 'Link', 'Flash', 'Media']

        assertEquals result, fckeditorService.getResourceTypes()

    }
}
