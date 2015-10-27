package template;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import logist.plan.Action;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

// All algorithms should be implemented here. Add your algorithm to the enum as well
enum Algorithm {
	BFS, ASTAR, NAIVE;

	// Given algorithm. Kinda stupid but works.
	static Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}
//
//	// BFS search for best plan
//	static Plan bfs(Vehicle v, TaskSet tasks) {
//		
//	}
	
	
	static Plan bfs(Vehicle v, TaskSet tasks) {
		System.out.println("Beginning of plan computation");
		// List<Action> l = new ArrayList<Action>();
		Map<State, Path> exploredStates = new HashMap<State, Path>();
		// Set<State> cycle = new HashSet<State>();
		TaskSet initialCarriedTasks = (v.getCurrentTasks() == null) ? TaskSet
				.create(new Task[0]) : v.getCurrentTasks();
		State current = new BFSState(v.getCurrentCity(), tasks,
				initialCarriedTasks, v.capacity());

		Queue<State> queue = new LinkedList<State>();
		queue.add(current);
		Path p = new Path();
		exploredStates.put(current, p);

		List<Action> bestPlan = new ArrayList<Action>();
		double bestCost = Integer.MAX_VALUE;

		Path bestPath = new Path();

		while (!queue.isEmpty()) {
			current = queue.poll();

			// Final State
			// Compare current plan with best plan (compare costs)
			// Keep only the best one
			if (current.isFinalState()) {

				// bestPlan = current.getActions();
				// bestCost = current.getCost();
				if (exploredStates.get(current).cost < bestCost) {
					bestPath = new Path(exploredStates.get(current));
					bestCost = exploredStates.get(current).cost;
				}

				// Not in a final state. Try to act on all tasks
			} else {

				for (Task t : current.getAllTasks()) {
					State newState = null;

					Path previous = new Path(exploredStates.get(current));

					if (current.canPickup(t)) {
						newState = current.pickup(t, previous);
					} else if (current.canDeliver(t)) {
						newState = current.deliver(t, previous);
					}

					if (exploredStates.containsKey(newState)) {

						// we have arrived in a previously visited state, but
						// this time with lower cost
						if (exploredStates.get(newState).cost > previous.cost) {
							// if(!queue.contains(newState)){
							// queue.add(newState);
							// }
							exploredStates.put(newState, previous);
						}
					} else if (newState != null) {
						queue.add(newState);
						exploredStates.put(newState, previous);
					}

				}

			}
		}

		System.out.println(bestPath.actions);
		System.out.println(bestPath.cost);
		System.out.println("End of plan computation");
		return new Plan(v.getCurrentCity(), bestPath.actions).seal();
	}

	static Plan astar(Vehicle v, TaskSet tasks) {
		Comparator<AStarState> comparator = new StateComparator();
		PriorityQueue<AStarState> opened = new PriorityQueue<AStarState>(10, comparator);
		Set<State> closed = new HashSet<State>();

		// List<Action> l = new ArrayList<Action>();
		TaskSet initialCarriedTasks = (v.getCurrentTasks() == null) ? TaskSet
				.create(new Task[0]) : v.getCurrentTasks();
				AStarState current = new AStarState(v.getCurrentCity(), tasks,
				initialCarriedTasks, v.capacity());
		current.sethValue();
		opened.add(current);

		while (!opened.peek().isFinalState()) {
			current = opened.poll();
			closed.add(current);
			State neighbor = null;
			for (Task t : current.getAllTasks()) {
				/*
				 * if (current.canPickup(t)) { neighbor = current.pickup(t); }
				 * else if (current.canDeliver(t)) { neighbor =
				 * current.deliver(t); }
				 */
				if (opened.contains(neighbor)) {

				}

			}

		}

		// OPEN = priority queue containing START
		// CLOSED = empty set
		// while lowest rank in OPEN is not the GOAL:
		// current = remove lowest rank item from OPEN
		// add current to CLOSED
		// for neighbors of current:
		// cost = g(current) + movementcost(current, neighbor)
		// if neighbor in OPEN and cost less than g(neighbor):
		// remove neighbor from OPEN, because new path is better
		// if neighbor in CLOSED and cost less than g(neighbor): **
		// remove neighbor from CLOSED
		// if neighbor not in OPEN and neighbor not in CLOSED:
		// set g(neighbor) to cost
		// add neighbor to OPEN
		// set priority queue rank to g(neighbor) + h(neighbor)
		// set neighbor's parent to current
		//
		// reconstruct reverse path from goal to start
		// by following parent pointers
		//
		return null;
	}
}
