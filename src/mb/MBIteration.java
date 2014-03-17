package mb;

public abstract class MBIteration {
	
	public static final MBIteration sqpc = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.sq();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
	};
	
	public static MBIteration getMBIteration (final double power) {
		if (power == 2) {
			return sqpc;
			
		} else {
			return new MBIteration() {
				@Override
				public int iterate (Complex z, Complex c, int iterationDepth, double bound) {
					final double boundSq = Math.pow(bound, 2);
					for (int i = 0; i < iterationDepth; i++) {
						z.pow(power);
						z.add(c);
						if (z.getAbsSq() > boundSq) {
							return i;
						}
					}
					return iterationDepth;
				}
			};
		}
	}
	
	public abstract int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound);
	
}
