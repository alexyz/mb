package mb;

abstract class MBFunction {
	
	public static MBFunction getFunction(double power) {
		if (power == 4) {
			return qupc;
		} else if (power == 3) {
			return cupc; 
		} else if (power == 2) {
			return sqpc;
		} else {
			return powpc(power);
		}
	}
	
	public static MBFunction sqpc = new MBFunction() {
		@Override
		public final void apply (Complex z, Complex p) {
			z.mul(z);
			z.add(p);
		}
	};
	
	public static MBFunction cupc = new MBFunction() {
		@Override
		public void apply (Complex z, Complex p) {
			double re = z.re;
			double im = z.im;
			z.mul(z);
			z.mul(re, im);
			z.add(p);
		}
	};
	
	public static MBFunction qupc = new MBFunction() {
		@Override
		public void apply (Complex z, Complex p) {
			z.mul(z);
			z.mul(z);
			z.add(p);
		}
	};
	
	public static MBFunction powpc (final double pow) {
		return new MBFunction() {
			@Override
			public void apply (Complex z, Complex p) {
				z.pow(pow);
				z.add(p);
			}
		};
	}
	
	public abstract void apply (Complex z, Complex p);
}