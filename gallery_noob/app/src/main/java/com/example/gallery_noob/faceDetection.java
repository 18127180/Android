package com.example.gallery_noob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class faceDetection extends AppCompatActivity {
    InputImage image;
    ImageView imageView;
    String path;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        imageView=findViewById(R.id.image_detect);
        textView=findViewById(R.id.text_detect);

        path=getIntent().getStringExtra("current_path");
        if (path!=null)
        {
            Uri uri=Uri.fromFile(new File(path));
//            imageView.setImageURI(uri);
            detectFaceFromImage(uri);
            //FirebaseVisionImage image=FirebaseVisionImage.fromFilePath(this,uri);
        }
    }

    private void detectFaceFromImage(Uri uri) {
        try {
            image = InputImage.fromFilePath(this, uri);
            FaceDetectorOptions highAccuracyOpts =
                    new FaceDetectorOptions.Builder()
                            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                            .build();
            FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

            detector.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {
                            Log.e("Failed detect","Success");
                            textView.setText("dfgdfg");
                            int count=0;
                            for (Face face : faces) {
                                count++;
                                textView.setText(String.valueOf(count));
                                Rect bounds = face.getBoundingBox();
//                                textView.setText("Bounding Polygon "+ "("+bounds.centerX()+","+bounds.centerY()+")"+"\n\n");
//                                float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
//                                float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
//                                textView.append("Angles of rotation " + "Y:"+rotY+","+ "Z: "+rotZ+ "\n\n");
                                // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                // nose available):
                                // If face tracking was enabled:
//                                if (face.getTrackingId() != -1) {
//                                    int id = face.getTrackingId();
//                                    textView.append("id: " + id + "\n\n");
//                                }
//                                FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
//                                if (leftEar != null) {
//                                    PointF leftEarPos = leftEar.getPosition();
//                                    textView.append("LeftEarPos: " + "("+leftEarPos.x+"," + leftEarPos.y+")"+"\n\n");
//                                }
//                                FaceLandmark rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR);
//                                if (rightEar != null) {
//                                    PointF rightEarPos = rightEar.getPosition();
//                                    textView.append("RightEarPos: " + "("+rightEarPos.x+","+rightEarPos.y +")"+ "\n\n");
//                                }


                                // If classification was enabled:
//                                if (face.getSmilingProbability() != Face.UNCOMPUTED_PROBABILITY) {
//                                    float smileProb = face.getSmilingProbability();
//                                    textView.append("SmileProbability: " + ("" + smileProb * 100).subSequence(0, 4) + "%" + "\n\n");
//                                }
//                                if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
//                                    float rightEyeOpenProb = face.getRightEyeOpenProbability();
//                                    textView.append("RightEyeOpenProbability: " + ("" + rightEyeOpenProb * 100).subSequence(0, 4) + "%" + "\n\n");
//                                }
//                                if (face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
//                                    float leftEyeOpenProbability = face.getLeftEyeOpenProbability();
//                                    textView.append("LeftEyeOpenProbability: " + ("" + leftEyeOpenProbability * 100).subSequence(0, 4) + "%" + "\n\n");
//                                }
                            }
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    Log.e("Failed detect","Fail");
                                }
                            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}