/*
 * For FCKeditor 2.3
 * 
 * File Name: fckplugin.js
 * 	Add a toolbar button to insert youtube.
 * 
 * File Authors:
 * 		Uprush (uprushworld@yahoo.co.jp) 2007/10/30
 */


// Register the related commands.
FCKCommands.RegisterCommand( 'YouTube'		, new FCKDialogCommand( FCKLang['DlgYouTubeTitle']	, FCKLang['DlgYouTubeTitle']		, FCKConfig.PluginsPath + 'youtube/youtube.html'	, 450, 350 ) ) ;

// Create the "YouTube" toolbar button.
var oFindItem		= new FCKToolbarButton( 'YouTube', FCKLang['YouTubeTip'] ) ;
oFindItem.IconPath	= FCKConfig.PluginsPath + 'youtube/youtube.gif' ;

FCKToolbarItems.RegisterItem( 'YouTube', oFindItem ) ;			// 'YouTube' is the name used in the Toolbar config.
