package com.krajinov.qualification;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

import model.Person;
import utils.DataHelper;

public class PersonAdapter extends ArrayAdapter<Person> {

    private Context context;
    private List<Person> personList;

    private LruCache<String, Bitmap> imageCache;

    public PersonAdapter(Context context, int resource, List<Person> objects) {
        super(context, resource, objects);
        this.context = context;
        this.personList = objects;


        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = (int) (maxMemory / 8L);
        imageCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.person_item, parent, false);

        final Person person = personList.get(position);
        DataHelper dataHelper = new DataHelper(person);
        AssetManager mng = context.getAssets();

        //Prikaz imena i prezimena osobe u TextView widget-u
        TextView name = (TextView) view.findViewById(R.id.nameText);
        Typeface tf = Typeface.createFromAsset(mng, "fonts/roboto-slab-regular.ttf");
        name.setTypeface(tf);
        name.setText(person.getFirst_name() + " " + person.getLast_name());
        
        //
        ImageView favImage = (ImageView) view.findViewById(R.id.imageFavorites);
        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, person.getFirst_name() + " added to favorites!", Toast.LENGTH_SHORT).show();
            }
        });

        //Prikaz email-a u TextView widget-u
        TextView email = (TextView) view.findViewById(R.id.emailText);
        email.setText(person.getEmail());

        //Prikaz lokacije u TextView widget-u
        TextView location = (TextView) view.findViewById(R.id.locationText);
        location.setText(dataHelper.getLocation());

        //Prikaz datuma u TextView widget-u
        TextView date = (TextView) view.findViewById(R.id.calendarText);
        date.setText(dataHelper.getDate());

        //Prikaz radnog mjesta u TextView widget-u
        TextView workingPosition = (TextView) view.findViewById(R.id.jobText);
        workingPosition.setText(dataHelper.getWorkingPosition());

        //Prikaz slike kartice u ImageView widget-u
        ImageView card = (ImageView) view.findViewById(R.id.imageCard);
        card.setImageBitmap(dataHelper.getCardImage(context));

        //Prikaz stanja računa u TextView widget-u
        TextView salary = (TextView) view.findViewById(R.id.salaryText);
        BigDecimal bd = dataHelper.getSalary();
        if (bd != null) {
            try {
                if (bd.compareTo(BigDecimal.ZERO) < 0) {
                    salary.setTextColor(context.getResources().getColor(R.color.colorSalaryMinus));
                }
                salary.setText(bd.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Prikaz slike osobe u ImageView widget-u
        Bitmap bitmap = imageCache.get(person.getId());

        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.imagePhoto);
            image.setImageBitmap(bitmap);
        } else {
            PersonAndView container = new PersonAndView();
            container.person = person;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }


        return view;
    }

    class PersonAndView {
        public Person person;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<PersonAndView, Void, PersonAndView> {

        @Override
        protected PersonAndView doInBackground(PersonAndView... params) {

            PersonAndView container = params[0];
            Person person = container.person;

            try {
                String imageUrl = person.getAvatar();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PersonAndView result) {
            try {
                ImageView image = (ImageView) result.view.findViewById(R.id.imagePhoto);
                image.setImageBitmap(result.bitmap);
                imageCache.put(result.person.getId(), result.bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
