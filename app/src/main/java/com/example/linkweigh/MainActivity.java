package com.example.linkweigh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.linkweigh.TeacherUpload.uploaderID;
import static com.example.linkweigh.TeacherUpload.uploaderName;


public class MainActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;
    private static final String TAG = "Fire_log";
    int count_video;
    int count_jpg;
    int count_cap;
    int count_sentence;
    int count_video_final;
    int count_jpg_final;
    int count_cap_final;
    int count_sentence_final;
    int count2;
    int next;
    int i;
    String jpg;
    String gif;
    float output_1;
    float output_2;
    float output_3;
    float output_4;
    float output_5;
    float output_0;
    int prediction;
    int final_prediction;
    Button upload;
    public static String urlString;
    EditText editText;
    List<Integer> input;
    public static String clipwordsearch;
    public static Switch webwordswitched;
    public static String url;
    public static String question1;
    public static String option1A;
    public static String option1B;
    public static String option1C;
    public static String option1D;
    public static String question1Answer;
    public static String uploaderName;
    //    public static boolean noQuestion = false;
    public static FirebaseFirestore db;
    public static DocumentSnapshot docc;
    Button watch;
    RatingBar ratingBar;
    TextView info;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upload = findViewById(R.id.upload);
        watch = findViewById(R.id.go);
    }


    public void uploader(View view) {

        showProgressDialog();
        editText = findViewById(R.id.url_edittext);
        ratingBar = findViewById(R.id.ratingbar);
        info = findViewById(R.id.info);

        urlString = editText.getText().toString();
        if (urlString.equals("")) {
            Toast.makeText(MainActivity.this, "Kindly paste a full link for prediction", Toast.LENGTH_LONG).show();
            ratingBar.setRating(0);
            hideProgressDialog();
            return;
        }
        if (urlString.startsWith("http")) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                new Thread() {
                    @Override
                    public void run() {
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(urlString).get();
                        } catch (IOException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Your internet connection seems to be slow, please try again!", Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                            hideProgressDialog();
                            return;
                        }

                        assert doc != null;

                        //count video, images, captions, sentences
                        Elements video = doc.getElementsByTag("video");
                        Elements media = doc.getElementsByTag("img");
                        Elements figcaption = doc.getElementsByTag("figcaption");
                        char[] character = doc.normalise().body().text().toCharArray();

                        //counting video elements
                        count_video = Objects.requireNonNull(video.toArray()).length;
                        System.out.println("Total number of Videos: " + count_video);

                        //getting video elements
                        for (Element e_vid : video) {
                            System.out.println("Video: " + e_vid.toString());
                        }

                        //counting elements that contain captions
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            int count_media = Objects.requireNonNull(media.toArray()).length;
                            System.out.println("Total number of images: " + count_media);
                        }

                        //getting elements that contain images
                        for (Element e_img : media) {
                            System.out.println("Images: " + e_img.toString());
                        }

                        //counting elements that contain captions
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            count_cap = Objects.requireNonNull(figcaption.toArray()).length;
                            System.out.println("Total number of captions: " + count_cap);
                        }

                        //getting elements that contain captions
                        for (Element e_cap : figcaption) {
                            System.out.println("Captions: " + e_cap.toString());
                        }


                        // Loop through img tags for .jpg files
                        count_jpg = 0;
                        for (Element el : media) {
                            jpg = el.toString();
                            if (jpg.contains(".jpg")) {
                                count_jpg++;
                                System.out.println(el.toString());
                            }

                        }
                        System.out.println("Number of .JPG images: " + count_jpg);


                        // Loop through img tags for .gif files
                        for (Element el : media) {
                            gif = el.toString();
                            if (gif.contains(".gif")) {
                                count2++;
                                System.out.println(el.toString());
                            }
                        }

                        System.out.println("Number of .gif images: " + count2);
                        System.out.println(character);
                        System.out.println(character.length);

                        count_sentence = 0;
                        for (i = 0; i < character.length - 1; i++) {
                            next = i + 1;
                            if ((character[i] == '.') && (character[next] == ' '))
                                count_sentence++;
                        }
                        System.out.println("Number of Sentences: " + count_sentence);


                        // ifs for videos
                        if (count_video > 0) {
                            count_video_final = 1;
                        } else count_video_final = 0;

                        // ifs for photos
                        if (count_jpg == 0) {
                            count_jpg_final = 0;
                        }
                        if (count_jpg > 0 && count_jpg < 6) {
                            count_jpg_final = 1;
                        }
                        if (count_jpg > 5 && count_jpg < 11) {
                            count_jpg_final = 2;
                        }
                        if (count_jpg > 10 && count_jpg < 21) {
                            count_jpg_final = 3;
                        }
                        if (count_jpg > 20) {
                            count_jpg_final = 4;
                        }

                        // ifs for captions
                        if (count_cap == 0) {
                            count_cap_final = 0;
                        }
                        if (count_cap > 0 && count_cap < 3) {
                            count_cap_final = 1;
                        }
                        if (count_cap == 3) {
                            count_cap_final = 2;
                        }
                        if (count_cap > 3) {
                            count_cap_final = 3;
                        }

                        // ifs for sentences
                        if (count_sentence == 0) {
                            count_sentence_final = 5;
                        }
                        if (count_sentence > 0 && count_sentence < 21) {
                            count_sentence_final = 5;
                        }
                        if (count_sentence > 20 && count_sentence < 41) {
                            count_sentence_final = 4;
                        }
                        if (count_sentence > 40 && count_sentence < 61) {
                            count_sentence_final = 3;
                        }
                        if (count_sentence > 60 && count_sentence < 81) {
                            count_sentence_final = 2;
                        }
                        if (count_sentence > 80) {
                            count_sentence_final = 1;
                        }


                        input = Arrays.asList(count_video_final, count_jpg_final, count_cap_final, count_sentence_final);

                        //from List of Interger to float array
                        float[] bytebuffer_float = new float[input.size()];
                        for (int a = 0; a < input.size(); a++) {
                            bytebuffer_float[a] = input.get(a);
                        }

                        //initializing the output
                        float[][] output = new float[1][6];
                        AssetManager assetManager = getAssets();
                        try (Interpreter interpreter = new Interpreter(loadModelFile(assetManager))) {

                            //fixing input and output parameters into the model and running it
                            interpreter.run(bytebuffer_float, output);

                            //finding the highest value
                            output_0 = output[0][0];
                            output_1 = output[0][1];
                            output_2 = output[0][2];
                            output_3 = output[0][3];
                            output_4 = output[0][4];
                            output_5 = output[0][5];

                            ArrayList<Float> arrayList = new ArrayList<>();
                            arrayList.add(output_0);
                            arrayList.add(output_1);
                            arrayList.add(output_2);
                            arrayList.add(output_3);
                            arrayList.add(output_4);
                            arrayList.add(output_5);

                            Object obj = Collections.max(arrayList);
                            prediction = arrayList.indexOf(obj);
                            final_prediction = prediction;

                            //toasting on UI thread
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ratingBar.setRating(final_prediction);
                                    if (final_prediction == 1) {
                                        hideProgressDialog();
                                        final Toast toast = Toast.makeText(MainActivity.this, "Very poor rating, you may consider sharing a different article", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                    if (final_prediction == 2) {
                                        hideProgressDialog();
                                        final Toast toast = Toast.makeText(MainActivity.this, "Low rating, you may consider sharing a different article", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                    if (final_prediction == 3) {
                                        hideProgressDialog();
                                        final Toast toast = Toast.makeText(MainActivity.this, "Averagely Enjoyable...", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                    if (final_prediction == 4) {
                                        hideProgressDialog();
                                        final Toast toast = Toast.makeText(MainActivity.this, "Great!!!", Toast.LENGTH_LONG);
                                        toast.show();

                                    }
                                    if (final_prediction == 5) {
                                        hideProgressDialog();
                                        final Toast toast = Toast.makeText(MainActivity.this, "Excellent!!!", Toast.LENGTH_LONG);
                                        toast.show();

                                    }
                                    info.setVisibility(View.VISIBLE);
                                    System.clearProperty(urlString);
                                }
                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }.start();
            }
        } else {
            hideProgressDialog();
            ratingBar.setRating(0);
            Toast.makeText(MainActivity.this, "Sorry, this link isn't recognized. Kindly paste the full link starting from Http", Toast.LENGTH_LONG).show();
        }


    }

    //loading model as a MappedByteBuffer type
    private MappedByteBuffer loadModelFile(AssetManager assets)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd("converted_url_ranker_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("A.I is weighing your article...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void showProgressDialog_eat() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("serving your appetizer");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void textview_onclick(View view) {
        editText = findViewById(R.id.url_edittext);
        urlString = editText.getText().toString();
        if (urlString.equals("")) {
            Toast.makeText(MainActivity.this, "Kindly paste a full link for prediction", Toast.LENGTH_LONG).show();
//            ratingBar.setRating(0);
//            hideProgressDialog();
            return;
        }

        Intent intent = new Intent(MainActivity.this, TeacherUpload.class);
        //take the teacher to the success page!
        intent.putExtra("url", urlString);
        startActivity(intent);

    }

    public void Scholar(View view) {
//        Intent intent = new Intent(MainActivity.this, StudentWatch.class);
//        //take the teacher to the success page!
//        startActivity(intent);
        upload.setVisibility(View.GONE);
        watch.setVisibility(View.VISIBLE);
//        ratingBar.setVisibility(View.GONE);
    }

    public void Educator(View view) {
        upload.setVisibility(View.VISIBLE);
        watch.setVisibility(View.GONE);
    }

    public void watch(View view) {

        showProgressDialog_eat();
        editText = findViewById(R.id.url_edittext);
//        final RatingBar ratingBar = findViewById(R.id.ratingbar);
//        textView.setVisibility(View.VISIBLE);

        clipwordsearch = editText.getText().toString();
        if (clipwordsearch.equals("")) {
            Toast.makeText(MainActivity.this, "Kindly paste a keyword", Toast.LENGTH_LONG).show();
//            ratingBar.setRating(0);
            hideProgressDialog();
            return;
        }
//        EditText clipwordIni = findViewById(R.id.editText);
//        clipwordsearch = clipwordIni.getText().toString();
//        String character_zero = String.valueOf(clipwordsearch.charAt(0));


//        if (webwordswitched.isChecked()) {
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("webword");
        Query query = collectionReference.whereEqualTo("webwords", clipwordsearch);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {//stops here if webword not found
                        url = document.getString("url");//it extracts the url as it approaches the next line
                        question1 = document.getString("question");
                        option1A = document.getString("optionA");
                        option1B = document.getString("optionB");
                        option1C = document.getString("optionC");
                        option1D = document.getString("optionD");
                        question1Answer = document.getString("answer");
                        uploaderID = document.getString("uploaderID");
                        uploaderName = document.getString("uploaderName");
                        docc = document;

                        Log.d(TAG, url);
                        Log.d(TAG, question1);
                        Log.d(TAG, question1Answer);
                        Log.d(TAG, option1A);
                        Log.d(TAG, option1B);
                        Log.d(TAG, option1C);
                        Log.d(TAG, option1D);
                        Log.d(TAG, uploaderID);
                        Log.d(TAG, uploaderName);


                    }
                }//if task is successful it ignores this block
                else {
                    hideProgressDialog();
                    Toast.makeText(MainActivity.this, "Sorry, something went wrong", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "get failed with ", task.getException());
                    return;
                }
                //escaped an error if you leave the editbox empty and search but ive used the null string
                //for a new video
                if (url == null) {
                    hideProgressDialog();
                    Toast.makeText(MainActivity.this, "Sorry, the Webword you entered doesn't exist", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "get failed with clipword doesnt exist ", task.getException());
                    return;
                }
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    hideProgressDialog();
//                    startActivity(browserIntent);
            }
        });
//        }
    }
}