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

    private double[][] myWeigths;
    private int myVertices;
    private double[][] myPheromones;

    public TSP( double[][] weights ){
        myWeigths = weights;
        myVertices = weights.length;

    }

    private void initPheromones(){
        for( int i =0; i< myVertices; i++ ){
            for( int j =0; j< myVertices; j++ ){
                myPheromones[ i ][ j ] = 1.0 / myVertices; //небольшое положительное число
            }
        }
    }

}
