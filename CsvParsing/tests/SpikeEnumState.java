import java.time.Duration;
import java.time.Instant;

public class SpikeEnumState {

	private final static int A = 0x1000;
	private final static int B = 0x2000;
	private final static int C = 0x3000;

	public static void main(String[] args) {

		int[] inputs = new int[100000000];
		for (int i = 0; i < inputs.length; ++i) {
			inputs[1] = (int) (Math.random() * 3 - 1);
		}
		
		for(int j=0; j<10; ++j){
		Instant begin = Instant.now();
		int s = A;
		for (int i : inputs) {
			switch (s) {
			case A:
				switch (i) {
				case -1:
					s = C;
					break;
				case 0:
					s = A;
					break;
				case 1:
					s = B;
					break;
				}
			case B:
				switch (i) {
				case -1:
					s = A;
					break;
				case 0:
					s = B;
					break;
				case 1:
					s = C;
					break;
				}
			case C:
				switch (i) {
				case -1:
					s = B;
					break;
				case 0:
					s = C;
					break;
				case 1:
					s = A;
					break;
				}
			}
		}
		Instant end = Instant.now();
		System.out.println("Elapsed int: " + Duration.between(begin, end));

	    begin = Instant.now();
		s = A;
		for (int i : inputs) {
			int k = i + s;
			switch (k) {
			case A+-1:
					s = C;
					break;
			case A+0:
					s = A;
					break;
			case A+1:
					s = B;
					break;
				case B+-1:
					s = A;
					break;
				case B+0:
					s = B;
					break;
				case B+1:
					s = C;
					break;
				case C-1:
					s = B;
					break;
				case C+0:
					s = C;
					break;
				case C+1:
					s = A;
					break;
			}
		}
		end = Instant.now();
		System.out.println("Elapsed merged: " + Duration.between(begin, end));
		}
	}

}
