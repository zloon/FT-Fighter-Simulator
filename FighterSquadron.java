package sim;

import java.util.Random;
import java.util.Vector;

public class FighterSquadron {
	private static final int WEIGHT_MISS = 2;
	private static final int WEIGHT_HIT = 3;
	private static final int WEIGHT_REROLL = 1;
	private Random rand;
	boolean targetScreens;
	
	private int fighters;
	private int endurance;
	private int distance;
	private int carrierPos;
	private boolean latched;
	private boolean moraleFine;
	
	/**
	 * Creates a new fighter squadron a specified distance from the attacked ship.
	 * @param distance The distance between the fighters and the ship.
	 * @param s The ship the fighters are fighting. Used to check for shields.
	 */
	public FighterSquadron(int distance, Ship s){
		if (s.hasScreens()){ targetScreens = true; }
		
		fighters = 6;
		endurance = 6;
		this.distance = distance;
		carrierPos = distance;
		latched = checkLatch();
		moraleFine = true;
		
		rand = new Random();
	}
	
	/**
	 * Copies an exisiting fighter squadron.
	 * @param f
	 */
	public FighterSquadron(FighterSquadron f){
		this.targetScreens = f.targetScreens;
		this.fighters = f.fighters;
		this.endurance = f.endurance;
		this.distance = f.distance;
		this.carrierPos = f.carrierPos;
		this.latched = f.latched;
		this.moraleFine = f.moraleFine;
		this.rand = f.rand;
	}
	
	/**
	 * Check if the fighter latches to the ship
	 * @return
	 */
	private boolean checkLatch(){
		return distance <= 6;
	}
	
	/**
	 * Moves the fighters towards the ship
	 */
	public void move(){
		if (latched) { return; }
		else if (endurance > 0){
			distance = Math.min(distance - 24, 0);
			latched = checkLatch();
		}
		else {
			distance = Math.min(distance + 24, carrierPos);
		}
	}
	
	/**
	 * Attacks the ship if the fighter is in range, has endurance left, and passed the morale check.
	 * @return the amount of damage dealt.
	 */
	public int attack(){
		if (!latched || endurance == 0 || !moraleFine ){ return 0; }
		
		int damage = 0;
		for (int i = 0; i < fighters; i++){
			damage += fire(true);
		}
		endurance--;
		
		return damage;
	}
	
	/**
	 * Recursive method for rolling beam dice.
	 * @param firstRoll - Is this the first roll for the shot?
	 * Used to add screen modifier.
	 * @return
	 */
	private int fire(boolean firstRoll){
		
		int beamDice = rand.nextInt(WEIGHT_MISS + WEIGHT_HIT + WEIGHT_REROLL) + 1;
		
		int screenModifier = 0;
		if (firstRoll) { screenModifier = 1; }
		
		if (beamDice <= WEIGHT_MISS + screenModifier){ return 0; }
		else if (beamDice <= (WEIGHT_MISS + WEIGHT_HIT - screenModifier)) {
			return 1;
		} else {
			return 2 + fire(false);
		}
	}
	
	public static void getHit(Vector<FighterSquadron> f, int damage){
		for (FighterSquadron fs : f){
			while(fs.isLatched() && damage > 0 && fs.isAlive()){
				fs.getHit();
				damage--;
			}
		}
	}
	
	public boolean isLatched(){
		return latched;
	}
	
	private void getHit(){
		fighters--;
	}
	
	public boolean isAlive(){
		return fighters > 0;
	}
	
	/**
	 * Checks if there are any fighters left alive in a fight
	 * @param f - A vector of all the fighters involved.
	 * @return true if any fighters are alive.
	 */
	public static boolean fightersAlive(Vector<FighterSquadron> f){
		boolean alive = false;
		
		for (FighterSquadron fs : f){
			if (fs.isAlive()) { alive = true; }
		}
		
		return alive;
	}
	
	public void checkMorale(){
		moraleFine = (rand.nextInt(5) + 1) < fighters;
	}
}
