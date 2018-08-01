import com.spear.canslim.AllStockAnalyzer;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TestAllStockFundamentalAnalyzer {

    AllStockAnalyzer allStockAnalyzer = AllStockAnalyzer.getInstance();

    @Test
    public void testGettingAllStocks() {
        List<String> stocks = allStockAnalyzer.getAllStocks();

        allStockAnalyzer.analyzeAllStocks(
                stocks
                .stream()
                .filter(s -> s.toLowerCase().startsWith("aa"))
                .collect(Collectors.toList())
        );
    }

    @Test
    public void rerunErrors() {
        allStockAnalyzer.retryList();
    }
    @Test
    public void fillStockData() {
        allStockAnalyzer.populateStockWithIndsutrySlope();

    }


    @Test
    public void testRank() {
        allStockAnalyzer.rankStocks();
    }

    @Test
    public void testScore() {
        allStockAnalyzer.saveStockScore();
    }

    @Test
    public void testBest() {
        allStockAnalyzer.findBestStocks();
    }

    @Test
    public void testWorst() {
        allStockAnalyzer.findWorstStocks();
    }
}
