/*
 * For FCKeditor 2.5
 * 
 * File Name: youtube.js
 * 	Scripts related to the YouTube dialog window (see youtube.html).
 * 
 * File Authors:
 * 		Uprush (uprushworld@yahoo.co.jp) 2007/10/30
 */

var oEditor		= window.parent.InnerDialogLoaded() ;
var FCK			= oEditor.FCK ;
var FCKLang		= oEditor.FCKLang ;
var FCKConfig	= oEditor.FCKConfig ;

//security RegExp
// スクリプトを呼び出せるタグは禁止
var REG_SCRIPT = new RegExp("< *script.*>|< *style.*>|< *link.*>|< *body .*>", "i");
// 疑似プロトコルは禁止
var REG_PROTOCOL = new RegExp("javascript:|vbscript:|about:", "i");
// スクリプトを呼び出せるので禁止
var REG_CALL_SCRIPT = new RegExp("&\{.*\};", "i");
// イベントハンドラは禁止
var REG_EVENT = new RegExp("onError|onUnload|onBlur|onFocus|onClick|onMouseOver|onMouseOut|onSubmit|onReset|onChange|onSelect|onAbort", "i");
// CookieやBasic認証情報にアクセスできるので禁止
var REG_AUTH = new RegExp("document\.cookie|Microsoft\.XMLHTTP", "i");
// 改行コードは禁止（ただし、TEXTAREAが存在する場合はこのままではダメ）
var REG_NEWLINE = new RegExp("\x0d|\x0a", "i");

//#### Dialog Tabs

// Set the dialog tabs.
window.parent.AddTab( 'Info', oEditor.FCKLang.DlgInfoTab ) ;

// Get the selected flash embed (if available).
var oFakeImage = FCK.Selection.GetSelectedElement() ;
var oEmbed ;

window.onload = function()
{
	// Translate the dialog box texts.
	oEditor.FCKLanguageManager.TranslatePage(document) ;

	window.parent.SetAutoSize( true ) ;

	// Activate the "OK" button.
	window.parent.SetOkButton( true ) ;
}

//#### The OK button was hit.
function Ok()
{
	if ( GetE('txtUrl').value.length == 0 )
	{
		window.parent.SetSelectedTab( 'Info' ) ;
		GetE('txtUrl').focus() ;

		alert( oEditor.FCKLang.DlgAlertYouTubeCode ) ;

		return false ;
	}
	
	// check security
	if (checkCode(GetE('txtUrl').value) == false) {
		alert( oEditor.FCKLang.DlgAlertYouTubeSecurity ) ;
		return false;
	}
	
    oEditor.FCKUndo.SaveUndoStep() ;
    if ( !oEmbed )
	{
		oEmbed		= FCK.EditorDocument.createElement( 'EMBED' ) ;
		oFakeImage  = null ;
	}
	UpdateEmbed( oEmbed ) ;

	if ( !oFakeImage )
	{
		oFakeImage	= oEditor.FCKDocumentProcessor_CreateFakeImage( 'FCK__Flash', oEmbed ) ;
		oFakeImage.setAttribute( '_fckflash', 'true', 0 ) ;
		oFakeImage	= FCK.InsertElement( oFakeImage ) ;
	}

	oEditor.FCKFlashProcessor.RefreshView( oFakeImage, oEmbed ) ;

	return true ;
}

function UpdateEmbed( e )
{
	SetAttribute( e, 'type'			, 'application/x-shockwave-flash' ) ;
	SetAttribute( e, 'pluginspage'	, 'http://www.macromedia.com/go/getflashplayer' ) ;

	SetAttribute( e, 'src',  getSourceUrl()) ;

	SetAttribute( e, "width" , 425 ) ;
	SetAttribute( e, "height", 355 ) ;
}

function getSourceUrl()
{
	var yt = GetE('txtUrl').value;
	var begin = yt.indexOf('src=\"') + 5;
    var end = yt.indexOf('\"', begin);
    return yt.substring(begin, end);
}

function checkCode(code)
{
	if (code.search(REG_SCRIPT) != -1) {
		return false;
	}
	
	if (code.search(REG_PROTOCOL) != -1) {
		return false;
	}

	if (code.search(REG_CALL_SCRIPT) != -1) {
		return false;
	}

	if (code.search(REG_EVENT) != -1) {
		return false;
	}

	if (code.search(REG_AUTH) != -1) {
		return false;
	}

	if (code.search(REG_NEWLINE) != -1) {
		return false;
	}
}


