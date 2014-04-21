package com.hulzenga.ioi_apps.app_009;

import com.hulzenga.ioi_apps.util.networking.nanohttpd.NanoHTTPD;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by jouke on 21-4-14.
 */
public class ThumbsUpServer extends NanoHTTPD {

  private static final int PORT = 8080;

  public ThumbsUpServer() throws IOException {
    super(PORT);
  }

  public int getPort() {
    return PORT;
  }

  @Override
  public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {

    Map<String, List<String>> decodedQueryParameters =
        decodeParameters(session.getQueryParameterString());

    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append("<head><title>Thumbs Up</title></head>");
    sb.append("<body>");
    sb.append("<h1>Thumbs Up</h1>");
    sb.append("</body>");
    sb.append("</html>");
    return new NanoHTTPD.Response(sb.toString());
  }
}
