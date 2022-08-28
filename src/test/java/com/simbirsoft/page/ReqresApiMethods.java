package com.simbirsoft.page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReqresApiMethods {
    public static String checkValue(String data) {
        Document html = Jsoup.parse(data);
        return html.body().selectXpath("//input[@name='removefromcart']").val();
    }

    private static String checkQuantity(String data) {
        Document html = Jsoup.parse(data);
        String count = html.body().select(".cart-qty").text();
        return count;
    }

    public static Integer getQuantity(String data) {
        String regexp = "(?<=\\()\\d{1,}";
        Pattern pattern = Pattern.compile(regexp);
        Matcher math = pattern.matcher(checkQuantity(data));
        math.find();
        Integer integg = Integer.valueOf(math.group());
        return integg;
    }
}
