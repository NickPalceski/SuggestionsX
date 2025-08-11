package nick.boptart.suggestionsX.suggestion;

import nick.boptart.suggestionsX.manager.PlayerManager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Suggestion {
    //TODO add validation for title and description length
    private static final int TITLE_MAX_LENGTH = 50;
    private static final int DESC_MAX_LENGTH = 200;

    private final Set<UUID> upVoters;
    private final Set<UUID> downVoters;

    private final UUID uniqueID;
    private final String creator;
    private final String title;
    private final String description;
    private int status;                 //0: pending, 1: approved, 2: denied


    public int totalVotes;
    public int posVotes;
    public int negVotes;

    // Constructor for new player suggestions (Generates a new UUID)
    public Suggestion(String title, String description, String creator) {
        this.uniqueID = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.status = 0;

        this.totalVotes = 0;
        this.posVotes = 0;
        this.negVotes = 0;

        this.upVoters = new HashSet<>();
        this.downVoters = new HashSet<>();
    }
    // Constructor for new admin created suggestions. Handles status (Generates a new UUID)
    public Suggestion(String title, String description, String creator, int status) {
        this.uniqueID = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.status = status;

        this.totalVotes = 0;
        this.posVotes = 0;
        this.negVotes = 0;

        this.upVoters = new HashSet<>();
        this.downVoters = new HashSet<>();
    }


    // Constructor for loading suggestions from a file (Uses existing UUID)
    public Suggestion(UUID uniqueID, String title, String description, String creator, int status) {
        this.uniqueID = uniqueID;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.status = status;

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

    public void updateStatus(int newStatus) {
        if (this.status == newStatus) {
            throw new IllegalArgumentException("Status is already " + newStatus);
        }

        File playerFile = PlayerManager.getPlayerFileByName(this.creator);

        switch(newStatus) {
            case 0:
                this.status = 0;
                PlayerManager.savePlayerFile(playerFile);
                break;
            case 1:
                this.status = 1;
                PlayerManager.savePlayerFile(playerFile);
                break;
            case 2:
                this.status = 2;
                PlayerManager.savePlayerFile(playerFile);
                break;
            default:
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



}
