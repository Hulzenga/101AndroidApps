package com.hulzenga.ioi.android.app_007;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Wiki {

  private static final String TAG = "WIKI";

  private static final String KEY_NAME    = "name";
  private static final String KEY_ADDRESS = "address";
  private static final String KEY_LINKS   = "links";
  private static final String KEY_CORRECT = "correct";

  private String       mName;
  private String       mAddress;
  private List<String> mLinks;
  private boolean      mCorrect;

  public Wiki(String name, String adress, List<String> links) {
    this(name, adress, links, false);
  }

  public Wiki(String name, String address, List<String> links, boolean correct) {
    mName = name;
    mAddress = address;
    mLinks = links;
    mCorrect = correct;
  }

  public String getName() {
    return mName;
  }

  public String getAddress() {
    return mAddress;
  }

  public List<String> getLinks() {
    return mLinks;
  }

  public Wiki stripLinks() {
    mLinks.clear();
    return this;
  }

  public boolean isCorrect() {
    return mCorrect;
  }

  public void setCorrect(boolean correct) {
    mCorrect = correct;
  }

  public static List<Wiki> loadWikis(Context context, String fileName, boolean deleteAfterReading) {
    List<Wiki> wikiList = new ArrayList<Wiki>();

    File saveFile = new File(context.getFilesDir(), fileName + ".json");
    // if there is no save file return empty list
    if (!saveFile.exists()) {
      return wikiList;
    }

    // try and read wikis from the list
    try {
      FileReader fileReader = new FileReader(saveFile);
      JsonReader jsonReader = new JsonReader(fileReader);

      jsonReader.beginArray();
      while (jsonReader.hasNext()) {
        wikiList.add(readWiki(jsonReader));
      }
      jsonReader.endArray();

      jsonReader.close();
    } catch (IOException e) {
      // if write fails, log it. Nothing else necessary
      Log.e(TAG, "failed to write wikis to file: " + e.getMessage());
    } catch (IllegalStateException e) {
      Log.e(TAG, "JSON reader state failure: " + e.getMessage());
    } finally {
      if (deleteAfterReading) {
        saveFile.delete();
      }
    }

    return wikiList;
  }

  private static Wiki readWiki(JsonReader reader) throws IOException {
    String wikiName = null;
    String adress = null;
    List<String> links = new ArrayList<String>();
    boolean correct = false;

    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();

      if (name.equals(KEY_NAME)) {
        wikiName = reader.nextString();
      } else if (name.equals(KEY_ADDRESS)) {
        adress = reader.nextString();
      } else if (name.equals(KEY_LINKS)) {
        reader.beginArray();
        while (reader.hasNext()) {
          links.add(reader.nextString());
        }
        reader.endArray();
      } else if (name.equals(KEY_CORRECT)) {
        correct = reader.nextBoolean();
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();

    return new Wiki(wikiName, adress, links, correct);
  }

  public static void saveWikis(Context context, List<Wiki> wikis, String fileName) {
    File saveFile = new File(context.getFilesDir(), fileName + ".json");
    boolean succes = false;
    try {
      FileWriter fileWriter = new FileWriter(saveFile);
      JsonWriter jsonWriter = new JsonWriter(fileWriter);
      jsonWriter.beginArray();
      for (Wiki wiki : wikis) {
        wiki.writeWiki(jsonWriter);
      }
      jsonWriter.endArray();
      jsonWriter.close();
      succes = true;
    } catch (IOException e) {
      // if write fails, log it. Nothing else necessary
      Log.e(TAG, "failed to write wikis in buffer to file");
    } finally {
      if (!succes) {
        saveFile.delete();
      }
    }
  }

  private void writeWiki(JsonWriter writer) throws IOException {
    writer.beginObject();
    writer.name(KEY_NAME).value(mName);
    writer.name(KEY_ADDRESS).value(mAddress);

    writer.name(KEY_LINKS);
    writer.beginArray();
    for (String link : mLinks) {
      writer.value(link);
    }
    writer.endArray();

    writer.name(KEY_CORRECT).value(mCorrect);
    writer.endObject();
  }
}
