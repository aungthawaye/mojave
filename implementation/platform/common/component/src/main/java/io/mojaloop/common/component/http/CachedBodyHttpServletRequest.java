package io.mojaloop.common.component.http;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {

        super(request);

        request.getParameterMap();
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = requestInputStream.readAllBytes();
    }

    public String getCachedBodyAsString() {

        return new String(this.cachedBody, StandardCharsets.UTF_8);
    }

    @Override
    public ServletInputStream getInputStream() {

        var byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);

        return new ServletInputStream() {

            public boolean isFinished() {

                return byteArrayInputStream.available() == 0;
            }

            public boolean isReady() {

                return true;
            }

            public int read() {

                return byteArrayInputStream.read();
            }

            public void setReadListener(ReadListener listener) { }
        };
    }

    @Override
    public BufferedReader getReader() {

        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

}
