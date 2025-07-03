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

    private final Set<UUID> upVoters;
    private final Set<UUID> downVoters;

    private final UUID uniqueID;
    private final String creator;
    private final String title;
    private final String description;
    private final int status;                 //0: pending, 1: approved, 2: denied


    public int totalVotes;
    public int posVotes;
    public int negVotes;

    // Constructor for new suggestions (Generates a new UUID)
    public Suggestion(String title, String description, String creator) {
        this.uniqueID = UUID.randomUUID();
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.status = 0;

        this.totalVotes = 0;
        this.posVotes = 0;
        this.negVotes = 0;

        this.upVoters = new HashSet<>();
        this.downVoters = new HashSet<>();
    }

    // Constructor for loading suggestions from a file (Uses existing UUID)
    public Suggestion(UUID uniqueID, String title, String description, String creator) {
        this.uniqueID = uniqueID;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.status = this.getStatus();

        this.totalVotes = this.getTotalVotes();
        this.posVotes = this.getPosVotes();
        this.negVotes = this.getNegVotes();

        this.upVoters = this.getUpVoters();
        this.downVoters = this.getDownVoters();
    }

    public void increasePosVotes(){posVotes++;}

    public void decreasePosVotes(){posVotes--;}

    public void increaseNegVotes(){negVotes++;}

    public void decreaseNegVotes(){negVotes--;}

    public int updateStatus(int newStatus) {
        if (this.status == newStatus) {
            throw new IllegalArgumentException("Status is already " + newStatus);
        } else if (newStatus == 0) {
            return 0;
        } else if (newStatus == 1) {
            return 1;
        } else if (newStatus == 2) {
            return 2;
        } else {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    public Set<UUID> getUpVoters() {
        return upVoters;
    }
    public Set<UUID> getDownVoters() {
        return downVoters;
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
    public int getPosVotes() {return posVotes; }
    public int getNegVotes() {
        return negVotes;
    }



    public static Suggestion getSuggestionByTitle(String title) {

        System.out.println("Pending suggestions count: " + ConfigManager.getPendingSuggestions().size());

        for (Suggestion suggestion : ConfigManager.getPendingSuggestions()) {

            if (suggestion.getTitle().equalsIgnoreCase(title)) {
                System.out.println("Found exact match: " + suggestion.getTitle());
                return suggestion;
            }

            if (ChatColor.stripColor(suggestion.getTitle()).equalsIgnoreCase(ChatColor.stripColor(title))) {
                System.out.println("Found match after stripping color codes: " + suggestion.getTitle());
                return suggestion;
            }
        }

        System.out.println("Suggestion not found for title: " + title);
        return null;
    }


}
