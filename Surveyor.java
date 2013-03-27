//package civ_surveyor;

//import Constant.*;
//import Terrain;
import sdljava.*;
import sdljava.video.*;
import sdljava.x.swig.SDLPressedState;
import sdljava.event.*;
import sdljava.image.*;

/*
 * TODO:
 * rivers, special ocean squares?
 * when changing terrain, see if irrigation still holds
 * implement govs
 * implement summary for city
 */

public class Surveyor
{
	private Square[][] map;
	public SDLSurface screen;
	public SDLSurface mapSurface;
	public SDLSurface tiles;
	public SDLSurface roads;
	public SDLSurface smalls;
	public SDLSurface buildings;
	public SDLSurface buildingsA;
	
	int anim;
	boolean inc = true;
	int cursorSize = 1;
	Constants.Governments gov = Constants.Governments.ANARCHY;
	String msg = "";

	public int selectedTerrainX = 0; 
	public int selectedTerrainY = 0; 

	int lastMouseX = -1;
	int lastMouseY = -1;
	
	int mapleft   = 110;
	int maptop    =  10;
	int mapright  = 640;
	int mapbottom = 370;
	
	int ccolour = 0;
	boolean casc = true;
	SDLSurface undercursor;
	SDLRect underrect;

	public Surveyor()
	{
	}

	private void draw()
	{
		int D = Constants.DIM;

		SDLRect sr = new SDLRect( 0, 0, D, D );
		SDLRect dr = new SDLRect( 0, 0, D, D );

		try
		{
			for( int x = 0; x < Constants.mapW; x++ )
			{
				for( int y = 0; y < Constants.mapH; y++ )
				{
					map[x][y].draw( mapSurface, tiles, roads, map );
				}
			}

			// draw map
			dr.x = Constants.mapLocX;
			dr.y = Constants.mapLocY;
			dr.width = mapSurface.getWidth();
			dr.height = mapSurface.getHeight();
			sr.x = 0;
			sr.y = 0;
			sr.width = mapSurface.getWidth();
			sr.height = mapSurface.getHeight();
			mapSurface.blitSurface( sr, screen, dr );

			// draw terrain menu
			dr.x = Constants.menuTerrainLocX;
			dr.y = Constants.menuTerrainLocY;
			dr.width = D;
			dr.height = 12 * D;
			sr.x = 0;
			sr.y = D;
			sr.width = D;
			sr.height = 10 * D;
			tiles.blitSurface( sr, screen, dr );

			dr.y += 10 * D;
			sr.x = 4 * D;
			sr.y = 10 * D;
			dr.height = sr.height = D;
			tiles.blitSurface( sr, screen, dr );
			sr.x += 4 * D;
			dr.y += D;
			tiles.blitSurface( sr, screen, dr );

			// draw terrain improvements
			dr.x = Constants.menuTerrainLocX - D;
			dr.y = Constants.menuTerrainLocY;
			dr.width =	dr.height = sr.width = sr.height = D;
			sr.x = 7*D; sr.y = 12*D;
			for( int loop=0; loop<6; loop++ )
			{
				screen.fillRect( dr, screen.mapRGB(0,0,0));
				tiles.blitSurface( sr, screen, dr );
				sr.x += D; dr.y += D; 
			}
			// roads
			sr.x = 15 * D; sr.y = 0;
			screen.fillRect( dr, screen.mapRGB(0,0,0));
			roads.blitSurface( sr, screen, dr );
			sr.y += 2*D; dr.y += D;
			screen.fillRect( dr, screen.mapRGB(0,0,0));
			roads.blitSurface( sr, screen, dr );
			
			// draw special resources
			sr.x = 16*D; sr.y = 5*D;
			dr.y = Constants.menuTerrainLocY;; dr.x += 2*D;
			for( int spec=0; spec<4; spec++ )
			{
				screen.fillRect( dr, screen.mapRGB(0,0,0));
				tiles.blitSurface( sr, screen, dr );
				sr.x += D; dr.y += D;
			}
			sr.y += D; sr.x = 16*D;
			for( int spec=0; spec<4; spec++ )
			{
				screen.fillRect( dr, screen.mapRGB(0,0,0));
				tiles.blitSurface( sr, screen, dr );
				sr.x += D; dr.y += D;
			}
			sr.y += D; sr.x = 16*D;
			for( int spec=0; spec<4; spec++ )
			{
				screen.fillRect( dr, screen.mapRGB(0,0,0));
				tiles.blitSurface( sr, screen, dr );
				sr.x += D; dr.y += D;
			}
			dr.x += D; dr.y = Constants.menuTerrainLocY;
			sr.y = 11*D; sr.x = 0;
			for( int spec=0; spec<12; spec++ )
			{
				screen.fillRect( dr, screen.mapRGB(0,0,0));
				tiles.blitSurface( sr, screen, dr );
				sr.x += D; dr.y += D;
			}
			
			
			// draw 'cursor'
			dr.x = Constants.menuTerrainLocX - D + selectedTerrainX * D;
			dr.y = Constants.menuTerrainLocY + selectedTerrainY * D;
			dr.width = dr.height = sr.width = sr.height = D;
			sr.x = 17 * D;
			sr.y = 9 * D;
			tiles.blitSurface( sr, screen, dr );

			// get stuff under mouse cursor
			if( (lastMouseX > mapleft) && (lastMouseX < mapright) &&
			    (lastMouseY > maptop) && (lastMouseY < mapbottom ) )
			{

				//screen.fillRect( new SDLRect(100,100,100,100), screen.mapRGB(ccolour,ccolour,ccolour) );
				int sx = ( lastMouseX - Constants.mapLocX ) / Constants.DIM;
				int sy = ( lastMouseY - Constants.mapLocY ) / Constants.DIM;
				underrect.x = Constants.mapLocX + (sx-1)*Constants.DIM;
				underrect.y = Constants.mapLocY + (sy-1)*Constants.DIM;
				screen.blitSurface( underrect, undercursor, new SDLRect(0,0,Constants.DIM*3,Constants.DIM*3) );
			}

			
			// wipe gov and cursor area
			dr.width = 100; dr.height = 20;
			dr.x = dr.y = 0;
			screen.fillRect( dr, screen.mapRGB( 0, 0, 0) );

			dr.width = 800; dr.height = 200;
			dr.x = 0; dr.y = 400;
			screen.fillRect( dr, screen.mapRGB( 0, 0, 0 ) );

			// draw government
			sr.width = dr.width = 15;
			sr.height = dr. height = 20;
			sr.y = 0; sr.x = 0;
			dr.x = dr.y = 0;
			if( gov == Constants.Governments.ANARCHY )
				sr.x = 8 * 15;
			if( gov == Constants.Governments.DESPOTISM )
				sr.x = 9 * 15;
			if( gov == Constants.Governments.MONARCHY )
				sr.x = 10 * 15;
			if( gov == Constants.Governments.COMMUNISM )
				sr.x = 11 * 15;
			if( gov == Constants.Governments.REPUBLIC )
				sr.x = 13 * 15;
			if( gov == Constants.Governments.DEMOCRACY )
				sr.x = 14 * 15;
			smalls.blitSurface( sr, screen, dr );
			
			// draw text parts
			String text = "" + cursorSize + "x" + cursorSize;
			CivFont.drawString( text, screen, 18, 0 );
			CivFont.drawString( ""+(Runtime.getRuntime().freeMemory()/1024)+"/"+(Runtime.getRuntime().totalMemory()/1024)+"K",
													screen, 660, 560 );
			CivFont.drawString( "KEYS:", screen, 550, 420 );
			CivFont.drawString( " f: toggle resources", screen, 550, 440 );
			CivFont.drawString( " g: toggle grid", screen, 550, 460 );
			CivFont.drawString( " p: test patch", screen, 550, 480 );
			CivFont.drawString( msg, screen, 10, 560 );
			msg = "";
			
			// draw buildings
			dr.width = 36; dr.height = 21*4;
			dr.x = 0; dr.y = 30;
			screen.fillRect( dr, screen.mapRGB(0,0,0) );
			
			sr.width = dr.width = 36;
			sr.height = dr.height = 20;
			dr.x = 0; dr.y = 30;
			// harbour
			sr.x = 5*36; sr.y = 3*20;
			if( Square.hasHarbour )
				buildings.blitSurface( sr, screen, dr );
			else
				buildingsA.blitSurface( sr, screen, dr );
			// supermarket
			dr.y += 21;
			sr.x = 7*36; sr.y = 2*20;
			if( Square.hasSupermarket )
				buildings.blitSurface( sr, screen, dr );
			else
				buildingsA.blitSurface( sr, screen, dr );
			// offshoreplatform
			dr.y += 21;
			sr.x = 6*36; sr.y = 3*20;
			if( Square.hasOffshore )
				buildings.blitSurface( sr, screen, dr );
			else
				buildingsA.blitSurface( sr, screen, dr );
			// superhighways
			dr.y += 21;
			sr.x = 0*36; sr.y = 3*20;
			if( Square.hasSuperhighways )
				buildings.blitSurface( sr, screen, dr );
			else
				buildingsA.blitSurface( sr, screen, dr );
		}
		catch( SDLException sdle )
		{
			sdle.printStackTrace();
		}

	}
	
	private void mapClicked( int sx, int sy )
	{
		
		switch( selectedTerrainX )
		{
			case 1:
			{
				map[sx][sy].i = Constants.Improvements.NONE;
				map[sx][sy].r = Constants.Roads.NONE;
				if( Constants.int2Terrain(selectedTerrainY) != Constants.Terrain.RIVER )
				{
					map[sx][sy].t = Constants.int2Terrain(selectedTerrainY);
					map[sx][sy].hasRiver = false;
					map[sx][sy].s = null;
				}
				else
				{
					if( (map[sx][sy].t != Constants.Terrain.UNDISCOVERED) && (map[sx][sy].t != Constants.Terrain.OCEAN) )
						map[sx][sy].hasRiver = true;
				}
				break;
			}
			case 0:
			{
				if( (map[sx][sy].t != Constants.Terrain.UNDISCOVERED) &&
					(map[sx][sy].t != Constants.Terrain.OCEAN) )
				{
					if( selectedTerrainY == 0 )
					{
/*						if( (map[sx][sy].t == Constants.Terrain.DESERT) ||
							(map[sx][sy].t == Constants.Terrain.GRASS) ||
							(map[sx][sy].t == Constants.Terrain.PLAINS) ||
							(map[sx][sy].t == Constants.Terrain.RIVER) ||
							(map[sx][sy].t == Constants.Terrain.TUNDRA) 
						)*/
						if( map[sx][sy].i == Constants.Improvements.IRRIGATION )
							map[sx][sy].i = Constants.Improvements.FARMLAND;
					}
					if( selectedTerrainY == 1 )
					{
/*						if( (map[sx][sy].t == Constants.Terrain.DESERT) ||
							(map[sx][sy].t == Constants.Terrain.GRASS) ||
							(map[sx][sy].t == Constants.Terrain.PLAINS) ||
							(map[sx][sy].t == Constants.Terrain.RIVER) ||
							(map[sx][sy].t == Constants.Terrain.TUNDRA) 
						)*/
						if( (map[sx][sy].getWaterNeighbours(map) > 0) || (map[sx][sy].getIrriNeighbours(map) > 0) )
							map[sx][sy].i = Constants.Improvements.IRRIGATION;
					}
					if( selectedTerrainY == 2 )
					{
						if( (map[sx][sy].t == Constants.Terrain.HILLS) ||
							(map[sx][sy].t == Constants.Terrain.MOUNTAIN) ||
							(map[sx][sy].t == Constants.Terrain.POLAR) 
						)
							map[sx][sy].i = Constants.Improvements.MINE;
					}
					if( selectedTerrainY == 3 )
					{
						if( (map[sx][sy].t == Constants.Terrain.DESERT) )
							map[sx][sy].i = Constants.Improvements.MINE_DESERT;
					}
					if( selectedTerrainY == 4 )
					{
						map[sx][sy].i = Constants.Improvements.POLLUTION;
					}
					if( selectedTerrainY == 6 )
					{
						map[sx][sy].r = Constants.Roads.ROAD;
					}
					if( selectedTerrainY == 7 )
					{
						map[sx][sy].r = Constants.Roads.RAIL;
					}
				}
				break;
			}
			case 2:
			case 3:
			{
				Constants.Specials selectedS = null;
				if( selectedTerrainX == 2 )
					selectedS = Constants.Specials.values()[selectedTerrainY];
				if( selectedTerrainX == 3 )
					selectedS = Constants.Specials.values()[selectedTerrainY+12];
				if( possible( selectedS, map[sx][sy].t ) )
					map[sx][sy].s = selectedS;
				break;
			}
		}
	}

	private boolean possible( Constants.Specials selectedS, Constants.Terrain terrain )
	{
		if( null == selectedS)
			return false;
		else
		{
			Constants.Terrain[] possibles = 
			{
					Constants.Terrain.SWAMP,
					Constants.Terrain.TUNDRA,
					Constants.Terrain.SWAMP,
					Constants.Terrain.POLAR,
					Constants.Terrain.JUNGLE,
					Constants.Terrain.MOUNTAIN,
					Constants.Terrain.OCEAN,
					Constants.Terrain.PLAINS,
					Constants.Terrain.FOREST,
					Constants.Terrain.PLAINS,
					Constants.Terrain.FOREST,
					Constants.Terrain.HILLS,
					null,
					Constants.Terrain.DESERT,
					Constants.Terrain.TUNDRA,
					Constants.Terrain.GRASS,
					Constants.Terrain.HILLS,
					Constants.Terrain.JUNGLE,
					Constants.Terrain.MOUNTAIN,
					Constants.Terrain.OCEAN,
					null,
					null,
					Constants.Terrain.DESERT,
					null
			};
			return ( possibles[selectedS.ordinal()] == terrain );
		}
	}

	private void start()
	{
		try
		{
			// init SDL
			SDLMain.init( SDLMain.SDL_INIT_VIDEO );

			screen = SDLVideo.setVideoMode( 800, 600, 32, SDLVideo.SDL_HWSURFACE | SDLVideo.SDL_DOUBLEBUF );
			SDLSurface temp = SDLVideo.createRGBSurface( SDLVideo.SDL_SRCCOLORKEY, Constants.mapW * Constants.DIM, Constants.mapH * Constants.DIM, 32, 0, 0, 0, 0 );
			mapSurface = temp.displayFormat();
			temp.freeSurface();

			// load some stuff
			tiles = SDLImage.load( "images/tiles.png" );
			roads = SDLImage.load( "images/roads.png" );
			smalls = SDLImage.load( "images/small.png" );
			buildings = SDLImage.load( "images/buildings.png" );  // 36x20
			buildingsA = buildings.displayFormat();
			buildingsA.setAlpha( SDLVideo.SDL_SRCALPHA|SDLVideo.SDL_RLEACCEL, 128 );
			CivFont.initFont();

			underrect = new SDLRect( 0, 0, Constants.DIM*3, Constants.DIM*3 );
			temp = SDLVideo.createRGBSurface( SDLVideo.SDL_SRCCOLORKEY, Constants.DIM*3, Constants.DIM*3, 32, 0, 0, 0, 0 );
			undercursor = temp.displayFormat();
			temp.freeSurface();

			anim = 0;
			
			// init map
			map = new Square[Constants.mapW][Constants.mapH];
			for( int x = 0; x < Constants.mapW; x++ )
			{
				for( int y = 0; y < Constants.mapH; y++ )
				{
					map[x][y] = new Square( x, y, Constants.Terrain.UNDISCOVERED );
				}
			}

			msg = "Civ surveyor started";
			draw();
			screen.flip();

			boolean quit = false;
			while( !quit )
			{
				SDLEvent event = SDLEvent.waitEvent( true );

				if( event instanceof SDLQuitEvent )
				{
					quit = true;
					continue;
				}
				
				if( event instanceof SDLKeyboardEvent )
				{
					SDLKeyboardEvent kevent = (SDLKeyboardEvent) event;
					switch( kevent.getSym() )
					{
						case SDLKey.SDLK_ESCAPE:
							quit = true;
							break;
						case SDLKey.SDLK_g:
							if( kevent.getState() == SDLPressedState.PRESSED )
								Square.border = !Square.border;
							break;
						case SDLKey.SDLK_f:
							if( kevent.getState() == SDLPressedState.PRESSED )
								Square.resources = !Square.resources;
							break;
						case SDLKey.SDLK_1:
							cursorSize = 1;
							msg = "Using 1x1 pointer";
							break;
						case SDLKey.SDLK_2:
							cursorSize = 2;
							msg = "Using 2x2 pointer";
							break;
						case SDLKey.SDLK_3:
							cursorSize = 3;
							msg = "Using 3x3 pointer";
							break;
						case SDLKey.SDLK_q:
							changeGov( Constants.Governments.ANARCHY );
							msg = "Changed government to Anarchy";
							break;
						case SDLKey.SDLK_w:
							changeGov( Constants.Governments.DESPOTISM );
							msg = "Changed government to Despotism";
							break;
						case SDLKey.SDLK_e:
							changeGov( Constants.Governments.MONARCHY );
							msg = "Changed government to Monarchy";
							break;
						case SDLKey.SDLK_r:
							changeGov( Constants.Governments.COMMUNISM );
							msg = "Changed government to Communism";
							break;
						case SDLKey.SDLK_t:
							changeGov( Constants.Governments.REPUBLIC );
							msg = "Changed government to Republic";
							break;
						case SDLKey.SDLK_y:
							changeGov( Constants.Governments.DEMOCRACY );
							msg = "Changed government to Democracy";
							break;
						case SDLKey.SDLK_p:
							map[0][0].t = Constants.Terrain.DESERT;
							map[1][0].t = Constants.Terrain.FOREST;
							map[2][0].t = Constants.Terrain.GRASS;
							map[0][1].t = Constants.Terrain.HILLS;
							map[1][1].t = Constants.Terrain.JUNGLE;
							map[2][1].t = Constants.Terrain.MOUNTAIN;
							map[0][2].t = Constants.Terrain.OCEAN;
							map[1][2].t = Constants.Terrain.PLAINS;
							map[2][2].t = Constants.Terrain.RIVER;
							map[0][3].t = Constants.Terrain.SWAMP;
							map[1][3].t = Constants.Terrain.TUNDRA;
							map[2][3].t = Constants.Terrain.POLAR;

							for( int q=0; q<3; q++ )
								for( int w=0; w<4; w++ )
								{
									map[q][w].i = Constants.Improvements.NONE;
									map[q][w].r = Constants.Roads.NONE;
									map[q][w].s = null;
								}

							break;
						case SDLKey.SDLK_v:
							Runtime.getRuntime().gc();
							break;
					}
					draw();
					screen.flip();
				}

				if( event instanceof SDLMouseMotionEvent )
				{
					SDLMouseMotionEvent mmevent = (SDLMouseMotionEvent) event;
					if( (lastMouseX > mapleft) && (lastMouseX < mapright) &&
					    (lastMouseY > maptop) && (lastMouseY < mapbottom ) )
					{
						//first set back old stuff
						undercursor.blitSurface( new SDLRect(0,0,Constants.DIM*3,Constants.DIM*3), screen, underrect );
						
						lastMouseX = mmevent.getX();
						lastMouseY = mmevent.getY();
						if( casc )
			    		{
			    			ccolour+=20;
			    			if( ccolour>255 )
			    			{
			    				ccolour = 255;
			    				casc = false;
			    			}
						}
			    		else
			    		{
			    			ccolour-=20;
			    			if( ccolour<0 )
			    			{
			    				ccolour = 0;
			    				casc = true;
			    			}
						}
						// get stuff under mouse cursor
						int sx = ( lastMouseX - Constants.mapLocX ) / Constants.DIM;
						int sy = ( lastMouseY - Constants.mapLocY ) / Constants.DIM;
						underrect.x = Constants.mapLocX + (sx-1)*Constants.DIM;
						underrect.y = Constants.mapLocY + (sy-1)*Constants.DIM;
						screen.blitSurface( underrect, undercursor, new SDLRect(0,0,Constants.DIM*3,Constants.DIM*3) );
System.out.println( underrect.x +":" + underrect.y );
						screen.fillRect( underrect, screen.mapRGB(ccolour,ccolour,ccolour) );
						screen.flip();
					}
				}

				if( event instanceof SDLMouseButtonEvent )
				{
					SDLMouseButtonEvent mevent = (SDLMouseButtonEvent) event;
					if( ( mevent.getButton() == SDLMouseButtonEvent.SDL_BUTTON_LEFT ) &&
					    ( mevent.getState() == SDLPressedState.PRESSED ) )
					{
						int mx = mevent.getX();
						int my = mevent.getY();
						// map clicked
						if( mx > Constants.mapLocX && mx < ( Constants.mapLocX + Constants.mapW * Constants.DIM ) &&
							  my > Constants.mapLocY && my < ( Constants.mapLocY + Constants.mapH * Constants.DIM ) )
						{
							int sx = ( mx - Constants.mapLocX ) / Constants.DIM;
							int sy = ( my - Constants.mapLocY ) / Constants.DIM;

							// fall through switch
							switch( cursorSize )
							{
								case 3:
									if( sx != 0 )
									{
										mapClicked( sx-1, sy );
										if( sy != 0 )
											mapClicked( sx-1, sy-1 );
										if( sy != Constants.mapH-1 )
											mapClicked( sx-1, sy+1 );
									}
									if( sy != 0 )
									{
										mapClicked( sx, sy-1 );
										if( sx != Constants.mapW-1 )
											mapClicked( sx+1, sy-1 );
									}
									
								case 2:
									if( sx != Constants.mapW-1 )
									{
										mapClicked( sx+1, sy );
										if( sy != Constants.mapH-1 )
											mapClicked( sx+1, sy+1 );
									}
									if( sy != Constants.mapH-1 )
										mapClicked( sx, sy+1 );
								case 1:
									mapClicked( sx, sy );
									break;
							}
						}

						// terrain menu clicked
						if( mx > Constants.menuTerrainLocX && mx < ( Constants.menuTerrainLocX + Constants.DIM ) &&
							  my > Constants.menuTerrainLocY && my < ( Constants.menuTerrainLocY + 12 * Constants.DIM ) )
						{
							selectedTerrainX = 1;
							selectedTerrainY = ( my - Constants.menuTerrainLocY ) / Constants.DIM;
							msg = Constants.Terrain.values()[selectedTerrainY].toString(); 
						}
						// improve terrain menu clicked
						if( mx > (Constants.menuTerrainLocX-Constants.DIM) && mx < ( Constants.menuTerrainLocX - Constants.DIM + Constants.DIM ) &&
							  my > Constants.menuTerrainLocY && my < ( Constants.menuTerrainLocY + 8 * Constants.DIM ) )
						{
							selectedTerrainX = 0;
							selectedTerrainY = ( my - Constants.menuTerrainLocY ) / Constants.DIM;
						}
						// special resources menu clicked
						if( mx > (Constants.menuTerrainLocX+Constants.DIM) && mx < ( Constants.menuTerrainLocX + 2*Constants.DIM ) &&
							  my > Constants.menuTerrainLocY && my < ( Constants.menuTerrainLocY + 12 * Constants.DIM ) )
						{
							selectedTerrainX = 2;
							selectedTerrainY = ( my - Constants.menuTerrainLocY ) / Constants.DIM;
							msg = Constants.Specials.values()[selectedTerrainY].toString(); 
						}
						if( mx > (Constants.menuTerrainLocX+2*Constants.DIM) && mx < ( Constants.menuTerrainLocX + 3*Constants.DIM ) &&
							  my > Constants.menuTerrainLocY && my < ( Constants.menuTerrainLocY + 12 * Constants.DIM ) )
						{
							selectedTerrainX = 3;
							selectedTerrainY = ( my - Constants.menuTerrainLocY ) / Constants.DIM;
							msg = Constants.Specials.values()[selectedTerrainY+12].toString(); 
						}

						// buildings clicked
						if( mx > 0 && mx < 36 &&
						    my > 30 && my < 30+4*20+3 )
						{
							int buildingClicked = ( my - 30 ) / 21;
							switch( buildingClicked )
							{
								case 0: 
									Square.hasHarbour 	     = !Square.hasHarbour;
									msg = "Harbour";
									break;
								case 1: 
									Square.hasSupermarket   = !Square.hasSupermarket; 
									msg = "Supermarket";
									break;
								case 2: 
									Square.hasOffshore      = !Square.hasOffshore; 
									msg = "Offshore platform";
									break;
								case 3: 
									Square.hasSuperhighways = !Square.hasSuperhighways; 
									msg = "Superhighways";
									break;
							}
						}

						// only draw at mousedown
						draw();
						screen.flip();
					}
				}
				
			}

			SDLMain.quit();
		}
		catch( SDLException sdle )
		{
			sdle.printStackTrace();
		}

	}
	
	private void changeGov( Constants.Governments g )
	{
		gov = g;
		for( int i = 0; i < Constants.mapW; i++ )
			for( int k = 0; k < Constants.mapH; k++ )
				map[i][k].g = g;
	}

	public static void main( String[] args )
	{
		Surveyor main = new Surveyor();
		main.start();
	}

}
