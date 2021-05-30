package pl.kamilkime.footruler.util;

import androidx.camera.core.ImageProxy;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import pl.kamilkime.footruler.FootRuler;
import pl.kamilkime.footruler.database.entity.FootData;

public final class HttpUtil {

    private static final RequestConfig REQUEST_CONFIG;

    static {
        final Timeout timeout = Timeout.ofSeconds(10L);
        REQUEST_CONFIG = RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setResponseTimeout(timeout).build();
    }

    public static Optional<FootData> sendImage(final ImageProxy image) {
        final InputStream imageStream = ImageUtil.imageToInputStream(image);
        final String address = FootRuler.getFootDatabase().settingsDao().getSetting("server_address").settingValue;
        final boolean keepImage = Boolean.parseBoolean(FootRuler.getFootDatabase().settingsDao().getSetting("save_on_server").settingValue);

        try (final CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(REQUEST_CONFIG).build()) {
            final HttpPost post = new HttpPost(address + "analyze?keepImage=" + keepImage);

            final MultipartEntityBuilder multipart = MultipartEntityBuilder.create();
            multipart.addBinaryBody("image", imageStream, ContentType.APPLICATION_OCTET_STREAM, "foot");
            post.setEntity(multipart.build());

            try (final CloseableHttpResponse response = httpClient.execute(post)) {
                final JSONObject json = new JSONObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
                if (!"OK".equals(json.getString("status"))) {
                    return Optional.empty();
                }

                return Optional.ofNullable(DataUtil.decodeJSON(json));
            } catch (final IOException | JSONException exception) {
                return Optional.empty();
            }
        } catch (final IOException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<ByteArrayOutputStream> downloadImage(final String imageName) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final String address = FootRuler.getFootDatabase().settingsDao().getSetting("server_address").settingValue;

        try (final CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(REQUEST_CONFIG).build()) {
            final HttpGet get = new HttpGet(address + "image?fileName=" + imageName);

            try (final CloseableHttpResponse response = httpClient.execute(get)) {
                final InputStream inputStream = response.getEntity().getContent();
                final byte[] buffer = new byte[8 * 1024];

                int bytesRead;
                while((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                return Optional.of(outputStream);
            } catch (final IOException exception) {
                return Optional.empty();
            }
        } catch (final IOException ignored) {
            return Optional.empty();
        }
    }

    public static boolean removeImage(final String fileName) {
        final String address = FootRuler.getFootDatabase().settingsDao().getSetting("server_address").settingValue;

        try (final CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(REQUEST_CONFIG).build()) {
            final HttpPost post = new HttpPost(address + "remove");
            post.setEntity(new StringEntity(fileName));

            try (final CloseableHttpResponse response = httpClient.execute(post)) {
                final JSONObject json = new JSONObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
                return "OK".equals(json.getString("status"));
            } catch (final IOException | JSONException exception) {
                return false;
            }
        } catch (final IOException ignored) {
            return false;
        }
    }

    private HttpUtil() {}

}
