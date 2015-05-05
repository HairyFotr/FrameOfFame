package si.majcn.frameoffame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Random;

import si.majcn.frameoffame.facecropper.FaceCropper;
import si.majcn.frameoffame.fragment.image.view.ImageGridView;
import si.majcn.frameoffame.fragment.image.view.ImageGridViewAdapter;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("imageprocessing");
    }

    public static native int getNumberOfEffects();
    public static native void applyEffect(Bitmap bmp, int i);

    public static int TAKE_PICTURE = 1;

    private FaceCropper mCropper;
    private Random mRandomGenerator;

    private ImageButton mCameraButton;

    private ImageGridView mGridView;
    private ImageGridViewAdapter mGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridViewAdapter = new ImageGridViewAdapter(this);

        mCameraButton = (ImageButton) findViewById(R.id.cameraButton);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        mGridView = (ImageGridView) findViewById(R.id.gridview);
        mGridView.setNumColumns(40);
        mGridView.setAdapter(mGridViewAdapter);

        mCropper = new FaceCropper();
        mRandomGenerator = new Random();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");

            Bitmap srcBmp = mCropper.getCroppedImage(image);
            Bitmap dstBmp;

            if (srcBmp.getWidth() >= srcBmp.getHeight()) {
                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                        0,
                        srcBmp.getHeight(),
                        srcBmp.getHeight()
                );

            } else {
                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                        srcBmp.getWidth(),
                        srcBmp.getWidth()
                );
            }

            applyEffect(dstBmp, mRandomGenerator.nextInt(getNumberOfEffects()));
            mGridViewAdapter.setImage(dstBmp);
            mGridView.invalidateViews();
        }
    }
}
