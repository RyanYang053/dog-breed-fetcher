package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException(String.valueOf(breed));
        }

        final String normalized = breed.trim().toLowerCase(Locale.ROOT);
        final String url = "https://dog.ceo/api/breed/" + normalized + "/list";

        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "");

            if ("error".equalsIgnoreCase(status)) {
                // API returns error for unknown breeds
                throw new BreedNotFoundException(breed);
            }
            if (!"success".equalsIgnoreCase(status)) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray arr = json.optJSONArray("message"); // array of sub-breeds
            List<String> result = new ArrayList<>();
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    String sub = arr.optString(i, "").trim();
                    if (!sub.isEmpty()) {
                        result.add(sub);
                    }
                }
            }
            return Collections.unmodifiableList(result);
        } catch (IOException | org.json.JSONException e) {
            // Per assignment requirement: surface any failure as BreedNotFoundException
            throw new BreedNotFoundException(breed);
        }
    }
}
