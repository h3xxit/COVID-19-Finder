package com.anticoronabrigade.frontend.UtilityClasses;

import android.content.Context;
import android.widget.Toast;

import com.anticoronabrigade.frontend.ActivityClasses.Main.User;
import com.anticoronabrigade.frontend.ActivityClasses.Main.UserDto;
import com.anticoronabrigade.frontend.DatabaseSimulator;
import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReadWriteFileHandler {
    //region Variables
    Context context;
    //endregion

    //region Constructors
    public ReadWriteFileHandler(Context context) {
        this.context = context;
    }
    //endregion

    //region Public functions

    //region Read
    public List<WalkedPath> protectedReadPathsFromFile(String filePath) {
        try {
            return readPaths(context.openFileInput(filePath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public UserDto protectedReadUserFromFile(String filePath) {
        try {
            //return readUser(context.openFileInput(filePath));
            return readInfected(context.openFileInput(filePath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    //region Write
    public void protectedWriteToFile(String filePath, List<WalkedPath> from) {
        try {
            context.deleteFile(filePath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            writeProfileStream(context.openFileOutput(filePath, Context.MODE_PRIVATE), from);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void protectedWriteToFile(String filePath, UserDto user) {
        try {
            context.deleteFile(filePath);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            //writeProfileStream(context.openFileOutput(filePath, Context.MODE_PRIVATE), user);
            writeInfected(context.openFileOutput(filePath, Context.MODE_PRIVATE), user);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //endregion

    //region Private functions

    //region write
    private void writeProfileStream(FileOutputStream out, List<WalkedPath> from) {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            for (WalkedPath p : from) {
                writer.write(p.getDate().toString());
                writer.write('|');
                for (MapLocation mapLocation : p.getPoints()) {
                    if(mapLocation == null)
                        break;
                    writer.write(mapLocation.latitude.toString());
                    writer.write('&');
                    writer.write(mapLocation.longitude.toString());
                    writer.write(' ');
                }
                writer.write('\n');
            }
            writer.close();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeProfileStream(FileOutputStream out, UserDto from){
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            writer.write(from.getEmail());
            writer.write("\n");
            writer.write(from.getPassword());
            writer.write("\n");
            writer.close();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeInfected(FileOutputStream out, UserDto from){
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            writer.write(from.getIsInfected());
            writer.close();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region read
    private List<WalkedPath> readPaths(FileInputStream in) throws NumberFormatException{
        List<WalkedPath> arrL = new ArrayList<>();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            StringBuilder stringBuilder = new StringBuilder();
            boolean buildingDate = true;

            Double latit = 0d, longit = 0d;
            List<MapLocation> currPath = new ArrayList<>();
            Long date = 0L;

            int c = inputStreamReader.read();
            while (c != -1) {
                if(c == (int)'|') {
                    date = Long.valueOf(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    buildingDate = false;
                }
                else if(c == (int)'&' && !buildingDate) {
                    latit = Double.valueOf(stringBuilder.toString());
                    stringBuilder.setLength(0);
                }
                else if(c == (int)' ' && !buildingDate) {
                    longit = Double.valueOf(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    currPath.add(new MapLocation(latit, longit));
                }
                else if(c == (int)'\n'){
                    if(Calendar.getInstance().getTimeInMillis() - date < Const.TEN_DAYS)
                        arrL.add(new WalkedPath(date, currPath));
                    currPath = new ArrayList<>();
                    buildingDate = true;
                }
                else {
                    stringBuilder.append((char) c);
                }
                c = inputStreamReader.read();
            }
            inputStreamReader.close();
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return arrL;
    }

    private UserDto readUser(FileInputStream in) throws NumberFormatException{
        UserDto currentUser = new UserDto();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            StringBuilder stringBuilder = new StringBuilder();
            boolean buildingEmail = true;

            int c = inputStreamReader.read();
            while (c != (int)'\n') {
                stringBuilder.append((char) c);
                c = inputStreamReader.read();
            }
            currentUser.setEmail(stringBuilder.toString());
            stringBuilder.setLength(0);

            c = inputStreamReader.read();
            while (c != (int)'\n') {
                stringBuilder.append((char) c);
                c = inputStreamReader.read();
            }
            currentUser.setPassword(stringBuilder.toString());

            inputStreamReader.close();
            in.close();
            return currentUser;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private UserDto readInfected(FileInputStream in) throws NumberFormatException{
        UserDto currentUser = new UserDto();
        currentUser.setEmail("");
        currentUser.setPassword("");

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            boolean buildingEmail = true;

            int c = (inputStreamReader.read());
            currentUser.setIsInfected(c);

            inputStreamReader.close();
            in.close();
            return currentUser;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    //endregion

    //endregion
}
