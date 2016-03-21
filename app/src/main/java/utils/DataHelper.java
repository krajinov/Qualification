package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import model.Person;

public class DataHelper {

    private Person person;
    Map<String, Float> map = null;
    Map<String, String> job = null;

    public DataHelper(Person person) {
        this.person = person;
    }

    public String getLocation() {
        StringBuilder sb = new StringBuilder();
        sb.append(person.getCity() + ", " + person.getCountry());
        return sb.toString();
    }

    public String getDate() {
        try {
            Date d = new Date(person.getDate() * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            String date = dateFormat.format(d).toString();
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getWorkingPosition() {
        job = person.getJob();
        StringBuilder sb = new StringBuilder();
        sb.append(job.get("position") + " (" + job.get("company") + ")");
        return sb.toString();
    }

    public Bitmap getCardImage(Context context) {
        job = person.getJob();
        String card = job.get("card");
        InputStream stream = null;
        try {
            stream = context.getAssets().open(card.toLowerCase() + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public BigDecimal getSalary() {
        job = person.getJob();
        try {
            String salary = job.get("salary");
            BigDecimal bd = new BigDecimal(salary);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
