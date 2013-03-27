//package civ_surveyor;

public class Constants
{
	public static final int DIM = 30;
	public static final int T_T  = 1;  // top
	public static final int T_R  = 2;  // right
	public static final int T_B  = 4;  // bottom
	public static final int T_L  = 8;  // left
	public static final int T_TR = 1;  // topright
	public static final int T_BR = 2;  // bottomright
	public static final int T_BL = 4;  // bottomleft
	public static final int T_TL = 8;  // topleft
	
	public static final int mapW    = 18;
	public static final int mapH    = 12;
	public static final int mapLocX = 100;
	public static final int mapLocY =  10;
	public static final int menuTerrainLocX = 700;
	public static final int menuTerrainLocY =  10;
	
	
	public static enum Terrain { 
		GRASS,
		DESERT,
		POLAR,
		JUNGLE,
		PLAINS,
		SWAMP,
		TUNDRA,
		RIVER,
		OCEAN,
		HILLS,
		FOREST,
		MOUNTAIN,
		UNDISCOVERED
	};
	
	public static enum Improvements	{
		FARMLAND,
		IRRIGATION,
		MINE,
		MINE_DESERT,
		POLLUTION,
		NONE
	}

	public static enum Roads	{
		ROAD,
		RAIL,
		NONE
	}

	public static enum Governments	{
		ANARCHY,
		DESPOTISM,
		MONARCHY,
		COMMUNISM,
		REPUBLIC,
		DEMOCRACY
	}

	public static enum Specials 
	{
		SPICE,
		FURS,
		PEAT,
		IVORY,
		FRUIT,
		IRON,
		WHALE,
		WHEAT,
		PHEASANT,
		BUFFALO,
		SILK,
		WINE,
		SEAL, // start second row
		OASIS,
		GAME,
		SHIELD,
		COAL,
		GEMS,
		GOLD,
		FISH,
		HORSE,
		SHIELD_,
		OIL,
		GAME_
	}

	public static Terrain int2Terrain( int tint )
	{
		for( Terrain t : Terrain.values() )
		{
			if( t.ordinal() == tint )
				return t;
		}
		return Terrain.UNDISCOVERED;
	}
		
}
