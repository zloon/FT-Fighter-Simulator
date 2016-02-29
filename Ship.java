package sim;

import java.util.Random;

public class Ship {
	private static final int WEIGHT_MISS = 2;
	private static final int WEIGHT_HIT = 3;
	private static final int WEIGHT_REROLL = 1;
	private static final int WEIGHT_MISS_BEAMPDS = 4;
	private static final int WEIGHT_HIT_BEAMPDS = 1;
	private static final int WEIGHT_REROLL_BEAMPDS = 1;
	private Random rand;
	
	private int maxHitpoints;
	private int hitpoints;
	private int numPDS;
	private int numBeams;
	private boolean shields;
	private String description;
	
	/**
	 * Copies another ship
	 * @param s
	 */
	public Ship(Ship s){
		this.maxHitpoints = s.maxHitpoints;
		this.hitpoints = s.hitpoints;
		this.numPDS = s.numPDS;
		this.numBeams = s.numBeams;
		this.shields = s.shields;
		this.description = s.description;
		this.rand = s.rand;
	}
	
	public Ship(int hitpoints, int numPDS, int numBeams, boolean shields){
		maxHitpoints = hitpoints;
		this.hitpoints = hitpoints;
		this.numPDS = numPDS;
		this.numBeams = numBeams;
		this.shields = shields;
		
		description = "SHIP: " + maxHitpoints + " hitpoints, " + numPDS + " PDS, " + numBeams + " beam 1." + (shields ? " Shields." : " No shields.");
		
		rand = new Random();
	}
	
	public boolean hasScreens(){
		return shields;
	}
	
	public boolean isAlive(){
//		System.out.println("ALIVE CHECK (Ship): Hitpoints: " + hitpoints + ". PDS: " + numPDS + ". Beam 1s: " + numBeams + ".");
		return (hitpoints > 0) && (numPDS + numBeams > 0);
	}
	
	
	/**
	 * Performs PDS attacks, including using beam 1s as PDS.
	 * @return The amount of damage dealt.
	 */
	public int PDSfire(){
		int damage = 0;
		
		for (int i = 0; i < numPDS; i++){
			damage += firePDS();
		}
		
		for (int i = 0; i < numPDS; i++){
			damage += fireBeams();
		}

		return damage;
	}
	
	private int firePDS(){
		int beamDice = rand.nextInt(WEIGHT_MISS + WEIGHT_HIT + WEIGHT_REROLL) + 1;
		
		if (beamDice <= WEIGHT_MISS){ return 0; }
		else if (beamDice <= (WEIGHT_MISS + WEIGHT_HIT)) {
			return 1;
		} else {
			return 2 + fireBeams();
		}
	}

	private int fireBeams(){
		int beamDice = rand.nextInt(WEIGHT_MISS_BEAMPDS + WEIGHT_HIT_BEAMPDS + WEIGHT_REROLL_BEAMPDS) + 1;
		
		if (beamDice <= WEIGHT_MISS_BEAMPDS){ return 0; }
		else if (beamDice <= (WEIGHT_MISS_BEAMPDS + WEIGHT_HIT_BEAMPDS)) {
			return 1;
		} else {
			return 2 + fireBeams();
		}
	}
	
	/**
	 * Hits the ship for an amount of damage.
	 * @param damage The amount of damage taken.
	 * @return true if treshold checks should be done.
	 */
	public boolean getHit(int damage){
		int initialHitpoints = hitpoints;
		hitpoints = Math.max(hitpoints - damage, 0);
//		System.out.println("Ship damage taken: " + (initialHitpoints - hitpoints));
		// Checks for treshold
		return (initialHitpoints - 1)/(maxHitpoints/4) == (hitpoints - 1)/(maxHitpoints/4);
	}
	
	public void treshold(){
		int lostRows = (maxHitpoints - 1)/(maxHitpoints/4) - (hitpoints - 1)/(maxHitpoints/4);
		
		int lostPDS = 0;
		for (int i = 0; i < numPDS; i++){
			if (rand.nextInt(6) + 1 <= lostRows) { lostPDS++; }
		}
		numPDS -= lostPDS;
		
		int lostBeams = 0;
		for (int i = 0; i < numBeams; i++){
			if (rand.nextInt(6) + 1 <= lostRows) { lostBeams++; }
		}
		numBeams -= lostBeams;
	}
	
	public String printStats(){
		return description;
	}
}
