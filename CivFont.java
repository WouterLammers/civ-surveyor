/*
 * This code is not industry-strength! It is the result of a hack session.
 */
//package civ_surveyor;

import sdljava.SDLException;
import sdljava.image.SDLImage;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

public class CivFont
{
	private static SDLSurface fontString = null;
	
	public static void initFont()
	{
		try
		{
			SDLSurface temp = SDLImage.load( "font/civfont.png" );
			fontString = temp.displayFormatAlpha();
			temp.freeSurface();
		}
		catch( SDLException sdle )
		{
			fontString = null;
			System.err.println( "Couldn't load font.\n" + sdle );
		}
	}
	
	private static SDLSurface createString( String s )
	{
		if( null == fontString )
			return null;
		else
		{
			SDLSurface text;
			try
			{
				text = SDLVideo.createRGBSurface( SDLVideo.SDL_SRCCOLORKEY|SDLVideo.SDL_SRCALPHA, s.length()*12, fontString.getHeight(), 32, 0, 0, 0, 0 );
				int c;
				SDLRect sr = new SDLRect();
				SDLRect dr = new SDLRect();
				sr.width = dr.width = 12;
				sr.height = dr.height = 20;
				sr.y = dr.y = 0;
				dr.x = 0;
				for( int i=0; i<s.length(); i++ )
				{
					c = ((int)s.charAt(i));
					sr.x = 0;
					if( (c>31) && (c<127) )
						sr.x = (c-32)*12 - 1;  // -1 cause image is screwed one pixel
					fontString.blitSurface( sr, text, dr );
					dr.x += 12;
				}
				text.setColorKey( SDLVideo.SDL_SRCCOLORKEY | SDLVideo.SDL_RLEACCEL, 0 );
				SDLSurface result = text.displayFormatAlpha();
				text.freeSurface();
				return result;
			}
			catch( SDLException sdle )
			{
				System.err.println( "Error creating text surface.\n" + sdle );
				return null;
			}
		}
	}
	
	public static void drawString( String s, SDLSurface screen, int x, int y ) throws SDLException
	{
		SDLRect sr = new SDLRect();
		SDLRect dr = new SDLRect();
		SDLSurface textSurface = CivFont.createString( s );
		sr.width = dr.width = textSurface.getWidth();
		sr.height = dr. height = textSurface.getHeight();
		sr.y = 0; sr.x = 0;
		dr.x = x; dr.y = y;
		textSurface.blitSurface( sr, screen, dr );
		textSurface.freeSurface();
		textSurface = null;
		sr = dr = null;
	}
}
