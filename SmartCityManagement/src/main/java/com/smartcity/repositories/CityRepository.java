package main.java.com.smartcity.repositories;

import main.java.com.smartcity.models.CityResource;
import java.io.*;
import java.util.ArrayList;

public class CityRepository<T extends CityResource> implements Serializable {
    private ArrayList<T> resources;
    private final String filePath;

    public CityRepository(String filePath) {
        this.resources = new ArrayList<>();
        this.filePath = filePath;
    }

    public void addResource(T resource) {
        if (resource != null) resources.add(resource);
    }

    public boolean removeResourceById(String id) {
        return resources.removeIf(r -> r.getResourceID().equals(id));
    }

    public T getResourceById(String id) {
        for (T resource : resources) {
            if (resource.getResourceID().equals(id)) return resource;
        }
        return null;
    }

    public ArrayList<T> getAllResources() {
        return new ArrayList<>(resources);
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(resources);
        } catch (IOException e) {
            System.err.println("Save error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) this.resources = (ArrayList<T>) obj;
        } catch (IOException | ClassNotFoundException e) {
            this.resources = new ArrayList<>();
        }
    }

    public boolean updateResource(T updatedResource) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getResourceID().equals(updatedResource.getResourceID())) {
                resources.set(i, updatedResource);
                return true;
            }
        }
        return false;
    }
}
