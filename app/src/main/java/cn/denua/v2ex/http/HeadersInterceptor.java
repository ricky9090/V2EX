package cn.denua.v2ex.http;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

public class HeadersInterceptor implements Interceptor{

    private static final HeadersInterceptor interceptor = new HeadersInterceptor();
    private static final String ANDROID_HEADER =
            "Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36";
    private static final String PC_HEADER =
            "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0";

    public static HeadersInterceptor instance(){
        return interceptor;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request originRequest = chain.request();
        Request updateRequest = originRequest.newBuilder()
                .addHeader("host", originRequest.url().host())
                .addHeader("User-Agent",ANDROID_HEADER)
//                .addHeader("Accept", "*/*")
//                .addHeader("Accept-Encoding","gzip, deflate, br")
//                .method(originRequest.method(), gzip(originRequest.body()))
                .build();
        return chain.proceed(updateRequest);
    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() throws IOException {
                return -1;
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
