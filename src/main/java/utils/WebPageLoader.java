package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import testDataObject.LeapFrog.ProductInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static commons.Constants.LEAP_FROG_TEST_URL;

public class WebPageLoader {
    private final ExecutorService executor;

    private final String LF_PageSelectorCss = "div.top ul.inline-links select option";
    private final String LF_ProductCss = "div.results-list-container div.catalog-product";
    private final String LF_ProductNameCss = "p.heading a";
    private final String LF_ProductAgeCss = "p.ageDisplay";
    private final String LF_ProductPriceCss = "p.prices";

    public WebPageLoader(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public int fetchLeapFrogNumberOfPages() throws IOException {
        Document doc = fetchPage(LEAP_FROG_TEST_URL, 1);
        Elements options = doc.select(LF_PageSelectorCss);
        return options.size() - 1;
    }

    private Document fetchPage(String url, int page) throws IOException {
        String full_url = String.format(url, page);
        return Jsoup.connect(full_url).get();
    }

    private List<ProductInfo> fetchLeapFrogPage(int page) throws IOException {
        Document doc = fetchPage(LEAP_FROG_TEST_URL, page);
        List<ProductInfo> list = new ArrayList<>();
        Elements products = doc.select(LF_ProductCss);
        for (Element prod : products) {
            String name = prod.selectFirst(LF_ProductNameCss).text();
            Element ageEl = prod.selectFirst(LF_ProductAgeCss);
            String age = ageEl != null ? ageEl.text() : "";
            Element priceEl = prod.selectFirst(LF_ProductPriceCss);
            String price = priceEl != null ? priceEl.text().trim() : "";
            list.add(new ProductInfo(name, age, price));
        }
        return list;
    }

    public List<ProductInfo> loadLeapFrogPages(int fromPage, int toPage) throws InterruptedException {
        List<Future<List<ProductInfo>>> futures = new ArrayList<>();
        for (int i = fromPage; i <= toPage; i++) {
            final int page = i;
            futures.add(executor.submit(() -> fetchLeapFrogPage(page)));
        }
        List<ProductInfo> all = new ArrayList<>();
        for (Future<List<ProductInfo>> f : futures) {
            try {
                all.addAll(f.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return all;
    }

}