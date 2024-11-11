package com.example.ragchatbot;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class FileProcessor {
    private Activity activity;
    public static String extractedText = "";

    public interface OnTextExtractedListener {
        void onTextExtracted(String text);
    }

    public FileProcessor(Activity activity) {
        this.activity = activity;
    }

    public void processFile(Uri fileUri) throws FileNotFoundException {
        Log.d("jugaad","inside the process file function");
        String mimeType = this.activity.getContentResolver().getType(fileUri);
        InputStream inputStream = this.activity.getContentResolver().openInputStream(fileUri);
        TextExtractorFromFile textExtractorFromFile = new TextExtractorFromFile(this.activity);

        CompletableFuture<String> resultFuture = textExtractorFromFile.extractTextFromFile(fileUri, inputStream);
        resultFuture.thenAccept(result -> {
//            Log.d("jugaad_text", result);
            this.extractedText = result;
            Log.d("jugaad_extract", extractedText);
        });
    }

    public String getExtractedText() {
        Log.d("jugaad_extracted", "getExtractedText function is called");
        return extractedText;
    }
}
