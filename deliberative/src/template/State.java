package template;

import static logist.task.TaskSet.copyOf;
import static logist.task.TaskSet.union;

import java.util.ArrayList;
import java.util.List;

import logist.plan.Action;
import logist.plan.Action.Delivery;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

import java.util.Comparator;


// Describes a state and makes the transitions if they are possible
class State {
	private City city;
	private List<Action> actions;
	private double cost;

	private TaskSet remainingTasks;
	private TaskSet carriedTasks;
	private int capacity;
	
	private double hValue;

	public double gethValue() {
		return hValue;
	}

	public void sethValue() {
		//set F to the cost of the currently most expensive task
		double maxCost = 0;
		
		for(Task t : remainingTasks){
			double currCost = city.distanceTo(t.pickupCity);
			currCost += t.pickupCity.distanceTo(t.deliveryCity);
			if(currCost > maxCost) 
				maxCost = currCost;
			
		}
		
		for(Task t : carriedTasks){
			double currCost = city.distanceTo(t.deliveryCity);
			if(currCost > maxCost) 
				maxCost = currCost;
			
		}
		
		this.hValue = maxCost;
		
	}

	public State(City city, List<Action> actions, double cost, TaskSet remainingTasks, TaskSet carriedTasks,
			int capacity) {
		assert (actions != null && remainingTasks != null && carriedTasks != null && capacity >= 0 && cost >= 0);
		this.city = city;
		this.actions = new ArrayList<Action>(actions.size());
		this.actions.addAll(actions);
		this.cost = cost;
		this.remainingTasks = copyOf(remainingTasks);
		this.carriedTasks = copyOf(carriedTasks);
		this.capacity = capacity;
	}

	List<Action> getActions() {
		return actions;
	}

	double getCost() {
		return cost;
	}

	City getCity() {
		return city;
	}

	boolean isBetterFinalState(double bestCost) {
		return remainingTasks.isEmpty() && carriedTasks.isEmpty() && cost <= bestCost;
	}
	
	boolean isFinalState() {
		return remainingTasks.isEmpty() && carriedTasks.isEmpty();
	}


	TaskSet getAllTasks() {
		return union(remainingTasks, carriedTasks);
	}

	State pickup(Task t) {
		State next = clone();
		next.move(t.pickupCity);
		next.actions.add(new Pickup(t));
		next.carriedTasks.add(t);
		next.remainingTasks.remove(t);
		next.capacity -= t.weight;
		return next;
	}

	boolean canPickup(Task t) {
		return remainingTasks.contains(t) && capacity - t.weight >= 0;
	}

	State deliver(Task t) {
		assert (carriedTasks.contains(t) && !remainingTasks.contains(t));
		State next = clone();
		next.move(t.deliveryCity);
		next.actions.add(new Delivery(t));
		next.carriedTasks.remove(t);
		assert (!(carriedTasks.contains(t) || remainingTasks.contains(t)));
		next.capacity += t.weight;
		return next;
	}

	boolean canDeliver(Task t) {
		return carriedTasks.contains(t);
	}

	public State clone() {
		return new State(city, actions, cost, remainingTasks, carriedTasks, capacity);
	}

	private State move(City endCity) {
		if (!endCity.equals(city)) {
			City currentCity = city;
			for (City c : city.pathTo(endCity)) {
				actions.add(new Move(c));
				cost += currentCity.distanceTo(c);
				currentCity = c;
			}
			city = endCity;
		}
		return this;
	}

	
	// TODO Auto-generated methods
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((carriedTasks == null) ? 0 : carriedTasks.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((remainingTasks == null) ? 0 : remainingTasks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (carriedTasks == null) {
			if (other.carriedTasks != null)
				return false;
		} else if (!carriedTasks.equals(other.carriedTasks))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (Double.doubleToLongBits(cost) != Double
				.doubleToLongBits(other.cost))
			return false;
		if (remainingTasks == null) {
			if (other.remainingTasks != null)
				return false;
		} else if (!remainingTasks.equals(other.remainingTasks))
			return false;
		return true;
	}

	
	
}



class StateComparator implements Comparator<State>
{
	@Override
	public int compare(State o1, State o2) {
		// TODO Auto-generated method stub
		
		
		return (int)(o1.gethValue() - o2.gethValue());
	}
}
