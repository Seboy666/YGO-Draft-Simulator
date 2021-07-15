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
    private double totalWeight;
    private Random rand = new Random();
    
    public void addEntry(T object, double weight) {
        Entry e = new Entry(object, weight);
        entries.add(e);
        double tempWeight = 0;
        for (Entry entry: entries) {
        	tempWeight += entry.weight;
        }
        totalWeight = tempWeight;
    }
    
    /**
     * Modifies the weight of the object at the given index.
     * 
     * @param index The index
     * @param newWeight The new weight we want to give it
     */
    public void modifyWeight(int index, double newWeight) {
    	double oldWeight = entries.get(index).weight;
    	totalWeight = (totalWeight - oldWeight) + newWeight;
    	entries.get(index).weight = newWeight;
    }
    
    /**
     * Finds the entry containing the given object, and modifies its weight to newWeight.
     * 
     * @param object The object whose weight we want to modify
     * @param newWeight The new weight we want to give it
     */
    public void modifyWeight(T object, double newWeight) {
    	for (Entry entry: entries) {
    		if(entry.object.equals(object)) {
    			double oldWeight = entry.weight;
    	    	totalWeight = (totalWeight - oldWeight) + newWeight;
    	    	entry.weight = newWeight;
    	    	return;
    		}
    	}
    	System.out.println("Object " + object + " not found in the list of entries!");
    	return;
    }

    public T getRandom() {
        double r = rand.nextDouble() * totalWeight;
        double accumulatedWeight = 0;
        for (Entry entry: entries) {
        	accumulatedWeight += entry.weight;
            if (accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null; //should only happen when there are no entries
    }
}
