import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (other == null) {
            return 1;
        }
        return this.nickname.compareToIgnoreCase(other.nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dog other = (Dog) o;
        return this.nickname.equalsIgnoreCase(other.nickname);
    }

    @Override
    public int hashCode() {
        return nickname.toLowerCase(Locale.ROOT).hashCode();
    }
}

public class Practicum {

    public static void main(String[] args) {
        List<Dog> unsortedDogs = Stream.of(
                        "Дружок", "Пушок", "Тузик", "Ромео",
                        "Белка", "Стрелка", "Бобик", "Афоня",
                        "Волчок")
                .map(Dog::new)
                .collect(Collectors.toList());

        // добавим несколько собак с одинаковыми кличками (регистр различается)
        unsortedDogs.add(new Dog("бЕЛКА"));
        unsortedDogs.add(new Dog("БелКА"));

        // найдите Белку
        Dog dog = new Dog("Белка");
        String result = search(unsortedDogs, dog)
                .map(d -> "А вот и собака по кличке " + d + " нашлась")
                .orElseGet(() -> "Нет собаки по кличке " + dog + " :(");

        System.out.println(result);

        // найдите всех Белок
        List<Dog> allFound = searchAll(unsortedDogs, dog);
        if (allFound.isEmpty()) {
            System.out.println("Собак по кличке '" + dog + "' не найдено");
        } else {
            System.out.println("Нашлись все собаки по кличке '" + dog + "': " + allFound);
        }
    }

    private static <T extends Comparable<T>> Optional<T> search(List<T> unsortedList, T searchObject) {
        List<T> sortedList = mergeSort(unsortedList);
        int idx = binarySearch(sortedList, searchObject);
        return idx >= 0 ? Optional.of(sortedList.get(idx)) : Optional.empty();
    }

    private static <T extends Comparable<T>> List<T> searchAll(List<T> unsortedList, T searchObject) {
        List<T> sortedList = mergeSort(unsortedList);
        int anyIdx = binarySearch(sortedList, searchObject);
        if (anyIdx < 0) {
            return Collections.emptyList();
        }
        int left = anyIdx;
        while (left >= 0 && sortedList.get(left).compareTo(searchObject) == 0) {
            left--;
        }
        int right = anyIdx + 1;
        while (right < sortedList.size() && sortedList.get(right).compareTo(searchObject) == 0) {
            right++;
        }
        return new ArrayList<>(sortedList.subList(left + 1, right));
    }

    // --- методы сортировки и поиска ---

    private static <T extends Comparable<T>> List<T> mergeSort(List<T> list) {
        if (list == null || list.size() <= 1) {
            return list == null ? Collections.emptyList() : new ArrayList<>(list);
        }
        int mid = list.size() / 2;
        List<T> left = mergeSort(list.subList(0, mid));
        List<T> right = mergeSort(list.subList(mid, list.size()));
        return merge(left, right);
    }

    private static <T extends Comparable<T>> List<T> merge(List<T> left, List<T> right) {
        ArrayList<T> result = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) {
            result.add(left.get(i++));
        }
        while (j < right.size()) {
            result.add(right.get(j++));
        }
        return result;
    }

    private static <T extends Comparable<T>> int binarySearch(List<T> list, T target) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = low + ((high - low) >>> 1);
            int cmp = list.get(mid).compareTo(target);
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}

