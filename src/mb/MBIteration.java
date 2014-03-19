package mb;

import java.lang.reflect.Field;

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
		public String toString () {
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
		public String toString () {
			return "z^2.01+c";
		}
	};
	
	public static final MBIteration POW_2I_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.pow(0, 2);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		
		@Override
		public String toString () {
			return "z^2i+c";
		}
	};
	
	public static final MBIteration POW_2P2I_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.pow(2, 2);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		
		@Override
		public String toString () {
			return "z^(2+2i)+c";
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
		public String toString () {
			return "z^3+c";
		}
	};
	
	public static final MBIteration POW_301_ADD_C = new MBIteration() {
		@Override
		public final int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound) {
			final double boundSq = Math.pow(bound, 2);
			for (int i = 0; i < iterationDepth; i++) {
				z.pow(3.01);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return iterationDepth;
		}
		
		@Override
		public String toString () {
			return "z^3.01+c";
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
		public String toString () {
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
		public String toString () {
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
		public String toString () {
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
		public String toString () {
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
		public String toString () {
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
			public String toString () {
				return "z^power+c";
			}
		};
	}
	
	public static final MBIteration[] all () {
		try {
			Field[] f = MBIteration.class.getFields();
			MBIteration[] i = new MBIteration[f.length];
			for (int n = 0; n < f.length; n++) {
				i[n] = (MBIteration) f[n].get(null);
			}
			return i;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract int iterate (final Complex z, final Complex c, final int iterationDepth, final double bound);
	
}
