import java.time.Duration;
import java.time.Instant;

public class SpikeNestedSwithc {

	enum State {
		A, B, C;
	}


	private final static int A = 0;
	private final static int B = 1;
	private final static int C = 2;

	public static void main(String[] args) {

		int[] inputs = new int[10000000];
		for (int i = 0; i < inputs.length; ++i) {
			inputs[1] = (int) (Math.random() * 3 - 1);
		}

		Instant begin = Instant.now();
		State es = State.A;
		for (int i : inputs) {
			switch (es) {
			case A:
				switch (i) {
				case -1:
					es = State.C;
					break;
				case 0:
					es = State.A;
					break;
				case 1:
					es = State.B;
					break;
				}
			case B:
				switch (i) {
				case -1:
					es = State.A;
					break;
				case 0:
					es = State.B;
					break;
				case 1:
					es = State.C;
					break;
				}
			case C:
				switch (i) {
				case -1:
					es = State.B;
					break;
				case 0:
					es = State.C;
					break;
				case 1:
					es = State.A;
					break;
				}
			}
		}
		Instant end = Instant.now();
		System.out.println("Elapsed enum: " + Duration.between(begin, end));

		
		 begin = Instant.now();
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
		 end = Instant.now();
		System.out.println("Elapsed int: " + Duration.between(begin, end));

	}

}
