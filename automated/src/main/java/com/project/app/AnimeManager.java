package com.project.app;

import java.io.*;
import java.util.ArrayList;

import com.project.Exception.AnimeException;

public class AnimeManager implements Serializable{  

    private ArrayList<Anime> animeList;
    private static String filename = "automated/save/anime.dat";
    
    public AnimeManager() {
        animeList = new ArrayList<Anime>();
    }

    public void insertAnime(Anime anime) throws AnimeException{
        for (Anime a : animeList){
            if (a.getTitle().equals(anime.getTitle())){
                throw new AnimeException("Anime already exists");
            }
        }
        animeList.add(anime);
    }

    public void saveData() throws IOException{
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            System.out.println("Anime successfully saved!");
            oos.close();
        } catch (IOException e) {
            System.out.println("Error during saving: " + e.getMessage());
            throw e; // Consider rethrowing to let the caller handle it
        }
    }

    public static AnimeManager loadData() throws Exception{
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            AnimeManager animeManager = (AnimeManager) ois.readObject();
            ois.close();
            return animeManager;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error during loading: " + e.getMessage());
            throw e; // Rethrow to let the caller handle it
        }
    }

    public ArrayList<Anime> getAnimeList(){
        return animeList;
    }
}
