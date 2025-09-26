import java.util.*;
import java.util.stream.*;

class Dog implements Comparable<Dog> {
    private final String nickname;

    public Dog(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return nickname;
    }

    @Override
    public int compareTo(Dog other) {
        if (other == null) return 1;
        String a = this.nickname == null ? "" : this.nickname.toLowerCase(Locale.ROOT);
        String b = other.nickname == null ? "" : other.nickname.toLowerCase(Locale.ROOT);
        return a.compareTo(b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dog dog = (Dog) o;
        String a = this.nickname == null ? "" : this.nickname.toLowerCase(Locale.ROOT);
        String b = dog.nickname == null ? "" : dog.nickname.toLowerCase(Locale.ROOT);
        return a.equals(b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname == null ? "" : nickname.toLowerCase(Locale.ROOT));
    }
}

public class Practicum {

    public static void main(String[] args) {
        List<Dog> unsortedDogs = Stream.of(
                "Дружок", "Пушок", "Тузик", "Ромео",
                "Белка", "Стрелка", "Бобик", "Афоня",
                "Волчок", "белка")
            .map(Dog::new)
            .collect(Collectors.toList());

        Dog dog = new Dog("Белка");
        String result = search(unsortedDogs, dog)
                .map(d -> "А вот и собака по кличке " + d + " нашлась")
                .orElseGet(() -> "Нет собаки по кличке " + dog + " :(");
        System.out.println(result);

        List<Dog> foundAll = searchAll(unsortedDogs, dog);
        System.out.println("Найдено совпадений: " + foundAll.size() + ": " + foundAll);
    }

    private static <T extends Comparable<T>> Optional<T> search(List<T> unsortedList, T searchObject) {
        List<T> sortedList = new ArrayList<>(unsortedList);
        mergeSort(sortedList);
        int idx = binarySearch(sortedList, searchObject);
        if (idx < 0) {
            return Optional.empty();
        }
        return Optional.of(sortedList.get(idx));
    }

    private static <T extends Comparable<T>> List<T> searchAll(List<T> unsortedList, T searchObject) {
        List<T> sortedList = new ArrayList<>(unsortedList);
        mergeSort(sortedList);
        int idx = binarySearch(sortedList, searchObject);
        if (idx < 0) return Collections.emptyList();
        List<T> results = new ArrayList<>();
        int left = idx;
        while (left >= 0 && sortedList.get(left).compareTo(searchObject) == 0) {
            left--;
        }
        int right = idx;
        while (right < sortedList.size() && sortedList.get(right).compareTo(searchObject) == 0) {
            right++;
        }
        for (int i = left + 1; i < right; i++) {
            results.add(sortedList.get(i));
        }
        return results;
    }

    // Generic merge sort
    public static <T extends Comparable<T>> void mergeSort(List<T> list) {
        if (list.size() <= 1) return;
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Comparable[list.size()];
        mergeSort(list, aux, 0, list.size() - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(List<T> list, T[] aux, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(list, aux, left, mid);
        mergeSort(list, aux, mid + 1, right);
        if (list.get(mid).compareTo(list.get(mid + 1)) <= 0) return; // already sorted
        merge(list, aux, left, mid, right);
    }

    private static <T extends Comparable<T>> void merge(List<T> list, T[] aux, int left, int mid, int right) {
        for (int i = left; i <= right; i++) {
            aux[i] = list.get(i);
        }
        int i = left, j = mid + 1;
        for (int k = left; k <= right; k++) {
            if (i > mid) list.set(k, aux[j++]);
            else if (j > right) list.set(k, aux[i++]);
            else if (aux[i].compareTo(aux[j]) <= 0) list.set(k, aux[i++]);
            else list.set(k, aux[j++]);
        }
    }

    // Generic binary search: returns index or -1 if not found
    public static <T extends Comparable<T>> int binarySearch(List<T> sortedList, T key) {
        int left = 0, right = sortedList.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = sortedList.get(mid).compareTo(key);
            if (cmp == 0) return mid;
            if (cmp < 0) left = mid + 1; else right = mid - 1;
        }
        return -1;
    }
}
 
