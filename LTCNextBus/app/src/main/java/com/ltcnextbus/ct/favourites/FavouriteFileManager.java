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
 * Created by Tyler on 04/10/2014.
 *
 * Encapsulation  of the reading and writing of the file that the
 * favourite stops are saved in
 *
 */
public class FavouriteFileManager {

    private FileOutputStream fos;
    private FileInputStream fis;
    private String FILENAME = "favourite-stops";

    private Context context;
    public FavouriteFileManager(Context context) {
        this.context = context;
    }


    public void saveFavouritesToFile(ArrayList<FavouriteStop> stops) {
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
    public ArrayList<FavouriteStop> readFromInternalStorage() {
        try {
            fis = context.openFileInput(FILENAME);
            ObjectInputStream oin = new ObjectInputStream(fis);
            return (ArrayList<FavouriteStop>)(oin.readObject());
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
