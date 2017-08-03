package tv.animetake.app.helper;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
        EventBus.getDefault().register(this);
    }

    public void release() {
        this.context = null;
        this.downloader.release();
        EventBus.getDefault().unregister(this);
    }

    public void updateAnimeList(final OnProgress onProgress) {
        downloader.download(BASE_PATH + "/animelist/poster/", new Downloader.Callback() {
            @Override
            public void onDownloaded(String contents) {
                EventBus.getDefault().post(new SaveAnimeListEvent(contents, onProgress));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSaveAnimeList(SaveAnimeListEvent ev) {
        Document doc = Jsoup.parse(ev.html);
        Elements els = doc.getElementsByClass("animelist_poster");
        int counter = 1;

        for (Element el : els) {
            Element a = el.getElementsByTag("a").first();
            Element img = el.getElementsByTag("img").first();
            Element center = el.getElementsByTag("center").first();

            String name = center.text();
            String url = a.attr("href");
            String thumbnail = img.attr("data-original");

            Anime anime = new Anime(0, name, "", url, thumbnail);
            anime.saveAnime(context);

            ev.onProgress.progress(counter++, els.size());
        }

        ev.onProgress.progress(els.size(), els.size());
    }

    public void updateAnimeEpisodeList(final Anime anime, final OnProgress onProgress) {
        downloader.download(BASE_PATH + anime.getUrl(), new Downloader.Callback() {
            @Override
            public void onDownloaded(String contents) {
                EventBus.getDefault().post(new SaveAnimeEpisodeListEvent(contents, anime, onProgress));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSaveAnimeEpisodeList(SaveAnimeEpisodeListEvent ev) {
        Document doc = Jsoup.parse(ev.html);
        Elements els = doc.getElementsByClass("list-group-item");
        int counter = 1;

        for (Element el : els) {
            Element title = el.getElementsByTag("b").first();

            String url = el.attr("href");
            String name = title.text();

            Episode episode = new Episode(0, ev.anime.getId(), name, url, "");
            episode.saveEpisode(context);

            ev.onProgress.progress(counter++, els.size());
        }

        ev.onProgress.progress(els.size(), els.size());
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

    public static class SaveAnimeListEvent {
        public String html;
        public OnProgress onProgress;

        public SaveAnimeListEvent(String html, OnProgress onProgress) {
            this.html = html;
            this.onProgress = onProgress;
        }
    }

    public static class SaveAnimeEpisodeListEvent {
        public String html;
        public Anime anime;
        public OnProgress onProgress;

        public SaveAnimeEpisodeListEvent(String html, Anime anime, OnProgress onProgress) {
            this.html = html;
            this.anime = anime;
            this.onProgress = onProgress;
        }
    }

    public interface OnUpdated {
        void updated(Object object);
    }

    public interface OnProgress {
        void progress(int amount, int total);
    }
}
