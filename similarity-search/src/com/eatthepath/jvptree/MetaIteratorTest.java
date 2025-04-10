package com.eatthepath.jvptree;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class MetaIteratorTest {

    public void testHasNextAndNext() {
        {
            final MetaIterator<Object> emptyIterator = new MetaIterator<>(new ArrayList<Iterator<Object>>());


            try {
                emptyIterator.next();
            } catch (NoSuchElementException e) {
                // This is supposed to happen for empty iterators
            }
        }

        {
            final ArrayList<Integer> integers = new ArrayList<>();
            integers.add(1);
            integers.add(2);
            integers.add(3);

            final MetaIterator<Integer> singleIterator =
                    new MetaIterator<>(Collections.singletonList(integers.iterator()));

            final ArrayList<Integer> integersFromIterator = new ArrayList<>();

            while (singleIterator.hasNext()) {
                integersFromIterator.add(singleIterator.next());
            }

        }

        {
            final ArrayList<Integer> firstIntegers = new ArrayList<>();
            firstIntegers.add(1);
            firstIntegers.add(2);
            firstIntegers.add(3);

            final ArrayList<Integer> emptyList = new ArrayList<>();

            final ArrayList<Integer> secondIntegers = new ArrayList<>();
            secondIntegers.add(4);
            secondIntegers.add(5);
            secondIntegers.add(6);

            final ArrayList<Iterator<Integer>> iterators = new ArrayList<>();
            iterators.add(firstIntegers.iterator());
            iterators.add(emptyList.iterator());
            iterators.add(secondIntegers.iterator());

            final MetaIterator<Integer> multipleIterator = new MetaIterator<>(iterators);

            final ArrayList<Integer> integersFromIterator = new ArrayList<>();

            while (multipleIterator.hasNext()) {
                integersFromIterator.add(multipleIterator.next());
            }

            final ArrayList<Integer> combinedList = new ArrayList<>();
            combinedList.addAll(firstIntegers);
            combinedList.addAll(emptyList);
            combinedList.addAll(secondIntegers);

        }
    }

    public void testRemove() {
        new MetaIterator<>(new ArrayList<Iterator<Object>>()).remove();
    }

}
