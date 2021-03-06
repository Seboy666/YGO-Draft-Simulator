package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedRandomBag<T extends Object> {
	private class Entry {
        double weight;
        T object;
        
        public Entry(T object, double weight) {
        	this.weight = weight;
        	this.object = object;
        }
    }

    private List<Entry> entries = new ArrayList<>();
    private double totalWeight = 0;
    private Random rand = new Random();
    private static final double STANDARD_WEIGHT = 1.0d;
    
    public int size() { return entries.size(); }
    
    public void recalcTotalWeight() {
    	double tempWeight = 0;
    	for(Entry each : entries) {
    		tempWeight += each.weight;
    	}
    	totalWeight = tempWeight;
    }
    
    public void concatenate(WeightedRandomBag<T> bag) {
    	for(Entry each : bag.entries) {
    		this.addEntry(each);
    	}
    }
    
    public void addEntry(T object, double weight) {
    	if(weight <= 0.0d) { // weight should always be positive
    		weight = STANDARD_WEIGHT;
    	}
        addEntry(new Entry(object, weight));
    }
    
    private void addEntry(Entry newEntry) {
        entries.add(newEntry);
        totalWeight += newEntry.weight;
    }
    
    public void removeEntry(T object) {
    	for(Entry entry : entries) {
    		if(entry.object.equals(object)) {
    			removeEntry(entry);
    	    	return;
    		}
    	}
    }
    
    private void removeEntry(Entry entry) {
    	if(totalWeight > 0) { // weight should always be positive
    		totalWeight -= entry.weight;
    	}
    	entries.remove(entry);
    }
    
	/**
	 * Modifies the weight of the object at the given index. Ensures this object's
	 * totalWeight variable to be of the correct value.
	 * 
	 * @param index     The index
	 * @param newWeight The new weight we want to give it
	 */
    public void modifyWeight(int index, double newWeight) {
    	double oldWeight = entries.get(index).weight;
    	totalWeight = (totalWeight - oldWeight) + newWeight;
    	entries.get(index).weight = newWeight;
    }
    
    /**
     * Finds the entry containing the given object, and modifies its weight to newWeight.
     * Ensures this object's totalWeight variable to be of the correct value.
     * 
     * @param object The object whose weight we want to modify
     * @param newWeight The new weight we want to give it
     */
    public void modifyWeight(T object, double newWeight) {
    	for (Entry entry: entries) {
    		if(entry.object.equals(object)) {
    	    	totalWeight = (totalWeight - entry.weight) + newWeight;
    	    	entry.weight = newWeight;
    	    	return;
    		}
    	}
    	System.out.println("Object " + object + " not found in the list of entries!");
    	return;
    }
    
    public void setWeightOfAllEntries(double newWeight) {
    	for(Entry entry : entries) {
    		totalWeight = (totalWeight - entry.weight) + newWeight;
    		entry.weight = newWeight;
    	}
    }

    public T getRandom() {
        return getRandom(false);
    }
    
    public T getRandom(boolean withElim) {
    	double r = rand.nextDouble() * totalWeight;
        double accumulatedWeight = 0;
        for (Entry entry: entries) {
        	accumulatedWeight += entry.weight;
            if (accumulatedWeight >= r) {
                T obj = entry.object;
                if(withElim) {
                	removeEntry(entry);
                }
                return obj;
            }
        }
        System.out.println("Cannot get random entry!");
        return null; //should only happen when there are no entries
    }
}
