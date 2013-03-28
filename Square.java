//This code is not industry-strength! It is the result of a hack session.
//package civ_surveyor;

//import Specials;
import sdljava.SDLException;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;

/*
 * This class is really ugly.. (the functions at the bottom)
 */

public class Square
{
	public Constants.Terrain t;
	public Constants.Improvements i;
	public Constants.Roads r;
	public Constants.Governments g;
	public Constants.Specials s;
	public boolean hasRiver = false;
	public int x;
	public int y;
	public int mod;
	public static boolean border = false;
	public static boolean resources = false;
	public static boolean hasHarbour       = false;
	public static boolean hasSupermarket   = false;
	public static boolean hasOffshore 	   = false;
	public static boolean hasSuperhighways = false;
	
	
	public Square( int x, int y, Constants.Terrain t )
	{
		this.x = x;
		this.y = y;
		this.t = t;
		this.i = Constants.Improvements.NONE;
		this.r = Constants.Roads.NONE;
		this.g = Constants.Governments.ANARCHY;
		this.s = null;
		this.mod = 0;
	}
	
	public void draw( SDLSurface mapSurface, SDLSurface tiles, SDLSurface roads, Square[][] map ) throws SDLException
	{
		int D = Constants.DIM;
		
		SDLRect sr = new SDLRect( 0, 0, D, D );
		SDLRect dr = new SDLRect( 0, 0, D, D );

		dr.x = x * D;	dr.y = y * D;
		dr.width = dr.height = D;

		sr.y = (1+t.ordinal()) * D;
		doMod( map );
		sr.x = mod*D;
		switch( t )
		{
			case HILLS:
				sr.x = (0+getSpecialMod())*D;
				sr.y = 10*D;
				break;
			case FOREST :
				sr.x = (4+getSpecialMod())*D;
				sr.y = 10*D;
				break;
			case MOUNTAIN :
				sr.x = (8+getSpecialMod())*D;
				sr.y = 10*D;
				break;
		}
		
		if( t != Constants.Terrain.UNDISCOVERED )
		{
			tiles.blitSurface( sr, mapSurface, dr );
			// draw river
			if( hasRiver)
			{
				sr.x = getWaterNeighbours( map )*D;
				sr.y = 18*D;
				tiles.blitSurface( sr, mapSurface, dr );
			}
			
			if( t == Constants.Terrain.OCEAN )
			{
				// draw sea deltas
				int deltas = getRiverNeighbours( map );
				sr.y = 8*D;
				if( (deltas & Constants.T_T) == Constants.T_T)
				{
					sr.x = 16*D;
					tiles.blitSurface( sr, mapSurface, dr );
				}
				if( (deltas & Constants.T_L) == Constants.T_L)
				{
					sr.x = 17*D;
					tiles.blitSurface( sr, mapSurface, dr );
				}
				if( (deltas & Constants.T_B) == Constants.T_B)
				{
					sr.x = 18*D;
					tiles.blitSurface( sr, mapSurface, dr );
				}
				if( (deltas & Constants.T_R) == Constants.T_R)
				{
					sr.x = 19*D;
					tiles.blitSurface( sr, mapSurface, dr );
				}
			}
		}
		
		// draw improvements farmland/irri
		boolean draw = false;
		sr.y = 12 * D;
		switch( i )
		{
			case FARMLAND:
				sr.x = 7*D;
				draw = true;
				break;
			case IRRIGATION:
				sr.x = 8*D;
				draw = true;
				break;
/*			case MINE:
				sr.x = 9*D;
				break;
			case MINE_DESERT:
				sr.x = 10*D;
				break;
			case POLLUTION:
				sr.x = 11*D;
				break;*/
		}
		if( i != Constants.Improvements.NONE && draw )
			tiles.blitSurface( sr, mapSurface, dr );
		
		// draw roads
		if( r == Constants.Roads.ROAD )
		{
			sr.x = nrOfNeighboursR( map, Constants.Roads.ROAD )*D;
			sr.y = 0*D;
			roads.blitSurface( sr, mapSurface, dr );
			sr.x = nrOfNeighboursDiagR( map, Constants.Roads.ROAD )*D;
			sr.y = 1*D;
			roads.blitSurface( sr, mapSurface, dr );
		}

		if( r == Constants.Roads.RAIL )
		{
			sr.x = nrOfNeighboursR( map, Constants.Roads.RAIL )*D;
			sr.y = 2*D;
			roads.blitSurface( sr, mapSurface, dr );
			sr.x = nrOfNeighboursDiagR( map, Constants.Roads.RAIL )*D;
			sr.y = 3*D;
			roads.blitSurface( sr, mapSurface, dr );
		}

		// draw special resources if any
		if( null != s )
		{
			getSpecialSourceRect( s, sr );
			tiles.blitSurface( sr, mapSurface, dr );
		}
		
		// draw improvements mines/pillu
		draw = false;
		sr.y = 12 * D;
		switch( i )
		{
			case MINE:
				sr.x = 9*D;
				draw = true;
				break;
			case MINE_DESERT:
				sr.x = 10*D;
				draw = true;
				break;
			case POLLUTION:
				sr.x = 11*D;
				draw = true;
				break;
		}
		if( i != Constants.Improvements.NONE && draw )
			tiles.blitSurface( sr, mapSurface, dr );
		
		if( Square.border )
		{
			dr.x = x * D;	dr.y = y * D;
			dr.width = 1; dr.height = D;
			mapSurface.fillRect( dr, mapSurface.mapRGB( 0, 0, 0 ) );
			dr.width = D; dr.height = 1;
			mapSurface.fillRect( dr, mapSurface.mapRGB( 0, 0, 0 ) );
			dr.y = y * D + D-1;
			mapSurface.fillRect( dr, mapSurface.mapRGB( 0, 0, 0 ) );
			dr.x = x * D + D-1;
			dr.width = 1; dr.height = D;
			mapSurface.fillRect( dr, mapSurface.mapRGB( 0, 0, 0 ) );
		}
		
		// draw black stuff.
		dr.x = x * D;	dr.y = y * D;
		dr.width = dr.height = sr.width = sr.height = D;
		sr.x = nrOfNeighbours(map, Constants.Terrain.UNDISCOVERED) * D; sr.y = 13 * D;
		tiles.blitSurface( sr, mapSurface, dr );
		
		// draw resources
		if( Square.resources && (t != Constants.Terrain.UNDISCOVERED) )
		{
			// draw food
			int food = getFood();
			if( s != null ) food += getSpecialFood();
			if( i == Constants.Improvements.IRRIGATION ) food += getIrri();
			if( i == Constants.Improvements.FARMLAND )
			{
				food += getIrri();
				if( hasSupermarket )
					food = (int)(food*1.5);
			}
			if( hasHarbour && t == Constants.Terrain.OCEAN )
				food += 1;
			// compensate for anarchy/despotism
			if( (food > 2) && (g == Constants.Governments.ANARCHY || g == Constants.Governments.DESPOTISM) )
				food -= 1;
			sr.y = 17 * D;
			sr.x = food * D;
			tiles.blitSurface( sr, mapSurface, dr );
			// draw prod
			int prod = getProd();
			if( s != null ) prod += getSpecialProd();
			if( i == Constants.Improvements.MINE ) prod += getMine();
			if( i == Constants.Improvements.MINE_DESERT ) prod += getMine();
			if( r == Constants.Roads.RAIL ) 
			{
				prod = (int)(prod*1.5);
			}
			if( hasOffshore && t == Constants.Terrain.OCEAN )
				prod += 1;
			sr.y = 15 * D;
			sr.x = prod * D;
			tiles.blitSurface( sr, mapSurface, dr );
			// draw trade
			int trade = getTrade();
			if( s != null ) trade += getSpecialTrade();
			if( hasRiver ) trade += 1;
			if( r == Constants.Roads.ROAD || r == Constants.Roads.RAIL )
				trade += getRoad();
			// add republic/democracy
			if( (trade > 0) && (g == Constants.Governments.REPUBLIC || g == Constants.Governments.DEMOCRACY) )
				trade += 1;
			if( hasSuperhighways && (r == Constants.Roads.ROAD || r == Constants.Roads.RAIL) )
				trade = (int)(trade*1.5);
			
			if( trade>9 ) trade = 9;
			sr.y = 15 * D;
			sr.x = (trade+10) * D;
			tiles.blitSurface( sr, mapSurface, dr );
		}
		
	}
	
	private void getSpecialSourceRect( Constants.Specials spec, SDLRect sr )
	{
		if( spec.ordinal() > Constants.Specials.SEAL.ordinal() )
		{
			sr.y = 11*Constants.DIM;
			sr.x = (spec.ordinal()-Constants.Specials.SEAL.ordinal())*Constants.DIM;
		}
		else
		{
			int[] X = { 16, 17, 18, 19, 16, 17, 18, 19, 16, 17, 18, 19 };
			int[] Y = { 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7 };
			sr.x = X[spec.ordinal()]*Constants.DIM;
			sr.y = Y[spec.ordinal()]*Constants.DIM;
		}
	}

	public int getSpecialMod()
	{
		int[] conv = { 0, 0, 1, 0, 0, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 0 };
		int result = ~mod + 16;
		if( (result & Constants.T_T) == Constants.T_T) result -= Constants.T_T;
		if( (result & Constants.T_B) == Constants.T_B) result -= Constants.T_B;
		return conv[result];
	}
	
	public void doMod( Square[][] map )
	{
		mod = 0;
		
		// get top
		if( y != 0 )
		{
			if( map[x][y-1].t != t )
				mod += Constants.T_T;
		}
		
		// get right
		if( x != Constants.mapW-1 )
		{
			if( map[x+1][y].t != t )
				mod += Constants.T_R;
		}
		
		// get bottom
		if( y != Constants.mapH-1 )
		{
			if( map[x][y+1].t != t )
				mod += Constants.T_B;
		}

		// get left
		if( x != 0 )
		{
			if( map[x-1][y].t != t )
				mod += Constants.T_L;
		}
		
		if( t == Constants.Terrain.RIVER )
		{
			mod = ~mod + 16;
		}
	}
	
	public int nrOfNeighbours( Square[][] map, Constants.Terrain nt )
	{
		int neighbours = 0;
		
		// get top
		if( y != 0 )
		{
			if( map[x][y-1].t == nt )
				neighbours += Constants.T_T;
		} else
			neighbours += Constants.T_T;
		
		// get right
		if( x != Constants.mapW-1 )
		{
			if( map[x+1][y].t == nt )
				neighbours += Constants.T_R;
		} else
			neighbours += Constants.T_R;
		
		// get bottom
		if( y != Constants.mapH-1 )
		{
			if( map[x][y+1].t == nt )
				neighbours += Constants.T_B;
		} else
			neighbours += Constants.T_B;

		// get left
		if( x != 0 )
		{
			if( map[x-1][y].t == nt )
				neighbours += Constants.T_L;
		} else
			neighbours += Constants.T_L;
		
		return neighbours;
	}
	
	public int getWaterNeighbours( Square[][] map )
	{
		int neighbours = 0;
		
		// get top
		if( y != 0 )
		{
			if( (map[x][y-1].hasRiver) || (map[x][y-1].t == Constants.Terrain.OCEAN) )
				neighbours += Constants.T_T;
		} 
		
		// get right
		if( x != Constants.mapW-1 )
		{
			if( (map[x+1][y].hasRiver) || (map[x+1][y].t == Constants.Terrain.OCEAN) )
				neighbours += Constants.T_R;
		} 
		
		// get bottom
		if( y != Constants.mapH-1 )
		{
			if( (map[x][y+1].hasRiver) || (map[x][y+1].t == Constants.Terrain.OCEAN) )
				neighbours += Constants.T_B;
		}

		// get left
		if( x != 0 )
		{
			if( (map[x-1][y].hasRiver) || (map[x-1][y].t == Constants.Terrain.OCEAN) )
				neighbours += Constants.T_L;
		}
		
		return neighbours;
	}

	public int getRiverNeighbours( Square[][] map )
	{
		int neighbours = 0;
		
		// get top
		if( y != 0 )
		{
			if( (map[x][y-1].hasRiver) )
				neighbours += Constants.T_T;
		} else
			neighbours += Constants.T_T;
		
		// get right
		if( x != Constants.mapW-1 )
		{
			if( (map[x+1][y].hasRiver) )
				neighbours += Constants.T_R;
		} else
			neighbours += Constants.T_R;
		
		// get bottom
		if( y != Constants.mapH-1 )
		{
			if( (map[x][y+1].hasRiver) )
				neighbours += Constants.T_B;
		} else
			neighbours += Constants.T_B;

		// get left
		if( x != 0 )
		{
			if( (map[x-1][y].hasRiver) )
				neighbours += Constants.T_L;
		} else
			neighbours += Constants.T_L;
		
		return neighbours;
	}

	public int getIrriNeighbours( Square[][] map )
	{
		int neighbours = 0;
		
		// get top
		if( y != 0 )
		{
			if( (map[x][y-1].i == Constants.Improvements.IRRIGATION) || 
					(map[x][y-1].i == Constants.Improvements.FARMLAND) )
				neighbours += Constants.T_T;
		}
		
		// get right
		if( x != Constants.mapW-1 )
		{
			if( (map[x+1][y].i == Constants.Improvements.IRRIGATION) || 
  				(map[x+1][y].i == Constants.Improvements.FARMLAND) )
				neighbours += Constants.T_R;
		}
		
		// get bottom
		if( y != Constants.mapH-1 )
		{
			if( (map[x][y+1].i == Constants.Improvements.IRRIGATION) || 
  				(map[x][y+1].i == Constants.Improvements.FARMLAND) )
				neighbours += Constants.T_B;
		} 

		// get left
		if( x != 0 )
		{
			if( (map[x-1][y].i == Constants.Improvements.IRRIGATION) || 
  				(map[x-1][y].i == Constants.Improvements.FARMLAND) )
				neighbours += Constants.T_L;
		}
		
		return neighbours;
	}

	// lelijk!!
	public int nrOfNeighboursR( Square[][] map, Constants.Roads nt )
	{
		int neighbours = 0;
		
		// get top
		if( y != 0 )
		{
			if( map[x][y-1].r == nt )
				neighbours += Constants.T_T;
		} else
			neighbours += Constants.T_T;
		
		// get right
		if( x != Constants.mapW-1 )
		{
			if( map[x+1][y].r == nt )
				neighbours += Constants.T_R;
		} else
			neighbours += Constants.T_R;
		
		// get bottom
		if( y != Constants.mapH-1 )
		{
			if( map[x][y+1].r == nt )
				neighbours += Constants.T_B;
		} else
			neighbours += Constants.T_B;

		// get left
		if( x != 0 )
		{
			if( map[x-1][y].r == nt )
				neighbours += Constants.T_L;
		} else
			neighbours += Constants.T_L;
		
		return neighbours;
	}

	public int nrOfNeighboursDiagR( Square[][] map, Constants.Roads nt )
	{
		int neighbours = 0;
		
		// get top right
		if( y != 0 && x != Constants.mapW-1)
		{
			if( map[x+1][y-1].r == nt )
				neighbours += Constants.T_TR;
		} else
			neighbours += Constants.T_TR;
		
		// get bottom right
		if( x != Constants.mapW-1 && y != Constants.mapH-1 )
		{
			if( map[x+1][y+1].r == nt )
				neighbours += Constants.T_BR;
		} else
			neighbours += Constants.T_BR;
		
		// get bottom left
		if( x != 0 && y != Constants.mapH-1 )
		{
			if( map[x-1][y+1].r == nt )
				neighbours += Constants.T_BL;
		} else
			neighbours += Constants.T_BL;

		// get top left
		if( x != 0 && y != 0)
		{
			if( map[x-1][y-1].r == nt )
				neighbours += Constants.T_TL;
		} else
			neighbours += Constants.T_TL;
		
		return neighbours;
	}
	
	private int getFood()
	{
		switch( t )
		{
			case DESERT:   return 0;
			case FOREST:   return 1;
			case GRASS:    return 2;
			case HILLS:    return 1;
			case JUNGLE:   return 1;
			case MOUNTAIN: return 0;
			case OCEAN:    return 1;
			case PLAINS:   return 1;
			case POLAR:    return 0;
			case RIVER:    return 2;
			case SWAMP:    return 1;
			case TUNDRA:   return 1;
		}
		return 0;
	}

	private int getProd()
	{
		switch( t )
		{
			case DESERT:   return 1;
			case FOREST:   return 2;
			case GRASS:    return 0;
			case HILLS:    return 0;
			case JUNGLE:   return 0;
			case MOUNTAIN: return 1;
			case OCEAN:    return 0;
			case PLAINS:   return 1;
			case POLAR:    return 0;
			case RIVER:    return 0;
			case SWAMP:    return 0;
			case TUNDRA:   return 0;
		}
		return 0;
	}

	private int getTrade()
	{
		switch( t )
		{
			case DESERT:   return 0;
			case FOREST:   return 0;
			case GRASS:    return 0;
			case HILLS:    return 0;
			case JUNGLE:   return 0;
			case MOUNTAIN: return 0;
			case OCEAN:    return 2;
			case PLAINS:   return 0;
			case POLAR:    return 0;
			case RIVER:    return 1;
			case SWAMP:    return 0;
			case TUNDRA:   return 0;
		}
		return 0;
	}

	private int getRoad()
	{
		switch( t )
		{
			case DESERT:   return 1;
			case FOREST:   return 0;
			case GRASS:    return 1;
			case HILLS:    return 0;
			case JUNGLE:   return 0;
			case MOUNTAIN: return 0;
			case OCEAN:    return 2;
			case PLAINS:   return 1;
			case POLAR:    return 0;
			case RIVER:    return 1;
			case SWAMP:    return 0;
			case TUNDRA:   return 0;
		}
		return 0;
	}

	private int getIrri()
	{
		switch( t )
		{
			case DESERT:   return 1;
			case FOREST:   return 0;
			case GRASS:    return 1;
			case HILLS:    return 1;
			case JUNGLE:   return 0;
			case MOUNTAIN: return 0;
			case OCEAN:    return 0;
			case PLAINS:   return 1;
			case POLAR:    return 0;
			case RIVER:    return 1;
			case SWAMP:    return 0;
			case TUNDRA:   return 1;
		}
		return 0;
	}

	private int getFarm()
	{
		switch( t )
		{
			case DESERT:   return 2;
			case FOREST:   return 0;
			case GRASS:    return 2;
			case HILLS:    return 2;
			case JUNGLE:   return 0;
			case MOUNTAIN: return 0;
			case OCEAN:    return 0;
			case PLAINS:   return 2;
			case POLAR:    return 0;
			case RIVER:    return 2;
			case SWAMP:    return 0;
			case TUNDRA:   return 2;
		}
		return 0;
	}

	private int getMine()
	{
		switch( t )
		{
			case DESERT:   return 1;
			case FOREST:   return 0;
			case GRASS:    return 0;
			case HILLS:    return 3;
			case JUNGLE:   return 0;
			case MOUNTAIN: return 1;
			case OCEAN:    return 0;
			case PLAINS:   return 0;
			case POLAR:    return 1;
			case RIVER:    return 0;
			case SWAMP:    return 0;
			case TUNDRA:   return 0;
		}
		return 0;
	}

	private int getSpecialFood()
	{
		switch( s )
		{
			case SPICE:	return 2;
			case FURS:	return 1;
			case PEAT:	return 0;
			case IVORY:	return 1;
			case FRUIT:	return 3;
			case IRON:	return 0;
			case WHALE:	return 1;
			case WHEAT:	return 2;
			case PHEASANT:	return 2;
			case BUFFALO:	return 0;
			case SILK:	return 0;
			case WINE:	return 0;
			case SEAL: return 0;
			case OASIS:	return 3;
			case GAME:	return 2;
			case SHIELD:	return 0;
			case COAL:	return 0;
			case GEMS:	return 0;
			case GOLD:	return 0;
			case FISH:	return 2;
			case HORSE:	return 0;
			case SHIELD_:	return 0;
			case OIL:	return 0;
			case GAME_:	return 0;
		}
		return 0;
	}

	private int getSpecialProd()
	{
		switch( s )
		{
			case SPICE:	return 0;
			case FURS:	return 0;
			case PEAT:	return 4;
			case IVORY:	return 1;
			case FRUIT:	return 0;
			case IRON:	return 3;
			case WHALE:	return 1;
			case WHEAT:	return 0;
			case PHEASANT:	return 0;
			case BUFFALO:	return 2;
			case SILK:	return 0;
			case WINE:	return 0;
			case SEAL: return 0;
			case OASIS:	return 0;
			case GAME:	return 1;
			case SHIELD:	return 1;
			case COAL:	return 2;
			case GEMS:	return 0;
			case GOLD:	return 0;
			case FISH:	return 0;
			case HORSE:	return 0;
			case SHIELD_:	return 0;
			case OIL:	return 3;
			case GAME_:	return 0;
		}
		return 0;
	}

	private int getSpecialTrade()
	{
		switch( s )
		{
			case SPICE:	return 4;
			case FURS:	return 3;
			case PEAT:	return 0;
			case IVORY:	return 4;
			case FRUIT:	return 1;
			case IRON:	return 0;
			case WHALE:	return 0;
			case WHEAT:	return 0;
			case PHEASANT:	return 0;
			case BUFFALO:	return 0;
			case SILK:	return 3;
			case WINE:	return 4;
			case SEAL: return 0;
			case OASIS:	return 0;
			case GAME:	return 0;
			case SHIELD:	return 0;
			case COAL:	return 0;
			case GEMS:	return 4;
			case GOLD:	return 6;
			case FISH:	return 0;
			case HORSE:	return 0;
			case SHIELD_:	return 0;
			case OIL:	return 0;
			case GAME_:	return 0;
		}
		return 0;
	}

}
