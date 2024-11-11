package com.example.ragchatbot;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout roomNameBackground;
    private TextView roomNameTextView;
    private RoomNameHandler roomNameHandler;

    private TextView userMessageTextView, botMessageTextView;
    private TextInputEditText userMessageInput;

    private DrawerLayout drawerLayout;
    private NavigationView leftNavigationView, rightNavigationView;
    private ImageView leftNavigationDrawerIcon, rightNavigationDrawerIcon;

    private HandleNavigationDrawersVisibility handleNavigationDrawers;

    private GestureDetectorCompat gestureDetector;
    private HandleSwipeAndDrawers handleSwipeAndDrawers;

    private ImageButton uploadFilesButton, sendMessageButton;
    private Button processFilesButton;

    private TextView uploadFilesIndicator;

    private FileProcessor fileProcessor;

    public static Map<String, Uri> filesUriStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiateViews();
        instantiateObjects();
        setActionButtonsListener();

        roomNameHandler.setRoomNameListerners();
        handleNavigationDrawers.setNavigationDrawerListeners();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            if (requestCode == 1)
            {
                try{
                    Uri fileUri = data.getData();
                    String fileName = getFileName(fileUri);
                    String mimeType = getContentResolver().getType(fileUri);

                    if(filesUriStore.containsKey(fileName))
                    {
                        Toast.makeText(this, "File already added", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        filesUriStore.put(fileName, fileUri);
                        String fileType = getFileType(mimeType);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileName(Uri fileUri)
    {
        Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                return cursor.getString(nameIndex);
            }
            cursor.close();
        }

        return null;
    }

    public static String getFileType(String mimeType)
    {
        if (mimeType.equals("application/pdf"))
        {
            return "pdf";
        }
        else if (mimeType.equals("text/plain"))
        {
            return "txt";
        }
        else
        {
            return "unknown";
        }
    }


    private void instantiateViews()
    {
        roomNameBackground = findViewById(R.id.roomNameBackground);
        roomNameTextView = findViewById(R.id.roomNameTextView);

        drawerLayout = findViewById(R.id.drawerLayout);
        leftNavigationView = findViewById(R.id.leftNavigationView);
        rightNavigationView = findViewById(R.id.rightNavigationView);
        leftNavigationDrawerIcon = findViewById(R.id.leftNavigationDrawerIcon);
        rightNavigationDrawerIcon = findViewById(R.id.rightNavigationDrawerIcon);

        uploadFilesButton = findViewById(R.id.uploadFilesButton);
        sendMessageButton = findViewById(R.id.sendQueryButton);

        View rightHeaderView = rightNavigationView.getHeaderView(0);
        processFilesButton = rightHeaderView.findViewById(R.id.processFilesButton);

        userMessageTextView = findViewById(R.id.userMessage);
        botMessageTextView = findViewById(R.id.botMessage);
        userMessageInput = findViewById(R.id.userInput);

        uploadFilesIndicator = findViewById(R.id.uploadFilesIndicator);
    }

    private void instantiateObjects()
    {
        PDFBoxResourceLoader.init(getApplicationContext());

        roomNameHandler = new RoomNameHandler(
                roomNameTextView, roomNameBackground,
                this
        );

        handleNavigationDrawers = new HandleNavigationDrawersVisibility(
                leftNavigationDrawerIcon,
                rightNavigationDrawerIcon,
                leftNavigationView,
                rightNavigationView,
                drawerLayout
        );

        handleSwipeAndDrawers = new HandleSwipeAndDrawers(drawerLayout);
        gestureDetector = new GestureDetectorCompat(
                this,
                handleSwipeAndDrawers
        );

        filesUriStore = new HashMap<>();

    }

    private void setActionButtonsListener()
    {
        uploadFilesButton.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"text/plain", "application/pdf"});
            startActivityForResult(intent, 1);
            Toast.makeText(this, "file has been uploaded", Toast.LENGTH_SHORT).show();

        });

        sendMessageButton.setOnClickListener(v -> {
            String userMessage = userMessageInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                userMessageTextView.setText(userMessage);
                userMessageInput.setText("");

                String bot_query = "context: \n";
                FileProcessor fileProcessor = new FileProcessor(this);
                if (!fileProcessor.extractedText.isEmpty()) {
                    bot_query += fileProcessor.extractedText;
                    Log.d("jugaad_extracted", fileProcessor.extractedText);
//                    Log.d("jugaad_extracted", fileProcessor.getExtractedText() + "\nthis is the extracted text");
//                    Log.d("jugaad_context","file processor not null");
                }
                else {
                    Log.d("jugaad_extracted_check","extracted string is empty");
                }
                bot_query += "\n user query: " + userMessage;
                Log.d("jugaad_final_message", bot_query);
                GeminiCode(bot_query);
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });


        processFilesButton.setOnClickListener(v -> {
            if (filesUriStore.isEmpty()) {
                Toast.makeText(this, "No files to process!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "File is processing", Toast.LENGTH_SHORT).show();
            uploadFilesButton.setVisibility(View.GONE);
            sendMessageButton.setVisibility(View.VISIBLE);
            uploadFilesIndicator.setVisibility(View.GONE);
            processFilesButton.setEnabled(false);
            FileProcessor fileProcessor = new FileProcessor(this);
            Log.d("jugaad","called the file processor");
            for (Map.Entry<String, Uri> entry : filesUriStore.entrySet()) {
                try
                {
                    fileProcessor.processFile(entry.getValue());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            processFilesButton.setEnabled(true);
        });
    }

    public void GeminiCode(String userQuery)
    {
        SendMessage model = new SendMessage();
        botMessageTextView.setText("");

        model.getResponse(userQuery, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                botMessageTextView.setText(response);
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}

