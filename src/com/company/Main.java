package com.company;

import java.math.BigInteger;
import java.util.*;

public class Main {

    // "Not divisible by any prime greater than 20" is equivalent to
    // "Has a prime factorization containing only primes <= 20"
    // this array contains those primes, in BigInteger form so we get
    // arbitrary size and precise integer powers

    private static List<BigInteger> primes = new ArrayList<BigInteger>(
            Arrays.asList(BigInteger.valueOf(2L),
                    BigInteger.valueOf(3L),
                    BigInteger.valueOf(5L),
                    BigInteger.valueOf(7L),
                    BigInteger.valueOf(11L),
                    BigInteger.valueOf(13L),
                    BigInteger.valueOf(17L),
                    BigInteger.valueOf(19L)));
    private static List<Integer> startPowers = new ArrayList<Integer>(
            Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));

    // this is a variant of computing the Hamming numbers, extended to include
    // more prime factors. The same methodology will work.
    private static class HammingNumber implements Comparable<HammingNumber>
    {
        private List<Integer> powers;
        private BigInteger value;

        HammingNumber(List<Integer> powers)
        {
            if (powers.size() != primes.size())
            {
                throw new IllegalArgumentException();
            }

            this.powers = powers;

            // compute the value of primes[0]^powers[0] + primes[1]^powers[1]...
            BigInteger v = BigInteger.ONE;
            for (int i = 0; i < powers.size(); i++)
            {
                v = v.multiply(primes.get(i).pow(powers.get(i)));
            }

            this.value = v;
        }

        public BigInteger getValue()
        {
            return value;
        }

        public List<Integer> getPowers()
        {
            return powers;
        }

        // get the list of all possible numbers with exactly one increased power
        // omit lists that have already been seen, add the ones we do use to the seen set
        public ArrayList<HammingNumber> promotions(Set<List<Integer>> seen)
        {
            ArrayList<HammingNumber> nums = new ArrayList<HammingNumber>();

            for (int i = 0; i < powers.size(); i++) {
                powers.set(i, powers.get(i) + 1);

                if (!seen.contains(powers))
                {
                    List<Integer> newPowers = (List<Integer>) ((ArrayList<Integer>) powers).clone();
                    nums.add(new HammingNumber(newPowers));
                    seen.add(newPowers);
                }

                powers.set(i, powers.get(i) - 1);
            }

            return nums;
        }

        @Override
        public int compareTo(HammingNumber num)
        {
            return getValue().compareTo(num.getValue());
        }
    }

    // Given the current priority queue and seen set, find the next number
    // and update the priority queue and seen set
    private static HammingNumber nextStep(PriorityQueue<HammingNumber> queue,
                                          Set<List<Integer>> seen)
    {
        HammingNumber nextNum = queue.poll();

        ArrayList<HammingNumber> promotions = nextNum.promotions(seen);
        for (HammingNumber n : promotions)
        {
            queue.add(n);
        }

        return nextNum;
    }

    public static void main (String[] args) throws java.lang.Exception
    {
        // start with 1, and keep iterating until we hit 1,000,000
        HammingNumber current = new HammingNumber(startPowers);

        PriorityQueue<HammingNumber> queue = new PriorityQueue<HammingNumber>();
        Set<List<Integer>> seen = new HashSet<List<Integer>>();

        seen.add(startPowers);
        queue.add(current);

        System.out.println("Beginning calculation...");

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 1000000; i++)
        {
            current = nextStep(queue, seen);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("The 1,000,000th number is:");
        System.out.println(current.getValue().toString());
        System.out.print("(");
        for (int i = 0; i < primes.size(); i++)
        {
            if (i != 0)
            {
                System.out.print(" * ");
            }
            System.out.print(primes.get(i) + "^" + current.getPowers().get(i));
        }
        System.out.println(")");

        System.out.println((endTime - startTime) + " milliseconds");
    }
}
