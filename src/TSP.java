/**
 * Created by 1 on 09.12.2017.
 * граф
 * количество вершин
 * веса
 * начальное распределение феромонов
 * класс муравья
 */

import java.util.*;

public class TSP {
    public static void log( String msg ){
        System.out.println(msg);
    }
    public static void main( String[] args ){
        log( "asdf" );
        double[][] data = {
                {0  , 0.1, 6  , 5.2},
                {0.1, 0  , 4.1, 8  },
                {6  , 4.1, 0  , 3.8},
                {5.2, 8  , 3.8, 0  }
        };
        Matrix m = new Matrix( data );
        m.print();
//        m.process( new Matrix.Iterator(){ public double process( int n1, int n2, double value ){
//            return value + n1 + n2;
//        } } );
        m.process( ( int n1, int n2, double value ) -> {
            return value + n1 + n2;
        } );
        m.print();
        log( "" );
        TSP tsp = new TSP( m );
    }

    public static class Matrix{
        private double[][] myValues;
        public Matrix( double[][] values ){
            myValues = values;
        }
        public int size(){
            return myValues.length;
        }
        public void process( Iterator it ){
            int size = size();
            for( int i =0; i< size; i++ ){
                for( int j =0; j< size; j++ ){
                    if( j > i ) {
                        myValues[ i ][ j ] = it.process( i, j, myValues[ i ][ j ] );
                    }
                }
            }
            for( int i =0; i< size; i++ ){
                for( int j =0; j< size; j++ ){
                    if( j < i ){
                        myValues[ i ][ j ] = myValues[ j ][ i ];
                    }
                }
            }
        }
        public void print(){
            for( double[] row : myValues ) {
                log( Arrays.toString( row ) );
            }
        }
        public interface Iterator{
            public double process( int node1, int node2, double value );
        }
        public double get( int node1, int node2 ){
            return myValues[ node1 ][ node2 ];
        }
        public void set( int node1, int node2, double value ){
            myValues[ node1 ][ node2 ] = value;
            myValues[ node2 ][ node1 ] = value;
        }
    }

    private Matrix myWeigths;
    private int myVertices;
    private Matrix myPheromones;
    private static double EVAPORATION = 0.1;

    public TSP( Matrix weights ){
        myWeigths = weights;
        myVertices = weights.size();
        myPheromones = new Matrix( new double[ myVertices ][ myVertices ] );
        initPheromones();
        myPheromones.print();
        for( int i =0; i < 10; i++ ){
            iteration();
            myPheromones.print();
        }
    }

    public Matrix pheromones(){
        return myPheromones;
    }

    public Matrix weights(){
        return myWeigths;
    }

    public int count(){
        return myVertices;
    }

    private void initPheromones(){
        myPheromones.process( ( int n1, int n2, double value ) -> 1.0 / myVertices );
    }

    public void iteration(){
        myPheromones.process( ( int n1, int n2, double value ) -> value * ( 1 - EVAPORATION ) );
    }

    private static class It2 implements Matrix.Iterator{
        public double sum = 0;
        public double process( int n1, int n2, double value ){
            sum += value;
            return value;
        }
    }
    public double optimalWayWeight(){
        It2 mi = new It2();
        myWeigths.process( mi );
        return mi.sum / count();
    }

    public static class Ant{
        private static double ALPHA = 1;
        private static double BETA = 3;
        private List<Integer> myWay = new ArrayList<Integer>();
        private TSP myTSP;
        public Ant( TSP tsp, int start ){
            myWay.add( start );
            myTSP = tsp;
        }
        void add( int node ){
            myWay.add( node );
        }
        public int current(){
            return myWay.get( myWay.size() - 1 );
        }
        public double weight( int node ){
            double pheromone = myTSP.pheromones().get( current(), node );
            double weight = myTSP.weights().get( current(), node );
            return Math.pow( pheromone, ALPHA ) / Math.pow( weight, BETA );
        }
        public double sumWeights(){
            double sum = 0;
            for( int i =0; i < myTSP.count(); i++ ){
                if( myWay.contains( i ) ) continue;
                sum += weight( i );
            }
            return sum;
        }
        public double probability( int node ){
            return weight( node ) / sumWeights();
        }
        public double wayWeight(){
            double weight = 0;
            int last = -1;
            for( int i : myWay ){
                if( last >= 0 ){
                    weight += myTSP.weights().get( last, i );
                }
                last = i;
            }
            return weight;
        }
        private static class Node{
            public int number;
            public double start;
            public double finish;
            public double length;
        }
        public void move(){
            List<Node> possible = new ArrayList<Node>();
            Node last = null;
            for( int i =0; i < myTSP.count(); i++ ){
                if( myWay.contains( i ) ) continue;
                Node n = new Node();
                n.number = i;
                n.length = probability( i );
                n.start = last == null ? 0 : last.finish;
                n.finish = n.start + n.length;
                possible.add( n );
                last = n;
            }
            double random = Math.random();
            Node path = null;
            for( Node n : possible ){
                if( random >= n.start ){
                    path = n;
                    break;
                }
            }
            double pheromone = myTSP.pheromones().get( current(), path.number );
            myTSP.pheromones().set( current(), path.number, pheromone + wayWeight() / myTSP.optimalWayWeight() );
            myWay.add( path.number );
        }
    }
}
