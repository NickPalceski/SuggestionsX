package nick.boptart.suggestionsX.util;

import nick.boptart.suggestionsX.manager.ConfigManager;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Suggestion {

    private static final int TITLE_MAX_LENGTH = 50;
    private static final int DESC_MAX_LENGTH = 200;

    private final Set<UUID> voters;

    private final UUID uniqueID;
    private final String creator;
    private final String title;
    private final String description;
    private int status;                 //0: pending, 1: approved, 2: denied


    public int totalVotes;
    public int posVotes;
    public int negVotes;


    //constructor
    public Suggestion(String title, String description, String creator) {
        this.uniqueID = UUID.randomUUID();
        this.creator = creator;
        this.voters= new HashSet<>();
        this.status = 0;
        this.totalVotes = 0;
        this.posVotes = 0;
        this.negVotes = 0;

        if (title.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("Title cannot be longer than " + TITLE_MAX_LENGTH + " characters");
        }
        this.title = title;

        if (description.length() > DESC_MAX_LENGTH) {
            throw new IllegalArgumentException("Description cannot be longer than " + DESC_MAX_LENGTH + " characters");
        }
        this.description = description;

    }

    public void increasePosVotes(){posVotes++;};

    public void decreasePosVotes(){posVotes--;};

    public void increaseNegVotes(){negVotes++;};

    public void decreaseNegVotes(){negVotes--;};

    public void updateStatus(int newStatus) {
        if (this.status == newStatus) {
            throw new IllegalArgumentException("Status is already " + newStatus);
        } else if (newStatus == 0) {
        } else if (newStatus == 1) {
        } else if (newStatus == 2) {
        } else {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    public Set<UUID> getVoters() {
        return voters;
    }

    public void increaseTotalVotes(Suggestion suggestion){
        suggestion.totalVotes++;
    }

    public void decreaseTotalVotes(){
        totalVotes--;
    }


    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getCreator() {
        return creator;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() { return description; }

    public int getStatus() {
        return status;
    }

    public int getTotalVotes() {
        return totalVotes;
    }
    public int getPosVotes() {
        return posVotes;
    }
    public int getNegVotes() {
        return negVotes;
    }

    public static Suggestion getSuggestionByTitle(String title) {

        List<Suggestion> pendingSuggestions = ConfigManager.getPendingSuggestions();
        System.out.println("Pending suggestions count: " + pendingSuggestions.size());

        for (Suggestion s : pendingSuggestions) {
            System.out.println("Checking suggestion title: " + s.getTitle());
        }

        Suggestion suggestion = pendingSuggestions.stream()
                .filter(s -> s.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);

        if (suggestion == null) {
            System.out.println(ChatColor.RED + "Suggestion not found.");
            return null;
        }
        return suggestion;
    }


}
