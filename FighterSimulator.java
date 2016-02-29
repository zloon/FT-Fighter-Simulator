package sim;

import java.util.Vector;

/**
 Assumptions:
 * All decisions are "dumb", neither movement nor targeting will be finetuned for either side.
 * The fight is done in one dimension, the only movement that can be done is towards or away from the enemy.
 * The distance between the ship and the carrier is static.
 * Trying to dodge the fighters by moving further/shorter will not be done. This is a minor chance that if successful would, in general, cost 1 endurance to get back in position (or 1 turn delay).
 * The ship will not fire on approaching fighters, for simplicity. This works slightly to the fighters' favour.
 * For simplicity, in the initial version the ship will not overkill fighter squadrons (favours the ship), but targeting will be on the most damaged squadron in range first (favours the fighters).
 * In the first version, no secondary fighter movement is done.
 * All fighters will be launched round 1, this is just for simplicity, it could slightly favour to the ship's side.
 * The fighters will not regroup into new squadrons when resupplying. This should not matter for faster fights, but could be a big detriment to the fighters' side if simulating longer fights.
 * The ship only fights against the fighters and the fight ends in the ships favour when all fighters are dead, and in the fighters' favour when the ship is dead or has no offensive systems left.
 * The ship has 4 hull rows for treshold checks. For now treshold checks is only guaranteed to be correct for ships where all hull rows are of equal length (Total hullpoints is a multiplier of 4).
 * No repairs are done (favours the fighters), but no treshold checks are done for systems other than beam 1 and PDS (favours the ship).
 */


public class FighterSimulator {
	
	public static void main(String[] args) {
		Ship s = new Ship(24, 10, 4, true);
		
		Vector<FighterSquadron> f = new Vector<FighterSquadron>(); 
		for (int i = 0; i < 6; i++){
			f.add(new FighterSquadron(100, s));
		}
		
		simulate(s, f, 10000);
	}
	
	public static void simulate(Ship s, Vector<FighterSquadron> f, int iterations){
		int numFighters = f.size();
		
		int shipWins = 0;
		int fighterWins = 0;

		System.out.println("Simulating " + iterations + " fights.");
		System.out.println(s.printStats());
		System.out.println("Versus " + numFighters + " fighter squadrons.\n");
		
		while ((shipWins + fighterWins) < iterations){
			Ship shipcopy = new Ship(s);
			Vector<FighterSquadron> fightercopy = new Vector<FighterSquadron>();
			for (int i = 0; i < f.size(); i++){
				fightercopy.add(new FighterSquadron(f.elementAt(i)));
			}
			
			if (simulate(shipcopy, fightercopy)){
				shipWins++;
			} else {
				fighterWins++;
			}
		}
		
		System.out.println("Ship wins: " + shipWins + " (" + (double)100*shipWins/iterations + "%)");
		System.out.println("Fighter wins: " + fighterWins + " (" + (double)100*fighterWins/iterations + "%)");
	}
	
	/**
	 * 
	 * Simulates a fight between a ship and number of fighter squadrons.
	 * @param s The ship.
	 * @param f A number of fighter squads.
	 * @return true if the ship wins, false if the fighters win.
	 */
	private static boolean simulate(Ship s, Vector<FighterSquadron> f){
		boolean firstRound = true;
		
		// Loop through this for each round of combat. Continue as long both sides are in fighting shape
		while (s.isAlive() && FighterSquadron.fightersAlive(f)){
			boolean doTreshold = false;
//			System.out.println("-----New round-----");
						
			// 4. Move fighters, and latch to the ship if in range. (ignore the round fighters are launched)
			if (!firstRound){
				for (FighterSquadron fs : f){
					fs.move();
				}
			}
			firstRound = false;
			
			// Check fighter morale TODO - check how it works with already latched fighters
			for (FighterSquadron fs : f){
				if (fs.isLatched()){
					fs.checkMorale();
				}
			}
			
			// 9. Point defense fire
			int PDSDamage = s.PDSfire();
//			System.out.println("PDS dmg: " + PDSDamage);
			FighterSquadron.getHit(f, PDSDamage);
			
			// 10. Fighters vs hips (Yup, that's what it says in the reference sheet :D)
			int fighterDamage = 0;
			for (FighterSquadron fs : f){ fighterDamage += fs.attack(); }
//			System.out.println("Fighter dmg: " + fighterDamage);
			doTreshold = s.getHit(fighterDamage);
			if (!s.isAlive()) { return false; }
			
			// 11. Ships fire
			// Not implemented.
			
			// Treshold checks
			if (doTreshold){
				s.treshold();
			}
		}
		
		return (s.isAlive() ? true : false);
	}
}
