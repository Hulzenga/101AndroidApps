package com.hulzenga.ioi_apps.app_007;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

public class Wiki {

    private static final String TAG     = "WIKI";

    private String              mName;
    private static final String NAME    = "name";
    private String              mAddress;
    private static final String ADDRESS = "address";
    private List<String>        mLinks;
    private static final String LINKS   = "links";
    private boolean             mCorrect;
    private static final String CORRECT = "correct";

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

            if (name.equals(NAME)) {
                wikiName = reader.nextString();
            } else if (name.equals(ADDRESS)) {
                adress = reader.nextString();
            } else if (name.equals(LINKS)) {
                reader.beginArray();
                while (reader.hasNext()) {
                    links.add(reader.nextString());
                }
                reader.endArray();
            } else if (name.equals(CORRECT)) {
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
        writer.name(NAME).value(mName);
        writer.name(ADDRESS).value(mAddress);

        writer.name(LINKS);
        writer.beginArray();
        for (String link : mLinks) {
            writer.value(link);
        }
        writer.endArray();

        writer.name(CORRECT).value(mCorrect);
        writer.endObject();
    }
}
