package org.gualdi.grails.plugins.fckeditor

import org.gualdi.grails.plugins.fckeditor.exceptions.UnknownOptionException

/**
* @author Stefano Gualdi
*/
public class FckeditorConfig {
    static final REQUEST_CONFIG = "fckeditor.plugin.config"

    // See: http://docs.fckeditor.net/FCKeditor_2.x/Developers_Guide/Configuration/Configuration_Options
    static final ALLOWED_CONFIG_ITEMS = [
            // Editor Behavior
            'AutoDetectPasteFromWord',
            'BaseHref',
            'CleanWordKeepsStructure',
            'ContentLangDirection',
            'DefaultLinkTarget',
            'DocType',
            'ForcePasteAsPlainText',
            'FullPage',
            'LinkDlgHideTarget',
            'MaxUndoLevels',
            'StartupFocus',
            'StartupShowBlocks',
            'TemplateReplaceAll',
            'TemplateReplaceCheckbox',
            'ToolbarComboPreviewCSS',

            // Styles
            'BodyId',
            'BodyClass',
            'CoreStyles',
            'CustomStyles',
            'EditorAreaCSS',
            'EditorAreaStyles',
            'FontColors',
            'FontFormats',
            'FontNames',
            'FontSizes',
            'IndentClasses',
            'IndentLength',
            'IndentUnit',
            'JustifyClasses',
            'RemoveFormatTags',
            'StylesXmlPath',

            // HTML Output
            'AdditionalNumericEntities',
            'EMailProtection',
            'EMailProtectionFunction',
            'EnterMode',
            'FillEmptyBlocks',
            'ForceSimpleAmpersand',
            'FormatIndentator',
            'FormatOutput',
            'FormatSource',
            'HtmlEncodeOutput',
            'IgnoreEmptyParagraphValue',
            'IncludeGreekEntities',
            'IncludeLatinEntities',
            'ProcessHTMLEntities',
            'ProcessNumericEntities',
            'ShiftEnterMode',
            'TabSpaces',

            // User Interface
            'AutoDetectLanguage',
            'ContextMenu',
            'DefaultFontFormatLabel',
            'DefaultFontSizeLabel',
            'DefaultFontLabel',
            'DefaultLanguage',
            'DefaultStyleLabel',
            'DisableFFTableHandles',
            'DisableObjectResizing',
            'EnableMoreFontColors',
            'FlashDlgHideAdvanced',
            'FloatingPanelsZIndex',
            'ImageDlgHideLink',
            'ImageDlgHideAdvanced',
            'Keystrokes',
            'LinkDlgHideAdvanced',
            'ShowBorders',
            'ShowDropDialog',
            'SkinPath',
            'SourcePopup',
            'SmileyColumns',
            'SmileyWindowHeight',
            'SmileyWindowWidth',
            'ToolbarCanCollapse',
            'ToolbarStartExpanded',
            'ToolbarLocation',
            'ToolbarSets',

            // Advanced
            'AllowQueryStringDebug',
            'BrowserContextMenuOnCtrl',
            'CustomConfigurationsPath',
            'Debug',
            'FirefoxSpellChecker',
            'IeSpellDownloadUrl',
            'PluginsPath',
            // 'Plugins.Add',
            'PreloadImages',
            'PreserveSessionOnFileBrowser',
            // 'ProtectedSource.Add',
            'ProtectedTags',
            'SmileyPath',
            'SmileyImages',
            'SpellChecker',
            'SpellerPagesServerScript',
            'TemplatesXmlPath',
            'MsWebBrowserControlCompat',

            // File Browser and Uploader
            'LinkBrowser',
            'LinkBrowserURL',
            'LinkBrowserWindowWidth',
            'LinkBrowserWindowHeight',
            'ImageBrowser',
            'ImageBrowserURL',
            'ImageBrowserWindowWidth',
            'ImageBrowserWindowHeight',
            'FlashBrowser',
            'FlashBrowserURL',
            'FlashBrowserWindowWidth',
            'FlashBrowserWindowHeight',
            'LinkUpload',
            'LinkUploadURL',
            'LinkUploadAllowedExtensions',
            'LinkUploadDeniedExtensions',
            'ImageUpload',
            'ImageUploadURL',
            'ImageUploadAllowedExtensions',
            'ImageUploadDeniedExtensions',
            'FlashUpload',
            'FlashUploadURL',
            'FlashUploadAllowedExtensions',
            'FlashUploadDeniedExtensions'
    ]

    static final RESOURCE_TYPES = ['Image', 'Link', 'Flash', 'Media']

    def config

    FckeditorConfig(request) {
        if (!request[REQUEST_CONFIG]) {
            request[REQUEST_CONFIG] = [:]
        }
        this.config = request[REQUEST_CONFIG]
    }

    def addConfigItem(attrs) {
        attrs?.each { key, value ->
            if (key in ALLOWED_CONFIG_ITEMS) {
                this.config[key] = value
            }
            else {
                throw new UnknownOptionException("Unknown option: ${key}. Option names are case sensitive! Check the spelling.")
            }
        }
    }

    def getConfig() {
        return this.config
    }
    
    static getResourceTypes() {
        return RESOURCE_TYPES
    }
}


