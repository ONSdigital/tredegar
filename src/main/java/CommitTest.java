import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

public class CommitTest
{
    public static void main(String[] args) throws IOException {

        String json = "{\"sections\":[{\"name\":\"Economy\",\"link\":\"economy\",\"index\":0,\"items\":[{\"name\":\"CPI: Consumer Prices Index (% change)\",\"uri\":\"/economy/inflationandpriceindices/timeseries/d7g7/\"}]},{\"name\":\"Economy\",\"link\":\"economy\",\"index\":0,\"items\":[{\"name\":\"Gross Domestic Product (GDP)  - CVM\",\"uri\":\"/economy/grossdomesticproductgdp/timeseries/abmi/\"}]},{\"name\":\"Business, industry and trade\",\"link\":\"businessindustryandtrade\",\"index\":0,\"items\":[{\"name\":\"Trade in goods and services deficit (or surplus)\",\"uri\":\"/economy/inflationandpriceindices/timeseries/d7g7/\"}]},{\"name\":\"Employment and Labour market\",\"link\":\"employmentandlabourmarket\",\"index\":0,\"items\":[{\"name\":\"Employment rate (aged 16-64)\",\"uri\":\"/employmentandlabourmarket/peopleinwork/employmentandemployeetypes/timeseries/lf24\"}]},{\"name\":\"People, population and community\",\"link\":\"peoplepopulationandcommunity\",\"index\":0,\"items\":[{\"name\":\"UK population\",\"uri\":\"/peoplepopulationandcommunity/populationandmigration/populationestimates/timeseries/raid121\"}]}],\"level\":\"t1\",\"index\":0,\"type\":\"home\",\"name\":\"Home\",\"fileName\":\"/\",\"breadcrumb\":[]}";

        GitHub github = GitHub.connectUsingPassword("user", "pass");

        GHRepository repo = github.getRepository("ONSDigital/nightingale");

        GHContent content = repo.getFileContent("taxonomy/data.json");
        content.update(json, "totally updated!");

        System.out.println("complete....");

    }
}
