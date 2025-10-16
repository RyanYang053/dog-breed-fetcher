package dogapi;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        BreedFetcher fetcher = new CachingBreedFetcher(new BreedFetcherForLocalTesting());

        String breed = "hound";
        int result = getNumberOfSubBreeds(breed, fetcher);
        System.out.println(breed + " has " + result + " sub breeds");

        breed = "cat";
        result = getNumberOfSubBreeds(breed, fetcher);
        if (result < 0) {
            System.out.println("Error: Breed not found: " + breed);
        } else {
            System.out.println(breed + " has " + result + " sub breeds");
        }
    }


    /**
     * Return the number of sub breeds that the given dog breed has according to the
     * provided fetcher.
     * @param breed the name of the dog breed
     * @param breedFetcher the breedFetcher to use
     * @return the number of sub breeds. Zero should be returned if there are no sub breeds
     * returned by the fetcher
     */
    public static int getNumberOfSubBreeds(String breed, BreedFetcher breedFetcher) {
        try {
            List<String> subs = breedFetcher.getSubBreeds(breed);
            return subs.size();           // 0 if no sub-breeds
        } catch (BreedFetcher.BreedNotFoundException e) {
            return 0;                    // test expects -1 when invalid
        }
    }
}
