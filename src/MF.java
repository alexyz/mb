
abstract class MF {
	public static MF sqpc = new MF() {
		@Override
		public void apply(C z, C p) {
			z.mul(z);
			z.add(p);
		}
	};
	public static MF powpc(final double pow) {
		return new MF() {
			@Override
			public void apply(C z, C p) {
				z.pow(pow);
				z.add(p);
			}
		};
	};
	public abstract void apply(C z, C p);
}