package com.boredream.baseapplication.net;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    private RequestBody delegate;
    private UploadListener listener;

    public ProgressRequestBody(byte[] file, UploadListener listener) {
        this.delegate = RequestBody.create(MediaType.parse("image/*"), file);
        this.listener = listener;
    }

    public ProgressRequestBody(File file, UploadListener listener) {
        this.delegate = RequestBody.create(MediaType.parse("image/*"), file);
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;

        CountingSink countingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(countingSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            bytesWritten += byteCount;
            if (listener != null) {
                listener.onProgressUpdate((int) (100f * bytesWritten / contentLength()));
            }

            Log.i("DDD", "progress = " + bytesWritten/1000 + "/" + contentLength()/1000 + " ~ %" + (int) (100f * bytesWritten / contentLength()));
        }
    }

    public interface UploadListener {
        void onProgressUpdate(int percentage);
    }
}