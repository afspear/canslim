import com.spear.canslim.technical.TechnicalAnalyzer;
import org.junit.Test;

import java.io.IOException;

public class TestTechnical {

    @Test
    public void testTechnical() {
        try {
            TechnicalAnalyzer technicalAnalyzer = new TechnicalAnalyzer("AAPL");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
