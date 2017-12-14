import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

/**
 * Created by 1 on 10.12.2017.
 */

public class TSPTests {
    @Test
    public void wayWeight(){
        double[][] data = {
                {0  , 0.1, 6  , 5.2},
                {0.1, 0  , 4.1, 8  },
                {6  , 4.1, 0  , 3.8},
                {5.2, 8  , 3.8, 0  }
        };
        TSP.Matrix m = new TSP.Matrix( data );
        TSP tsp = new TSP( m );
        Ant ant = new Ant( tsp );
        ant.add( 0 );
        ant.add( 1 );
        ant.add( 3 );
        assertEquals( 8.1, ant.wayWeight(), 0.000000001 );
    }

    @Test
    public void wayFind(){
        double[][] data = {
                {0  , 99 , 3  , 7  },
                {99 , 0  , 2  , 1  },
                {3  , 2  , 0  , 88},
                {7  , 1  , 88 , 0  }
        };
        TSP.Matrix m = new TSP.Matrix( data );
        TSP tsp = new TSP( m );
        tsp.iterations();
        assertTrue( tsp.wayWeight( tsp.getWay() ) < 20 );
    }

}
