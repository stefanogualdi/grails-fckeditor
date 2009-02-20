var oEditor			= window.parent.InnerDialogLoaded(); 
var FCK				  = oEditor.FCK; 
var FCKLang			= oEditor.FCKLang ;
var FCKConfig		= oEditor.FCKConfig ;

// <object><param><embed> alternative (not working properly for reasons only 
//                                     microsoft can know)
var EmbedInObject = false; 

// get the selected embedded movie and its container div (if available)
var oMovie = null;
var oContainerDiv = FCK.Selection.GetSelectedElement();
if (oContainerDiv)
{
	if(oContainerDiv.tagName == 'DIV' && 
		 oContainerDiv.childNodes.length > 0 &&
		 oContainerDiv.childNodes[0].tagName == (EmbedInObject ? 'OBJECT' : 'EMBED'))
	 oMovie = oContainerDiv.childNodes[0];
	else if (oContainerDiv.tagName == (EmbedInObject ? 'OBJECT' : 'EMBED') &&
	         oContainerDiv.parentNode.tagName == 'DIV')
	{
		oMovie = oContainerDiv;
		oContainerDiv  = oContainerDiv.parentNode;
	}
	else
		oContainerDiv = null;
}
 
function GetParam(e, pname, defvalue)
{
	if (!e) return defvalue;
	if (EmbedInObject)
	{
		for (var i = 0; i < e.childNodes.length; i++)
		{
			if (e.childNodes[i].tagName == 'PARAM' && GetAttribute(e.childNodes[i], 'name') == pname)
			{
				var retval = GetAttribute(e.childNodes[i], 'value');
				if (retval == "false") return false;
				return retval;
			}
		}
		return defvalue;
	}
	else
	{
		var retval = GetAttribute(e, pname, defvalue);
		if (retval == "false") return false;
		return retval;
	}
}

window.onload = function ()	
{ 
	// First of all, translates the dialog box texts.
	oEditor.FCKLanguageManager.TranslatePage(document);
	
	// read settings from existing embedded movie or set to default		
	GetE('txtUrl').value = GetParam(oMovie, (EmbedInObject ? 'url' : 'src'), '');
	GetE('chkAutosize').checked      = GetParam(oMovie,  'autosize',     true);
	GetE('txtWidth').value           = GetParam(oMovie,  'width',        250  );
	GetE('txtHeight').value          = GetParam(oMovie,  'height',       250  );
	GetE('chkAutostart').checked     = GetParam(oMovie, 'autostart',     false);
	GetE('chkShowgotobar').checked   = GetParam(oMovie, 'showgotobar',   false);
	GetE('chkShowstatusbar').checked = GetParam(oMovie, 'showstatusbar', false);
	GetE('chkShowcontrols').checked  = GetParam(oMovie, 'showcontrols',  true );
	GetE('chkShowtracker').checked   = GetParam(oMovie, 'showtracker',   true );
	GetE('chkShowaudiocontrols').checked    = GetParam(oMovie, 'showaudiocontrols',    true);
	GetE('chkShowpositioncontrols').checked = GetParam(oMovie, 'showpositioncontrols', true);

	// Show/Hide according to settings
	ShowE('divSize',  !GetE('chkAutosize').checked);
	ShowE('tdBrowse', FCKConfig.LinkBrowser);
	ShowE('divControlsettings', GetE('chkShowcontrols').checked)

	// Show Ok button
	window.parent.SetOkButton( true );
} 

function BrowseServer()
{
	var url;
	if (FCKConfig.MediaBrowserURL) 
		url = FCKConfig.MediaBrowserURL;
	else
		url = FCKConfig.ImageBrowserURL.replace(/(&|\?)Type=Image/i, "$1Type=Media");
	OpenFileBrowser(
		url,
		FCKConfig.ScreenWidth * 0.7 ,
		FCKConfig.ScreenHeight * 0.7);
}

function SetUrl( url )
{
	GetE('txtUrl').value = url;
}

function CreateEmbeddedMovie(e, url)
{
	var sType, pluginspace, codebase, classid;
	var sExt   = url.match(/\.(mpg|mpeg|avi|wmv|mov|asf)$/i);
	if (sExt.length && sExt.length > 0)
		sExt = sExt[0];
	else
		sExt = '';
	sType = (sExt=="mpg"||sExt=="mpeg")             ?"video/mpeg":
          (sExt=="avi"||sExt=="wmv"||sExt=="asf") ?"video/x-msvideo":
				  (sExt=="mov") ?"video/quicktime" : "video/x-msvideo";
	
	// window media player?
	var wmp = sExt != "mov";
	if (wmp)
	{
		pluginspace = "http://www.microsoft.com/Windows/MediaPlayer/";
		codebase    = "http://www.microsoft.com/Windows/MediaPlayer/";
		classid     = 'CLASSID="CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95"';
	}
	else
	{
		pluginspace = "http://www.apple.com/quicktime/download/";
		codebase    = "http://www.apple.com/qtactivex/qtplugin.cab";
		classid     = "";
	}


	var html;
	if (EmbedInObject)
	{
		html  = '<OBJECT '+ classid +'>';
		html += '<PARAM name="url" value="'+ url +'" />';
		html += '<PARAM name="filename" value="'+ url +'" />';
		html += '<PARAM name="autostart" value="'+ (GetE('chkAutostart').checked?"true":"false") +'" />';
		html += '<PARAM name="showcontrols" value="'+ (GetE('chkShowcontrols').checked?"true":"false") +'" />';
		html += '<PARAM name="showpositioncontrols" value="'+ (GetE('chkShowpositioncontrols').checked?"true":"false") +'" />';
		html += '<PARAM name="showtracker" value="'+ (GetE('chkShowtracker').checked?"true":"false") +'" />';
		html += '<PARAM name="showaudiocontrols" value="'+ (GetE('chkShowaudiocontrols').checked?"true":"false") +'" />';
		html += '<PARAM name="showgotobar" value="'+ (GetE('chkShowgotobar').checked?"true":"false") +'" />';
		html += '<PARAM name="showstatusbar" value="'+ (GetE('chkShowstatusbar').checked?"true":"false") +'" />';
		html += '<PARAM name="standby" value="Loading Video..." />';
		html += '<PARAM name="pluginspace" value="'+ pluginspace +'" />';
		html += '<PARAM name="codebase" value="'+ codebase +'" />'; 
		html += '<EMBED type="'+ sType +'" src="'+ url +'"></EMBED>';
		html += '<NOEMBED>Download movie: <A HREF="'+ url +'">'+ url +'</A></NOEMBED>';
		html += '</OBJECT>';
	}
	else
	{
		html = '<EMBED type="'+ sType +'" src="'+ url +'" '+
		       'autosize="'+ (GetE('chkAutosize').checked?"true":"false") +'" '+
		       'autostart="'+ (GetE('chkAutostart').checked?"true":"false") +'" '+
		       'showcontrols="'+ (GetE('chkShowcontrols').checked?"true":"false") +'" '+
		       'showpositioncontrols="'+ (GetE('chkShowpositioncontrols').checked?"true":"false") +'" '+
		       'showtracker="'+ (GetE('chkShowtracker').checked?"true":"false") +'" '+
		       'showaudiocontrols="'+ (GetE('chkShowaudiocontrols').checked?"true":"false") +'" '+
		       'showgotobar="'+ (GetE('chkShowgotobar').checked?"true":"false") +'" '+
		       'showstatusbar="'+ (GetE('chkShowstatusbar').checked?"true":"false") +'" '+
		       'pluginspace="'+ pluginspace +'" '+
		       'codebase="'+ codebase +'"';
		if (!GetE('chkAutosize').checked)	
			html += 'width="'+ GetE('txtWidth').value +'" height="'+ GetE('txtHeight').value +'"';
		html += '></EMBED>';
	}
			
	e.innerHTML = html;
}

function Ok() 
{
	if ( GetE('txtUrl').value.length == 0 )
	{
		window.parent.SetSelectedTab( 'Info' ) ;
		GetE('txtUrl').focus() ;

		alert( FCKLang.EmbedMoviesAlertUrl ) ;

		return false ;
	}

	if (!oContainerDiv)
		oContainerDiv = FCK.CreateElement('DIV');
	CreateEmbeddedMovie(oContainerDiv, GetE('txtUrl').value);
	
	oEditor.FCKUndo.SaveUndoStep();

	return true;
}
