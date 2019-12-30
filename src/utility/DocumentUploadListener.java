package utility;


import com.google.cloud.storage.Blob;

public interface DocumentUploadListener {

    void onSuccess(Blob blob);

    void onFailure(Exception e);


}
