package mb;

abstract class MBFunction {
	public static MBFunction sqpc = new MBFunction() {
		@Override
		public void apply (Complex z, Complex p) {
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