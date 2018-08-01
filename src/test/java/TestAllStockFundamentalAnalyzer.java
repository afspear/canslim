import com.spear.canslim.AllStockAnalyzer;
import org.junit.Test;

public class TestAllStockFundamentalAnalyzer {

    AllStockAnalyzer allStockAnalyzer = AllStockAnalyzer.getInstance();



    @Test
    public void fillStockData() {
        //allStockAnalyzer.populateStockWithIndsutrySlope();

    }


    @Test
    public void testRank() {
        //allStockAnalyzer.rankStocks();
    }

    @Test
    public void testScore() {
        //allStockAnalyzer.saveStockScore();
    }

    @Test
    public void testBest() {
        //allStockAnalyzer.findBestStocks();
    }

    @Test
    public void testWorst() {
        //allStockAnalyzer.findWorstStocks();
    }
}
