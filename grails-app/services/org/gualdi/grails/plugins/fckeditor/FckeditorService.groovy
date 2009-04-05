package org.gualdi.grails.plugins.fckeditor

class FckeditorService {

    boolean transactional = false

    def getResourceTypes() {
        return FckeditorConfig.getResourceTypes()
    }
}
