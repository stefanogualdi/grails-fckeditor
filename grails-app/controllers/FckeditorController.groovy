import org.gualdi.grails.plugins.fckeditor.Fckeditor

/**
 * @author Stefano Gualdi
 */
class FckeditorController {

	def connector = {
		execute(params.Command, params.CurrentFolder)
	}

	def uploader = {
		execute('FileUpload', '/', true)
	}

    private execute(command, currentFolder, uploadOnly = false) {
		def config = grailsApplication.config.fckeditor

    	def baseDir = config.upload.basedir ?: Fckeditor.DEFAULT_BASEDIR
    			
    	if (!baseDir.startsWith('/')) {
    		baseDir = "/" + baseDir
    	}
    	if (!baseDir.endsWith('/')) {
    		baseDir = baseDir + "/"
    	}

		def type = params.Type

		def currentPath = "${baseDir}${type}${currentFolder}"
		def currentUrl = "${request.contextPath}${currentPath}"
		def realPath = servletContext.getRealPath(currentPath)

    	def finalDir = new File(realPath)
    	if (!finalDir.exists()) {
			finalDir.mkdirs()
    	}

    	log.debug("Command = ${command}")
    	log.debug("CurrentFolder = ${currentFolder}")
    	log.debug("Type = ${type}")
    	log.debug("finalDir = ${finalDir}")

    	def errorNo
    	def errorMsg

    	switch( command ) {
			case 'GetFolders':
				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector( command: command, resourceType: type) {
						CurrentFolder( path: currentFolder, url: currentUrl )
						Folders {
							finalDir.eachDir {
								Folder(name: it.name)
							}
						}
					}
				}
				break
			case 'GetFoldersAndFiles':
				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector( command: command, resourceType: type) {
						CurrentFolder( path: currentFolder, url: currentUrl )
						Folders {
							finalDir.eachDir {
								Folder(name: it.name)
							}
						}
						Files {
							finalDir.eachFile {
								if ( !it.directory ) {
									'File'(name: it.name, size: it.length() / 1024)
								}
							}
						}
					}
				}
				break
			case 'CreateFolder':
				def newFolderName = params.NewFolderName
				def newFinalDir = new File( finalDir, newFolderName )
				errorNo = Fckeditor.ERROR_NOERROR

				if (newFinalDir.exists()) {
					errorNo = Fckeditor.ERROR_FOLDER_EXISTS
				}
				else {
					try {
						if( newFinalDir.mkdir() ) {
							errorNo = Fckeditor.ERROR_NOERROR
						}
						else {
							errorNo = Fckeditor.ERROR_INVALID_FOLDER_NAME
						}
					}
					catch(SecurityException se) {
						errorNo = Fckeditor.ERROR_NO_CREATE_PERMISSIONS
					}
				}

				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector( command: command, resourceType: type) {
						CurrentFolder( path: currentFolder, url: currentUrl )
						'Error'( number: errorNo )
					}
				}
				break
			case 'DeleteFile':
                def fileName = params.FileName
				def fileFinalName = new File( finalDir, fileName )

                errorNo = Fckeditor.ERROR_NOERROR
                if (fileFinalName.exists()) {
                    try {
						if( fileFinalName.delete() ) {
							errorNo = Fckeditor.ERROR_NOERROR
						}
						else {
							errorNo = Fckeditor.ERROR_INVALID_FILE_NAME
						}
					}
					catch(SecurityException se) {
						errorNo = Fckeditor.ERROR_NO_CREATE_PERMISSIONS
					}
                }
                else {
                    errorNo = Fckeditor.ERROR_INVALID_FILE_NAME
                }

                response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector( command: command, resourceType: type) {
						CurrentFolder( path: currentFolder, url: currentUrl )
						'Error'( number: errorNo )
					}
				}
			    break
            case 'DeleteFolder':
                def folderName = params.FolderName
                def folderFinalName = new File( finalDir, folderName )
    
                errorNo = Fckeditor.ERROR_NOERROR
                if (folderFinalName.exists() && folderFinalName.isDirectory()) {
                    try {
                        def deleteClosure
                        deleteClosure = {
                           log.debug "Dir ${it.canonicalPath}"
                           it.eachDir(deleteClosure)
                           it.eachFile {
                               log.debug "Deleting file ${it.canonicalPath}"
                               it.delete()
                           }
                        }
                        deleteClosure folderFinalName
                        folderFinalName.delete()

                        errorNo = Fckeditor.ERROR_NOERROR
                    }
                    catch(SecurityException se) {
                        errorNo = Fckeditor.ERROR_NO_CREATE_PERMISSIONS
                    }
                }
                else {
                    errorNo = Fckeditor.ERROR_INVALID_FOLDER_NAME
                }

                response.setHeader("Cache-Control", "no-cache")
                render(contentType: "text/xml", encoding: "UTF-8") {
                    Connector( command: command, resourceType: type) {
                        CurrentFolder( path: currentFolder, url: currentUrl )
                        'Error'( number: errorNo )
                    }
                }
                break
            case 'RenameFile':
                def oldName = params.FileName
                def newName = params.NewName
				def oldFinalName = new File( finalDir, oldName )
				def newFinalName = new File( finalDir, newName )

                errorNo = Fckeditor.ERROR_NOERROR
                if (!newFinalName.exists() && isFileAllowed(newName, type)) {
                    try {
						if( oldFinalName.renameTo( newFinalName ) ) {
							errorNo = Fckeditor.ERROR_NOERROR
						}
						else {
							errorNo = Fckeditor.ERROR_INVALID_FILE_NAME
						}
					}
					catch(SecurityException se) {
						errorNo = Fckeditor.ERROR_NO_CREATE_PERMISSIONS
					}
                }
                else {
                    errorNo = Fckeditor.ERROR_INVALID_FILE_NAME 
                }

                response.setHeader("Cache-Control", "no-cache")
                render(contentType: "text/xml", encoding: "UTF-8") {
                    Connector( command: command, resourceType: type) {
                        CurrentFolder( path: currentFolder, url: currentUrl )
                        'Error'( number: errorNo )
                    }
                }
                break
            case 'RenameFolder':
                def oldName = params.FolderName
                def newName = params.NewName
                def oldFinalName = new File( finalDir, oldName )
                def newFinalName = new File( finalDir, newName )

                errorNo = Fckeditor.ERROR_NOERROR
                if (!newFinalName.exists()) {
                    try {
                        if( oldFinalName.renameTo( newFinalName ) ) {
                            errorNo = Fckeditor.ERROR_NOERROR
                        }
                        else {
                            errorNo = Fckeditor.ERROR_INVALID_FOLDER_NAME
                        }
                    }
                    catch(SecurityException se) {
                        errorNo = Fckeditor.ERROR_NO_CREATE_PERMISSIONS
                    }
                }
                else {
                    errorNo = Fckeditor.ERROR_INVALID_FOLDER_NAME
                }
            
                response.setHeader("Cache-Control", "no-cache")
                render(contentType: "text/xml", encoding: "UTF-8") {
                    Connector( command: command, resourceType: type) {
                        CurrentFolder( path: currentFolder, url: currentUrl )
                        'Error'( number: errorNo )
                    }
                }
                break
			case 'FileUpload':
				errorNo = Fckeditor.ERROR_NOERROR
				errorMsg = "ERROR"
				def newName = ""
				def overwrite = config.upload.overwrite ?: false

				if (isUploadEnabled(type)) {
					if (request.method != "POST" ) {
						errorNo = Fckeditor.ERROR_CUSTOM
						errorMsg = "INVALID CALL"
					}
					else {
						def file = request.getFile("NewFile")
						if (!file) {
							errorNo = Fckeditor.ERROR_CUSTOM
							errorMsg = "INVALID FILE"
						}
						else {
							errorNo = Fckeditor.ERROR_NOERROR
							newName = file.originalFilename

                            def f = splitFilename( newName )
							if (isAllowed(f.ext, type)) {
                                def fileToSave = new File( finalDir, newName )
								if ( !overwrite ) {
									def idx = 1
									while ( fileToSave.exists() ) {
										errorNo = Fckeditor.ERROR_FILE_RENAMED
										newName = "${f.name}(${idx}).${f.ext}"
										fileToSave = new File( finalDir, newName )
										idx++
									}
								}
								file.transferTo( fileToSave )
							}
							else {
								errorNo = Fckeditor.ERROR_INVALID_FILE_TYPE
								errorMsg = "INVALID FILE TYPE"
							}
						}
					}
    			}
				else {
					errorNo = Fckeditor.ERROR_CUSTOM
					errorMsg = "UPLOADS ARE DISABLED!"
				}

				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/html", encoding: "UTF-8") {
					if( uploadOnly ) {
						script( type: "text/javascript", "window.parent.OnUploadCompleted(${errorNo}, '${currentUrl}${newName}', '${newName}', '${errorMsg}');")
					}
					else {
						script( type: "text/javascript", "window.parent.frames['frmUpload'].OnUploadCompleted(${errorNo}, '${newName}');")
					}
				}
				break
    	}
        log.debug "errorNo = ${errorNo}"
        log.debug "errorMsg = ${errorMsg}"
    }

    // Utilities
    private isUploadEnabled(type) {
    	def config = grailsApplication.config.fckeditor

    	def resType = type?.toLowerCase()
    	if (resType == 'file') {
    		resType = 'link'
    	}
    	
        log.debug "Check upload flag for ${resType} = " + config.upload."${resType}".upload
    	return config.upload."${resType}".upload
    }

    private isFileAllowed( filename, type ) {
        def f = splitFilename(filename)
        return isAllowed(f.ext, type) 
    }
    
    private isAllowed( ext, type ) {
		def config = grailsApplication.config.fckeditor.upload

		def resourceType = type.toLowerCase()
		def fileExt = ext.toLowerCase()

		def allowed = config."${resourceType}".allowed ?: []
		def denied = config."${resourceType}".denied ?: []

		return ( ( fileExt in allowed || allowed.empty ) && !(fileExt in denied))
    }

    private splitFilename(fileName) {
    	def idx = fileName.lastIndexOf(".")
    	return  [name: fileName[0..idx - 1], ext: fileName[idx + 1..-1]]
    }
}
