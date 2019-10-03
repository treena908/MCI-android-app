package uic.hcilab.mciapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import com.myscript.iink.Configuration;
import com.myscript.iink.ContentPackage;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.Engine;
import com.myscript.iink.IEditorListener;
import com.myscript.iink.uireferenceimplementation.EditorView;
import com.myscript.iink.uireferenceimplementation.FontUtils;
import com.myscript.iink.uireferenceimplementation.InputController;



public class HandwritingActivity extends AppCompatActivity implements View.OnClickListener {

    List<String> wordList =  new ArrayList<String>();
    int wordIndex = 0;

    private Engine engine;
    private ContentPackage contentPackage;
    private ContentPart contentPart;
    private EditorView editorView;
    private TextView text;
    private EditText inputText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //iink
        ErrorActivity.installHandler(this);
        engine = IInkApplication.getEngine();

        // configure recognition
        Configuration conf = engine.getConfiguration();
        String confDir = "zip://" + getPackageCodePath() + "!/assets/conf";
        conf.setStringArray("configuration-manager.search-path", new String[]{confDir});
        String tempDir = getFilesDir().getPath() + File.separator + "tmp";
        conf.setString("content-package.temp-folder", tempDir);
        ////

        setContentView(R.layout.activity_handwriting);

        wordList.add("animal");
        wordList.add("coffee");
        wordList.add("computer");
        wordList.add("little");
        wordList.add("restaurant");
        wordList.add("rabbit");

        final Button button = (Button) findViewById(R.id.handwriting_enter_button);
        inputText = (EditText) findViewById(R.id.handwriting_inputText);
        text = (TextView) findViewById((R.id.handwriting_sentence));
        editorView = findViewById(R.id.editor_view);

        inputText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputText.setText("");
                text.setText(wordList.get(wordIndex));
                wordIndex = wordIndex+1;
                if(wordIndex == 5)
                {
                    Intent intent = new Intent(HandwritingActivity.this, AfterHandwritingActivity.class);
                    HandwritingActivity.this.startActivity(intent);
                }

            }
        });

        // load fonts
        AssetManager assetManager = getApplicationContext().getAssets();
        Map<String, Typeface> typefaceMap = FontUtils.loadFontsFromAssets(assetManager);
        editorView.setTypefaces(typefaceMap);

        editorView.setEngine(engine);

        final Editor editor = editorView.getEditor();
        editor.addListener(new IEditorListener()
        {
            @Override
            public void partChanging(Editor editor, ContentPart oldPart, ContentPart newPart)
            {
                inputText.setText(editorView.getWord());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inputText.setText(editorView.getWord());
                    }
                });
            }

            @Override
            public void partChanged(Editor editor)
            {
                invalidateOptionsMenu();
                invalidateIconButtons();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inputText.setText(editorView.getWord());
                    }
                });
            }

            @Override
            public void contentChanged(Editor editor, String[] blockIds)
            {
                invalidateOptionsMenu();
                invalidateIconButtons();
                inputText.setText(editorView.getWord());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inputText.setText(editorView.getWord());
                    }
                });
            }

            @Override
            public void onError(Editor editor, String blockId, String message)
            {
                Log.e("myLog", "Failed to edit block \"" + blockId + "\"" + message);
            }
        });

        setInputMode(InputController.INPUT_MODE_FORCE_PEN); // If using an active pen, put INPUT_MODE_AUTO here

        String packageName = "File1.iink";
        File file = new File(getFilesDir(), packageName);
        try
        {
            contentPackage = engine.createPackage(file);
            contentPart = contentPackage.createPart("Text Document"); // Choose type of content (possible values are: "Text Document", "Text", "Diagram", "Math", and "Drawing")
        }
        catch (IOException e)
        {
            Log.e("myLog", "Failed to open package \"" + packageName + "\"", e);
        }
        catch (IllegalArgumentException e)
        {
            Log.e("myLog", "Failed to open package \"" + packageName + "\"", e);
        }

        setTitle("Type: " + contentPart.getType());


        // wait for view size initialization before setting part
        editorView.post(new Runnable()
        {
            @Override
            public void run()
            {
                editorView.getRenderer().setViewOffset(0, 0);
                editorView.getRenderer().setViewScale(1);
                editorView.setVisibility(View.VISIBLE);
                editor.setPart(contentPart);


            }
        });

        findViewById(R.id.button_clear).setOnClickListener(this);

        invalidateIconButtons();
    }



    private void setInputMode(int inputMode)
    {
        editorView.setInputMode(inputMode);
    }

    private void invalidateIconButtons()
    {
        Editor editor = editorView.getEditor();

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Button imageButtonClear = (Button) findViewById(R.id.button_clear);
                imageButtonClear.setEnabled(contentPart != null);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_clear:
                editorView.getEditor().clear();
                break;

            default:
                Log.e("myLog", "Failed to handle click event");
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        editorView.setOnTouchListener(null);
        editorView.close();

        if (contentPart != null)
        {
            contentPart.close();
            contentPart = null;
        }
        if (contentPackage != null)
        {
            contentPackage.close();
            contentPackage = null;
        }


        // IInkApplication has the ownership, do not close here
        engine = null;

        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.albumAct:
                Intent intent = new Intent(this, AlbumActivity.class);
                this.startActivity(intent);
                break;
            case R.id.zoomAct:
                intent = new Intent(this, ZoomActivity.class);
                this.startActivity(intent);
                break;
            case R.id.patternAct:
                intent = new Intent(this, PatternActivity.class);
                this.startActivity(intent);
                break;
            case R.id.handwritingAct:
                intent = new Intent(this, HandwritingActivity.class);
                this.startActivity(intent);
                break;
            case R.id.keyboardAct:
                intent = new Intent(this, KeyboardActivity.class);
                this.startActivity(intent);
                break;
            case R.id.speechAct:
                intent = new Intent(this, SpeechActivity.class);
                this.startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


}
