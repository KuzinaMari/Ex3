/**
 * Created by 1 on 09.12.2017.
 * граф
 * количество вершин
 * веса
 * начальное распределение феромонов
 * класс муравья
 */

import java.util.*;
import java.util.stream.Collectors;

public class TSP {
    public static void log( String msg ){
        System.out.println(msg);
    }
    public static void main( String[] args ){
        log( "asdf" );
//        double[][] data = {
//                {0  , 0.1, 6  , 5.2},
//                {0.1, 0  , 4.1, 8  },
//                {6  , 4.1, 0  , 3.8},
//                {5.2, 8  , 3.8, 0  }
//        };
        double[][] data = {
                {0  , 99 , 3  , 7  },
                {99 , 0  , 2  , 1  },
                {3  , 2  , 0  , 88},
                {7  , 1  , 88 , 0  }
        };
        Matrix m = new Matrix( data );
        //m.print();
//        m.process( new Matrix.Iterator(){ public double process( int n1, int n2, double value ){
//            return value + n1 + n2;
//        } } );
//        m.process( ( int n1, int n2, double value ) -> {
//            return value + n1 + n2;
//        } );
//        m.print();
//        log( "" );
        TSP tsp = new TSP( m );
        tsp.iterations();
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
    private static double EVAPORATION = 0.5;
    private Ant myAnt;
    private List<Integer> myWay;

    public TSP( Matrix weights ){
        myWeigths = weights;
        myVertices = weights.size();
        myPheromones = new Matrix( new double[ myVertices ][ myVertices ] );
//        initPheromones();
//        myPheromones.print();
//        for( int i =0; i < 10; i++ ){
//            iteration();
//            myPheromones.print();
//        }
        myAnt = new Ant( this );
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
        for( int i =0; i < Ant.COUNT; i++ ){
            myAnt.passing();
            if( myWay == null || wayWeight( myWay ) > myAnt.wayWeight() ){
                myWay = new ArrayList<Integer>( myAnt.way() );
            }
        }
        myPheromones.process( ( int n1, int n2, double value ) -> value * ( 1 - EVAPORATION ) );
    }

    public void iterations(){
        initPheromones();
        for( int i =0; i < 20; i++ ){
            iteration();
        }
        log( String.join( ", ", myWay.stream().map(Object::toString)
                .collect(Collectors.joining(", ")) ) );
        log( Double.toString( wayWeight( myWay ) ) );
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
    public double wayWeight( List<Integer> way ){
        double weight = 0;
        int last = -1;
        for( int i : way ){
            if( last >= 0 ){
                weight += weights().get( last, i );
            }
            last = i;
        }
        return weight;
    }
    public List<Integer> getWay(){
        return myWay;
    }

}
