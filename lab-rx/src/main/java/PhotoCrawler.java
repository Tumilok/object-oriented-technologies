import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import model.Photo;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        Observable<Photo> downloadedExamples = photoDownloader.getPhotoExamples();
        downloadedExamples.subscribe(photoSerializer::savePhoto);
    }

    public void downloadPhotosForQuery(String query) {
        Observable<Photo> downloadedExamples = photoDownloader.searchForPhotos(query);
        downloadedExamples.subscribe(photoSerializer::savePhoto);
    }

//    public void downloadPhotosForMultipleQueries(List<String> queries) {
//        Observable<Observable<Photo>> downloadedExamples = photoDownloader.searchForPhotos(queries);
//        downloadedExamples.subscribe(p -> p.subscribeOn(Schedulers.io())
//                .take(5)
//                .compose((this::processPhotos))
//                .subscribe(photoSerializer::savePhoto));
//    }

    public Observable<Photo> processPhotos(Observable<Photo> stream) {
        return stream.filter(photoProcessor::isPhotoValid)
                .map(photoProcessor::convertToMiniature);
    }
}
