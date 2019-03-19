package com.equifax.hackathon.slimshadydetector;

import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;

import java.nio.ByteBuffer;
import java.util.List;

public class RekognitionService {

  private static final String ACCESS_KEY = "**********************";
  private static final String SECRET_KEY = "**********************";

  private final AmazonRekognition client;

  public RekognitionService() {
    final BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
    client = new AmazonRekognitionClient(credentials);
  }

  public boolean isMatch(final ByteBuffer byteBuffer) {
    final Image image = new Image();
    image.withBytes(byteBuffer);
    final DetectFacesRequest request = new DetectFacesRequest()
      .withAttributes(Attribute.ALL.toString())
      .withImage(image);
    final DetectFacesResult result = client.detectFaces(request);
    final List<FaceDetail> faceDetailList = result.getFaceDetails();
    Log.d("Response", faceDetailList.toString());

    return !faceDetailList.isEmpty() && isConfident(faceDetailList);
  }

  private boolean isConfident(final List<FaceDetail> faceDetails) {
    for (FaceDetail detail : faceDetails) {
      if (detail.getConfidence() > 75) {
        return true;
      }
    }

    return false;
  }

//  private ByteBuffer fileToByteBuffer2(final File file){
//    InputStream is = getResources().openRawResource(
//      getResources().getIdentifier("glasses",
//        "raw", getPackageName()));
//
//    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//
//    int nRead;
//    byte[] data = new byte[16384];
//
//    while ((nRead = is.read(data, 0, data.length)) != -1) {
//      buffer.write(data, 0, nRead);
//    }
//
//    buffer.flush();
//
//    return ByteBuffer.wrap(buffer.toByteArray());
//  }
}
