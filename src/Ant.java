import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 1 on 11.12.2017.
 */
public class Ant {
    public static int COUNT = 20;
    private static double ALPHA = 1;
    private static double BETA = 3;
    private List<Integer> myWay = new ArrayList<Integer>();
    private TSP myTSP;

    public Ant(TSP tsp) {
        myTSP = tsp;
    }

    void add(int node) {
        myWay.add(node);
    }

    public int current() {
        return myWay.get(myWay.size() - 1);
    }

    public double weight(int node) {
        double pheromone = myTSP.pheromones().get(current(), node);
        double weight = myTSP.weights().get(current(), node);
        return Math.pow(pheromone, ALPHA) / Math.pow(weight, BETA);
    }

    public double sumWeights() {
        double sum = 0;
        for (int i = 0; i < myTSP.count(); i++) {
            if (myWay.contains(i)) continue;
            sum += weight(i);
        }
        return sum;
    }

    public double probability(int node) {
        return weight(node) / sumWeights();
    }

    public double wayWeight() {
        return myTSP.wayWeight(myWay);
    }

    private static class Node {
        public int number;
        public double start;
        public double finish;
        public double length;
    }

    public void move() {
        List<Node> possible = new ArrayList<Node>();
        Node last = null;
        for (int i = 0; i < myTSP.count(); i++) {
            if (myWay.contains(i)) continue;
            Node n = new Node();
            n.number = i;
            n.length = probability(i);
            n.start = last == null ? 0 : last.finish;
            n.finish = n.start + n.length;
            possible.add(n);
            last = n;
        }
        double random = Math.random();
        Node path = null;
        for (Node n : possible) {
            if (random >= n.start) {
                path = n;
                break;
            }
        }
        double pheromone = myTSP.pheromones().get(current(), path.number);
        myTSP.pheromones().set(current(), path.number, pheromone + wayWeight() / myTSP.optimalWayWeight());
        myWay.add(path.number);
    }

    public void passing() {
        myWay.clear();
        int start = (new Random()).nextInt(myTSP.count());
        myWay.add(start);
        for (int i = 0; i < myTSP.count() - 1; i++) {
            move();
        }
        myWay.add( start );
    }

    public List<Integer> way() {
        return myWay;
    }
}
