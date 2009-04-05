/* 
 *  FCKPlugin.js
 *  ------------
 *  This is a generic file which is needed for plugins that are developed
 *  for FCKEditor. With the below statements that toolbar is created and
 *  several options are being activated.
 *
 *  See the online documentation for more information:
 *  http://wiki.fckeditor.net/
 */

// Register the related commands.
FCKCommands.RegisterCommand( 'EmbedMovies', new FCKDialogCommand('EmbedMovies',	FCKLang["EmbedMoviesDlgTitle"],
		FCKPlugins.Items['embedmovies'].Path + 'fck_embedmovies.html',
		450,
		370
	)
);
 
// Create the "EmbedMovies" toolbar button.
// FCKToolbarButton( commandName, label, tooltip, style, sourceView, contextSensitive )
var oEmbedMoviesItem = new FCKToolbarButton( 'EmbedMovies', FCKLang["EmbedMoviesBtn"], FCKLang["EmbedMoviesTooltip"], null, false, true ); 
oEmbedMoviesItem.IconPath = FCKConfig.PluginsPath + 'embedmovies/embedmovies.gif'; 

// 'EmbedMovies' is the name that is used in the toolbar config.
FCKToolbarItems.RegisterItem( 'EmbedMovies', oEmbedMoviesItem );
