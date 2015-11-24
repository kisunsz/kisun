package kisun.algorithm;

/**
 * @author Kingsley Sun - 24 Nov 2015
 */
public class Prime {
	public static void main(String[] args) {
		int n = 100000000;
		int[] primes = m1(n);
		primes = m2(n);
		// display(primes);
	}

	static int[] m1(int n) {
		long start = System.currentTimeMillis();
		int[] primes = new int[n / 2 + 1];
		primes[0] = 2;
		int index = 1;
		for (int i = 3; i <= n; i += 2) {
			if (isPrime1(i, n)) {
				primes[index++] = i;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("m1:" + (end - start));
		return primes;
	}

	static boolean isPrime1(int i, int n) {
		int s = (int) Math.sqrt(n);
		boolean hasOther = false;
		for (int j = 3; j <= s; j += 2) {
			if (i % j == 0 && i != j && i != 1) {
				hasOther = true;
				break;
			}
		}
		return !hasOther;
	}

	static int[] m2(int n) {
		long start = System.currentTimeMillis();
		int[] all = new int[n + 1];
		int[] primes = new int[n / 2 + 1];

		primes[0] = 2;
		// init
		for (int i = 1; i <= n; i += 2) {
			all[i] = i;
		}
		int index = 1;

		for (int i = 3; i <= n; i++) {
			if (all[i] != 0) {
				primes[index] = i;
				for (int j = primes[index]; j <= n; j += primes[index]) {
					all[j] = 0;
				}
				index++;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("m2:" + (end - start));
		return primes;
	}

	static void display(int[] primes) {
		for (int i = 0; i < primes.length - 1; i++)
			if (primes[i] != 0)
				System.out.println(primes[i]);
	}

}
