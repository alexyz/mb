package mb;

public abstract class MBIteration {
	
	public static final MBIteration SQUARE_ADD_C = new MBIteration() {
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
		@Override
		public String toString() {
			return "z^2+c";
		}
	};

	public static final MBIteration POW_201_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.pow(2.01);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		@Override
		public String toString() {
			return "z^2.01+c";
		}
	};
	
	public static final MBIteration CUBE_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.cu();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		@Override
		public String toString() {
			return "z^3+c";
		}
	};
	
	public static final MBIteration QUAD_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.sq();
				z.sq();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		@Override
		public String toString() {
			return "z^4+c";
		}
	};
	public static final MBIteration SQUARE_SIN_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.sq();
				z.sin();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		@Override
		public String toString() {
			return "sin(z^2)+c";
		}
	};
	
	public static final MBIteration SIN_SQUARE_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.sin();
				z.sq();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		@Override
		public String toString() {
			return "sin(z)^2+c";
		}
	};
	
	public static final MBIteration DIV_C_SIN_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.div(c);
				z.sin();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		@Override
		public String toString() {
			return "sin(z/c)+c";
		}
	};
	
	public static final MBIteration SIN_DIV_C_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.sin();
				z.div(c);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		@Override
		public String toString() {
			return "sin(z)/c+c";
		}
	};
	
	public static MBIteration powpc (final double power) {
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
			@Override
			public String toString() {
				return "z^power+c";
			}
		};
	}
	
	public static final MBIteration[] ALL = {
		SQUARE_ADD_C, POW_201_ADD_C, CUBE_ADD_C, QUAD_ADD_C, SQUARE_SIN_ADD_C, DIV_C_SIN_ADD_C, SIN_SQUARE_ADD_C, SIN_DIV_C_ADD_C
	};
	
	public abstract int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound);
	
}
