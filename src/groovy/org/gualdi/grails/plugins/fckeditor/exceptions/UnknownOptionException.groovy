package org.gualdi.grails.plugins.fckeditor.exceptions

import org.codehaus.groovy.grails.exceptions.GrailsException

/**
 * @author Stefano Gualdi
 *
 * Created: 17-Feb-2009
 */
class UnknownOptionException extends GrailsException {

    public UnknownOptionException() {
        super()
    }

    public UnknownOptionException(String arg0) {
        super(arg0)
    }

    public UnknownOptionException(String arg0, Throwable arg1) {
        super(arg0, arg1)
    }

    public UnknownOptionException(Throwable arg0) {
        super(arg0)
    }
}