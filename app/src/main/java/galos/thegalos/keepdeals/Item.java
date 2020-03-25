package galos.thegalos.keepdeals;

class Item {
    private String name;
    private String description;
    private String date;
    private String location;
    private String currentImagePath;

    public Item(String name, String description, String date, String location, String currentImagePath) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.currentImagePath = currentImagePath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

}
