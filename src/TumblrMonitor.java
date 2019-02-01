import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

/**
 *A short webscraping app that captures all of the posts and
 * their respective images from the first page of a given Tumblr profile.
 */
public class TumblrMonitor {
    /**
     * Get's a Tumblr URL from user input
     * @return - a string of a URL
     */
    private static String get_input() {
        System.out.print("Please enter the URL of a Tumblr page you want the posts of: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * Get's the html of each post from a Tumblr url
     * @param url - a string of a URL
     * @return Elements object containing each Tumblr post
     */
    private static Elements get_html(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.select("div#entry");
        } catch (Exception IOException) {
            System.out.println("Invalid URL, please try again (Don't forget to add 'http://' to the beginning");
             return get_html(get_input());
        }
    }

    /**
     * Uses Regex to get the piece of the date we want for each post
     * @param text - a string, the unfiltered text of the date
     * @return a string of the filtered text of the date in the form "11 January 2019"
     */
    private static String filter_date(String text) {
        String match;
        Pattern ptrn = Pattern.compile("([0-9]+) ([A-Z][a-z]+) (20[0-9][0-9])");
        Matcher matcher = ptrn.matcher(text);
        if (matcher.find()) {
            match = matcher.group(0);
        } else {
            match = "No date found";
        }
        return match;
    }

    public static void main(String[] args) throws IOException {
        System.out.println();
        Elements entries = get_html(get_input());
        for (Element entry: entries) {
            String date = entry.select("div.permalink span.permalink1").text();
            System.out.println("date: " + filter_date(date));
            String post_link = entry.select("a[href]").first().attr("href");
            System.out.println("url: " + post_link);
            if (entry.select("img").eachAttr("src").isEmpty()) {
                Document post_doc = Jsoup.connect(post_link).get();
                for (Element image: post_doc.select("meta[content$=.jpg]")) {
                    String img_url = image.attr("content");
                    System.out.println("pics: " + img_url);
                }
            }
            for (Element image: entry.select("img")) {
                String img_url = image.attr("src");
                System.out.println("pics: " + img_url);
                }
            System.out.println();
        }
    }
}
