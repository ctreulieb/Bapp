package com.ltcnextbus.ct.favourites;
import java.io.IOException;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.content.Context;
import java.io.StreamCorruptedException;

/**
 * Created by Owner on 04/10/2014.
 */
public class FavoriteFileManager {

    private FileOutputStream fos;
    private FileInputStream fis;
    private String FILENAME = "favorite-stops";

    private Context context;
    public FavoriteFileManager(Context context) {
        this.context = context;
    }


    public void saveFavoritesToFile(ArrayList<FavoriteStop> stops) {
        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(fos);
            objectStream.writeObject(stops);
            objectStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<FavoriteStop> readFromInternalStorage() {
        try {
            fis = context.openFileInput(FILENAME);
            ObjectInputStream oin = new ObjectInputStream(fis);
            return (ArrayList<FavoriteStop>)(oin.readObject());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
