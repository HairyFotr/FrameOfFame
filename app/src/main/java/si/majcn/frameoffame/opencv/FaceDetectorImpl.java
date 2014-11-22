package si.majcn.frameoffame.opencv;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import si.majcn.frameoffame.R;

public class FaceDetectorImpl implements FaceDetector {
    private static final int FACE_DETECT_FLAGS_ONE = Objdetect.CASCADE_FIND_BIGGEST_OBJECT | Objdetect.CASCADE_DO_ROUGH_SEARCH;
    private static final double SCALE_FACTOR = 1.05;
    private static final int MIN_NEIGHBORS = 3;

    private int mAbsoluteFaceSizeMin = 0;
    private float mRelativeFaceSizeMin = 0.2f;

    private int mAbsoluteFaceSizeMax = 0;
    private float mRelativeFaceSizeMax = 0.5f;

    private CascadeClassifier mFaceDetector;

    public FaceDetectorImpl(Context context) {
        mFaceDetector = OpenCVFactory.getClassifier(context, R.raw.haarcascade_frontalface_default);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Rect detectOne(Mat image) {
        if (mAbsoluteFaceSizeMin == 0) {
            int height = image.height();
            if (Math.round(height * mRelativeFaceSizeMin) > 0) {
                mAbsoluteFaceSizeMin = Math.round(height * mRelativeFaceSizeMin);
            }
        }


        if (mAbsoluteFaceSizeMax == 0) {
            int height = image.height();
            if (Math.round(height * mRelativeFaceSizeMax) > 0) {
                mAbsoluteFaceSizeMax = Math.round(height * mRelativeFaceSizeMax);
            }
        }

        MatOfRect faces = new MatOfRect();
        mFaceDetector.detectMultiScale(image, faces, SCALE_FACTOR, MIN_NEIGHBORS, FACE_DETECT_FLAGS_ONE, new Size(mAbsoluteFaceSizeMin, mAbsoluteFaceSizeMin), new Size(mAbsoluteFaceSizeMax, mAbsoluteFaceSizeMax));

        Rect[] facesArray = faces.toArray();
        Rect curFace = null;
        if (facesArray.length > 0) {
            curFace = getResizedRect(facesArray[0], image.width(), image.height());
        }
        faces.release();

        return curFace;
    }

    private Rect getResizedRect(Rect orig, int maxWidth, int maxHeight) {
        int x = Math.max(orig.x - orig.width / 4, 0);
        int w = Math.min(orig.width + orig.width / 2, maxWidth - x);
        int y = Math.max(orig.y - orig.height / 4, 0);
        int h = Math.min(orig.height + orig.height / 2, maxHeight - y);

        return new Rect(x, y, w, h);
    }
}
