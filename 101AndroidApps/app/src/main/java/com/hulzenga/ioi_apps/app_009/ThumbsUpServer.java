package com.hulzenga.ioi_apps.app_009;

import android.content.res.AssetManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by jouke on 21-4-14.
 */
public class ThumbsUpServer extends NanoHTTPD {

  public static final  String              MIME_DEFAULT_BINARY = "application/octet-stream";
  private static final int                 PORT                = 8080;
  private static final Map<String, String> MIME_TYPES          = new HashMap<String, String>() {{
    put("css", "text/css");
    put("htm", "text/html");
    put("html", "text/html");
    put("xml", "text/xml");
    put("java", "text/x-java-source, text/java");
    put("md", "text/plain");
    put("txt", "text/plain");
    put("asc", "text/plain");
    put("gif", "image/gif");
    put("jpg", "image/jpeg");
    put("jpeg", "image/jpeg");
    put("png", "image/png");
    put("mp3", "audio/mpeg");
    put("m3u", "audio/mpeg-url");
    put("mp4", "video/mp4");
    put("ogv", "video/ogg");
    put("flv", "video/x-flv");
    put("mov", "video/quicktime");
    put("swf", "application/x-shockwave-flash");
    put("js", "application/javascript");
    put("pdf", "application/pdf");
    put("doc", "application/msword");
    put("ogg", "application/x-ogg");
    put("zip", "application/octet-stream");
    put("exe", "application/octet-stream");
    put("class", "application/octet-stream");
  }};
  private AssetManager mAssetManager;
  private String       mRootDir;

  public ThumbsUpServer(AssetManager assetManager, String rootDir) throws IOException {
    super(PORT);
    mAssetManager = assetManager;
    mRootDir = rootDir;
  }

  public int getPort() {
    return PORT;
  }

  @Override
  public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {

    try {
      if (session.getUri().equals("/")) {
        return new Response(Response.Status.OK, NanoHTTPD.MIME_HTML, mAssetManager.open(mRootDir + "/index.html"));
      } else {
        return new Response(Response.Status.OK, getMimeTypeForFile(session.getUri()), mAssetManager.open(mRootDir + session.getUri()));
      }

    } catch (IOException e) {
      return new Response(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Could not open file");
    }

  }

  private String getMimeTypeForFile(String uri) {
    int dot = uri.lastIndexOf('.');
    String mime = null;
    if (dot >= 0) {
      mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
    }
    return mime == null ? MIME_DEFAULT_BINARY : mime;
  }
}
