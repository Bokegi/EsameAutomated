package com.project.UI;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import com.project.Exception.AnimeException;
import com.project.app.Anime;
import com.project.app.AnimeManager;
import com.project.app.AnimeState;

public class Home {
    private AnimeManager animeManager;
    private Scanner scanner = new Scanner(System.in);

    public Home(){
        try {
            animeManager = AnimeManager.loadData();
            if (animeManager == null){
                animeManager = new AnimeManager();
            }
        } catch (Exception e) {
            System.out.println("Error during loading: " + e.getMessage());
        }
        homePage();
    }

    private void homePage() {

        int choice = 0;

        do {
            System.out.println("Welcome to the Anime DataBase");
            System.out.println("1. Insert Anime");
            System.out.println("2. Update Anime");
            System.out.println("3. Stamp");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            try {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            insertAnime();
                        break;

                        case 2:
                            updateAnime();
                        break;

                        case 3:
                            stamp();
                        break;

                        case 4:
                            System.out.println("Exiting.....");
                        break;

                        default:
                            System.out.println("Invalid choice");
                        break;
                    }
                } else {
                    System.out.println("Input non valido. Inserisci un numero intero.");
                    scanner.next(); // Consuma l'input non valido
                }
            } catch (NoSuchElementException e) {
                System.out.println("Errore: Input non valido.");
                scanner.next(); // Consuma l'input non valido
            }
        } while (choice != 4);

        scanner.close(); // Chiudi lo scanner quando hai finito
    }

    

    private void insertAnime() {
        scanner.nextLine(); // Consume newline
        System.out.println("Insert title:");
        String title = scanner.nextLine();
        System.out.println("Insert author:");
        String author = scanner.nextLine();
        System.out.println("Insert production:");
        String production = scanner.nextLine();
        System.out.println("Insert year of production:");
        int year = scanner.nextInt();
        System.out.println("Insert episode:");
        int episode = scanner.nextInt();
        System.out.println("Insert n° episode watched:");
        int nEpisodeWatched = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        AnimeState state = selectAnimeState(); // Method to handle state selection

        Anime newAnime = new Anime(title, author, episode, production, year, nEpisodeWatched, state);

        try {
            animeManager.insertAnime(newAnime);
            animeManager.saveData(); // Save using this instance
            System.out.println("Anime inserted");
        } catch (AnimeException | IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private AnimeState selectAnimeState() {
        System.out.println("Select anime state (1 = STARTED, 2 = TO_WATCH, 3 = COMPLETED): ");
        int stateInput = scanner.nextInt();
        switch (stateInput) {
            case 1: return AnimeState.STARTED;
            case 2: return AnimeState.TO_WATCH;
            case 3: return AnimeState.COMPLETED;
            default:
                System.out.println("Invalid selection. Defaulting to TO_WATCH.");
                return AnimeState.TO_WATCH;
        }
    }

    public void updateAnime() {
        System.out.println("Insert the title of the anime you want to update:");
        scanner.nextLine(); // Consume newline
        String title = scanner.nextLine();
        Optional<Anime> animeOpt = animeManager.getAnimeList().stream()
            .filter(a -> a.getTitle().equalsIgnoreCase(title))
            .findFirst();

        if (animeOpt.isPresent()) {
            Anime anime = animeOpt.get();
            System.out.println("INFO: " + anime);
            modifyAnimeState(anime);
            System.out.println("Anime successfully updated!");
            try {
                animeManager.saveData();
            } catch (IOException e) {
                System.err.println("Error saving updated anime: " + e.getMessage());
            }
        } else {
            System.out.println("Anime not found!");
        }
    }

    private void modifyAnimeState(Anime anime) {
        System.out.println("Would you like to modify anime state? (y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            System.out.println("Insert anime state (COMPLETED, STARTED, TO_WATCH): ");
            String stateInput = scanner.nextLine().toUpperCase();
            try {
                AnimeState newState = AnimeState.valueOf(stateInput);
                anime.setState(newState);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid state entered. Please enter a valid state.");
            }
        }
    }

    public void stamp() {
        long completed = animeManager.getAnimeList().stream()
            .filter(a -> a.getState() == AnimeState.COMPLETED)
            .count();
        long watching = animeManager.getAnimeList().stream()
            .filter(a -> a.getState() == AnimeState.STARTED)
            .count();
        long toWatch = animeManager.getAnimeList().stream()
            .filter(a -> a.getState() == AnimeState.TO_WATCH)
            .count();

        System.out.println("Statistics:");
        System.out.println("- Anime completed: " + completed);
        System.out.println("- Anime watching: " + watching);
        System.out.println("- Anime to watch: " + toWatch);
    }
}
