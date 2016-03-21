package layout;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.krajinov.qualification.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {


    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        EditText myName = (EditText) rootView.findViewById(R.id.editName);

        EditText myDate = (EditText) rootView.findViewById(R.id.editName);

        ImageView cameraPhoto = (ImageView) rootView.findViewById(R.id.cameraPhoto);

        return rootView;
    }

}
