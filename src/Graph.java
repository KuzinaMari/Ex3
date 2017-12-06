/**
 * Created by 1 on 06.12.2017.
 */

import java.util.*;

public class Graph {

    public static class SampleGraph extends Graph{
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();
        Edge e1 = new Edge( 2, n1, n2 );
        Edge e2 = new Edge( 1.3, n1, n3 );
        Edge e3 = new Edge( 2.2, n2, n4 );
        Edge e4 = new Edge( 20, n2, n3 );
        public SampleGraph(){
            Graph g = this;
            g.getNodes().add( n1 ); g.getNodes().add( n2 );
            g.getNodes().add( n3 ); g.getNodes().add( n4 );
            g.getEdges().add( e1 ); g.getEdges().add( e2 );
            g.getEdges().add( e3 ); g.getEdges().add( e4 );
        }
    }

    private ArrayList<Edge> myEdges = new ArrayList<Edge>();
    private ArrayList<Node> myNodes = new ArrayList<Node>();

    public Graph(){
        log("graph created");
    }

    public Collection<Edge> getEdges(){
        return myEdges;
    }

    public Collection<Node> getNodes(){
        return myNodes;
    }
    public void printEdges(){
        for( Edge e : getEdges() ){
            log( e.toString() );
        }
    }

    public static void log( String msg ){
        System.out.println(msg);
    }
    public static void main( String[] args ){
        SampleGraph g = new SampleGraph();
        g.printEdges();
        SalesmanTask task = new SalesmanTask( g, g.n1 );
        task.iterations( 100 );
        g.printEdges();
    }
    public static class Node{
        private static int ourCounter = 1;
        private int myNumber = ourCounter++;
        private ArrayList<Edge> myEdges = new ArrayList<Edge>();
        public Node(){

        }
        public void connect( Edge edge ){
            myEdges.add( edge );
        }
        public Collection<Edge> getEdges(){
            return myEdges;
        }
        public String toString(){
            return "" + myNumber;
        }
    }
    public static class Edge{
        private ArrayList<Node> myNodes = new ArrayList<Node>();
        private double myLength;
        private double myWeight = 0;
        public double myWeightForAnt;
        public double myStartProb;
        public double myEndProb;
        public Edge( double length, Node node1, Node node2 ){
            myLength = length;
            myNodes.add( node1 );
            node1.connect( this );
            node2.connect( this );
            myNodes.add( node2 );
        }
        public void multiplyWeight( double coef ){
            myWeight *= coef;
        }
        public void addWeight( double toAdd ){
            myWeight += toAdd;
        }
        public Node opposite( Node node ){
            for( Node n : myNodes ){
                if( !n.equals( node ) ){
                    return n;
                }
            }
            return null;
        }
        public double getLength() {
            return myLength;
        }
        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append( "< " );
            sb.append( myNodes.get( 0 ) );
            sb.append( " " );
            sb.append( myNodes.get( 1 ) );
            sb.append( " | " );
            sb.append( myLength );
            sb.append( " " );
            sb.append( myWeight );
            sb.append( " >" );
            return sb.toString();
        }
    }
    public static class Ant{
        private ArrayList<Node> myNodes = new ArrayList<Node>();
        private double myValue = 0.2;
        private Graph myGraph;
        private Node myNode;

        public Ant( Graph graph ){
            myGraph = graph;
        }
        public boolean move(){
            if( myNode == null ){
                return false;
            }
            double sum = 0;
            for( Edge edge : myNode.getEdges() ){
                //проверить не ведёт ли ребро в вершину, где уже были
                Node opposite = edge.opposite( myNode );
                if( myNodes.contains( opposite ) ){
                    continue;
                }
                edge.myWeightForAnt = 1 / edge.getLength() + edge.myWeight;
                sum += edge.myWeightForAnt;
            }
            double lastEnd = 0;
            for( Edge edge : myNode.getEdges() ){
                Node opposite = edge.opposite( myNode );
                if( myNodes.contains( opposite ) ){
                    continue;
                }
                edge.myStartProb = lastEnd;
                edge.myEndProb = edge.myStartProb + edge.myWeightForAnt / sum;
                lastEnd = edge.myEndProb;
                //log( edge.toString() +" "+ edge.myStartProb +" "+ edge.myEndProb );
            }
            double random = Math.random();
            Edge path = null;
            for( Edge edge : myNode.getEdges() ){
                Node opposite = edge.opposite( myNode );
                if( myNodes.contains( opposite ) ){
                    continue;
                }
                if( ( random >= edge.myStartProb ) && ( random < edge.myEndProb ) ){
                    path = edge;
                    break;
                }
            }
            if( path == null ){
                return false;
            }
            Node next = path.opposite( myNode );
            path.addWeight( myValue / path.myLength );
            myNodes.add( myNode );
            myNode = next;
            return true;
        }
        public void reset( Node start ){
            myNode = start;
            myNodes.clear();
        }
    }

    public static class SalesmanTask{
        private Graph myGraph;
        private Node myStart;
        private Ant myAnt;
        private double myCoefficient = 0.9;
        public SalesmanTask( Graph graph, Node start ){
            myGraph = graph;
            myStart = start;
            myAnt = new Ant( myGraph );
        }
        public void iteration(){
            if( myAnt.move() ){

            }else{
                myAnt.reset( myStart );
            }
            for( Edge edge : myGraph.getEdges() ) {
                edge.multiplyWeight( myCoefficient );
            }
        }

        public void iterations( int count ){
            for( int i = 0; i < count; i++ ){
                iteration();
            }
        }
    }
}
