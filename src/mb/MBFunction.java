package mb;

import java.lang.reflect.Field;

public abstract class MBFunction {
	
	public static final MBFunction SQUARE_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final int id = p.depth;
			{
				final double rad = c.getAbsSq();
				if (rad < 0.5 && c.re < 0.25 && Math.abs(c.im) < 0.5) {
					return id;
				}
			}
			final double boundSq = p.bound * p.bound;
			double cr = c.re;
			double ci = c.im;
			double zr = z.re;
			double zi = z.im;
			for (int i = 0; i < id; i++) {
				final double zr2 = zr * zr - zi * zi + cr;
				final double zi2 = 2.0 * zr * zi + ci;
				zr = zr2;
				zi = zi2;
				double rSq = zr*zr + zi*zi;
				if (rSq > boundSq) {
					return i;
				}
			}
			return id;
		}
		
		@Override
		public String toString () {
			return "z^2+c";
		}
	};
	
	public static final MBFunction POW_2I_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.pow(0, 2);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "z^2i+c";
		}
	};
	
	public static final MBFunction POW_2P2I_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.pow(2, 2);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "z^(2+2i)+c";
		}
	};
	
	public static final MBFunction CUBE_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.cu();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "z^3+c";
		}
	};
	
	public static final MBFunction QUAD_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.sq();
				z.sq();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "z^4+c";
		}
	};
	
	public static final MBFunction SQUARE_SIN_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.sq();
				z.sin();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "sin(z^2)+c";
		}
	};
	
	public static final MBFunction SQUARE_COS_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.sq();
				z.cos();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "cos(z^2)+c";
		}
	};
	
	public static final MBFunction COS_SQUARE_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.cos();
				z.sq();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "cos(z)^2+c";
		}
	};
	
	public static final MBFunction SIN_SQUARE_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.sin();
				z.sq();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "sin(z)^2+c";
		}
	};
	
	public static final MBFunction DIV_C_SIN_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.div(c);
				z.sin();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "sin(z/c)+c";
		}
	};
	
	public static final MBFunction SIN_DIV_C_ADD_C = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.sin();
				z.div(c);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "sin(z)/c+c";
		}
	};
	
	public static final MBFunction POW_ADD_C = new MBFunction() {
		@Override
		public int iterate (Complex z, Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.pow(p.power);
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		@Override
		public boolean usesPower () {
			return true;
		}
		@Override
		public String toString () {
			return "z^n+c";
		}
	};
	
	public static final MBFunction EXP_SQ = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.exp();
				z.sq();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "exp(z)^2+c";
		}
	};
	public static final MBFunction SQ_EXP = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.sq();
				z.exp();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "exp(z^2)+c";
		}
	};
	public static final MBFunction CU_EXP = new MBFunction() {
		@Override
		public final int iterate (final Complex z, final Complex c, MBFunctionParams p) {
			final double boundSq = Math.pow(p.bound, 2);
			for (int i = 0; i < p.depth; i++) {
				z.cu();
				z.exp();
				z.add(c);
				if (z.getAbsSq() > boundSq) {
					return i;
				}
			}
			return p.depth;
		}
		
		@Override
		public String toString () {
			return "exp(z^4)+c";
		}
	};
	public static final MBFunction[] all () {
		try {
			Field[] f = MBFunction.class.getFields();
			MBFunction[] i = new MBFunction[f.length];
			for (int n = 0; n < f.length; n++) {
				i[n] = (MBFunction) f[n].get(null);
			}
			return i;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract int iterate (final Complex z, final Complex c, MBFunctionParams p);
	
	public boolean usesPower() {
		return false;
	}
}
