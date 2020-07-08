
package com.google.sps.servlets;

// Data Store
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
//BlobStore
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Runs when the form for a new comment is submitted. The comment
 * is then stored permenantly with DatastoreService.
 */
@WebServlet("/new-comment")
public class NewCommentDataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String imageUrl = getBlobUrl(request);
    String user = request.getParameter("user");
    String content = request.getParameter("content");
    UserService myUser = UserServiceFactory.getUserService();
    String email = myUser.getCurrentUser().getEmail();

    // Block chat spammers.
    if (!verified(user, content)) {
      response.sendRedirect("chat.html");
      return;
    }

    long timeStamp = System.currentTimeMillis();
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MMM d, HH:MM a");
    String displayTime = formatter.format(date);

    Entity myEntity = new Entity("Task");
    myEntity.setProperty("user", user);
    myEntity.setProperty("content", content);
    myEntity.setProperty("timeStamp", timeStamp);
    myEntity.setProperty("displayTime", displayTime);
    myEntity.setProperty("email", email);
    myEntity.setProperty("image", imageUrl);  

    DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
    dataStore.put(myEntity);

    // redirect causes html to refresh with new comments
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      System.out.println("Sleep Interrupted");
    } finally {
      response.sendRedirect("/chat.html");
    }
  }

  private boolean verified(String user, String content) {
    if (user.equals("") || content.equals("")) {
      return false;
    }
    return true;
  }


  private String getBlobUrl(HttpServletRequest request){
    BlobstoreService myBlobService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>>  blobs = myBlobService.getUploads(request);
    List<BlobKey> myKeys = blobs.get("image");

    if (myKeys == null || myKeys.isEmpty()) {
      return null;
    }

    BlobKey blobKey = myKeys.get(0);

    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      myBlobService.delete(blobKey);
      return null;
    }

    // Checks if file type is an image
    String type = blobInfo.getContentType();
    if (!type.startsWith("image")){
      myBlobService.delete(blobKey);
      return null;
    }

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
  
}
