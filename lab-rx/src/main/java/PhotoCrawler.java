import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import model.Photo;
import model.PhotoSize;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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

    public void downloadPhotosForMultipleQueries(List<String> queries) {
        photoDownloader.searchForPhotos(queries)
                .compose((this::processPhotos))
                .subscribe(photoSerializer::savePhoto);
    }

    private Observable<Photo> processPhotos2(Observable<Photo> photoObservable) {
        return photoObservable.filter(photoProcessor::isPhotoValid)
                .map(photoProcessor::convertToMiniature);
    }

    public Observable<Photo> processPhotos(Observable<Photo> photoObservable) {
        return photoObservable.groupBy(PhotoSize::resolve)
                .flatMap(photoGroup -> switch (photoGroup.getKey()) {
                    case SMALL -> ignoreElements(photoGroup);
                    case MEDIUM -> bufferMediumPhotos(photoGroup);
                    case LARGE -> convertLargePhotosToMiniatures(photoGroup);
                });
    }

    private Observable<Photo> ignoreElements(Observable<Photo> photos) {
        return photos.take(0);
    }

    private Observable<Photo> bufferMediumPhotos(Observable<Photo> photos) {
        return photos.buffer(5, TimeUnit.SECONDS)
                .flatMapIterable(photoList -> {
                    log.info("Buffer flush: " + photoList.size());
                    return photoList;
                });
    }

    private Observable<Photo> convertLargePhotosToMiniatures(Observable<Photo> photos) {
        return photos.observeOn(Schedulers.computation())
                .map(photoProcessor::convertToMiniature);
    }
}
