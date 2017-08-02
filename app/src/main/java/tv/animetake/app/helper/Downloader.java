package tv.animetake.app.helper;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by mauricio on 02/08/17.
 */

public class Downloader {
    public Downloader() {
        EventBus.getDefault().register(this);
    }

    public void release() {
        EventBus.getDefault().unregister(this);
    }

    public void download(String url, Callback callback) {
        EventBus.getDefault().post(
            new DownloadEvent(url, callback)
        );
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void downloadAsync(DownloadEvent ev) {
        String html = null;

        try {
            Request request = Bridge.get(ev.url).request();
            html = request.response().asString();
        } catch (BridgeException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(
            new DownloadEventResponse(html, ev.callback)
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloaded(DownloadEventResponse ev) {
        if (ev.callback != null) {
            ev.callback.onDownloaded(ev.html);
        }
    }

    public interface Callback {
        void onDownloaded(String contents);
    }

    public static class DownloadEvent {
        public String url;
        public Callback callback;

        public DownloadEvent(String url, Callback callback) {
            this.url = url;
            this.callback = callback;
        }
    }

    public static class DownloadEventResponse {
        public String html;
        public Callback callback;

        public DownloadEventResponse(String html, Callback callback) {
            this.html = html;
            this.callback = callback;
        }
    }
}
