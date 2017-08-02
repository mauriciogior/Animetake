package tv.animetake.app.helper;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tv.animetake.app.model.Anime;
import tv.animetake.app.model.Episode;

/**
 * Created by mauricio on 02/08/17.
 */

public class Updater {
    private static final String BASE_PATH = "https://animetake.tv";

    private Context context;
    private Downloader downloader;

    public Updater(Context context) {
        this.context = context;
        this.downloader = new Downloader();
    }

    public void release() {
        this.context = null;
        this.downloader.release();
    }

    public void updateAnimeList(final OnUpdated onUpdated) {
        downloader.download(BASE_PATH + "/animelist/poster/", new Downloader.Callback() {
            @Override
            public void onDownloaded(String contents) {
                Document doc = Jsoup.parse(contents);
                Elements els = doc.getElementsByClass("animelist_poster");

                for (Element el : els) {
                    Element a = el.getElementsByTag("a").first();
                    Element img = el.getElementsByTag("img").first();
                    Element center = el.getElementsByTag("center").first();

                    String name = center.html();
                    String url = a.attr("href");
                    String thumbnail = img.attr("data-original");

                    Anime anime = new Anime(0, name, "f", url, thumbnail);
                    anime.saveAnime(context);
                }

                onUpdated.updated(null);
            }
        });
    }

    public void updateAnimeEpisodeList(final Anime anime, final OnUpdated onUpdated) {
        downloader.download(BASE_PATH + anime.getUrl(), new Downloader.Callback() {
            @Override
            public void onDownloaded(String contents) {
                Document doc = Jsoup.parse(contents);
                Elements els = doc.getElementsByClass("list-group-item");

                for (Element el : els) {
                    Element title = el.getElementsByTag("b").first();

                    String url = el.attr("href");
                    String name = title.html();

                    Episode episode = new Episode(0, anime.getId(), name, url, "");
                    episode.saveEpisode(context);
                }

                onUpdated.updated(null);
            }
        });
    }

    public void getEpisodeVideoUrl(final Episode episode, final OnUpdated onUpdated) {
        downloader.download(BASE_PATH + episode.getUrl(), new Downloader.Callback() {
            @Override
            public void onDownloaded(String contents) {
                String videoUrls = "";
                String urlRegex = "(/redirect/[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
                Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
                Matcher urlMatcher = pattern.matcher(contents);

                while (urlMatcher.find()) {
                    videoUrls += (contents.substring(urlMatcher.start(0), urlMatcher.end(0))) + "|";
                }

                episode.setVideoUrl(videoUrls);
                episode.saveEpisode(context);

                onUpdated.updated(episode);
            }
        });
    }

    public interface OnUpdated {
        void updated(Object object);
    }
}
