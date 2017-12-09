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

    private void initPheromones(){
        myPheromones.process( ( int n1, int n2, double value ) -> 1.0 / myVertices );
    }

    public void iteration(){
        myPheromones.process( ( int n1, int n2, double value ) -> value * ( 1 - EVAPORATION ) );
    }

}
