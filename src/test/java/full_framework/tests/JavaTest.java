package full_framework.tests;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class JavaTest
{
    List<Object> mixArr = List.of("a", 1);
    List<Integer> intArr = List.of(1,3,6,9,2,9,4);

    @Test(enabled = false)
    public void reverseArray() {
    System.out.println("arrMix = " + mixArr);

    List<Object> arr2 = new ArrayList<>();
    for (int i = mixArr.size() - 1; i >= 0; i --) {
        arr2.add(mixArr.get(i));
    }
    System.out.println("arr2 = " + arr2);
    }

    @Test(
            enabled = false
    )
    public void verifySorted() {
        assert !intArr.stream().sorted().toList().equals(intArr) : "List is not sorted";
    }

    @Test(
            enabled = false
    )
    public void findRepeating() {
        List<Integer> arr2 = new ArrayList<>(List.of());
        for (int i = intArr.size() - 1; i >= 0; i --) {
            if (arr2.contains(intArr.get(i)) || intArr.subList(0, i).contains(intArr.get(i))) {
                arr2.add(intArr.get(i));
            }
            else {
                arr2.add(0, intArr.get(i));
            }
        }
        System.out.println("arr2 = " + arr2);
    }

    @Test(
            enabled = false
    )
    public void evenOddNumbers() {
        Map<Boolean, List<Integer>> partitioned = intArr.stream().collect(Collectors.partitioningBy(i -> i % 2 == 0));

        List<Integer> even = partitioned.get(true);
        System.out.println("even = " + even);

        List<Integer> odd = partitioned.get(false);
        System.out.println("odd = " + odd);

    }

}
